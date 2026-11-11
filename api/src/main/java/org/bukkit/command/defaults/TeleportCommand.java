package org.bukkit.command.defaults;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.apache.commons.lang.Validate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

@Deprecated
public class TeleportCommand extends VanillaCommand {
   public TeleportCommand() {
      super("tp");
      this.description = "Teleports the given player (or yourself) to another player or coordinates";
      this.usageMessage = "/tp [player] <target> and/or <x> <y> <z>";
      this.setPermission("bukkit.command.teleport");
   }

   @Override
   public boolean execute(CommandSender sender, String currentAlias, String[] args) {
      if (!this.testPermission(sender)) {
         return true;
      } else if (args.length >= 1 && args.length <= 4) {
         Player player;
         if (args.length != 1 && args.length != 3) {
            player = org.bukkit.Bukkit.getPlayerExact(args[0]);
         } else {
            if (!(sender instanceof Player)) {
               sender.sendMessage("Please provide a player!");
               return true;
            }

            player = (Player)sender;
         }

         if (player == null) {
            sender.sendMessage("Player not found: " + args[0]);
            return true;
         } else {
            if (args.length < 3) {
               Player target = org.bukkit.Bukkit.getPlayerExact(args[args.length - 1]);
               if (target == null) {
                  sender.sendMessage("Can't find player " + args[args.length - 1] + ". No tp.");
                  return true;
               }

               player.teleport(target, PlayerTeleportEvent.TeleportCause.COMMAND);
               Command.broadcastCommandMessage(sender, "Teleported " + player.getDisplayName() + " to " + target.getDisplayName());
            } else if (player.getWorld() != null) {
               org.bukkit.Location playerLocation = player.getLocation();
               double x = this.getCoordinate(sender, playerLocation.getX(), args[args.length - 3]);
               double y = this.getCoordinate(sender, playerLocation.getY(), args[args.length - 2], 0, 0);
               double z = this.getCoordinate(sender, playerLocation.getZ(), args[args.length - 1]);
               if (x == -3.0000001E7 || y == -3.0000001E7 || z == -3.0000001E7) {
                  sender.sendMessage("Please provide a valid location!");
                  return true;
               }

               playerLocation.setX(x);
               playerLocation.setY(y);
               playerLocation.setZ(z);
               player.teleport(playerLocation, PlayerTeleportEvent.TeleportCause.COMMAND);
               Command.broadcastCommandMessage(sender, String.format("Teleported %s to %.2f, %.2f, %.2f", player.getDisplayName(), x, y, z));
            }

            return true;
         }
      } else {
         sender.sendMessage(org.bukkit.ChatColor.RED + "Usage: " + this.usageMessage);
         return false;
      }
   }

   private double getCoordinate(CommandSender sender, double current, String input) {
      return this.getCoordinate(sender, current, input, -30000000, 30000000);
   }

   private double getCoordinate(CommandSender sender, double current, String input, int min, int max) {
      boolean relative = input.startsWith("~");
      double result = relative ? current : 0.0;
      if (!relative || input.length() > 1) {
         boolean exact = input.contains(".");
         if (relative) {
            input = input.substring(1);
         }

         double testResult = getDouble(sender, input);
         if (testResult == -3.0000001E7) {
            return -3.0000001E7;
         }

         result += testResult;
         if (!exact && !relative) {
            result += 0.5;
         }
      }

      if (min != 0 || max != 0) {
         if (result < min) {
            result = -3.0000001E7;
         }

         if (result > max) {
            result = -3.0000001E7;
         }
      }

      return result;
   }

   @Override
   public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
      Validate.notNull(sender, "Sender cannot be null");
      Validate.notNull(args, "Arguments cannot be null");
      Validate.notNull(alias, "Alias cannot be null");
      return (List<String>)(args.length != 1 && args.length != 2 ? ImmutableList.of() : super.tabComplete(sender, alias, args));
   }
}
