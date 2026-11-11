package org.bukkit.scoreboard;

public interface Score {
   @Deprecated
   org.bukkit.OfflinePlayer getPlayer();

   String getEntry();

   Objective getObjective();

   int getScore() throws IllegalStateException;

   void setScore(int var1) throws IllegalStateException;

   boolean isScoreSet() throws IllegalStateException;

   Scoreboard getScoreboard();
}
