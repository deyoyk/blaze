package org.bukkit.plugin.java;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PluginClassVersionTest {
   private static final byte[] MAGIC = new byte[]{(byte)0xCA, (byte)0xFE, (byte)0xBA, (byte)0xBE};

   @Test
   public void readsTheHighestBaseClassVersion() throws Exception {
      File jar = jar(
         "plugin.jar",
         entry("com/example/Main.class", classFile(55)),
         entry("com/example/Helper.class", classFile(52)),
         entry("META-INF/versions/17/com/example/Main.class", classFile(61)),
         entry("module-info.class", classFile(53)),
         entry("plugin.yml", "name: Example\n".getBytes("UTF-8"))
      );

      assertEquals(55, PluginClassVersion.scan(jar));
   }

   @Test
   public void readsTheMainClassVersionOnly() throws Exception {
      File jar = jar(
         "plugin.jar",
         entry("plugin.yml", "main: com.example.Main\n".getBytes("UTF-8")),
         entry("com/example/Main.class", classFile(52)),
         entry("com/example/Java21Helper.class", classFile(65))
      );

      assertEquals(52, PluginClassVersion.scanMain(jar, "com.example.Main"));
      assertEquals(65, PluginClassVersion.scan(jar));
      assertEquals(0, PluginClassVersion.scanMain(jar, "com.example.Missing"));
   }

   @Test
   public void ignoresEntriesWithoutAClassMagic() throws Exception {
      File jar = jar("broken.jar", entry("com/example/Broken.class", "not a class".getBytes("UTF-8")));

      assertEquals(0, PluginClassVersion.scan(jar));
   }

   @Test
   public void ignoresNonClassEntries() throws Exception {
      File jar = jar("resources.jar", entry("config.yml", "enabled: true\n".getBytes("UTF-8")));

      assertEquals(0, PluginClassVersion.scan(jar));
   }

   @Test
   public void mapsClassVersionsToJavaReleases() {
      assertEquals(8, PluginClassVersion.javaFeature(PluginClassVersion.JAVA_8));
      assertEquals(11, PluginClassVersion.javaFeature(55));
      assertEquals(21, PluginClassVersion.javaFeature(65));
      assertEquals(52, PluginClassVersion.classVersion(8));
      assertEquals(65, PluginClassVersion.classVersion(21));
      assertEquals("Java 11 (class file version 55)", PluginClassVersion.describe(55));
   }

   @Test
   public void missingFileHasNoRequirement() throws Exception {
      assertEquals(0, PluginClassVersion.scan(new File("does-not-exist.jar")));
      assertEquals(0, PluginClassVersion.scan(null));
   }

   private static byte[] classFile(int major) {
      return new byte[]{
         MAGIC[0], MAGIC[1], MAGIC[2], MAGIC[3],
         0, 0,
         (byte)((major >> 8) & 0xFF), (byte)(major & 0xFF),
         0, 0, 0, 0
      };
   }

   private static Entry entry(String name, byte[] data) {
      return new Entry(name, data);
   }

   private static File jar(String name, Entry... entries) throws IOException {
      File file = File.createTempFile(name, ".jar");
      file.deleteOnExit();
      JarOutputStream output = new JarOutputStream(new FileOutputStream(file));
      try {
         for (Entry entry : entries) {
            output.putNextEntry(new JarEntry(entry.name));
            output.write(entry.data);
            output.closeEntry();
         }
      } finally {
         output.close();
      }
      return file;
   }

   private static final class Entry {
      private final String name;
      private final byte[] data;

      private Entry(String name, byte[] data) {
         this.name = name;
         this.data = data;
      }
   }
}
