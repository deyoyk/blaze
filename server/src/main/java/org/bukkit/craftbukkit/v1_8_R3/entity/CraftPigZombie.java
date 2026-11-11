package org.bukkit.craftbukkit.v1_8_R3.entity;

import net.minecraft.server.v1_8_R3.EntityPigZombie;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.PigZombie;

public class CraftPigZombie extends CraftZombie implements PigZombie {
   public CraftPigZombie(CraftServer server, EntityPigZombie entity) {
      super(server, entity);
   }

   @Override
   public int getAnger() {
      return this.getHandle().angerLevel;
   }

   @Override
   public void setAnger(int level) {
      this.getHandle().angerLevel = level;
   }

   @Override
   public void setAngry(boolean angry) {
      this.setAnger(angry ? 400 : 0);
   }

   @Override
   public boolean isAngry() {
      return this.getAnger() > 0;
   }

   public EntityPigZombie getHandle() {
      return (EntityPigZombie)this.entity;
   }

   @Override
   public String toString() {
      return "CraftPigZombie";
   }

   @Override
   public EntityType getType() {
      return EntityType.PIG_ZOMBIE;
   }
}
