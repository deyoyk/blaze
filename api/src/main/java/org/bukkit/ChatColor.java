package org.bukkit;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.lang.Validate;

public enum ChatColor {
   BLACK('0', 0) {
      @Override
      public net.md_5.bungee.api.ChatColor asBungee() {
         return net.md_5.bungee.api.ChatColor.BLACK;
      }
   },
   DARK_BLUE('1', 1) {
      @Override
      public net.md_5.bungee.api.ChatColor asBungee() {
         return net.md_5.bungee.api.ChatColor.DARK_BLUE;
      }
   },
   DARK_GREEN('2', 2) {
      @Override
      public net.md_5.bungee.api.ChatColor asBungee() {
         return net.md_5.bungee.api.ChatColor.DARK_GREEN;
      }
   },
   DARK_AQUA('3', 3) {
      @Override
      public net.md_5.bungee.api.ChatColor asBungee() {
         return net.md_5.bungee.api.ChatColor.DARK_AQUA;
      }
   },
   DARK_RED('4', 4) {
      @Override
      public net.md_5.bungee.api.ChatColor asBungee() {
         return net.md_5.bungee.api.ChatColor.DARK_RED;
      }
   },
   DARK_PURPLE('5', 5) {
      @Override
      public net.md_5.bungee.api.ChatColor asBungee() {
         return net.md_5.bungee.api.ChatColor.DARK_PURPLE;
      }
   },
   GOLD('6', 6) {
      @Override
      public net.md_5.bungee.api.ChatColor asBungee() {
         return net.md_5.bungee.api.ChatColor.GOLD;
      }
   },
   GRAY('7', 7) {
      @Override
      public net.md_5.bungee.api.ChatColor asBungee() {
         return net.md_5.bungee.api.ChatColor.GRAY;
      }
   },
   DARK_GRAY('8', 8) {
      @Override
      public net.md_5.bungee.api.ChatColor asBungee() {
         return net.md_5.bungee.api.ChatColor.DARK_GRAY;
      }
   },
   BLUE('9', 9) {
      @Override
      public net.md_5.bungee.api.ChatColor asBungee() {
         return net.md_5.bungee.api.ChatColor.BLUE;
      }
   },
   GREEN('a', 10) {
      @Override
      public net.md_5.bungee.api.ChatColor asBungee() {
         return net.md_5.bungee.api.ChatColor.GREEN;
      }
   },
   AQUA('b', 11) {
      @Override
      public net.md_5.bungee.api.ChatColor asBungee() {
         return net.md_5.bungee.api.ChatColor.AQUA;
      }
   },
   RED('c', 12) {
      @Override
      public net.md_5.bungee.api.ChatColor asBungee() {
         return net.md_5.bungee.api.ChatColor.RED;
      }
   },
   LIGHT_PURPLE('d', 13) {
      @Override
      public net.md_5.bungee.api.ChatColor asBungee() {
         return net.md_5.bungee.api.ChatColor.LIGHT_PURPLE;
      }
   },
   YELLOW('e', 14) {
      @Override
      public net.md_5.bungee.api.ChatColor asBungee() {
         return net.md_5.bungee.api.ChatColor.YELLOW;
      }
   },
   WHITE('f', 15) {
      @Override
      public net.md_5.bungee.api.ChatColor asBungee() {
         return net.md_5.bungee.api.ChatColor.WHITE;
      }
   },
   MAGIC('k', 16, true) {
      @Override
      public net.md_5.bungee.api.ChatColor asBungee() {
         return net.md_5.bungee.api.ChatColor.MAGIC;
      }
   },
   BOLD('l', 17, true) {
      @Override
      public net.md_5.bungee.api.ChatColor asBungee() {
         return net.md_5.bungee.api.ChatColor.BOLD;
      }
   },
   STRIKETHROUGH('m', 18, true) {
      @Override
      public net.md_5.bungee.api.ChatColor asBungee() {
         return net.md_5.bungee.api.ChatColor.STRIKETHROUGH;
      }
   },
   UNDERLINE('n', 19, true) {
      @Override
      public net.md_5.bungee.api.ChatColor asBungee() {
         return net.md_5.bungee.api.ChatColor.UNDERLINE;
      }
   },
   ITALIC('o', 20, true) {
      @Override
      public net.md_5.bungee.api.ChatColor asBungee() {
         return net.md_5.bungee.api.ChatColor.ITALIC;
      }
   },
   RESET('r', 21) {
      @Override
      public net.md_5.bungee.api.ChatColor asBungee() {
         return net.md_5.bungee.api.ChatColor.RESET;
      }
   };

   public static final char COLOR_CHAR = '§';
   private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + String.valueOf('§') + "[0-9A-FK-OR]");
   private final int intCode;
   private final char code;
   private final boolean isFormat;
   private final String toString;
   private static final Map<Integer, org.bukkit.ChatColor> BY_ID = Maps.newHashMap();
   private static final Map<Character, org.bukkit.ChatColor> BY_CHAR = Maps.newHashMap();

   private ChatColor(char code, int intCode) {
      this(code, intCode, false);
   }

   private ChatColor(char code, int intCode, boolean isFormat) {
      this.code = code;
      this.intCode = intCode;
      this.isFormat = isFormat;
      this.toString = new String(new char[]{'§', code});
   }

   public net.md_5.bungee.api.ChatColor asBungee() {
      return net.md_5.bungee.api.ChatColor.RESET;
   }

   public char getChar() {
      return this.code;
   }

   @Override
   public String toString() {
      return this.toString;
   }

   public boolean isFormat() {
      return this.isFormat;
   }

   public boolean isColor() {
      return !this.isFormat && this != RESET;
   }

   public static org.bukkit.ChatColor getByChar(char code) {
      return BY_CHAR.get(code);
   }

   public static org.bukkit.ChatColor getByChar(String code) {
      Validate.notNull(code, "Code cannot be null");
      Validate.isTrue(code.length() > 0, "Code must have at least one char");
      return BY_CHAR.get(code.charAt(0));
   }

   public static String stripColor(String input) {
      return input == null ? null : STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
   }

   public static String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
      char[] b = textToTranslate.toCharArray();

      for (int i = 0; i < b.length - 1; i++) {
         if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1) {
            b[i] = 167;
            b[i + 1] = Character.toLowerCase(b[i + 1]);
         }
      }

      return new String(b);
   }

   public static String getLastColors(String input) {
      String result = "";
      int length = input.length();

      for (int index = length - 1; index > -1; index--) {
         char section = input.charAt(index);
         if (section == 167 && index < length - 1) {
            char c = input.charAt(index + 1);
            org.bukkit.ChatColor color = getByChar(c);
            if (color != null) {
               result = color.toString() + result;
               if (color.isColor() || color.equals(RESET)) {
                  break;
               }
            }
         }
      }

      return result;
   }

   static {
      for (org.bukkit.ChatColor color : values()) {
         BY_ID.put(color.intCode, color);
         BY_CHAR.put(color.code, color);
      }
   }
}
