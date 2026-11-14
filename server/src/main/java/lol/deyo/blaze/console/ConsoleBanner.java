package lol.deyo.blaze.console;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lol.deyo.blaze.updater.Versions;
import lol.deyo.blaze.utils.CC;
import net.minecrell.terminalconsole.TerminalConsoleAppender;

public final class ConsoleBanner {
   private static final String VERSION_LINE = "&7      Spigot &e1.8.8 Beta <version> &7by &edeyo";
   private static final String[] LINES = {
      "&f  ██████╗ ██╗      █████╗ ███████╗███████╗",
      "&f  ██╔══██╗██║     ██╔══██╗╚══███╔╝██╔════╝",
      "&7  ██████╔╝██║     ███████║  ███╔╝ █████╗",
      "&7  ██╔══██╗██║     ██╔══██║ ███╔╝  ██╔══╝",
      "&8  ██████╔╝███████╗██║  ██║███████╗███████╗",
      "&8  ╚═════╝ ╚══════╝╚═╝  ╚═╝╚══════╝╚══════╝",
      ""
   };

   private ConsoleBanner() {
   }

   public static void print() {
      boolean ansi = TerminalConsoleAppender.isAnsiSupported();
      write(System.lineSeparator());
      for (String line : LINES) {
         writeLine(ansi, line);
      }
      writeLine(ansi, VERSION_LINE.replace("<version>", version()));
      write(System.lineSeparator());
   }

   private static void writeLine(boolean ansi, String line) {
      String translated = CC.t(line);
      write((ansi ? ConsoleColors.minecraftToAnsi(translated) : ConsoleColors.stripMinecraft(translated)) + System.lineSeparator());
   }

   private static String version() {
      String version = ConsoleBanner.class.getPackage().getImplementationVersion();
      return version == null || Versions.parse(version) < 0 ? "dev" : version;
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
