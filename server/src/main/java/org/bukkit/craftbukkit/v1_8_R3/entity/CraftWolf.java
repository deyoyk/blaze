package org.bukkit.craftbukkit.v1_8_R3.entity;

import net.minecraft.server.v1_8_R3.EntityWolf;
import net.minecraft.server.v1_8_R3.EnumColor;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Wolf;

public class CraftWolf extends CraftTameableAnimal implements Wolf {
   public CraftWolf(CraftServer server, EntityWolf wolf) {
      super(server, wolf);
   }

   @Override
   public boolean isAngry() {
      return this.getHandle().isAngry();
   }

   @Override
   public void setAngry(boolean angry) {
      this.getHandle().setAngry(angry);
   }

   public EntityWolf getHandle() {
      return (EntityWolf)this.entity;
   }

   @Override
   public EntityType getType() {
      return EntityType.WOLF;
   }

   @Override
   public org.bukkit.DyeColor getCollarColor() {
      return org.bukkit.DyeColor.getByWoolData((byte)this.getHandle().getCollarColor().getColorIndex());
   }

   @Override
   public void setCollarColor(org.bukkit.DyeColor color) {
      this.getHandle().setCollarColor(EnumColor.fromColorIndex(color.getWoolData()));
   }
}
