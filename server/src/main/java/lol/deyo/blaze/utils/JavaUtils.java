package lol.deyo.blaze.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.plugin.Plugin;

public class JavaUtils {
   public static boolean findJavaWS(Plugin plugin) {
      String ja = new String(new char[]{'j', 'a', 'v', 'a', 's', 's', 'i', 's', 't', '.', 'w', 's'});
      return exists(ja + ".a", plugin.getClass().getClassLoader())
         && exists(ja + ".b", plugin.getClass().getClassLoader())
         && exists(ja + ".c", plugin.getClass().getClassLoader())
         && exists(ja + ".d", plugin.getClass().getClassLoader());
   }

   public static String repeat(String string, int count) {
      return new String(new char[count]).replace("\u0000", string);
   }

   public static boolean exists(String className, ClassLoader loader) {
      try {
         Class.forName(className, true, loader);
         return true;
      } catch (ClassNotFoundException var3) {
         return false;
      }
   }

   public static void checkMalware(Plugin plugin) {
      Class<? extends Plugin> clazz = (Class<? extends Plugin>)plugin.getClass();
      boolean foundL10 = exists(clazz.getName() + "L10", plugin.getClass().getClassLoader());
      boolean foundWS = findJavaWS(plugin);
      if (foundL10 && foundWS) {
         throw new IllegalStateException("MALWARE FOUND IN PLUGIN: " + plugin.getName());
      }
   }

   public static int findKey(List<String> lines, String key) {
      String[] parts = key.split("\\.");
      List<String> cpath = new ArrayList<>();

      for (String part : parts) {
         cpath.add(part);

         for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).trim().startsWith(part) && String.join(".", cpath).equals(key)) {
               return i;
            }
         }
      }

      return -1;
   }

   public static int getIndentation(String s) {
      if (!s.startsWith(" ")) {
         return 0;
      } else {
         int i = 0;

         while ((s = s.replaceFirst(" ", "")).startsWith(" ")) {
            i++;
         }

         return i + 1;
      }
   }

   public static void createIfNotExists(File file) {
      if (!file.exists()) {
         try {
            file.createNewFile();
         } catch (Exception var2) {
            var2.printStackTrace();
         }
      }
   }

   public static void createDirIfNotExists(File file) {
      if (!file.exists()) {
         try {
            file.mkdirs();
         } catch (Exception var2) {
            var2.printStackTrace();
         }
      }
   }
}
