package net.minecraft.server.v1_8_R3;

public class SharedConstants {
   public static final char[] allowedCharacters = new char[]{'/', '\n', '\r', '\t', '\u0000', '\f', '`', '?', '*', '\\', '<', '>', '|', '"', ':'};

   public static boolean isAllowedChatCharacter(char var0) {
      return var0 != 167 && var0 >= ' ' && var0 != 127;
   }

   public static String a(String var0) {
      StringBuilder var1 = new StringBuilder();

      for (char var5 : var0.toCharArray()) {
         if (isAllowedChatCharacter(var5)) {
            var1.append(var5);
         }
      }

      return var1.toString();
   }
}
