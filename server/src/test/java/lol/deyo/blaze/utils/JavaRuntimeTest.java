package lol.deyo.blaze.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class JavaRuntimeTest {
   @Test
   public void parsesLegacyAndModernVersions() {
      assertEquals(8, JavaRuntime.featureVersion("1.8.0_504"));
      assertEquals(8, JavaRuntime.featureVersion("1.8"));
      assertEquals(11, JavaRuntime.featureVersion("11.0.32.1"));
      assertEquals(21, JavaRuntime.featureVersion("21.0.13"));
      assertEquals(21, JavaRuntime.featureVersion("\"21.0.13\""));
      assertEquals(-1, JavaRuntime.featureVersion(null));
      assertEquals(-1, JavaRuntime.featureVersion("unknown"));
   }

   @Test
   public void findsTheHighestMainClassRequirementInAPluginDirectory() throws Exception {
      Path directory = Files.createTempDirectory("blaze-plugins");
      writeClassJar(directory.resolve("Legacy.jar"), 52, 52);
      writeClassJar(directory.resolve("Modern.jar"), 61, 65);
      writeClassJar(directory.resolve("Optional.jar"), 52, 65);
      Files.write(directory.resolve("notes.txt"), "ignored".getBytes(StandardCharsets.UTF_8));

      JavaRuntime.Requirement requirement = JavaRuntime.requirements(directory.toFile());
      assertEquals(17, requirement.feature());
      assertEquals(Collections.singletonList("Modern.jar"), requirement.plugins());
   }

   @Test
   public void missingPluginDirectoryHasNoRequirement() {
      JavaRuntime.Requirement requirement = JavaRuntime.requirements(new File("missing-plugins-directory"));
      assertEquals(0, requirement.feature());
      assertTrue(requirement.plugins().isEmpty());
   }

   @Test
   public void discoversJavaHomesBelowARoot() throws Exception {
      Path root = Files.createTempDirectory("blaze-jdks");
      Path java17 = fakeJdk(root.resolve("jdk-17.0.9"), "17.0.9");
      Path java21 = fakeJdk(root.resolve("jdk-21.0.13"), "21.0.13");

      Set<Path> homes = new LinkedHashSet<>();
      JavaRuntime.collectHomes(root, homes);
      assertEquals(2, homes.size());
      assertTrue(homes.contains(java17));
      assertTrue(homes.contains(java21));

      JavaRuntime.Installation first = JavaRuntime.readInstallation(java17);
      JavaRuntime.Installation second = JavaRuntime.readInstallation(java21);
      assertNotNull(first);
      assertNotNull(second);
      assertEquals(17, first.feature());
      assertEquals(21, second.feature());
   }

   @Test
   public void discoversMacStyleBundles() throws Exception {
      Path root = Files.createTempDirectory("blaze-bundles");
      Path home = fakeJdk(root.resolve("jdk-21.jdk").resolve("Contents").resolve("Home"), "21.0.1");

      Set<Path> homes = new LinkedHashSet<>();
      JavaRuntime.collectHomes(root, homes);
      assertEquals(1, homes.size());
      assertTrue(homes.contains(home));
   }

   @Test
   public void picksTheNewestUsableInstallation() throws Exception {
      Path root = Files.createTempDirectory("blaze-choice");
      Path java11 = fakeJdk(root.resolve("jdk-11"), "11.0.2");
      Path java17 = fakeJdk(root.resolve("jdk-17"), "17.0.1");
      Path java21 = fakeJdk(root.resolve("jdk-21"), "21.0.1");

      List<JavaRuntime.Installation> installations = new ArrayList<>();
      installations.add(JavaRuntime.readInstallation(java11));
      installations.add(JavaRuntime.readInstallation(java17));
      installations.add(JavaRuntime.readInstallation(java21));

      assertEquals(21, JavaRuntime.findBest(installations, 11, null).feature());
      assertEquals(17, JavaRuntime.findBest(installations, 8, java21).feature());
      assertNull(JavaRuntime.findBest(installations, 25, null));
   }

   @Test
   public void readsTheRuntimeExecutableFromTheReleaseFile() throws Exception {
      Path home = fakeJdk(Files.createTempDirectory("blaze-release").resolve("jdk"), "1.8.0_504");
      JavaRuntime.Installation installation = JavaRuntime.readInstallation(home);

      assertNotNull(installation);
      assertEquals(8, installation.feature());
      assertEquals(home.resolve("bin").resolve(executableName()), installation.javaExecutable());
   }

   private static Path fakeJdk(Path home, String version) throws Exception {
      Files.createDirectories(home.resolve("bin"));
      Files.write(home.resolve("bin").resolve(executableName()), new byte[0]);
      Files.write(
         home.resolve("release"),
         ("JAVA_VERSION=\"" + version + "\"\n").getBytes(StandardCharsets.UTF_8)
      );
      return home;
   }

   private static String executableName() {
      return System.getProperty("os.name", "").toLowerCase().contains("win") ? "java.exe" : "java";
   }

   private static void writeClassJar(Path path, int mainMajor, int helperMajor) throws Exception {
      JarOutputStream output = new JarOutputStream(new FileOutputStream(path.toFile()));
      try {
         output.putNextEntry(new JarEntry("plugin.yml"));
         output.write("name: Test\nversion: 1.0\nmain: com.example.Main\n".getBytes(StandardCharsets.UTF_8));
         output.closeEntry();

         output.putNextEntry(new JarEntry("com/example/Main.class"));
         output.write(classHeader(mainMajor));
         output.closeEntry();

         output.putNextEntry(new JarEntry("com/example/Helper.class"));
         output.write(classHeader(helperMajor));
         output.closeEntry();
      } finally {
         output.close();
      }
   }

   private static byte[] classHeader(int major) {
      return new byte[]{
         (byte)0xCA, (byte)0xFE, (byte)0xBA, (byte)0xBE,
         0, 0,
         (byte)((major >> 8) & 0xFF), (byte)(major & 0xFF),
         0, 0, 0, 0
      };
   }
}
