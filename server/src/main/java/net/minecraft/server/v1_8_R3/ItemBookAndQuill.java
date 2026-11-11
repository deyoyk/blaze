package net.minecraft.server.v1_8_R3;

import java.nio.charset.StandardCharsets;

public class ItemBookAndQuill extends Item {
   public static final String[] MOJANG_CRASH_TRANSLATIONS = new String[]{"translation.test.invalid", "translation.test.invalid2"};

   public ItemBookAndQuill() {
      this.c(1);
   }

   public static boolean b(NBTTagCompound var0) {
      if (var0 != null && var0.hasKeyOfType("pages", 9)) {
         NBTTagList pages = var0.getList("pages", 8);
         if (pages.size() > 50) {
            return false;
         } else {
            long byteTotal = 0L;
            long byteAllowed = 2560L;

            for (int i = 0; i < pages.size(); i++) {
               String page = pages.getString(i);
               if (page == null || page.equals("null") || page.length() > 500) {
                  return false;
               }

               int byteLength = page.getBytes(StandardCharsets.UTF_8).length;
               byteTotal += byteLength;
               int length = page.length();
               int multibytes = 0;
               if (byteLength != length) {
                  char[] var14;
                  for (char c : var14 = page.toCharArray()) {
                     if (c > 127) {
                        multibytes++;
                     }
                  }
               }

               byteAllowed += (long)(2560.0 * Math.min(1.0, Math.max(0.1, length / 255.0)) * 0.98);
               if (multibytes > 1) {
                  byteAllowed -= multibytes;
               }

               if (byteTotal > byteAllowed) {
                  return false;
               }

               if (page.replace(" ", "").startsWith("{\"translate\"")) {
                  for (String crashTranslation : MOJANG_CRASH_TRANSLATIONS) {
                     if (page.equalsIgnoreCase(String.format("{\"translate\":\"%s\"}", crashTranslation))) {
                        return false;
                     }
                  }
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   @Override
   public ItemStack a(ItemStack var1, World var2, EntityHuman var3) {
      var3.openBook(var1);
      return var1;
   }
}
