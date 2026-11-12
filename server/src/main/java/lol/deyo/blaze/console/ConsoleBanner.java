package lol.deyo.blaze.console;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lol.deyo.blaze.utils.CC;
import net.minecrell.terminalconsole.TerminalConsoleAppender;

public final class ConsoleBanner {
   private static final String[] LINES = {
      "&f  ██████╗ ██╗      █████╗ ███████╗███████╗",
      "&f  ██╔══██╗██║     ██╔══██╗╚══███╔╝██╔════╝",
      "&7  ██████╔╝██║     ███████║  ███╔╝ █████╗",
      "&7  ██╔══██╗██║     ██╔══██║ ███╔╝  ██╔══╝",
      "&8  ██████╔╝███████╗██║  ██║███████╗███████╗",
      "&8  ╚═════╝ ╚══════╝╚═╝  ╚═╝╚══════╝╚══════╝",
      "",
      "&7      Spigot &e1.8.8 Beta v1 &7by &edeyo"
   };

   private ConsoleBanner() {
   }

   public static void print() {
      boolean ansi = TerminalConsoleAppender.isAnsiSupported();
      write(System.lineSeparator());
      for (String line : LINES) {
         String translated = CC.t(line);
         write((ansi ? ConsoleColors.minecraftToAnsi(translated) : ConsoleColors.stripMinecraft(translated)) + System.lineSeparator());
      }
      write(System.lineSeparator());
   }

   private static void write(String text) {
      try {
         System.out.write(text.getBytes(StandardCharsets.UTF_8));
         System.out.flush();
      } catch (IOException exception) {
         throw new RuntimeException(exception);
      }
   }
}
