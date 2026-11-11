package org.bukkit.scoreboard;

public interface Objective {
   String getName() throws IllegalStateException;

   String getDisplayName() throws IllegalStateException;

   void setDisplayName(String var1) throws IllegalStateException, IllegalArgumentException;

   String getCriteria() throws IllegalStateException;

   boolean isModifiable() throws IllegalStateException;

   Scoreboard getScoreboard();

   void unregister() throws IllegalStateException;

   void setDisplaySlot(DisplaySlot var1) throws IllegalStateException;

   DisplaySlot getDisplaySlot() throws IllegalStateException;

   @Deprecated
   Score getScore(org.bukkit.OfflinePlayer var1) throws IllegalArgumentException, IllegalStateException;

   Score getScore(String var1) throws IllegalArgumentException, IllegalStateException;
}
