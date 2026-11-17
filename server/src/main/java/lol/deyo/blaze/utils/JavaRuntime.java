package lol.deyo.blaze.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import lol.deyo.blaze.updater.RestartCommand;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.PluginClassVersion;

/**
 * Handles plugin/runtime Java version compatibility.
 *
 * <p>A JVM cannot load class files newer than itself. Instead of failing a
 * plugin with an opaque {@link UnsupportedClassVersionError}, Blaze inspects the
 * plugin jars before startup and, when a plugin needs a newer Java release than
 * the current process, restarts itself on the newest compatible JVM that is
 * already installed. When no such JVM exists it leaves the server running and
 * the plugin loader reports exactly which Java version is required.</p>
 */
public final class JavaRuntime {
   /** Set for the child process so a failed relaunch can never loop. */
   public static final String RELAUNCH_ENV = "BLAZE_JAVA_RELAUNCHED";
   /** Set to {@code false} to keep Blaze on the JVM that was used to start it. */
   public static final String RELAUNCH_PROPERTY = "blaze.javaRelaunch";

   private JavaRuntime() {
   }

   /**
    * Restarts the server on a newer installed JVM when at least one plugin in
    * {@code pluginsDirectory} needs one. When a restart is possible the current
    * process exits and this method does not return; otherwise it returns after
    * reporting the problem.
    */
   public static void maybeRelaunch(File pluginsDirectory) {
      if (!relaunchEnabled() || System.getenv(RELAUNCH_ENV) != null) {
         return;
      }

      int current = currentFeature();
      Requirement requirement = requirements(pluginsDirectory);
      if (requirement.feature <= current) {
         return;
      }

      String names = join(requirement.plugins);
      System.out.println(
         "[Blaze] " + names + " " + (requirement.plugins.size() == 1 ? "requires" : "require")
            + " Java " + requirement.feature + " or newer, but this server is running Java " + current + "."
      );

      Installation installation = pinnedInstallation(requirement.feature);
      if (installation == null) {
         installation = findBest(discover(), requirement.feature, currentHome());
      }
      if (installation == null) {
         System.out.println(
            "[Blaze] No installed Java " + requirement.feature + " runtime was found. Install Java " + requirement.feature
               + "+ or set BLAZE_JAVA_HOME to its directory, then start Blaze again."
         );
         return;
      }

      Path serverJar = serverJar();
      if (serverJar == null) {
         System.out.println("[Blaze] Could not locate the running server jar, so the Java runtime cannot be upgraded automatically.");
         return;
      }

      List<String> command = RestartCommand.build(serverJar, installation.javaExecutable().toString());
      if (command == null) {
         System.out.println("[Blaze] Could not rebuild the server command line to restart on Java " + installation.feature + ".");
         return;
      }

      System.out.println(
         "[Blaze] Restarting on Java " + installation.feature + " (" + installation.home + ") so " + names + " can load."
      );

      ProcessBuilder builder = new ProcessBuilder(command);
      builder.directory(new File(System.getProperty("user.dir", ".")));
      builder.environment().put(RELAUNCH_ENV, "1");
      builder.inheritIO();

      try {
         final Process process = builder.start();
         Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
               process.destroy();
            }
         });
         System.exit(process.waitFor());
      } catch (InterruptedException exception) {
         Thread.currentThread().interrupt();
      } catch (IOException exception) {
         System.out.println("[Blaze] Could not restart on Java " + installation.feature + ": " + exception.getMessage());
      }
   }

   /**
    * The Java feature release of the currently running JVM, for example 8 or 21.
    */
   public static int currentFeature() {
      return featureVersion(System.getProperty("java.specification.version", System.getProperty("java.version", "1.8")));
   }

   /**
    * Highest Java feature release required by the jars in a plugin directory.
    */
   public static Requirement requirements(File directory) {
      if (directory == null || !directory.isDirectory()) {
         return new Requirement(0, Collections.<String>emptyList());
      }

      File[] files = directory.listFiles();
      if (files == null) {
         return new Requirement(0, Collections.<String>emptyList());
      }

      int highestFeature = 0;
      List<String> names = new ArrayList<>();
      for (File file : files) {
         if (file == null || !file.isFile() || !file.getName().toLowerCase(Locale.ROOT).endsWith(".jar")) {
            continue;
         }

         int classVersion = pluginMainClassVersion(file);
         if (classVersion <= 0) {
            continue;
         }

         int feature = PluginClassVersion.javaFeature(classVersion);
         if (feature > highestFeature) {
            highestFeature = feature;
            names.clear();
         }

         if (feature == highestFeature) {
            names.add(file.getName());
         }
      }

      Collections.sort(names);
      return new Requirement(highestFeature, names);
   }

   /**
    * Parses a Java version string into its feature release. Both legacy
    * ({@code 1.8.0_504}) and modern ({@code 21.0.13}) forms are accepted.
    */
   public static int featureVersion(String version) {
      if (version == null) {
         return -1;
      }

      String value = version.trim();
      if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
         value = value.substring(1, value.length() - 1);
      }

      if (value.startsWith("1.")) {
         value = value.substring(2);
      }

      int end = 0;
      while (end < value.length() && Character.isDigit(value.charAt(end))) {
         end++;
      }

      if (end == 0) {
         return -1;
      }

      try {
         return Integer.parseInt(value.substring(0, end));
      } catch (NumberFormatException exception) {
         return -1;
      }
   }

   static List<Installation> discover() {
      Set<Path> homes = new LinkedHashSet<>();
      addEnvironmentHome(homes, "BLAZE_JAVA_HOME");
      addEnvironmentHome(homes, "JAVA_HOME");
      addEnvironmentHome(homes, "JDK_HOME");
      addEnvironmentHome(homes, "JRE_HOME");

      String current = System.getProperty("java.home");
      if (current != null && !current.isEmpty()) {
         homes.add(Paths.get(current));
      }

      addPathHomes(homes);
      for (Path root : defaultRoots()) {
         collectHomes(root, homes);
      }

      List<Installation> installations = new ArrayList<>();
      Set<String> seen = new HashSet<>();
      for (Path home : homes) {
         Installation installation = readInstallation(home);
         if (installation == null) {
            continue;
         }

         String key = installation.home.toString().toLowerCase(Locale.ROOT);
         if (seen.add(key)) {
            installations.add(installation);
         }
      }

      Collections.sort(installations, new Comparator<Installation>() {
         @Override
         public int compare(Installation first, Installation second) {
            if (first.feature != second.feature) {
               return second.feature - first.feature;
            }

            return first.home.toString().compareTo(second.home.toString());
         }
      });
      return installations;
   }

   static Installation findBest(List<Installation> installations, int requiredFeature, Path currentHome) {
      Installation best = null;
      for (Installation installation : installations) {
         if (installation.feature < requiredFeature) {
            continue;
         }

         if (currentHome != null && samePath(installation.home, currentHome)) {
            continue;
         }

         if (best == null || installation.feature > best.feature) {
            best = installation;
         }
      }

      return best;
   }

   static void collectHomes(Path root, Set<Path> homes) {
      if (root == null || !Files.isDirectory(root)) {
         return;
      }

      if (isJavaHome(root)) {
         homes.add(root);
         return;
      }

      Path bundleHome = root.resolve("Contents").resolve("Home");
      if (isJavaHome(bundleHome)) {
         homes.add(bundleHome);
         return;
      }

      File[] children = root.toFile().listFiles();
      if (children == null) {
         return;
      }

      for (File child : children) {
         if (child == null || !child.isDirectory()) {
            continue;
         }

         Path path = child.toPath();
         if (isJavaHome(path)) {
            homes.add(path);
            continue;
         }

         Path macHome = path.resolve("Contents").resolve("Home");
         if (isJavaHome(macHome)) {
            homes.add(macHome);
         }
      }
   }

   static Installation readInstallation(Path home) {
      if (home == null) {
         return null;
      }

      Path executable = executable(home);
      if (executable == null || !Files.isRegularFile(executable)) {
         return null;
      }

      int feature = featureFromRelease(home);
      if (feature <= 0) {
         feature = probe(executable);
      }

      if (feature <= 0) {
         return null;
      }

      return new Installation(home.toAbsolutePath().normalize(), feature);
   }

   /**
    * Reads the class-file version of the plugin's {@code plugin.yml} main class.
    */
   private static int pluginMainClassVersion(File file) {
      JarFile jar = null;
      try {
         jar = new JarFile(file);
         JarEntry entry = jar.getJarEntry("plugin.yml");
         if (entry == null) {
            return 0;
         }

         PluginDescriptionFile description;
         InputStream stream = jar.getInputStream(entry);
         try {
            description = new PluginDescriptionFile(stream);
         } finally {
            stream.close();
         }

         return PluginClassVersion.scanMain(file, description.getMain());
      } catch (Exception ignored) {
         return 0;
      } finally {
         if (jar != null) {
            try {
               jar.close();
            } catch (IOException ignored) {
            }
         }
      }
   }

   private static Installation pinnedInstallation(int requiredFeature) {
      String value = System.getenv("BLAZE_JAVA_HOME");
      if (value == null || value.trim().isEmpty()) {
         return null;
      }

      Path home = Paths.get(value.trim());
      Installation installation = readInstallation(home);
      if (installation == null) {
         System.out.println("[Blaze] BLAZE_JAVA_HOME does not point at a usable Java runtime: " + value);
         return null;
      }

      if (installation.feature < requiredFeature) {
         System.out.println(
            "[Blaze] BLAZE_JAVA_HOME (" + value + ") is Java " + installation.feature + ", but Java "
               + requiredFeature + "+ is required; looking for another installation."
         );
         return null;
      }

      return installation;
   }

   private static boolean relaunchEnabled() {
      String setting = System.getProperty(RELAUNCH_PROPERTY);
      return setting == null || !setting.equalsIgnoreCase("false");
   }

   private static Path currentHome() {
      String home = System.getProperty("java.home");
      return home == null || home.isEmpty() ? null : Paths.get(home);
   }

   private static Path serverJar() {
      try {
         URL location = JavaRuntime.class.getProtectionDomain().getCodeSource().getLocation();
         if (location == null) {
            return null;
         }

         Path path = Paths.get(location.toURI());
         return Files.isRegularFile(path) ? path.toAbsolutePath() : null;
      } catch (Exception exception) {
         return null;
      }
   }

   private static void addEnvironmentHome(Set<Path> homes, String name) {
      String value = System.getenv(name);
      if (value != null && !value.trim().isEmpty()) {
         homes.add(Paths.get(value.trim()));
      }
   }

   private static void addPathHomes(Set<Path> homes) {
      String path = System.getenv("PATH");
      if (path == null) {
         return;
      }

      for (String entry : path.split(File.pathSeparator)) {
         String value = entry.trim();
         if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
         }

         if (value.isEmpty()) {
            continue;
         }

         try {
            Path bin = Paths.get(value);
            Path home = bin.getParent();
            if (home != null && isJavaHome(home)) {
               homes.add(home);
            }
         } catch (RuntimeException ignored) {
            // Invalid PATH entries are not our problem.
         }
      }
   }

   private static List<Path> defaultRoots() {
      List<Path> roots = new ArrayList<>();
      boolean windows = isWindows();
      String home = System.getProperty("user.home", ".");

      if (windows) {
         addRoot(roots, System.getenv("ProgramFiles"), "Java", "Eclipse Adoptium", "AdoptOpenJDK", "Amazon Corretto", "Microsoft", "Zulu", "BellSoft", "Semeru", "RedHat", "IBM", "SapMachine");
         addRoot(roots, System.getenv("ProgramFiles(x86)"), "Java", "Eclipse Adoptium", "Amazon Corretto", "Zulu", "BellSoft");
         addRoot(roots, System.getenv("ProgramW6432"), "Java", "Eclipse Adoptium", "Amazon Corretto", "Microsoft", "Zulu", "BellSoft", "Semeru");
         addRoot(roots, System.getenv("LOCALAPPDATA") == null ? null : new File(System.getenv("LOCALAPPDATA"), "Programs").getPath(), "Eclipse Adoptium", "Amazon Corretto", "Microsoft", "Zulu");
         addRoot(roots, new File(home, ".jdks").getPath());
         addRoot(roots, new File(home, ".sdkman/candidates/java").getPath());
         addRoot(roots, new File(home, "scoop/apps").getPath());
         addRoot(roots, "C:\\tools");
      } else {
         addRoot(roots, "/usr/lib/jvm");
         addRoot(roots, "/usr/java");
         addRoot(roots, "/usr/local/lib/jvm");
         addRoot(roots, "/opt");
         addRoot(roots, "/opt/java");
         addRoot(roots, "/opt/jdk");
         addRoot(roots, new File(home, ".sdkman/candidates/java").getPath());
         addRoot(roots, new File(home, ".jdks").getPath());
         addRoot(roots, "/Library/Java/JavaVirtualMachines");
         addRoot(roots, "/System/Library/Java/JavaVirtualMachines");
      }

      return roots;
   }

   private static void addRoot(List<Path> roots, String base, String... children) {
      if (base == null || base.isEmpty()) {
         return;
      }

      Path parent = Paths.get(base);
      if (children == null || children.length == 0) {
         roots.add(parent);
         return;
      }

      for (String child : children) {
         roots.add(parent.resolve(child));
      }
   }

   private static Path executable(Path home) {
      return home.resolve("bin").resolve(isWindows() ? "java.exe" : "java");
   }

   private static boolean isJavaHome(Path home) {
      Path executable = executable(home);
      return executable != null && Files.isRegularFile(executable);
   }

   private static int featureFromRelease(Path home) {
      Path release = home.resolve("release");
      if (!Files.isRegularFile(release)) {
         return -1;
      }

      try {
         List<String> lines = Files.readAllLines(release, StandardCharsets.UTF_8);
         for (String line : lines) {
            if (line.startsWith("JAVA_VERSION=")) {
               return featureVersion(line.substring("JAVA_VERSION=".length()));
            }
         }
      } catch (IOException ignored) {
      }

      return -1;
   }

   private static int probe(Path executable) {
      Process process = null;
      try {
         ProcessBuilder builder = new ProcessBuilder(executable.toString(), "-version");
         builder.redirectErrorStream(true);
         process = builder.start();
         if (!process.waitFor(5L, TimeUnit.SECONDS)) {
            process.destroyForcibly();
            return -1;
         }

         byte[] buffer = new byte[2048];
         int total = 0;
         InputStream stream = process.getInputStream();
         while (total < buffer.length) {
            int count = stream.read(buffer, total, buffer.length - total);
            if (count < 0) {
               break;
            }

            total += count;
         }

         String text = new String(buffer, 0, total, StandardCharsets.UTF_8);
         int start = text.indexOf('"');
         int end = start < 0 ? -1 : text.indexOf('"', start + 1);
         if (start >= 0 && end > start) {
            return featureVersion(text.substring(start + 1, end));
         }
      } catch (Exception ignored) {
      } finally {
         if (process != null) {
            process.destroy();
         }
      }

      return -1;
   }

   private static boolean samePath(Path first, Path second) {
      try {
         return Files.isSameFile(first, second);
      } catch (IOException exception) {
         return first.toAbsolutePath().normalize().equals(second.toAbsolutePath().normalize());
      }
   }

   private static boolean isWindows() {
      return System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win");
   }

   private static String join(List<String> values) {
      if (values.isEmpty()) {
         return "A plugin";
      }

      StringBuilder builder = new StringBuilder();
      for (int index = 0; index < values.size(); index++) {
         if (index > 0) {
            builder.append(index == values.size() - 1 ? " and " : ", ");
         }

         builder.append(values.get(index));
      }

      return builder.toString();
   }

   /**
    * A plugin directory requirement: the highest Java feature release and the
    * jars that contribute to it.
    */
   public static final class Requirement {
      private final int feature;
      private final List<String> plugins;

      private Requirement(int feature, List<String> plugins) {
         this.feature = feature;
         this.plugins = Collections.unmodifiableList(new ArrayList<>(plugins));
      }

      public int feature() {
         return this.feature;
      }

      public List<String> plugins() {
         return this.plugins;
      }
   }

   /**
    * A Java installation discovered on this machine.
    */
   public static final class Installation {
      private final Path home;
      private final int feature;

      private Installation(Path home, int feature) {
         this.home = home;
         this.feature = feature;
      }

      public Path home() {
         return this.home;
      }

      public int feature() {
         return this.feature;
      }

      public Path javaExecutable() {
         return executable(this.home);
      }
   }
}
