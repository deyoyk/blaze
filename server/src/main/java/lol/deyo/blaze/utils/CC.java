package lol.deyo.blaze.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CC {
   public static final String CREDITS = "&6Blaze Spigot &eby deyo";
   private static final String CONFIG_LINE_TOP = "================ [ Blaze ] ================";
   private static final String CONFIG_LINE_BOTTOM = "======================================================";

   private static String center(String string) {
      return StringUtils.center(string, "================ [ Blaze ] ================".length());
   }

   public static List<String> getConfigHeader(String... centerText) {
      List<String> header = new ArrayList<>();
      header.add("================ [ Blaze ] ================");
      header.add("");

      for (String s : centerText) {
         header.add(center(s));
      }

      header.add("");
      header.add("======================================================");
      return header;
   }

   public static String t(String t) {
      return ChatColor.translateAlternateColorCodes('&', t);
   }

   public static void sendMessage(CommandSender sender, String... strings) {
      for (String string : strings) {
         sender.sendMessage(t(string));
      }
   }

   public static List<String> t(List<String> t) {
      return t.stream().map(CC::t).collect(Collectors.toList());
   }
}
