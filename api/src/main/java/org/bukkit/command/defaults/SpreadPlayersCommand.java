package org.bukkit.command.defaults;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

@Deprecated
public class SpreadPlayersCommand extends VanillaCommand {
   private static final Random random = new Random();

   public SpreadPlayersCommand() {
      super("spreadplayers");
      this.description = "Spreads players around a point";
      this.usageMessage = "/spreadplayers <x> <z> <spreadDistance> <maxRange> <respectTeams true|false> <player ...>";
      this.setPermission("bukkit.command.spreadplayers");
   }

   @Override
   public boolean execute(CommandSender sender, String commandLabel, String[] args) {
      if (!this.testPermission(sender)) {
         return true;
      } else if (args.length < 6) {
         sender.sendMessage(org.bukkit.ChatColor.RED + "Usage: " + this.usageMessage);
         return false;
      } else {
         double x = getDouble(sender, args[0], -3.0E7, 3.0E7);
         double z = getDouble(sender, args[1], -3.0E7, 3.0E7);
         double distance = getDouble(sender, args[2]);
         double range = getDouble(sender, args[3]);
         if (distance < 0.0) {
            sender.sendMessage(org.bukkit.ChatColor.RED + "Distance is too small.");
            return false;
         } else if (range < distance + 1.0) {
            sender.sendMessage(org.bukkit.ChatColor.RED + "Max range is too small.");
            return false;
         } else {
            String respectTeams = args[4];
            boolean teams = false;
            if (respectTeams.equalsIgnoreCase("true")) {
               teams = true;
            } else if (!respectTeams.equalsIgnoreCase("false")) {
               sender.sendMessage(String.format(org.bukkit.ChatColor.RED + "'%s' is not true or false", args[4]));
               return false;
            }

            List<Player> players = Lists.newArrayList();
            org.bukkit.World world = null;

            for (int i = 5; i < args.length; i++) {
               Player player = org.bukkit.Bukkit.getPlayerExact(args[i]);
               if (player != null) {
                  if (world == null) {
                     world = player.getWorld();
                  }

                  players.add(player);
               }
            }

            if (world == null) {
               return true;
            } else {
               double xRangeMin = x - range;
               double zRangeMin = z - range;
               double xRangeMax = x + range;
               double zRangeMax = z + range;
               int spreadSize = teams ? this.getTeams(players) : players.size();
               org.bukkit.Location[] locations = this.getSpreadLocations(world, spreadSize, xRangeMin, zRangeMin, xRangeMax, zRangeMax);
               int rangeSpread = this.range(world, distance, xRangeMin, zRangeMin, xRangeMax, zRangeMax, locations);
               if (rangeSpread == -1) {
                  sender.sendMessage(
                     String.format(
                        "Could not spread %d %s around %s,%s (too many players for space - try using spread of at most %s)",
                        spreadSize,
                        teams ? "teams" : "players",
                        x,
                        z
                     )
                  );
                  return false;
               } else {
                  double distanceSpread = this.spread(world, players, locations, teams);
                  sender.sendMessage(String.format("Succesfully spread %d %s around %s,%s", locations.length, teams ? "teams" : "players", x, z));
                  if (locations.length > 1) {
                     sender.sendMessage(
                        String.format(
                           "(Average distance between %s is %s blocks apart after %s iterations)",
                           teams ? "teams" : "players",
                           String.format("%.2f", distanceSpread),
                           rangeSpread
                        )
                     );
                  }

                  return true;
               }
            }
         }
      }
   }

