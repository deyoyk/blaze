package lol.deyo.blaze.console;

public final class ConsoleColors {
   private static final String ESC = "\u001B[";
   private static final String[] COLORS = {
      ESC + "0;30m",
      ESC + "0;34m",
      ESC + "0;32m",
      ESC + "0;36m",
      ESC + "0;31m",
      ESC + "0;35m",
      ESC + "0;33m",
      ESC + "0;37m",
      ESC + "0;90m",
      ESC + "0;94m",
      ESC + "0;92m",
      ESC + "0;96m",
      ESC + "0;91m",
      ESC + "0;95m",
      ESC + "0;93m",
      ESC + "0;97m"
   };
   private static final String[] STYLES = {
      ESC + "5m",
      ESC + "1m",
      ESC + "9m",
      ESC + "4m",
      ESC + "3m",
      ESC + "0m"
   };

   private ConsoleColors() {
   }

   public static String minecraftToAnsi(String text) {
      if (text == null || text.indexOf('\u00A7') < 0) {
         return text;
      }

      StringBuilder result = new StringBuilder(text.length() + 32);
      for (int index = 0; index < text.length(); index++) {
         char character = text.charAt(index);
         if (character == '\u00A7' && index + 1 < text.length()) {
            char code = Character.toLowerCase(text.charAt(++index));
            if (code >= '0' && code <= '9') {
               result.append(COLORS[code - '0']);
            } else if (code >= 'a' && code <= 'f') {
               result.append(COLORS[code - 'a' + 10]);
            } else if (code >= 'k' && code <= 'o') {
               result.append(STYLES[code - 'k']);
            } else if (code == 'r') {
               result.append(STYLES[5]);
            }
         } else {
            result.append(character);
         }
      }

      return result.toString();
   }

   public static String stripMinecraft(String text) {
      if (text == null || text.indexOf('\u00A7') < 0) {
         return text;
      }

      StringBuilder result = new StringBuilder(text.length());
      for (int index = 0; index < text.length(); index++) {
         char character = text.charAt(index);
         if (character == '\u00A7' && index + 1 < text.length()) {
            index++;
         } else {
            result.append(character);
         }
      }

      return result.toString();
   }

   public static String stripAnsi(String text) {
      return text == null ? null : text.replaceAll("\u001B\\[[0-9;]*m", "");
   }
}
