package lol.deyo.blaze.updater;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import lol.deyo.blaze.configs.UpdaterConfig;
import lol.deyo.blaze.configs.extra.ConfigFolder;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.apache.logging.log4j.Logger;

public final class AutoUpdater {
   private static final Logger LOGGER = MinecraftServer.LOGGER;
   private static final String REPOSITORY = "deyoyk/blaze";
   private static final String API_BASE = "https://api.github.com";
   private static final String USER_AGENT = "Blaze-AutoUpdater";
   private static final String ATTEMPT_FILE = "attempted.txt";

   private final Path directory;
   private final String currentVersion;
   private final int currentNumber;
   private final UpdateChecker checker;
   private final AtomicBoolean applied = new AtomicBoolean();
   private volatile Path pendingFile;

   public AutoUpdater() {
      this.directory = new File(ConfigFolder.BLAZE_DIRECTORY, "updater").toPath();
      this.currentVersion = currentVersion();
      this.currentNumber = Math.max(Versions.parse(this.currentVersion), 0);
      this.checker = new UpdateChecker(this.directory, REPOSITORY, API_BASE, USER_AGENT, this.currentNumber);
   }

   public void start() {
      if (!UpdaterConfig.enabled.getValue()) {
         LOGGER.info("[Updater] Auto updates are disabled in blaze/updater.yml");
         return;
      }

      Runtime.getRuntime().addShutdownHook(new Thread(this::applyPending, "Blaze Updater"));

      Thread thread = new Thread(this::checkForUpdates, "Blaze Updater Check");
      thread.setDaemon(true);
      thread.start();
   }

