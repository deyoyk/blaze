package lol.deyo.blaze.updater;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class RestartCommandTest {
   @Test
   public void keepsQuotedArgumentsTogether() {
      List<String> tokens = RestartCommand.split("blaze.jar \"--world C:\\my world\" --nogui");
      assertEquals(3, tokens.size());
      assertEquals("blaze.jar", tokens.get(0));
      assertEquals("--world C:\\my world", tokens.get(1));
      assertEquals("--nogui", tokens.get(2));
   }

   @Test
   public void ignoresRepeatedWhitespace() {
      List<String> tokens = RestartCommand.split("  blaze.jar   --nogui  ");
      assertEquals(2, tokens.size());
      assertEquals("blaze.jar", tokens.get(0));
      assertEquals("--nogui", tokens.get(1));
   }

   @Test
   public void rebuildsJarInvocation() {
      Path jar = Paths.get("/srv/blaze.jar");
      List<String> command = RestartCommand.build(
         jar, "blaze.jar --nogui", "", Collections.singletonList("-Xmx2G"), "/opt/java/bin/java"
      );

      assertEquals(Arrays.asList("/opt/java/bin/java", "-Xmx2G", "-jar", jar.toString(), "--nogui"), command);
   }

   @Test
   public void rebuildsClasspathInvocation() {
      Path jar = Paths.get("/srv/blaze.jar");
      List<String> command = RestartCommand.build(
         jar, "org.bukkit.craftbukkit.Main --nogui", "/srv/blaze.jar", Collections.<String>emptyList(), "/opt/java/bin/java"
      );

      assertEquals(Arrays.asList("/opt/java/bin/java", "-cp", "/srv/blaze.jar", "org.bukkit.craftbukkit.Main", "--nogui"), command);
   }

   @Test
   public void refusesUnknownInvocation() {
      List<String> command = RestartCommand.build(
         Paths.get("blaze.jar"), "", "", Collections.<String>emptyList(), "/opt/java/bin/java"
      );

      assertNull(command);
   }

   @Test
   public void systemInvocationIsDetected() {
      List<String> command = RestartCommand.build(Paths.get("server.jar"));
      assertTrue(command == null || !command.isEmpty());
   }
}
