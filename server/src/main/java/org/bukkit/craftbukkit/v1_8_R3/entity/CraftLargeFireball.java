package org.bukkit.craftbukkit.v1_8_R3.entity;

import net.minecraft.server.v1_8_R3.EntityLargeFireball;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LargeFireball;

public class CraftLargeFireball extends CraftFireball implements LargeFireball {
   public CraftLargeFireball(CraftServer server, EntityLargeFireball entity) {
      super(server, entity);
   }

   @Override
   public void setYield(float yield) {
      super.setYield(yield);
      this.getHandle().yield = (int)yield;
   }

   public EntityLargeFireball getHandle() {
      return (EntityLargeFireball)this.entity;
   }

   @Override
   public String toString() {
      return "CraftLargeFireball";
   }

   @Override
   public EntityType getType() {
      return EntityType.FIREBALL;
   }
}
