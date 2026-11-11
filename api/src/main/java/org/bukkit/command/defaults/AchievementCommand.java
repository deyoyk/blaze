package org.bukkit.command.defaults;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.Validate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;

@Deprecated
public class AchievementCommand extends VanillaCommand {
   public AchievementCommand() {
      super("achievement");
      this.description = "Gives the specified player an achievement or changes a statistic value. Use '*' to give all achievements.";
      this.usageMessage = "/achievement give <stat_name> [player]";
      this.setPermission("bukkit.command.achievement");
   }

   @Override
   public boolean execute(CommandSender sender, String currentAlias, String[] args) {
      if (!this.testPermission(sender)) {
         return true;
      } else if (args.length < 2) {
         sender.sendMessage(org.bukkit.ChatColor.RED + "Usage: " + this.usageMessage);
         return false;
      } else if (!args[0].equalsIgnoreCase("give")) {
         sender.sendMessage(org.bukkit.ChatColor.RED + "Usage: " + this.usageMessage);
         return false;
      } else {
         String statisticString = args[1];
         Player player = null;
         if (args.length > 2) {
            player = org.bukkit.Bukkit.getPlayer(args[1]);
         } else if (sender instanceof Player) {
            player = (Player)sender;
         }

         if (player == null) {
            sender.sendMessage("You must specify which player you wish to perform this action on.");
            return true;
         } else if (statisticString.equals("*")) {
            for (org.bukkit.Achievement achievement : org.bukkit.Achievement.values()) {
               if (!player.hasAchievement(achievement)) {
                  PlayerAchievementAwardedEvent event = new PlayerAchievementAwardedEvent(player, achievement);
                  org.bukkit.Bukkit.getServer().getPluginManager().callEvent(event);
                  if (!event.isCancelled()) {
                     player.awardAchievement(achievement);
                  }
               }
            }

            Command.broadcastCommandMessage(sender, String.format("Successfully given all achievements to %s", player.getName()));
            return true;
         } else {
            org.bukkit.Achievement achievementx = org.bukkit.Bukkit.getUnsafe().getAchievementFromInternalName(statisticString);
            org.bukkit.Statistic statistic = org.bukkit.Bukkit.getUnsafe().getStatisticFromInternalName(statisticString);
            if (achievementx != null) {
               if (player.hasAchievement(achievementx)) {
                  sender.sendMessage(String.format("%s already has achievement %s", player.getName(), statisticString));
                  return true;
               } else {
                  PlayerAchievementAwardedEvent event = new PlayerAchievementAwardedEvent(player, achievementx);
                  org.bukkit.Bukkit.getServer().getPluginManager().callEvent(event);
                  if (event.isCancelled()) {
                     sender.sendMessage(String.format("Unable to award %s the achievement %s", player.getName(), statisticString));
                     return true;
                  } else {
                     player.awardAchievement(achievementx);
                     Command.broadcastCommandMessage(sender, String.format("Successfully given %s the stat %s", player.getName(), statisticString));
                     return true;
                  }
               }
            } else if (statistic == null) {
               sender.sendMessage(String.format("Unknown achievement or statistic '%s'", statisticString));
               return true;
            } else if (statistic.getType() == org.bukkit.Statistic.Type.UNTYPED) {
               PlayerStatisticIncrementEvent event = new PlayerStatisticIncrementEvent(
                  player, statistic, player.getStatistic(statistic), player.getStatistic(statistic) + 1
               );
               org.bukkit.Bukkit.getServer().getPluginManager().callEvent(event);
               if (event.isCancelled()) {
                  sender.sendMessage(String.format("Unable to increment %s for %s", statisticString, player.getName()));
                  return true;
               } else {
                  player.incrementStatistic(statistic);
                  Command.broadcastCommandMessage(sender, String.format("Successfully given %s the stat %s", player.getName(), statisticString));
                  return true;
               }
            } else {
               if (statistic.getType() == org.bukkit.Statistic.Type.ENTITY) {
                  EntityType entityType = EntityType.fromName(statisticString.substring(statisticString.lastIndexOf(".") + 1));
                  if (entityType == null) {
                     sender.sendMessage(String.format("Unknown achievement or statistic '%s'", statisticString));
                     return true;
                  }

                  PlayerStatisticIncrementEvent event = new PlayerStatisticIncrementEvent(
                     player, statistic, player.getStatistic(statistic), player.getStatistic(statistic) + 1, entityType
                  );
                  org.bukkit.Bukkit.getServer().getPluginManager().callEvent(event);
                  if (event.isCancelled()) {
                     sender.sendMessage(String.format("Unable to increment %s for %s", statisticString, player.getName()));
                     return true;
                  }

                  try {
                     player.incrementStatistic(statistic, entityType);
                  } catch (IllegalArgumentException var14) {
                     sender.sendMessage(String.format("Unknown achievement or statistic '%s'", statisticString));
                     return true;
                  }
               } else {
                  int id;
                  try {
                     id = this.getInteger(sender, statisticString.substring(statisticString.lastIndexOf(".") + 1), 0, Integer.MAX_VALUE, true);
                  } catch (NumberFormatException var13) {
                     sender.sendMessage(var13.getMessage());
                     return true;
                  }

                  org.bukkit.Material material = org.bukkit.Material.getMaterial(id);
                  if (material == null) {
                     sender.sendMessage(String.format("Unknown achievement or statistic '%s'", statisticString));
                     return true;
                  }

                  PlayerStatisticIncrementEvent event = new PlayerStatisticIncrementEvent(
                     player, statistic, player.getStatistic(statistic), player.getStatistic(statistic) + 1, material
                  );
                  org.bukkit.Bukkit.getServer().getPluginManager().callEvent(event);
                  if (event.isCancelled()) {
                     sender.sendMessage(String.format("Unable to increment %s for %s", statisticString, player.getName()));
                     return true;
                  }

                  try {
                     player.incrementStatistic(statistic, material);
                  } catch (IllegalArgumentException var12) {
                     sender.sendMessage(String.format("Unknown achievement or statistic '%s'", statisticString));
                     return true;
                  }
               }

               Command.broadcastCommandMessage(sender, String.format("Successfully given %s the stat %s", player.getName(), statisticString));
               return true;
            }
         }
      }
   }

   @Override
   public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
      Validate.notNull(sender, "Sender cannot be null");
      Validate.notNull(args, "Arguments cannot be null");
      Validate.notNull(alias, "Alias cannot be null");
      if (args.length == 1) {
         return Arrays.asList("give");
      } else if (args.length == 2) {
         return org.bukkit.Bukkit.getUnsafe().tabCompleteInternalStatisticOrAchievementName(args[1], new ArrayList<>());
      } else {
         return (List<String>)(args.length == 3 ? super.tabComplete(sender, alias, args) : ImmutableList.of());
      }
   }
}
