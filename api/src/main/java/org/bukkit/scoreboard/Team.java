package org.bukkit.scoreboard;

import java.util.Set;

public interface Team {
   String getName() throws IllegalStateException;

   String getDisplayName() throws IllegalStateException;

   void setDisplayName(String var1) throws IllegalStateException, IllegalArgumentException;

   String getPrefix() throws IllegalStateException;

   void setPrefix(String var1) throws IllegalStateException, IllegalArgumentException;

   String getSuffix() throws IllegalStateException;

   void setSuffix(String var1) throws IllegalStateException, IllegalArgumentException;

   boolean allowFriendlyFire() throws IllegalStateException;

   void setAllowFriendlyFire(boolean var1) throws IllegalStateException;

   boolean canSeeFriendlyInvisibles() throws IllegalStateException;

   void setCanSeeFriendlyInvisibles(boolean var1) throws IllegalStateException;

   NameTagVisibility getNameTagVisibility() throws IllegalArgumentException;

   void setNameTagVisibility(NameTagVisibility var1) throws IllegalArgumentException;

   @Deprecated
   Set<org.bukkit.OfflinePlayer> getPlayers() throws IllegalStateException;

   Set<String> getEntries() throws IllegalStateException;

   int getSize() throws IllegalStateException;

   Scoreboard getScoreboard();

   @Deprecated
   void addPlayer(org.bukkit.OfflinePlayer var1) throws IllegalStateException, IllegalArgumentException;

   void addEntry(String var1) throws IllegalStateException, IllegalArgumentException;

   @Deprecated
   boolean removePlayer(org.bukkit.OfflinePlayer var1) throws IllegalStateException, IllegalArgumentException;

   boolean removeEntry(String var1) throws IllegalStateException, IllegalArgumentException;

   void unregister() throws IllegalStateException;

   @Deprecated
   boolean hasPlayer(org.bukkit.OfflinePlayer var1) throws IllegalArgumentException, IllegalStateException;

   boolean hasEntry(String var1) throws IllegalArgumentException, IllegalStateException;
}
