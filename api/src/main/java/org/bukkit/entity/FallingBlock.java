package org.bukkit.entity;

public interface FallingBlock extends Entity {
   org.bukkit.Material getMaterial();

   @Deprecated
   int getBlockId();

   @Deprecated
   byte getBlockData();

   boolean getDropItem();

   void setDropItem(boolean var1);

   boolean canHurtEntities();

   void setHurtEntities(boolean var1);

   org.bukkit.Location getSourceLoc();
}
