package org.bukkit.craftbukkit.v1_8_R3.entity;

import net.minecraft.server.v1_8_R3.EntitySheep;
import net.minecraft.server.v1_8_R3.EnumColor;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Sheep;

public class CraftSheep extends CraftAnimals implements Sheep {
   public CraftSheep(CraftServer server, EntitySheep entity) {
      super(server, entity);
   }

   @Override
   public org.bukkit.DyeColor getColor() {
      return org.bukkit.DyeColor.getByWoolData((byte)this.getHandle().getColor().getColorIndex());
   }

   @Override
   public void setColor(org.bukkit.DyeColor color) {
      this.getHandle().setColor(EnumColor.fromColorIndex(color.getWoolData()));
   }

   @Override
   public boolean isSheared() {
      return this.getHandle().isSheared();
   }

   @Override
   public void setSheared(boolean flag) {
      this.getHandle().setSheared(flag);
   }

   public EntitySheep getHandle() {
      return (EntitySheep)this.entity;
   }

   @Override
   public String toString() {
      return "CraftSheep";
   }

   @Override
   public EntityType getType() {
      return EntityType.SHEEP;
   }
}