   private void checkForUpdates() {
      LOGGER.info("[Updater] Checking GitHub for a newer release (current: {})", this.currentVersion);

      try {
         Path existing = this.checker.findPending();
         if (existing != null) {
            this.pendingFile = existing;
            LOGGER.info("[Updater] Found previously downloaded update {}", existing.getFileName());
         }

         UpdateInfo latest = this.checker.findLatest();
         if (latest == null) {
            LOGGER.warn("[Updater] No release jar found for {}", REPOSITORY);
            return;
         }

         if (!this.checker.isNewer(latest)) {
            if ("dev".equals(this.currentVersion)) {
               LOGGER.warn("[Updater] This is a development build (no release version); release {} is ignored. " + "Build with a version tag or delete the local jar to update.", latest.getTag());
            } else {
               LOGGER.info("[Updater] Running the latest release ({})", latest.getTag());
            }
            this.pendingFile = null;
            this.checker.cleanup(null);
            this.clearAttempt();
            return;
         }

         if (existing != null && latest.getTag().equals(tagOf(existing))) {
            LOGGER.info("[Updater] Release {} is already downloaded", latest.getTag());
         } else {
            LOGGER.info("[Updater] Release {} is available, downloading it...", latest.getTag());
            Path download = this.checker.download(latest);
            this.checker.cleanup(download);
            this.pendingFile = download;
         }

         if (!UpdaterConfig.restart.getValue() || this.serverJar() == null) {
            LOGGER.info("[Updater] {} will be installed when the server stops; restart to apply it.", latest.getTag());
            return;
         }

         if (this.wasAttempted(latest.getTag())) {
            LOGGER.warn(
               "[Updater] {} was already downloaded but the running build is still {}; not restarting again. " +
                  "Install it manually or delete blaze/updater/{}.",
               latest.getTag(), this.currentVersion, ATTEMPT_FILE
            );
            return;
         }

         this.markAttempted(latest.getTag());
         this.requestRestart(latest.getTag());
      } catch (Throwable throwable) {
         LOGGER.warn("[Updater] Update check failed: {}", throwable.getMessage());
         if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("[Updater] Update check failure", throwable);
         }
      }
   }

   private void requestRestart(String tag) {
      LOGGER.warn("[Updater] Restarting the server to apply {}. Players will be disconnected.", tag);

      long deadline = System.currentTimeMillis() + 120_000L;
      while (System.currentTimeMillis() < deadline) {
         MinecraftServer server = MinecraftServer.getServer();
         if (server != null) {
            server.safeShutdown();
            return;
         }

         try {
            Thread.sleep(500L);
         } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return;
         }
      }

      LOGGER.warn("[Updater] The server did not start in time; {} will be installed when it stops.", tag);
   }

   private void applyPending() {
      Path file = this.pendingFile;
      if (file == null || !Files.isRegularFile(file) || !this.applied.compareAndSet(false, true)) {
         return;
      }

      String tag = tagOf(file);
      if (Versions.parse(tag) <= this.currentNumber) {
         this.delete(file);
         return;
      }

      try {
         Path serverJar = this.serverJar();
         if (serverJar == null) {
            LOGGER.warn("[Updater] Could not locate the running server jar; {} was not installed", file.getFileName());
            return;
         }

         List<String> restart = UpdaterConfig.restart.getValue() ? RestartCommand.build(serverJar) : null;
         UpdateInstaller installer = new UpdateInstaller(serverJar, file, this.directory, this.backupJar(), Long.toString(currentProcessId()), restart);
         installer.installOnExit();

         if (restart != null) {
            LOGGER.info("[Updater] Installing {} and restarting the server.", tag);
         } else {
            LOGGER.info("[Updater] Installing {} on exit; restart the server to apply it.", tag);
         }
      } catch (Throwable throwable) {
         LOGGER.warn("[Updater] Could not schedule the update installation: {}", throwable.getMessage());
      }
   }

   private Path backupJar() {
      if (!UpdaterConfig.keepBackup.getValue()) {
         return null;
      }

      String name = this.currentVersion.replaceAll("[^A-Za-z0-9._-]", "-");
      return this.directory.resolve("backup").resolve("blaze-" + name + ".jar");
   }

   private Path serverJar() {
      try {
         URL location = AutoUpdater.class.getProtectionDomain().getCodeSource().getLocation();
         if (location == null) {
            return null;
         }

         Path path = Paths.get(location.toURI());
         return Files.isRegularFile(path) ? path.toAbsolutePath() : null;
      } catch (Exception exception) {
         return null;
      }
   }

   private Path attemptedFile() {
      return this.directory.resolve(ATTEMPT_FILE);
   }

   private boolean wasAttempted(String tag) {
      try {
         Path file = this.attemptedFile();
         return Files.isRegularFile(file) && tag.equals(new String(Files.readAllBytes(file), StandardCharsets.UTF_8).trim());
      } catch (Exception exception) {
         return false;
      }
   }

   private void markAttempted(String tag) {
      try {
         Files.createDirectories(this.directory);
         Files.write(this.attemptedFile(), tag.getBytes(StandardCharsets.UTF_8));
      } catch (Exception exception) {
         LOGGER.debug("[Updater] Could not write {}", ATTEMPT_FILE);
      }
   }

   private void clearAttempt() {
      try {
         Files.deleteIfExists(this.attemptedFile());
      } catch (Exception exception) {
         LOGGER.debug("[Updater] Could not delete {}", ATTEMPT_FILE);
      }
   }

   private void delete(Path file) {
      try {
         Files.deleteIfExists(file);
      } catch (Exception exception) {
         LOGGER.debug("[Updater] Could not delete {}", file);
      }
   }

   private static String tagOf(Path file) {
      String name = file.getFileName().toString();
      if (!name.startsWith("blaze-") || !name.endsWith(".jar")) {
         return name;
      }

      return name.substring("blaze-".length(), name.length() - ".jar".length());
   }

   private static String currentVersion() {
      String version = AutoUpdater.class.getPackage().getImplementationVersion();
      if (version == null || Versions.parse(version) < 0) {
         return "dev";
      }

      return version;
   }

   private static long currentProcessId() {
      try {
         return Long.parseLong(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
      } catch (Exception exception) {
         return -1L;
      }
   }
}
