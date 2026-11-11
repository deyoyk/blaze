package org.bukkit.craftbukkit.v1_8_R3.entity;

import net.minecraft.server.v1_8_R3.EntityBat;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.entity.Bat;
import org.bukkit.entity.EntityType;

public class CraftBat extends CraftAmbient implements Bat {
   public CraftBat(CraftServer server, EntityBat entity) {
      super(server, entity);
   }

   public EntityBat getHandle() {
      return (EntityBat)this.entity;
   }

   @Override
   public String toString() {
      return "CraftBat";
   }

   @Override
   public EntityType getType() {
      return EntityType.BAT;
   }

   @Override
   public boolean isAwake() {
      return !this.getHandle().isAsleep();
   }

   @Override
   public void setAwake(boolean state) {
      this.getHandle().setAsleep(!state);
   }
}
