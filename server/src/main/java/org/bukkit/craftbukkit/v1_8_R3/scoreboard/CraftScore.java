package org.bukkit.craftbukkit.v1_8_R3.scoreboard;

import java.util.Map;
import net.minecraft.server.v1_8_R3.Scoreboard;
import net.minecraft.server.v1_8_R3.ScoreboardObjective;
import net.minecraft.server.v1_8_R3.ScoreboardScore;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

final class CraftScore implements Score {
   private final String entry;
   private final CraftObjective objective;

   CraftScore(CraftObjective objective, String entry) {
      this.objective = objective;
      this.entry = entry;
   }

   @Override
   public org.bukkit.OfflinePlayer getPlayer() {
      return org.bukkit.Bukkit.getOfflinePlayer(this.entry);
   }

   @Override
   public String getEntry() {
      return this.entry;
   }

   @Override
   public Objective getObjective() {
      return this.objective;
   }

   @Override
   public int getScore() throws IllegalStateException {
      Scoreboard board = this.objective.checkState().board;
      if (board.getPlayers().contains(this.entry)) {
         Map<ScoreboardObjective, ScoreboardScore> scores = board.getPlayerObjectives(this.entry);
         ScoreboardScore score = scores.get(this.objective.getHandle());
         if (score != null) {
            return score.getScore();
         }
      }

      return 0;
   }

   @Override
   public void setScore(int score) throws IllegalStateException {
      this.objective.checkState().board.getPlayerScoreForObjective(this.entry, this.objective.getHandle()).setScore(score);
   }

   public CraftScoreboard getScoreboard() {
      return this.objective.getScoreboard();
   }

   @Override
   public boolean isScoreSet() throws IllegalStateException {
      Scoreboard board = this.objective.checkState().board;
      return board.getPlayers().contains(this.entry) && board.getPlayerObjectives(this.entry).containsKey(this.objective.getHandle());
   }
}
