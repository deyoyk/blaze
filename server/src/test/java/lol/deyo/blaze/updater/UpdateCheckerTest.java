package lol.deyo.blaze.updater;

import com.sun.net.httpserver.HttpServer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class UpdateCheckerTest {
   private HttpServer server;
   private String baseUrl;
   private Path directory;

   @Before
   public void setUp() throws IOException {
      this.server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
      this.server.start();
      this.baseUrl = "http://127.0.0.1:" + this.server.getAddress().getPort();
      this.directory = Files.createTempDirectory("blaze-updater-test");
   }

   @After
   public void tearDown() throws IOException {
      this.server.stop(0);
      Files.walk(this.directory)
         .sorted(java.util.Comparator.reverseOrder())
         .forEach(path -> {
            try {
               Files.deleteIfExists(path);
            } catch (IOException ignored) {
            }
         });
   }

   @Test
   public void findsReleaseAndDownloadsVerifiedJar() throws Exception {
      byte[] jar = jar("v3");

      this.server.createContext("/repos/deyoyk/blaze/releases/latest", exchange -> {
         String json = "{"
            + "\"tag_name\":\"v3\","
            + "\"html_url\":\"https://example.test/releases/v3\","
            + "\"draft\":false,"
            + "\"assets\":[{\"name\":\"blaze-v3.jar\",\"browser_download_url\":\"" + this.baseUrl + "/assets/blaze-v3.jar\"}]"
            + "}";
         respond(exchange, json.getBytes(StandardCharsets.UTF_8));
      });
      this.server.createContext("/assets/blaze-v3.jar", exchange -> respond(exchange, jar));

      UpdateChecker checker = new UpdateChecker(this.directory, "deyoyk/blaze", this.baseUrl, "blaze-test", 2);
      UpdateInfo update = checker.findLatest();

      assertNotNull(update);
      assertEquals("v3", update.getTag());
      assertEquals(3, update.getVersion());
      assertTrue(checker.isNewer(update));

      Path downloaded = checker.download(update);
      assertEquals("blaze-v3.jar", downloaded.getFileName().toString());
      assertTrue(Files.size(downloaded) >= 1024L);
      assertEquals(downloaded, checker.findPending());
   }

   @Test
   public void doesNotTreatSameVersionAsNewer() throws Exception {
      this.server.createContext("/repos/deyoyk/blaze/releases/latest", exchange -> {
         String json = "{\"tag_name\":\"v2\",\"assets\":[{\"name\":\"blaze-v2.jar\",\"browser_download_url\":\"" + this.baseUrl + "/assets/blaze-v2.jar\"}]}";
         respond(exchange, json.getBytes(StandardCharsets.UTF_8));
      });

      UpdateChecker checker = new UpdateChecker(this.directory, "deyoyk/blaze", this.baseUrl, "blaze-test", 2);
      UpdateInfo update = checker.findLatest();

      assertEquals(2, update.getVersion());
      assertFalse(checker.isNewer(update));
   }

   @Test
   public void rejectsJarWithWrongVersion() throws Exception {
      byte[] jar = jar("v9");
      this.server.createContext("/assets/blaze-v3.jar", exchange -> respond(exchange, jar));

      UpdateChecker checker = new UpdateChecker(this.directory, "deyoyk/blaze", this.baseUrl, "blaze-test", 2);
      UpdateInfo update = new UpdateInfo("v3", 3, "blaze-v3.jar", this.baseUrl + "/assets/blaze-v3.jar", this.baseUrl);

      try {
         checker.download(update);
      } catch (IOException expected) {
         assertTrue(expected.getMessage().contains("v9"));
         assertFalse(Files.exists(this.directory.resolve("blaze-v3.jar")));
         return;
      }

      throw new AssertionError("Expected the download to be rejected");
   }

   private static byte[] jar(String version) throws IOException {
      ByteArrayOutputStream bytes = new ByteArrayOutputStream();
      Manifest manifest = new Manifest();
      manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
      manifest.getMainAttributes().putValue("Implementation-Version", version);

      byte[] payload = new byte[8192];
      new java.util.Random(7L).nextBytes(payload);

      try (JarOutputStream output = new JarOutputStream(bytes)) {
         output.putNextEntry(new JarEntry("META-INF/MANIFEST.MF"));
         manifest.write(output);
         output.closeEntry();

         output.putNextEntry(new JarEntry("org/bukkit/craftbukkit/Main.class"));
         output.write(payload);
         output.closeEntry();
      }

      return bytes.toByteArray();
   }

   private static void respond(com.sun.net.httpserver.HttpExchange exchange, byte[] body) throws IOException {
      exchange.getResponseHeaders().add("Content-Type", "application/json");
      exchange.sendResponseHeaders(200, body.length);
      try (OutputStream output = exchange.getResponseBody()) {
         output.write(body);
      }
   }
}
