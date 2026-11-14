package lol.deyo.blaze.updater;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UpdateInstallerTest {
   @Test
   public void replacesServerJarAndKeepsBackup() throws Exception {
      Path root = Files.createTempDirectory("blaze-installer-test");

      try {
         Path directory = root.resolve("updater");
         Files.createDirectories(directory);

         Path serverJar = root.resolve("blaze.jar");
         Path pendingJar = directory.resolve("blaze-v3.jar");
         Path backupJar = directory.resolve("backup").resolve("blaze-unknown.jar");
         Files.write(serverJar, "old".getBytes(StandardCharsets.UTF_8));
         Files.write(pendingJar, "new".getBytes(StandardCharsets.UTF_8));

         UpdateInstaller installer = new UpdateInstaller(serverJar, pendingJar, directory, backupJar, "999999");
         installer.installOnExit();

         long deadline = System.currentTimeMillis() + 15_000L;
         while (System.currentTimeMillis() < deadline) {
            boolean replaced = !Files.exists(pendingJar) && read(serverJar).equals("new");
            if (replaced) {
               break;
            }

            Thread.sleep(100L);
         }

         assertEquals("new", read(serverJar));
         assertEquals("old", read(backupJar));
      } finally {
         Files.walk(root)
            .sorted(Comparator.reverseOrder())
            .forEach(path -> {
               try {
                  Files.deleteIfExists(path);
               } catch (Exception ignored) {
               }
            });
      }
   }

   @Test
   public void scriptIncludesRestartCommand() throws Exception {
      Path root = Files.createTempDirectory("blaze-installer-script");

      try {
         Path directory = root.resolve("updater");
         Files.createDirectories(directory);

         List<String> restart = Arrays.asList("/opt/java/bin/java", "-jar", "/srv/blaze.jar", "--nogui");
         UpdateInstaller installer = new UpdateInstaller(
            root.resolve("blaze.jar"), directory.resolve("blaze-v3.jar"), directory, null, "4242", restart
         );

         String name = System.getProperty("os.name", "").toLowerCase(java.util.Locale.ROOT).contains("win")
            ? "apply-update.bat" : "apply-update.sh";
         String script = installer.script(directory.resolve(name));

         assertTrue(script.contains("-jar"));
         assertTrue(script.contains("blaze.jar"));
         assertTrue(script.contains("--nogui"));

         if (name.endsWith(".bat")) {
            assertTrue(script.indexOf("start \"\"") < script.indexOf("del \"%~f0\""));
         }
      } finally {
         Files.walk(root)
            .sorted(Comparator.reverseOrder())
            .forEach(path -> {
               try {
                  Files.deleteIfExists(path);
               } catch (Exception ignored) {
               }
            });
      }
   }

   private static String read(Path path) throws Exception {
      return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
   }
}
