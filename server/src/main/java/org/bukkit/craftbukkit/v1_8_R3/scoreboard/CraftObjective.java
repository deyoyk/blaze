package org.bukkit.craftbukkit.v1_8_R3.scoreboard;

import java.util.Objects;
import net.minecraft.server.v1_8_R3.Scoreboard;
import net.minecraft.server.v1_8_R3.ScoreboardObjective;
import org.apache.commons.lang.Validate;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

final class CraftObjective extends CraftScoreboardComponent implements Objective {
   private final ScoreboardObjective objective;
   private final CraftCriteria criteria;

   CraftObjective(CraftScoreboard scoreboard, ScoreboardObjective objective) {
      super(scoreboard);
      this.objective = objective;
      this.criteria = CraftCriteria.getFromNMS(objective);
   }

   ScoreboardObjective getHandle() {
      return this.objective;
   }

   @Override
   public String getName() throws IllegalStateException {
      this.checkState();
      return this.objective.getName();
   }

   @Override
   public String getDisplayName() throws IllegalStateException {
      this.checkState();
      return this.objective.getDisplayName();
   }

   @Override
   public void setDisplayName(String displayName) throws IllegalStateException, IllegalArgumentException {
      Validate.notNull(displayName, "Display name cannot be null");
      Validate.isTrue(displayName.length() <= 32, "Display name '" + displayName + "' is longer than the limit of 32 characters");
      this.checkState();
      this.objective.setDisplayName(displayName);
   }

   @Override
   public String getCriteria() throws IllegalStateException {
      this.checkState();
      return this.criteria.bukkitName;
   }

   @Override
   public boolean isModifiable() throws IllegalStateException {
      this.checkState();
      return !this.criteria.criteria.isReadOnly();
   }

   @Override
   public void setDisplaySlot(DisplaySlot slot) throws IllegalStateException {
      CraftScoreboard scoreboard = this.checkState();
      Scoreboard board = scoreboard.board;
      ScoreboardObjective objective = this.objective;

      for (int i = 0; i < 3; i++) {
         if (board.getObjectiveForSlot(i) == objective) {
            board.setDisplaySlot(i, null);
         }
      }

      if (slot != null) {
         int slotNumber = CraftScoreboardTranslations.fromBukkitSlot(slot);
         board.setDisplaySlot(slotNumber, this.getHandle());
      }
   }

   @Override
   public DisplaySlot getDisplaySlot() throws IllegalStateException {
      CraftScoreboard scoreboard = this.checkState();
      Scoreboard board = scoreboard.board;
      ScoreboardObjective objective = this.objective;

      for (int i = 0; i < 3; i++) {
         if (board.getObjectiveForSlot(i) == objective) {
            return CraftScoreboardTranslations.toBukkitSlot(i);
         }
      }

      return null;
   }

   @Override
   public Score getScore(org.bukkit.OfflinePlayer player) throws IllegalArgumentException, IllegalStateException {
      Validate.notNull(player, "Player cannot be null");
      this.checkState();
      return new CraftScore(this, player.getName());
   }

   @Override
   public Score getScore(String entry) throws IllegalArgumentException, IllegalStateException {
      Validate.notNull(entry, "Entry cannot be null");
      if (entry.length() > 40) {
         throw new IllegalArgumentException("Entry cannot be longer than 40 characters!");
      } else {
         this.checkState();
         return new CraftScore(this, entry);
      }
   }

   @Override
   public void unregister() throws IllegalStateException {
      this.checkState().board.unregisterObjective(this.objective);
   }

   @Override
   CraftScoreboard checkState() throws IllegalStateException {
      if (this.getScoreboard().board.getObjective(this.objective.getName()) == null) {
         throw new IllegalStateException("Unregistered scoreboard component");
      } else {
         return this.getScoreboard();
      }
   }

   @Override
   public int hashCode() {
      int hash = 7;
      return 31 * hash + (this.objective != null ? this.objective.hashCode() : 0);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         CraftObjective other = (CraftObjective)obj;
         return Objects.equals(this.objective, other.objective);
      }
   }
}
