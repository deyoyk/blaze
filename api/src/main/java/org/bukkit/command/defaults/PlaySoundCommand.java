package org.bukkit.command.defaults;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Deprecated
public class PlaySoundCommand extends VanillaCommand {
   public PlaySoundCommand() {
      super("playsound");
      this.description = "Plays a sound to a given player";
      this.usageMessage = "/playsound <sound> <player> [x] [y] [z] [volume] [pitch] [minimumVolume]";
      this.setPermission("bukkit.command.playsound");
   }

   @Override
   public boolean execute(CommandSender sender, String currentAlias, String[] args) {
      if (!this.testPermission(sender)) {
         return true;
      } else if (args.length < 2) {
         sender.sendMessage(org.bukkit.ChatColor.RED + "Usage: " + this.usageMessage);
         return false;
      } else {
         String soundArg = args[0];
         String playerArg = args[1];
         Player player = org.bukkit.Bukkit.getPlayerExact(playerArg);
         if (player == null) {
            sender.sendMessage(org.bukkit.ChatColor.RED + "Can't find player " + playerArg);
            return false;
         } else {
            org.bukkit.Location location = player.getLocation();
            double x = Math.floor(location.getX());
            double y = Math.floor(location.getY() + 0.5);
            double z = Math.floor(location.getZ());
            double volume = 1.0;
            double pitch = 1.0;
            double minimumVolume = 0.0;
            switch (args.length) {
               case 8:
               default:
                  minimumVolume = getDouble(sender, args[7], 0.0, 1.0);
               case 7:
                  pitch = getDouble(sender, args[6], 0.0, 2.0);
               case 6:
                  volume = getDouble(sender, args[5], 0.0, Float.MAX_VALUE);
               case 5:
                  z = getRelativeDouble(z, sender, args[4]);
               case 4:
                  y = getRelativeDouble(y, sender, args[3]);
               case 3:
                  x = getRelativeDouble(x, sender, args[2]);
               case 2:
                  double fixedVolume = volume > 1.0 ? volume * 16.0 : 16.0;
                  org.bukkit.Location soundLocation = new org.bukkit.Location(player.getWorld(), x, y, z);
                  if (location.distanceSquared(soundLocation) > fixedVolume * fixedVolume) {
                     if (minimumVolume <= 0.0) {
                        sender.sendMessage(org.bukkit.ChatColor.RED + playerArg + " is too far away to hear the sound");
                        return false;
                     }

                     double deltaX = x - location.getX();
                     double deltaY = y - location.getY();
                     double deltaZ = z - location.getZ();
                     double delta = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) / 2.0;
                     if (delta > 0.0) {
                        location.add(deltaX / delta, deltaY / delta, deltaZ / delta);
                     }

                     player.playSound(location, soundArg, (float)minimumVolume, (float)pitch);
                  } else {
                     player.playSound(soundLocation, soundArg, (float)volume, (float)pitch);
                  }

                  sender.sendMessage(String.format("Played '%s' to %s", soundArg, playerArg));
                  return true;
            }
         }
      }
   }
}
