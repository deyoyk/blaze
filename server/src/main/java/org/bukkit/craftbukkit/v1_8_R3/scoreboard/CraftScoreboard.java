package org.bukkit.craftbukkit.v1_8_R3.scoreboard;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.ImmutableSet.Builder;
import net.minecraft.server.v1_8_R3.ScoreboardObjective;
import net.minecraft.server.v1_8_R3.ScoreboardTeam;
import org.apache.commons.lang.Validate;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public final class CraftScoreboard implements Scoreboard {
   final net.minecraft.server.v1_8_R3.Scoreboard board;

   CraftScoreboard(net.minecraft.server.v1_8_R3.Scoreboard board) {
      this.board = board;
   }

   public CraftObjective registerNewObjective(String name, String criteria) throws IllegalArgumentException {
      Validate.notNull(name, "Objective name cannot be null");
      Validate.notNull(criteria, "Criteria cannot be null");
      Validate.isTrue(name.length() <= 16, "The name '" + name + "' is longer than the limit of 16 characters");
      Validate.isTrue(this.board.getObjective(name) == null, "An objective of name '" + name + "' already exists");
      CraftCriteria craftCriteria = CraftCriteria.getFromBukkit(criteria);
      ScoreboardObjective objective = this.board.registerObjective(name, craftCriteria.criteria);
      return new CraftObjective(this, objective);
   }

   @Override
   public Objective getObjective(String name) throws IllegalArgumentException {
      Validate.notNull(name, "Name cannot be null");
      ScoreboardObjective nms = this.board.getObjective(name);
      return nms == null ? null : new CraftObjective(this, nms);
   }

   public ImmutableSet<Objective> getObjectivesByCriteria(String criteria) throws IllegalArgumentException {
      Validate.notNull(criteria, "Criteria cannot be null");
      Builder<Objective> objectives = ImmutableSet.builder();

      for (ScoreboardObjective netObjective : this.board.getObjectives()) {
         CraftObjective objective = new CraftObjective(this, netObjective);
         if (objective.getCriteria().equals(criteria)) {
            objectives.add(objective);
         }
      }

      return objectives.build();
   }

   public ImmutableSet<Objective> getObjectives() {
      return ImmutableSet.copyOf(Iterables.transform(this.board.getObjectives(), input -> new CraftObjective(this, input)));
   }

   @Override
   public Objective getObjective(DisplaySlot slot) throws IllegalArgumentException {
      Validate.notNull(slot, "Display slot cannot be null");
      ScoreboardObjective objective = this.board.getObjectiveForSlot(CraftScoreboardTranslations.fromBukkitSlot(slot));
      return objective == null ? null : new CraftObjective(this, objective);
   }

   public ImmutableSet<Score> getScores(org.bukkit.OfflinePlayer player) throws IllegalArgumentException {
      Validate.notNull(player, "OfflinePlayer cannot be null");
      return this.getScores(player.getName());
   }

   public ImmutableSet<Score> getScores(String entry) throws IllegalArgumentException {
      Validate.notNull(entry, "Entry cannot be null");
      Builder<Score> scores = ImmutableSet.builder();

      for (ScoreboardObjective objective : this.board.getObjectives()) {
         scores.add(new CraftScore(new CraftObjective(this, objective), entry));
      }

      return scores.build();
   }

   @Override
   public void resetScores(org.bukkit.OfflinePlayer player) throws IllegalArgumentException {
      Validate.notNull(player, "OfflinePlayer cannot be null");
      this.resetScores(player.getName());
   }

   @Override
   public void resetScores(String entry) throws IllegalArgumentException {
      Validate.notNull(entry, "Entry cannot be null");

      for (ScoreboardObjective objective : this.board.getObjectives()) {
         this.board.resetPlayerScores(entry, objective);
      }
   }

   @Override
   public Team getPlayerTeam(org.bukkit.OfflinePlayer player) throws IllegalArgumentException {
      Validate.notNull(player, "OfflinePlayer cannot be null");
      ScoreboardTeam team = this.board.getPlayerTeam(player.getName());
      return team == null ? null : new CraftTeam(this, team);
   }

   @Override
   public Team getEntryTeam(String entry) throws IllegalArgumentException {
      Validate.notNull(entry, "Entry cannot be null");
      ScoreboardTeam team = this.board.getPlayerTeam(entry);
      return team == null ? null : new CraftTeam(this, team);
   }

   @Override
   public Team getTeam(String teamName) throws IllegalArgumentException {
      Validate.notNull(teamName, "Team name cannot be null");
      ScoreboardTeam team = this.board.getTeam(teamName);
      return team == null ? null : new CraftTeam(this, team);
   }

   public ImmutableSet<Team> getTeams() {
      return ImmutableSet.copyOf(Iterables.transform(this.board.getTeams(), input -> new CraftTeam(this, input)));
   }

   @Override
   public Team registerNewTeam(String name) throws IllegalArgumentException {
      Validate.notNull(name, "Team name cannot be null");
      Validate.isTrue(name.length() <= 16, "Team name '" + name + "' is longer than the limit of 16 characters");
      Validate.isTrue(this.board.getTeam(name) == null, "Team name '" + name + "' is already in use");
      return new CraftTeam(this, this.board.createTeam(name));
   }

   public ImmutableSet<org.bukkit.OfflinePlayer> getPlayers() {
      Builder<org.bukkit.OfflinePlayer> players = ImmutableSet.builder();

      for (Object playerName : this.board.getPlayers()) {
         players.add(org.bukkit.Bukkit.getOfflinePlayer(playerName.toString()));
      }

      return players.build();
   }

   public ImmutableSet<String> getEntries() {
      Builder<String> entries = ImmutableSet.builder();

      for (Object entry : this.board.getPlayers()) {
         entries.add(entry.toString());
      }

      return entries.build();
   }

   @Override
   public void clearSlot(DisplaySlot slot) throws IllegalArgumentException {
      Validate.notNull(slot, "Slot cannot be null");
      this.board.setDisplaySlot(CraftScoreboardTranslations.fromBukkitSlot(slot), null);
   }

   public net.minecraft.server.v1_8_R3.Scoreboard getHandle() {
      return this.board;
   }
}
