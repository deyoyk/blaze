package org.bukkit.craftbukkit.v1_8_R3.scoreboard;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import java.util.Objects;
import java.util.Set;
import net.minecraft.server.v1_8_R3.ScoreboardTeam;
import net.minecraft.server.v1_8_R3.ScoreboardTeamBase.EnumNameTagVisibility;
import org.apache.commons.lang.Validate;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Team;

final class CraftTeam extends CraftScoreboardComponent implements Team {
   private final ScoreboardTeam team;

   CraftTeam(CraftScoreboard scoreboard, ScoreboardTeam team) {
      super(scoreboard);
      this.team = team;
   }

   @Override
   public String getName() throws IllegalStateException {
      this.checkState();
      return this.team.getName();
   }

   @Override
   public String getDisplayName() throws IllegalStateException {
      this.checkState();
      return this.team.getDisplayName();
   }

   @Override
   public void setDisplayName(String displayName) throws IllegalStateException {
      Validate.notNull(displayName, "Display name cannot be null");
      Validate.isTrue(displayName.length() <= 32, "Display name '" + displayName + "' is longer than the limit of 32 characters");
      this.checkState();
      this.team.setDisplayName(displayName);
   }

   @Override
   public String getPrefix() throws IllegalStateException {
      this.checkState();
      return this.team.getPrefix();
   }

   @Override
   public void setPrefix(String prefix) throws IllegalStateException, IllegalArgumentException {
      Validate.notNull(prefix, "Prefix cannot be null");
      Validate.isTrue(prefix.length() <= 32, "Prefix '" + prefix + "' is longer than the limit of 32 characters");
      this.checkState();
      this.team.setPrefix(prefix);
   }

   @Override
   public String getSuffix() throws IllegalStateException {
      this.checkState();
      return this.team.getSuffix();
   }

   @Override
   public void setSuffix(String suffix) throws IllegalStateException, IllegalArgumentException {
      Validate.notNull(suffix, "Suffix cannot be null");
      Validate.isTrue(suffix.length() <= 32, "Suffix '" + suffix + "' is longer than the limit of 32 characters");
      this.checkState();
      this.team.setSuffix(suffix);
   }

   @Override
   public boolean allowFriendlyFire() throws IllegalStateException {
      this.checkState();
      return this.team.allowFriendlyFire();
   }

   @Override
   public void setAllowFriendlyFire(boolean enabled) throws IllegalStateException {
      this.checkState();
      this.team.setAllowFriendlyFire(enabled);
   }

   @Override
   public boolean canSeeFriendlyInvisibles() throws IllegalStateException {
      this.checkState();
      return this.team.canSeeFriendlyInvisibles();
   }

   @Override
   public void setCanSeeFriendlyInvisibles(boolean enabled) throws IllegalStateException {
      this.checkState();
      this.team.setCanSeeFriendlyInvisibles(enabled);
   }

   @Override
   public NameTagVisibility getNameTagVisibility() throws IllegalArgumentException {
      this.checkState();
      return notchToBukkit(this.team.getNameTagVisibility());
   }

   @Override
   public void setNameTagVisibility(NameTagVisibility visibility) throws IllegalArgumentException {
      this.checkState();
      this.team.setNameTagVisibility(bukkitToNotch(visibility));
   }

   @Override
   public Set<org.bukkit.OfflinePlayer> getPlayers() throws IllegalStateException {
      this.checkState();
      Builder<org.bukkit.OfflinePlayer> players = ImmutableSet.builder();

      for (String playerName : this.team.getPlayerNameSet()) {
         players.add(org.bukkit.Bukkit.getOfflinePlayer(playerName));
      }

      return players.build();
   }

   @Override
   public Set<String> getEntries() throws IllegalStateException {
      this.checkState();
      Builder<String> entries = ImmutableSet.builder();

      for (String playerName : this.team.getPlayerNameSet()) {
         entries.add(playerName);
      }

      return entries.build();
   }

   @Override
   public int getSize() throws IllegalStateException {
      this.checkState();
      return this.team.getPlayerNameSet().size();
   }

   @Override
   public void addPlayer(org.bukkit.OfflinePlayer player) throws IllegalStateException, IllegalArgumentException {
      Validate.notNull(player, "OfflinePlayer cannot be null");
      this.addEntry(player.getName());
   }

   @Override
   public void addEntry(String entry) throws IllegalStateException, IllegalArgumentException {
      Validate.notNull(entry, "Entry cannot be null");
      CraftScoreboard scoreboard = this.checkState();
      scoreboard.board.addPlayerToTeam(entry, this.team.getName());
   }

   @Override
   public boolean removePlayer(org.bukkit.OfflinePlayer player) throws IllegalStateException, IllegalArgumentException {
      Validate.notNull(player, "OfflinePlayer cannot be null");
      return this.removeEntry(player.getName());
   }

   @Override
   public boolean removeEntry(String entry) throws IllegalStateException, IllegalArgumentException {
      Validate.notNull(entry, "Entry cannot be null");
      CraftScoreboard scoreboard = this.checkState();
      if (!this.team.getPlayerNameSet().contains(entry)) {
         return false;
      } else {
         scoreboard.board.removePlayerFromTeam(entry, this.team);
         return true;
      }
   }

   @Override
   public boolean hasPlayer(org.bukkit.OfflinePlayer player) throws IllegalArgumentException, IllegalStateException {
      Validate.notNull(player, "OfflinePlayer cannot be null");
      return this.hasEntry(player.getName());
   }

   @Override
   public boolean hasEntry(String entry) throws IllegalArgumentException, IllegalStateException {
      this.checkState();
      return this.team.getPlayerNameSet().contains(entry);
   }

   @Override
   public void unregister() throws IllegalStateException {
      CraftScoreboard scoreboard = this.checkState();
      scoreboard.board.removeTeam(this.team);
   }

   public static EnumNameTagVisibility bukkitToNotch(NameTagVisibility visibility) {
      switch (visibility) {
         case ALWAYS:
            return EnumNameTagVisibility.ALWAYS;
         case NEVER:
            return EnumNameTagVisibility.NEVER;
         case HIDE_FOR_OTHER_TEAMS:
            return EnumNameTagVisibility.HIDE_FOR_OTHER_TEAMS;
         case HIDE_FOR_OWN_TEAM:
            return EnumNameTagVisibility.HIDE_FOR_OWN_TEAM;
         default:
            throw new IllegalArgumentException("Unknown visibility level " + visibility);
      }
   }

   public static NameTagVisibility notchToBukkit(EnumNameTagVisibility visibility) {
      switch (visibility) {
         case ALWAYS:
            return NameTagVisibility.ALWAYS;
         case NEVER:
            return NameTagVisibility.NEVER;
         case HIDE_FOR_OTHER_TEAMS:
            return NameTagVisibility.HIDE_FOR_OTHER_TEAMS;
         case HIDE_FOR_OWN_TEAM:
            return NameTagVisibility.HIDE_FOR_OWN_TEAM;
         default:
            throw new IllegalArgumentException("Unknown visibility level " + visibility);
      }
   }

   @Override
   CraftScoreboard checkState() throws IllegalStateException {
      if (this.getScoreboard().board.getTeam(this.team.getName()) == null) {
         throw new IllegalStateException("Unregistered scoreboard component");
      } else {
         return this.getScoreboard();
      }
   }

   @Override
   public int hashCode() {
      int hash = 7;
      return 43 * hash + (this.team != null ? this.team.hashCode() : 0);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         CraftTeam other = (CraftTeam)obj;
         return Objects.equals(this.team, other.team);
      }
   }
}
