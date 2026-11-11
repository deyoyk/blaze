package org.bukkit.craftbukkit.v1_8_R3.entity;

import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntityTNTPrimed;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;

public class CraftTNTPrimed extends CraftEntity implements TNTPrimed {
   public CraftTNTPrimed(CraftServer server, EntityTNTPrimed entity) {
      super(server, entity);
   }

   @Override
   public float getYield() {
      return this.getHandle().yield;
   }

   @Override
   public boolean isIncendiary() {
      return this.getHandle().isIncendiary;
   }

   @Override
   public void setIsIncendiary(boolean isIncendiary) {
      this.getHandle().isIncendiary = isIncendiary;
   }

   @Override
   public void setYield(float yield) {
      this.getHandle().yield = yield;
   }

   @Override
   public int getFuseTicks() {
      return this.getHandle().fuseTicks;
   }

   @Override
   public void setFuseTicks(int fuseTicks) {
      this.getHandle().fuseTicks = fuseTicks;
   }

   public EntityTNTPrimed getHandle() {
      return (EntityTNTPrimed)this.entity;
   }

   @Override
   public String toString() {
      return "CraftTNTPrimed";
   }

   @Override
   public EntityType getType() {
      return EntityType.PRIMED_TNT;
   }

   @Override
   public Entity getSource() {
      EntityLiving source = this.getHandle().getSource();
      if (source != null) {
         Entity bukkitEntity = source.getBukkitEntity();
         if (bukkitEntity.isValid()) {
            return bukkitEntity;
         }
      }

      return null;
   }

   @Override
   public org.bukkit.Location getSourceLoc() {
      return this.getHandle().sourceLoc;
   }
}
