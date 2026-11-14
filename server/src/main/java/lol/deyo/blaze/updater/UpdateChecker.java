package lol.deyo.blaze.updater;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public final class UpdateChecker {
   private static final int CONNECT_TIMEOUT = 10_000;
   private static final int READ_TIMEOUT = 30_000;
   private static final long MINIMUM_JAR_SIZE = 1_024L;

   private final Path directory;
   private final String repository;
   private final String apiBaseUrl;
   private final String userAgent;
   private final int currentVersion;

   public UpdateChecker(Path directory, String repository, String apiBaseUrl, String userAgent, int currentVersion) {
      this.directory = directory;
      this.repository = repository;
      this.apiBaseUrl = apiBaseUrl;
      this.userAgent = userAgent;
      this.currentVersion = currentVersion;
   }

   public boolean isNewer(UpdateInfo update) {
      return update != null && update.getVersion() > this.currentVersion;
   }

   public UpdateInfo findLatest() throws IOException {
      JsonObject release = this.readJson(this.apiBaseUrl + "/repos/" + this.repository + "/releases/latest");
      if (release.has("draft") && release.get("draft").getAsBoolean()) {
         return null;
      }

      if (!release.has("tag_name")) {
         throw new IOException("Release response has no tag_name");
      }

      String tag = release.get("tag_name").getAsString();
      int version = Versions.parse(tag);
      if (version < 0) {
         return null;
      }

      JsonObject asset = this.findAsset(release, tag);
      if (asset == null) {
         return null;
      }

      String pageUrl = release.has("html_url") ? release.get("html_url").getAsString() : this.apiBaseUrl;
      return new UpdateInfo(tag, version, asset.get("name").getAsString(), asset.get("browser_download_url").getAsString(), pageUrl);
   }

   public Path download(UpdateInfo update) throws IOException {
      Files.createDirectories(this.directory);

      Path temporary = this.directory.resolve("blaze-" + update.getTag() + ".download");
      Path target = this.directory.resolve("blaze-" + update.getTag() + ".jar");
      Files.deleteIfExists(temporary);

      try {
         HttpURLConnection connection = this.open(update.getDownloadUrl());
         long total = 0L;

         try (InputStream input = connection.getInputStream();
              OutputStream output = Files.newOutputStream(temporary, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            byte[] buffer = new byte[8192];
            int read;

            while ((read = input.read(buffer)) != -1) {
               output.write(buffer, 0, read);
               total += read;
            }
         }

         if (total < MINIMUM_JAR_SIZE) {
            throw new IOException("Downloaded jar is too small (" + total + " bytes)");
         }

         this.validate(temporary.toFile(), update.getTag());
         Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
         return target;
      } catch (IOException exception) {
         Files.deleteIfExists(temporary);
         throw exception;
      }
   }

   public Path findPending() throws IOException {
      if (!Files.isDirectory(this.directory)) {
         return null;
      }

      Path newest = null;
      int newestVersion = this.currentVersion;

      try (DirectoryStream<Path> stream = Files.newDirectoryStream(this.directory, "blaze-v*.jar")) {
         for (Path path : stream) {
            int version = versionOf(path);
            if (version > newestVersion) {
               newestVersion = version;
               newest = path;
            }
         }
      }

      return newest;
   }

   public void cleanup(Path keep) throws IOException {
      if (!Files.isDirectory(this.directory)) {
         return;
      }

      try (DirectoryStream<Path> stream = Files.newDirectoryStream(this.directory, "blaze-v*.jar")) {
         for (Path path : stream) {
            if (keep == null || !path.toAbsolutePath().equals(keep.toAbsolutePath())) {
               Files.deleteIfExists(path);
            }
         }
      }
   }

   private JsonObject findAsset(JsonObject release, String tag) {
      if (!release.has("assets") || !release.get("assets").isJsonArray()) {
         return null;
      }

      JsonArray assets = release.getAsJsonArray("assets");
      JsonObject fallback = null;

      for (JsonElement element : assets) {
         if (!element.isJsonObject()) {
            continue;
         }

         JsonObject asset = element.getAsJsonObject();
         if (!asset.has("name") || !asset.has("browser_download_url")) {
            continue;
         }

         String name = asset.get("name").getAsString();
         if (!name.endsWith(".jar")) {
            continue;
         }

         if (name.equalsIgnoreCase("blaze-" + tag + ".jar")) {
            return asset;
         }

         if (fallback == null) {
            fallback = asset;
         }
      }

      return fallback;
   }

   private JsonObject readJson(String url) throws IOException {
      HttpURLConnection connection = this.open(url);

      try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
         JsonElement element = JsonParser.parseString(join(reader));
         if (!element.isJsonObject()) {
            throw new IOException("Unexpected GitHub API response");
         }

         return element.getAsJsonObject();
      }
   }

   private HttpURLConnection open(String url) throws IOException {
      HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();
      connection.setRequestMethod("GET");
      connection.setConnectTimeout(CONNECT_TIMEOUT);
      connection.setReadTimeout(READ_TIMEOUT);
      connection.setInstanceFollowRedirects(true);
      connection.setRequestProperty("User-Agent", this.userAgent);
      connection.setRequestProperty("Accept", "application/vnd.github+json");

      int status = connection.getResponseCode();
      if (status != HttpURLConnection.HTTP_OK) {
         connection.disconnect();
         throw new IOException("HTTP " + status + " for " + url);
      }

      return connection;
   }

   private void validate(File file, String tag) throws IOException {
      if (file.length() < MINIMUM_JAR_SIZE) {
         throw new IOException("Downloaded jar is too small");
      }

      try (JarFile jar = new JarFile(file)) {
         if (jar.getEntry("org/bukkit/craftbukkit/Main.class") == null) {
            throw new IOException("Downloaded jar is not a Blaze server build");
         }

         Manifest manifest = jar.getManifest();
         String version = manifest == null ? null : manifest.getMainAttributes().getValue("Implementation-Version");
         if (!tag.equals(version)) {
            throw new IOException("Downloaded jar reports version " + version + " instead of " + tag);
         }
      }
   }

   private static int versionOf(Path path) {
      String name = path.getFileName().toString();
      if (!name.startsWith("blaze-") || !name.endsWith(".jar")) {
         return -1;
      }

      return Versions.parse(name.substring("blaze-".length(), name.length() - ".jar".length()));
   }

   private static String join(BufferedReader reader) throws IOException {
      StringBuilder builder = new StringBuilder();
      String line;

      while ((line = reader.readLine()) != null) {
         builder.append(line);
      }

      return builder.toString();
   }
}
