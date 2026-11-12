package lol.deyo.blaze.console;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.logging.log4j.Level;
import org.junit.Test;

public class BlazeConsoleConverterTest {
   private static final long TIME = 0L;

   @Test
   public void colorsTimeLevelAndMinecraftCodes() {
      String line = BlazeConsoleConverter.format(Level.INFO, "MinecraftServer", "\u00A7cHello", true, TIME);
      assertTrue(line.contains("\u001B[36m"));
      assertTrue(line.contains("\u001B[32m"));
      assertTrue(line.contains("\u001B[0;91m"));
      assertFalse(line.contains("\u00A7"));
      assertTrue(ConsoleColors.stripAnsi(line).matches("\\[\\d{2}:\\d{2}:\\d{2} INFO\\]: Hello"));
   }

   @Test
   public void stripsFormattingWhenAnsiIsUnavailable() {
      String line = BlazeConsoleConverter.format(Level.WARN, "BedWars", "\u00A7eWarning", false, TIME);
      assertFalse(line.contains("\u001B["));
      assertFalse(line.contains("\u00A7"));
      assertTrue(line.contains("[BedWars] Warning"));
   }

   @Test
   public void colorsPluginTagsAndPrefixesPluginLoggers() {
      String tagLine = BlazeConsoleConverter.format(Level.INFO, "MinecraftServer", "[BedWars] Starting game", true, TIME);
      assertTrue(tagLine.contains("BedWars"));
      assertTrue(ConsoleColors.stripAnsi(tagLine).contains("[BedWars] Starting game"));

      String loggerLine = BlazeConsoleConverter.format(Level.INFO, "BedWars", "Starting game", true, TIME);
      assertTrue(ConsoleColors.stripAnsi(loggerLine).contains("[BedWars] Starting game"));

      String vanillaLine = BlazeConsoleConverter.format(Level.INFO, "MinecraftServer", "Starting server", true, TIME);
      assertFalse(ConsoleColors.stripAnsi(vanillaLine).contains("[MinecraftServer]"));
   }
}
