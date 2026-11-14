package lol.deyo.blaze.updater;

import java.lang.management.ManagementFactory;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class RestartCommand {
   private RestartCommand() {
   }

   public static List<String> build(Path serverJar) {
      return build(
         serverJar,
         System.getProperty("sun.java.command", ""),
         System.getProperty("java.class.path", ""),
         ManagementFactory.getRuntimeMXBean().getInputArguments(),
         javaExecutable()
      );
   }

   static List<String> build(Path serverJar, String sunCommand, String classpath, List<String> jvmArguments, String javaExecutable) {
      List<String> program = split(sunCommand);
      if (program.isEmpty()) {
         return null;
      }

      List<String> command = new ArrayList<>();
      command.add(javaExecutable);
      command.addAll(jvmArguments);

      String first = program.get(0).toLowerCase(Locale.ROOT);
      if (first.endsWith(".jar")) {
         command.add("-jar");
         command.add(serverJar.toString());
         command.addAll(program.subList(1, program.size()));
      } else {
         if (classpath.isEmpty()) {
            return null;
         }

         command.add("-cp");
         command.add(classpath);
         command.addAll(program);
      }

      return command;
   }

   static List<String> split(String value) {
      List<String> tokens = new ArrayList<>();
      StringBuilder token = new StringBuilder();
      boolean quoted = false;

      for (int i = 0; i < value.length(); i++) {
         char character = value.charAt(i);
         if (character == '"') {
            quoted = !quoted;
         } else if (Character.isWhitespace(character) && !quoted) {
            if (token.length() > 0) {
               tokens.add(token.toString());
               token.setLength(0);
            }
         } else {
            token.append(character);
         }
      }

      if (token.length() > 0) {
         tokens.add(token.toString());
      }

      return tokens;
   }

   private static String javaExecutable() {
      String executable = System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win") ? "java.exe" : "java";
      return Paths.get(System.getProperty("java.home"), "bin", executable).toString();
   }
}
