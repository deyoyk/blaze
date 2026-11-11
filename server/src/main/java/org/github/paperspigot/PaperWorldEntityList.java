package org.github.paperspigot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.EnumCreatureType;
import net.minecraft.server.v1_8_R3.IAnimal;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.World;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class PaperWorldEntityList extends ArrayList<Entity> {
   private final WorldServer world;
   private final int[] entityCounts = new int[EnumCreatureType.values().length];

   public PaperWorldEntityList(World world) {
      this.world = (WorldServer)world;
   }

   @Override
   public boolean addAll(Collection<? extends Entity> c) {
      for (Entity e : c) {
         this.updateEntityCount(e, 1);
      }

      return super.addAll(c);
   }

   @Override
   public boolean removeAll(Collection<?> c) {
      for (Object e : c) {
         if (e instanceof Entity && ((Entity)e).getWorld() == this.world) {
            this.updateEntityCount((Entity)e, -1);
         }
      }

      return super.removeAll(c);
   }

   public boolean add(Entity e) {
      this.updateEntityCount(e, 1);
      return super.add(e);
   }

   public Entity remove(int index) {
      this.guard();
      Entity entity = (Entity)super.remove(index);
      if (entity != null) {
         this.updateEntityCount(entity, -1);
      }

      return entity;
   }

   @Override
   public boolean remove(Object o) {
      this.guard();
      if (super.remove(o)) {
         this.updateEntityCount((Entity)o, -1);
         return true;
      } else {
         return false;
      }
   }

   private void guard() {
      if (this.world.guardEntityList) {
         throw new ConcurrentModificationException();
      }
   }

   public int getCreatureCount(EnumCreatureType type) {
      return this.entityCounts[type.ordinal()];
   }

   private void updateEntityCount(EnumCreatureType type, int amt) {
      int count = this.entityCounts[type.ordinal()];
      count += amt;
      if (count < 0) {
         MinecraftServer.LOGGER.error("Paper - Entity count cache has gone negative");
         count = 0;
      }

      this.entityCounts[type.ordinal()] = count;
   }

   public void updateEntityCount(Entity entity, int amt) {
      if (entity instanceof IAnimal
         && (this.world.paperSpigotConfig.countAllMobsForSpawning || entity.spawnReason == SpawnReason.NATURAL || entity.spawnReason == SpawnReason.CHUNK_GEN)) {
         if (entity instanceof EntityInsentient) {
            EntityInsentient entityinsentient = (EntityInsentient)entity;
            if (amt > 0 && entityinsentient.isTypeNotPersistent() && entityinsentient.isPersistent()) {
               return;
            }
         }

         if (amt < 0) {
            if (!entity.hasBeenCounted) {
               return;
            }

            entity.hasBeenCounted = false;
         } else {
            if (entity.hasBeenCounted) {
               return;
            }

            entity.hasBeenCounted = true;
         }

         EnumCreatureType[] var6;
         for (EnumCreatureType type : var6 = EnumCreatureType.values()) {
            if (type.matches(entity)) {
               this.updateEntityCount(type, amt);
               break;
            }
         }
      }
   }
}
