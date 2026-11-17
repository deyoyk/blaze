package org.bukkit.plugin.java;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Reads Java class-file versions from plugin jars.
 *
 * <p>The JVM refuses to load a class whose major version is newer than the
 * running JVM supports, which surfaces as an {@link UnsupportedClassVersionError}
 * deep inside the plugin class loader. Reading the versions up front lets Blaze
 * explain the problem and, when possible, restart itself on a newer runtime
 * before any plugin is touched.</p>
 */
public final class PluginClassVersion {
   /** Class-file major version emitted by javac 8 (Java 8). */
   public static final int JAVA_8 = 52;
   private static final int BASE_VERSION = 44;
   private static final String MULTI_RELEASE_PREFIX = "META-INF/versions/";

   private PluginClassVersion() {
   }

   /**
    * The class-file version supported by the currently running JVM.
    */
   public static int runtimeClassVersion() {
      String value = System.getProperty("java.class.version", "52.0");
      int dot = value.indexOf('.');
      try {
         return Integer.parseInt(dot < 0 ? value : value.substring(0, dot));
      } catch (NumberFormatException ignored) {
         return JAVA_8;
      }
   }

   /**
    * The Java feature release that emits the supplied class-file version.
    * For example 52 becomes 8, 55 becomes 11 and 65 becomes 21.
    */
   public static int javaFeature(int classVersion) {
      return classVersion - BASE_VERSION;
   }

   /**
    * The class-file version emitted by the supplied Java feature release.
    */
   public static int classVersion(int javaFeature) {
      return javaFeature + BASE_VERSION;
   }

   /**
    * A human readable Java requirement, such as {@code Java 11 (class file version 55)}.
    */
   public static String describe(int classVersion) {
      return "Java " + javaFeature(classVersion) + " (class file version " + classVersion + ")";
   }

   /**
    * Finds the highest class-file version contained in a plugin jar.
    *
    * <p>Multi-release entries under {@code META-INF/versions/} are ignored: older
    * JVMs never load them, and the base classes are what every runtime must be
    * able to read. {@code module-info.class} is ignored for the same reason.</p>
    *
    * @return the highest major version, or {@code 0} when the file is not a jar
    *         or contains no loadable classes
    */
   public static int scan(File file) throws IOException {
      if (file == null || !file.isFile()) {
         return 0;
      }

      JarFile jar = new JarFile(file);
      try {
         int highest = 0;
         Enumeration<JarEntry> entries = jar.entries();
         while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            if (entry.isDirectory() || !name.endsWith(".class")) {
               continue;
            }

            if (name.startsWith(MULTI_RELEASE_PREFIX) || name.endsWith("module-info.class")) {
               continue;
            }

            int version = readMajor(jar, entry);
            if (version > highest) {
               highest = version;
            }
         }

         return highest;
      } finally {
         jar.close();
      }
   }

   /**
    * Class-file version of a plugin's declared main class. This is the class the
    * JVM must load before a plugin can start, so it is the only reliable signal
    * that the whole plugin cannot run on the current JVM. Optional helper
    * classes may legitimately target newer releases.
    *
    * @return the major version, or {@code 0} when the class is not present
    */
   public static int scanMain(File file, String mainClass) throws IOException {
      if (file == null || !file.isFile() || mainClass == null || mainClass.isEmpty()) {
         return 0;
      }

      String entryName = mainClass.replace('.', '/') + ".class";
      JarFile jar = new JarFile(file);
      try {
         JarEntry entry = jar.getJarEntry(entryName);
         return entry == null ? 0 : readMajor(jar, entry);
      } finally {
         jar.close();
      }
   }

   private static int readMajor(JarFile jar, JarEntry entry) throws IOException {
      InputStream stream = jar.getInputStream(entry);
      try {
         byte[] header = new byte[8];
         int read = 0;
         while (read < header.length) {
            int count = stream.read(header, read, header.length - read);
            if (count < 0) {
               break;
            }

            read += count;
         }

         if (read < header.length
            || (header[0] & 0xFF) != 0xCA
            || (header[1] & 0xFF) != 0xFE
            || (header[2] & 0xFF) != 0xBA
            || (header[3] & 0xFF) != 0xBE) {
            return 0;
         }

         return ((header[6] & 0xFF) << 8) | (header[7] & 0xFF);
      } finally {
         stream.close();
      }
   }
}
