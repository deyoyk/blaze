package org.bukkit.scoreboard;

import java.util.Set;

public interface Scoreboard {
   Objective registerNewObjective(String var1, String var2) throws IllegalArgumentException;

   Objective getObjective(String var1) throws IllegalArgumentException;

   Set<Objective> getObjectivesByCriteria(String var1) throws IllegalArgumentException;

   Set<Objective> getObjectives();

   Objective getObjective(DisplaySlot var1) throws IllegalArgumentException;

   @Deprecated
   Set<Score> getScores(org.bukkit.OfflinePlayer var1) throws IllegalArgumentException;

   Set<Score> getScores(String var1) throws IllegalArgumentException;

   @Deprecated
   void resetScores(org.bukkit.OfflinePlayer var1) throws IllegalArgumentException;

   void resetScores(String var1) throws IllegalArgumentException;

   @Deprecated
   Team getPlayerTeam(org.bukkit.OfflinePlayer var1) throws IllegalArgumentException;

   Team getEntryTeam(String var1) throws IllegalArgumentException;

   Team getTeam(String var1) throws IllegalArgumentException;

   Set<Team> getTeams();

   Team registerNewTeam(String var1) throws IllegalArgumentException;

   @Deprecated
   Set<org.bukkit.OfflinePlayer> getPlayers();

   Set<String> getEntries();

   void clearSlot(DisplaySlot var1) throws IllegalArgumentException;
}
