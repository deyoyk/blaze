package lol.deyo.blaze.updater;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class UpdateInstaller {
   private static final boolean WINDOWS = System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win");

   private final Path serverJar;
   private final Path pendingJar;
   private final Path directory;
   private final Path backupJar;
   private final String processId;
   private final List<String> restartCommand;

   public UpdateInstaller(Path serverJar, Path pendingJar, Path directory, Path backupJar, String processId) {
      this(serverJar, pendingJar, directory, backupJar, processId, null);
   }

   public UpdateInstaller(Path serverJar, Path pendingJar, Path directory, Path backupJar, String processId, List<String> restartCommand) {
      this.serverJar = serverJar;
      this.pendingJar = pendingJar;
      this.directory = directory;
      this.backupJar = backupJar;
      this.processId = processId;
      this.restartCommand = restartCommand;
   }

   public Path installOnExit() throws IOException {
      Files.createDirectories(this.directory);

      Path script = this.directory.resolve(WINDOWS ? "apply-update.bat" : "apply-update.sh");
      Files.write(script, this.script(script).getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

      List<String> command = new ArrayList<>();
      if (WINDOWS) {
         command.add("cmd.exe");
         command.add("/c");
      } else {
         command.add("sh");
      }
      command.add(script.getFileName().toString());

      ProcessBuilder builder = new ProcessBuilder(command);
      builder.directory(this.directory.toFile());
      builder.redirectErrorStream(true);
      builder.redirectOutput(ProcessBuilder.Redirect.appendTo(this.directory.resolve("apply.log").toFile()));
      builder.start();
      return script;
   }

   String script(Path script) {
      return WINDOWS ? this.windowsScript(script) : this.unixScript(script);
   }

   private String windowsScript(Path script) {
      StringBuilder builder = new StringBuilder();
      builder.append("@echo off\r\n");
      builder.append("setlocal\r\n");
      builder.append("set \"APPLIED=0\"\r\n");
      builder.append("cd /d \"").append(this.serverJar.getParent()).append("\"\r\n");
      builder.append("if not exist \"").append(this.pendingJar).append("\" goto done\r\n");
      if (this.backupJar != null) {
         builder.append("if exist \"").append(this.serverJar).append("\" if not exist \"")
            .append(this.backupJar.getParent()).append("\" mkdir \"").append(this.backupJar.getParent()).append("\" >nul 2>&1\r\n");
         builder.append("if exist \"").append(this.serverJar).append("\" copy /Y \"")
            .append(this.serverJar).append("\" \"").append(this.backupJar).append("\" >nul 2>&1\r\n");
      }
      builder.append("set /a TRIES=0\r\n");
      builder.append(":retry\r\n");
      builder.append("if not exist \"").append(this.pendingJar).append("\" goto done\r\n");
      builder.append("move /Y \"").append(this.pendingJar).append("\" \"").append(this.serverJar).append("\" >nul 2>&1\r\n");
      builder.append("if errorlevel 1 (\r\n");
      builder.append("  set /a TRIES+=1\r\n");
      builder.append("  if %TRIES% GEQ 120 goto done\r\n");
      builder.append("  timeout /t 1 /nobreak >nul\r\n");
      builder.append("  goto retry\r\n");
      builder.append(")\r\n");
      builder.append("set \"APPLIED=1\"\r\n");
      builder.append(":done\r\n");
      if (this.restartCommand != null && !this.restartCommand.isEmpty()) {
         builder.append("if \"%APPLIED%\"==\"1\" start \"\"");
         for (String argument : this.restartCommand) {
            builder.append(" \"").append(argument).append('"');
         }
         builder.append("\r\n");
      }
      builder.append("(goto) 2>nul & del \"%~f0\" >nul 2>&1\r\n");
      return builder.toString();
   }

   private String unixScript(Path script) {
      StringBuilder builder = new StringBuilder();
      builder.append("#!/bin/sh\n");
      builder.append("cd ").append(quote(this.serverJar.getParent().toString())).append(" || exit 0\n");
      if (this.processId != null && this.processId.matches("\\d+")) {
         builder.append("while kill -0 ").append(this.processId).append(" 2>/dev/null; do sleep 1; done\n");
      }
      if (this.backupJar != null) {
         builder.append("if [ -f ").append(quote(this.serverJar.toString())).append(" ]; then\n");
         builder.append("  mkdir -p ").append(quote(this.backupJar.getParent().toString())).append("\n");
         builder.append("  cp -f ").append(quote(this.serverJar.toString())).append(' ').append(quote(this.backupJar.toString())).append(" 2>/dev/null || true\n");
         builder.append("fi\n");
      }
      builder.append("if [ ! -f ").append(quote(this.pendingJar.toString())).append(" ]; then rm -f ").append(quote(script.toString())).append("; exit 0; fi\n");
      builder.append("while ! mv -f ").append(quote(this.pendingJar.toString())).append(' ').append(quote(this.serverJar.toString())).append(" 2>/dev/null; do sleep 1; done\n");
      builder.append("rm -f ").append(quote(script.toString())).append("\n");
      if (this.restartCommand != null && !this.restartCommand.isEmpty()) {
         builder.append("exec");
         for (String argument : this.restartCommand) {
            builder.append(' ').append(quote(argument));
         }
         builder.append("\n");
      }
      return builder.toString();
   }

   private static String quote(String value) {
      return "'" + value.replace("'", "'\\''") + "'";
   }
}