   private int range(
      org.bukkit.World world, double distance, double xRangeMin, double zRangeMin, double xRangeMax, double zRangeMax, org.bukkit.Location[] locations
   ) {
      boolean flag = true;

      int i;
      for (i = 0; i < 10000 && flag; i++) {
         flag = false;
         double max = Float.MAX_VALUE;

         for (int k = 0; k < locations.length; k++) {
            org.bukkit.Location loc2 = locations[k];
            int j = 0;
            org.bukkit.Location loc1 = new org.bukkit.Location(world, 0.0, 0.0, 0.0);

            for (int l = 0; l < locations.length; l++) {
               if (k != l) {
                  org.bukkit.Location loc3 = locations[l];
                  double dis = loc2.distanceSquared(loc3);
                  max = Math.min(dis, max);
                  if (dis < distance) {
                     j++;
                     loc1.add(loc3.getX() - loc2.getX(), 0.0, 0.0);
                     loc1.add(loc3.getZ() - loc2.getZ(), 0.0, 0.0);
                  }
               }
            }

            if (j > 0) {
               loc2.setX(loc2.getX() / j);
               loc2.setZ(loc2.getZ() / j);
               double d7 = Math.sqrt(loc1.getX() * loc1.getX() + loc1.getZ() * loc1.getZ());
               if (d7 > 0.0) {
                  loc1.setX(loc1.getX() / d7);
                  loc2.add(-loc1.getX(), 0.0, -loc1.getZ());
               } else {
                  double x = xRangeMin >= xRangeMax ? xRangeMin : random.nextDouble() * (xRangeMax - xRangeMin) + xRangeMin;
                  double z = zRangeMin >= zRangeMax ? zRangeMin : random.nextDouble() * (zRangeMax - zRangeMin) + zRangeMin;
                  loc2.setX(x);
                  loc2.setZ(z);
               }

               flag = true;
            }

            boolean swap = false;
            if (loc2.getX() < xRangeMin) {
               loc2.setX(xRangeMin);
               swap = true;
            } else if (loc2.getX() > xRangeMax) {
               loc2.setX(xRangeMax);
               swap = true;
            }

            if (loc2.getZ() < zRangeMin) {
               loc2.setZ(zRangeMin);
               swap = true;
            } else if (loc2.getZ() > zRangeMax) {
               loc2.setZ(zRangeMax);
               swap = true;
            }

            if (swap) {
               flag = true;
            }
         }

         if (!flag) {
            for (org.bukkit.Location loc1 : locations) {
               if (world.getHighestBlockYAt(loc1) == 0) {
                  double x = xRangeMin >= xRangeMax ? xRangeMin : random.nextDouble() * (xRangeMax - xRangeMin) + xRangeMin;
                  double z = zRangeMin >= zRangeMax ? zRangeMin : random.nextDouble() * (zRangeMax - zRangeMin) + zRangeMin;
                  locations[i] = new org.bukkit.Location(world, x, 0.0, z);
                  loc1.setX(x);
                  loc1.setZ(z);
                  flag = true;
               }
            }
         }
      }

      return i >= 10000 ? -1 : i;
   }

   private double spread(org.bukkit.World world, List<Player> list, org.bukkit.Location[] locations, boolean teams) {
      double distance = 0.0;
      int i = 0;
      Map<Team, org.bukkit.Location> hashmap = Maps.newHashMap();

      for (int j = 0; j < list.size(); j++) {
         Player player = list.get(j);
         org.bukkit.Location location;
         if (teams) {
            Team team = player.getScoreboard().getPlayerTeam(player);
            if (!hashmap.containsKey(team)) {
               hashmap.put(team, locations[i++]);
            }

            location = hashmap.get(team);
         } else {
            location = locations[i++];
         }

         player.teleport(
            new org.bukkit.Location(
               world,
               Math.floor(location.getX()) + 0.5,
               world.getHighestBlockYAt((int)location.getX(), (int)location.getZ()),
               Math.floor(location.getZ()) + 0.5
            )
         );
         double value = Double.MAX_VALUE;

         for (int k = 0; k < locations.length; k++) {
            if (location != locations[k]) {
               double d = location.distanceSquared(locations[k]);
               value = Math.min(d, value);
            }
         }

         distance += value;
      }

      return distance / list.size();
   }

   private int getTeams(List<Player> players) {
      Set<Team> teams = Sets.newHashSet();

      for (Player player : players) {
         teams.add(player.getScoreboard().getPlayerTeam(player));
      }

      return teams.size();
   }

   private org.bukkit.Location[] getSpreadLocations(org.bukkit.World world, int size, double xRangeMin, double zRangeMin, double xRangeMax, double zRangeMax) {
      org.bukkit.Location[] locations = new org.bukkit.Location[size];

      for (int i = 0; i < size; i++) {
         double x = xRangeMin >= xRangeMax ? xRangeMin : random.nextDouble() * (xRangeMax - xRangeMin) + xRangeMin;
         double z = zRangeMin >= zRangeMax ? zRangeMin : random.nextDouble() * (zRangeMax - zRangeMin) + zRangeMin;
         locations[i] = new org.bukkit.Location(world, x, 0.0, z);
      }

      return locations;
   }
}
