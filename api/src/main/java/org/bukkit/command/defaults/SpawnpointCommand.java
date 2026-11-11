package org.bukkit.command.defaults;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.apache.commons.lang.Validate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Deprecated
public class SpawnpointCommand extends VanillaCommand {
   public SpawnpointCommand() {
      super("spawnpoint");
      this.description = "Sets a player's spawn point";
      this.usageMessage = "/spawnpoint OR /spawnpoint <player> OR /spawnpoint <player> <x> <y> <z>";
      this.setPermission("bukkit.command.spawnpoint");
   }

   @Override
   public boolean execute(CommandSender sender, String currentAlias, String[] args) {
      if (!this.testPermission(sender)) {
         return true;
      } else {
         Player player;
         if (args.length == 0) {
            if (!(sender instanceof Player)) {
               sender.sendMessage("Please provide a player!");
               return true;
            }

            player = (Player)sender;
         } else {
            player = org.bukkit.Bukkit.getPlayerExact(args[0]);
            if (player == null) {
               sender.sendMessage("Can't find player " + args[0]);
               return true;
            }
         }

         org.bukkit.World world = player.getWorld();
         if (args.length == 4) {
            if (world != null) {
               int pos = 1;

               int x;
               int y;
               int z;
               try {
                  x = this.getInteger(sender, args[pos++], -30000000, 30000000, true);
                  y = this.getInteger(sender, args[pos++], 0, world.getMaxHeight());
                  z = this.getInteger(sender, args[pos], -30000000, 30000000, true);
               } catch (NumberFormatException var11) {
                  sender.sendMessage(var11.getMessage());
                  return true;
               }

               player.setBedSpawnLocation(new org.bukkit.Location(world, x, y, z), true);
               Command.broadcastCommandMessage(sender, "Set " + player.getDisplayName() + "'s spawnpoint to " + x + ", " + y + ", " + z);
            }
         } else {
            if (args.length > 1) {
               sender.sendMessage(org.bukkit.ChatColor.RED + "Usage: " + this.usageMessage);
               return false;
            }

            org.bukkit.Location location = player.getLocation();
            player.setBedSpawnLocation(location, true);
            Command.broadcastCommandMessage(
               sender, "Set " + player.getDisplayName() + "'s spawnpoint to " + location.getX() + ", " + location.getY() + ", " + location.getZ()
            );
         }

         return true;
      }
   }

   @Override
   public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
      Validate.notNull(sender, "Sender cannot be null");
      Validate.notNull(args, "Arguments cannot be null");
      Validate.notNull(alias, "Alias cannot be null");
      return (List<String>)(args.length == 1 ? super.tabComplete(sender, alias, args) : ImmutableList.of());
   }
}
