package lol.deyo.blaze.console;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import net.minecrell.terminalconsole.TerminalConsoleAppender;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.core.pattern.PatternConverter;

@Plugin(name = "BlazeConsole", category = PatternConverter.CATEGORY)
@ConverterKeys({"blazeConsole"})
public final class BlazeConsoleConverter extends LogEventPatternConverter {
   private static final String RESET = "\u001B[0m";
   private static final String GRAY = "\u001B[90m";
   private static final String CYAN = "\u001B[36m";
   private static final String[] TAG_COLORS = {
      "\u001B[36m",
      "\u001B[35m",
      "\u001B[33m",
      "\u001B[32m",
      "\u001B[34m",
      "\u001B[96m",
      "\u001B[95m",
      "\u001B[93m"
   };
   private static final String[] INTERNAL_LOGGERS = {
      "net.minecraft",
      "Minecraft",
      "com.mojang",
      "org.bukkit",
      "org.spigotmc",
      "net.minecrell",
      "org.apache",
      "io.netty",
      "lol.deyo",
      "jline",
      "org.fusesource"
   };
   private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.systemDefault());

   private BlazeConsoleConverter() {
      super("blazeConsole", "blazeConsole");
   }

   public static BlazeConsoleConverter newInstance(String[] options) {
      return new BlazeConsoleConverter();
   }

   @Override
   public void format(LogEvent event, StringBuilder toAppendTo) {
      String message = event.getMessage() == null ? "" : event.getMessage().getFormattedMessage();
      toAppendTo.append(
         format(event.getLevel(), event.getLoggerName(), message, TerminalConsoleAppender.isAnsiSupported(), event.getTimeMillis())
      );
   }

   static String format(Level level, String loggerName, String message, boolean ansi, long timeMillis) {
      String time = TIME_FORMAT.format(Instant.ofEpochMilli(timeMillis));
      String levelName = level == null ? "INFO" : level.name();
      StringBuilder line = new StringBuilder(message.length() + 64);

      if (!ansi) {
         line.append('[').append(time).append(' ').append(levelName).append("]: ");
         appendPlainMessage(line, loggerName, message);
         return line.toString();
      }

      line.append(GRAY).append('[').append(CYAN).append(time).append(GRAY).append(' ')
         .append(levelColor(level)).append(levelName).append(GRAY).append("]: ").append(RESET);
      appendColorMessage(line, loggerName, message);
      return line.append(RESET).toString();
   }

   private static void appendPlainMessage(StringBuilder line, String loggerName, String message) {
      int tagStart = tagStart(message);
      if (tagStart >= 0) {
         line.append(ConsoleColors.stripMinecraft(message));
      } else if (isPluginLogger(loggerName)) {
         line.append('[').append(loggerName).append("] ").append(ConsoleColors.stripMinecraft(message));
      } else {
         line.append(ConsoleColors.stripMinecraft(message));
      }
   }

   private static void appendColorMessage(StringBuilder line, String loggerName, String message) {
      int tagStart = tagStart(message);
      if (tagStart >= 0) {
         int tagEnd = message.indexOf(']', tagStart);
         line.append(ConsoleColors.minecraftToAnsi(message.substring(0, tagStart)));
         String tag = message.substring(tagStart + 1, tagEnd);
         line.append(GRAY).append('[').append(tagColor(tag)).append(tag).append(RESET).append(GRAY).append(']').append(RESET);
         line.append(ConsoleColors.minecraftToAnsi(message.substring(tagEnd + 1)));
      } else if (isPluginLogger(loggerName)) {
         line.append(GRAY).append('[').append(tagColor(loggerName)).append(loggerName).append(RESET).append(GRAY).append(']').append(RESET).append(' ');
         line.append(ConsoleColors.minecraftToAnsi(message));
      } else {
         line.append(ConsoleColors.minecraftToAnsi(message));
      }
   }

   private static int tagStart(String message) {
      int index = 0;
      while (index + 1 < message.length() && message.charAt(index) == '\u00A7') {
         index += 2;
      }

      return index < message.length() && message.charAt(index) == '[' ? index : -1;
   }

   private static boolean isPluginLogger(String loggerName) {
      if (loggerName == null || loggerName.isEmpty()) {
         return false;
      }

      for (String internal : INTERNAL_LOGGERS) {
         if (loggerName.startsWith(internal)) {
            return false;
         }
      }

      return true;
   }

   private static String tagColor(String tag) {
      return TAG_COLORS[Math.floorMod(tag.toLowerCase().hashCode(), TAG_COLORS.length)];
   }

   private static String levelColor(Level level) {
      if (level == null) {
         return GRAY;
      }

      switch (level.name()) {
         case "FATAL":
            return "\u001B[1;91m";
         case "ERROR":
            return "\u001B[31m";
         case "WARN":
            return "\u001B[33m";
         case "INFO":
            return "\u001B[32m";
         case "DEBUG":
            return "\u001B[36m";
         case "TRACE":
            return "\u001B[90m";
         default:
            return GRAY;
      }
   }

}
