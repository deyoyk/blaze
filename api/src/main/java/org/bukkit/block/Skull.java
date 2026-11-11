package org.bukkit.block;

public interface Skull extends BlockState {
   boolean hasOwner();

   String getOwner();

   boolean setOwner(String var1);

   BlockFace getRotation();

   void setRotation(BlockFace var1);

   org.bukkit.SkullType getSkullType();

   void setSkullType(org.bukkit.SkullType var1);
}
