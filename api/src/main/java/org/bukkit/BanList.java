package org.bukkit;

import java.util.Date;
import java.util.Set;

public interface BanList {
   org.bukkit.BanEntry getBanEntry(String var1);

   org.bukkit.BanEntry addBan(String var1, String var2, Date var3, String var4);

   Set<org.bukkit.BanEntry> getBanEntries();

   boolean isBanned(String var1);

   void pardon(String var1);

   public static enum Type {
      NAME,
      IP;
   }
}
