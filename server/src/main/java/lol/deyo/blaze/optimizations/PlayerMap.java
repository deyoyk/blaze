package lol.deyo.blaze.optimizations;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.MathHelper;
import net.minecraft.server.v1_8_R3.Packet;

public class PlayerMap {
   private static final int CHUNK_BITS = 5;
   private final Long2ObjectOpenHashMap<List<EntityPlayer>> map = new Long2ObjectOpenHashMap();

   private static long xzToKey(long x, long z) {
      return (x << 32) + z - -2147483648L;
   }

   public void add(EntityPlayer player) {
      int x = MathHelper.floor(player.locX) >> 5;
      int z = MathHelper.floor(player.locZ) >> 5;
      long key = xzToKey(x, z);
      List<EntityPlayer> list = (List<EntityPlayer>)this.map.get(key);
      if (list == null) {
         list = new ObjectMapList<EntityPlayer>();
         this.map.put(key, list);
      }

      list.add(player);
      player.playerMapX = x;
      player.playerMapZ = z;
   }

   public void move(EntityPlayer player) {
      int x = MathHelper.floor(player.locX) >> 5;
      int z = MathHelper.floor(player.locZ) >> 5;
      if (x != player.playerMapX || z != player.playerMapZ) {
         long key = xzToKey(player.playerMapX, player.playerMapZ);
         List<EntityPlayer> list = (List<EntityPlayer>)this.map.get(key);
         list.remove(player);
         if (list.isEmpty()) {
            this.map.remove(key);
         }

         key = xzToKey(x, z);
         list = (List<EntityPlayer>)this.map.get(key);
         if (list == null) {
            list = new ObjectMapList<EntityPlayer>();
            this.map.put(key, list);
         }

         list.add(player);
         player.playerMapX = x;
         player.playerMapZ = z;
      }
   }

   public void remove(EntityPlayer player) {
      long key = xzToKey(player.playerMapX, player.playerMapZ);
      List<EntityPlayer> list = (List<EntityPlayer>)this.map.get(key);
      if (list != null) {
         list.remove(player);
         if (list.isEmpty()) {
            this.map.remove(key);
         }
      }
   }

   public void forEachNearby(double x, double y, double z, double distance, boolean useRadius, Consumer<EntityPlayer> function) {
      for (int chunkX = MathHelper.floor(x - distance) >> 5; chunkX <= MathHelper.floor(x + distance) >> 5; chunkX++) {
         for (int chunkZ = MathHelper.floor(z - distance) >> 5; chunkZ <= MathHelper.floor(z + distance) >> 5; chunkZ++) {
            List<EntityPlayer> players = (List<EntityPlayer>)this.map.get(xzToKey(chunkX, chunkZ));
            if (players != null) {
               for (EntityPlayer player : players) {
                  if (!useRadius || player.e(x, y, z) < distance * distance) {
                     function.accept(player);
                  }
               }
            }
         }
      }
   }

   public EntityPlayer getNearestPlayer(double x, double y, double z, double distance) {
      double bestDistanceSqrd = -1.0;
      EntityPlayer bestPlayer = null;

      for (int chunkX = MathHelper.floor(x - distance) >> 5; chunkX <= MathHelper.floor(x + distance) >> 5; chunkX++) {
         for (int chunkZ = MathHelper.floor(z - distance) >> 5; chunkZ <= MathHelper.floor(z + distance) >> 5; chunkZ++) {
            List<EntityPlayer> players = (List<EntityPlayer>)this.map.get(xzToKey(chunkX, chunkZ));
            if (players != null) {
               for (EntityPlayer player : players) {
                  double playerDistSqrd = player.e(x, y, z);
                  if (playerDistSqrd < distance * distance && (bestDistanceSqrd == -1.0 || playerDistSqrd < bestDistanceSqrd)) {
                     bestDistanceSqrd = playerDistSqrd;
                     bestPlayer = player;
                  }
               }
            }
         }
      }

      return bestPlayer;
   }

   public boolean isPlayerNearby(double x, double y, double z, double distance, boolean respectSpawningApi) {
      for (int chunkX = MathHelper.floor(x - distance) >> 5; chunkX <= MathHelper.floor(x + distance) >> 5; chunkX++) {
         for (int chunkZ = MathHelper.floor(z - distance) >> 5; chunkZ <= MathHelper.floor(z + distance) >> 5; chunkZ++) {
            List<EntityPlayer> players = (List<EntityPlayer>)this.map.get(xzToKey(chunkX, chunkZ));
            if (players != null) {
               for (EntityPlayer player : players) {
                  if (player != null && !player.dead && (!respectSpawningApi || player.affectsSpawning)) {
                     double playerDistSqrd = player.e(x, y, z);
                     if (playerDistSqrd < distance * distance) {
                        return true;
                     }
                  }
               }
            }
         }
      }

      return false;
   }

   public EntityPlayer getNearbyPlayer(double x, double y, double z, double distance, boolean respectSpawningApi) {
      return this.getNearbyPlayer(x, y, z, distance, respectSpawningApi, true);
   }

