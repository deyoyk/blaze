package org.bukkit.craftbukkit.v1_8_R3;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.junit.Test;

public class BundledSparkTest {
   @Test
   public void bundlesTheSparkPlugin() throws Exception {
      try (InputStream input = CraftServer.class.getResourceAsStream("/builtin/spark.jar")) {
         assertNotNull(input);
         String pluginDescription = null;
         try (ZipInputStream zip = new ZipInputStream(input)) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
               if (entry.getName().equals("plugin.yml")) {
                  byte[] data = new byte[4096];
                  int length = zip.read(data);
                  pluginDescription = new String(data, 0, length, StandardCharsets.UTF_8);
                  break;
               }
            }
         }

         assertNotNull(pluginDescription);
         assertTrue(pluginDescription.contains("name: spark"));
         assertTrue(pluginDescription.contains("me.lucko.spark.bukkit.BukkitSparkPlugin"));
      }
   }
}
