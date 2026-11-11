package org.bukkit.craftbukkit.v1_8_R3.entity;

import com.eatthepath.uuid.FastUUID;
import java.util.UUID;
import net.minecraft.server.v1_8_R3.EntityTameableAnimal;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Tameable;

public class CraftTameableAnimal extends CraftAnimals implements Tameable, Creature {
   public CraftTameableAnimal(CraftServer server, EntityTameableAnimal entity) {
      super(server, entity);
   }

   public EntityTameableAnimal getHandle() {
      return (EntityTameableAnimal)super.getHandle();
   }

   public UUID getOwnerUUID() {
      try {
         return FastUUID.parseUUID(this.getHandle().getOwnerUUID());
      } catch (IllegalArgumentException var2) {
         return null;
      }
   }

   public void setOwnerUUID(UUID uuid) {
      if (uuid == null) {
         this.getHandle().setOwnerUUID("");
      } else {
         this.getHandle().setOwnerUUID(uuid.toString());
      }
   }

   @Override
   public AnimalTamer getOwner() {
      if (this.getOwnerUUID() == null) {
         return null;
      } else {
         AnimalTamer owner = this.getServer().getPlayer(this.getOwnerUUID());
         if (owner == null) {
            owner = this.getServer().getOfflinePlayer(this.getOwnerUUID());
         }

         return owner;
      }
   }

   @Override
   public boolean isTamed() {
      return this.getHandle().isTamed();
   }

   @Override
   public void setOwner(AnimalTamer tamer) {
      if (tamer != null) {
         this.setTamed(true);
         this.getHandle().setGoalTarget(null, null, false);
         this.setOwnerUUID(tamer.getUniqueId());
      } else {
         this.setTamed(false);
         this.setOwnerUUID(null);
      }
   }

   @Override
   public void setTamed(boolean tame) {
      this.getHandle().setTamed(tame);
      if (!tame) {
         this.setOwnerUUID(null);
      }
   }

   public boolean isSitting() {
      return this.getHandle().isSitting();
   }

   public void setSitting(boolean sitting) {
      this.getHandle().getGoalSit().setSitting(sitting);
   }

   @Override
   public String toString() {
      return this.getClass().getSimpleName() + "{owner=" + this.getOwner() + ",tamed=" + this.isTamed() + "}";
   }
}