   public EntityPlayer getNearbyPlayer(double x, double y, double z, double distance, boolean respectSpawningApi, boolean best) {
      double bestDistanceSqrd = -1.0;
      EntityPlayer bestPlayer = null;

      for (int chunkX = MathHelper.floor(x - distance) >> 5; chunkX <= MathHelper.floor(x + distance) >> 5; chunkX++) {
         for (int chunkZ = MathHelper.floor(z - distance) >> 5; chunkZ <= MathHelper.floor(z + distance) >> 5; chunkZ++) {
            List<EntityPlayer> players = (List<EntityPlayer>)this.map.get(xzToKey(chunkX, chunkZ));
            if (players != null) {
               for (EntityPlayer player : players) {
                  if (player != null && !player.dead && (!respectSpawningApi || player.affectsSpawning)) {
                     double playerDistSqrd = player.e(x, y, z);
                     if (playerDistSqrd < distance * distance && (bestDistanceSqrd == -1.0 || playerDistSqrd < bestDistanceSqrd)) {
                        bestDistanceSqrd = playerDistSqrd;
                        if (!best) {
                           return player;
                        }

                        bestPlayer = player;
                     }
                  }
               }
            }
         }
      }

      return bestPlayer;
   }

   public List<EntityPlayer> getNearbyPlayersIgnoreHeight(double x, double z, double distance) {
      List<EntityPlayer> toReturn = null;

      for (int chunkX = MathHelper.floor(x - distance) >> 5; chunkX <= MathHelper.floor(x + distance) >> 5; chunkX++) {
         for (int chunkZ = MathHelper.floor(z - distance) >> 5; chunkZ <= MathHelper.floor(z + distance) >> 5; chunkZ++) {
            List<EntityPlayer> players = (List<EntityPlayer>)this.map.get(xzToKey(chunkX, chunkZ));
            if (players != null) {
               for (EntityPlayer player : players) {
                  if (player != null && !player.dead && Math.abs(player.locX - x) <= distance && Math.abs(player.locZ - z) <= distance) {
                     if (toReturn == null) {
                        toReturn = new ObjectMapList<EntityPlayer>();
                     }

                     toReturn.add(player);
                  }
               }
            }
         }
      }

      return toReturn == null ? Collections.emptyList() : toReturn;
   }

   public EntityPlayer getNearestAttackablePlayer(double x, double y, double z, double maxXZ, double maxY, Function<EntityHuman, Double> visibility) {
      return this.getNearestAttackablePlayer(x, y, z, maxXZ, maxY, visibility, null);
   }

   public EntityPlayer getNearestAttackablePlayer(
      double x, double y, double z, double maxXZ, double maxY, Function<EntityHuman, Double> visibility, Predicate<EntityHuman> condition
   ) {
      double bestDistanceSqrd = -1.0;
      EntityPlayer bestPlayer = null;

      for (int chunkX = MathHelper.floor(x - maxXZ) >> 5; chunkX <= MathHelper.floor(x + maxXZ) >> 5; chunkX++) {
         for (int chunkZ = MathHelper.floor(z - maxXZ) >> 5; chunkZ <= MathHelper.floor(z + maxXZ) >> 5; chunkZ++) {
            List<EntityPlayer> players = (List<EntityPlayer>)this.map.get(xzToKey(chunkX, chunkZ));
            if (players != null) {
               for (EntityPlayer player : players) {
                  if (!player.abilities.isInvulnerable && player.isAlive() && (condition == null || condition.apply(player))) {
                     double dx = player.locX - x;
                     double dz = player.locZ - z;
                     double playerDistSqrd = dx * dx + dz * dz;
                     double dy = Math.abs(player.locY - y);
                     double distForPlayer = maxXZ;
                     if (player.isSneaking()) {
                        distForPlayer = maxXZ * 0.8;
                     }

                     if (visibility != null) {
                        Double v = (Double)visibility.apply(player);
                        if (v != null) {
                           distForPlayer *= v;
                        }
                     }

                     if ((maxY < 0.0 || dy < maxY * maxY)
                        && playerDistSqrd < distForPlayer * distForPlayer
                        && (bestDistanceSqrd == -1.0 || playerDistSqrd < bestDistanceSqrd)) {
                        bestDistanceSqrd = playerDistSqrd;
                        bestPlayer = player;
                     }
                  }
               }
            }
         }
      }

      return bestPlayer;
   }

   public void sendPacketNearby(EntityPlayer source, double x, double y, double z, double distance, Packet<?> packet) {
      this.sendPacketNearby(source, x, y, z, distance, packet, false);
   }

   public void sendPacketNearby(EntityPlayer source, double x, double y, double z, double distance, Packet<?> packet, boolean self) {
      for (int chunkX = MathHelper.floor(x - distance) >> 5; chunkX <= MathHelper.floor(x + distance) >> 5; chunkX++) {
         for (int chunkZ = MathHelper.floor(z - distance) >> 5; chunkZ <= MathHelper.floor(z + distance) >> 5; chunkZ++) {
            List<EntityPlayer> players = (List<EntityPlayer>)this.map.get(xzToKey(chunkX, chunkZ));
            if (players != null) {
               for (EntityPlayer player : players) {
                  if ((self || player != source) && (source == null || player.getBukkitEntity().canSee(source.getBukkitEntity()))) {
                     double playerDistSqrd = player.e(x, y, z);
                     if (playerDistSqrd < distance * distance) {
                        player.playerConnection.sendPacket(packet);
                     }
                  }
               }
            }
         }
      }
   }
}
