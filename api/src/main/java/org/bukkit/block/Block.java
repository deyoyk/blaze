package org.bukkit.block;

import java.util.Collection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.Metadatable;

public interface Block extends Metadatable {
   int getDurability();

   void setDurability(int var1);

   @Deprecated
   byte getData();

   Block getRelative(int var1, int var2, int var3);

   Block getRelative(BlockFace var1);

   Block getRelative(BlockFace var1, int var2);

   org.bukkit.Material getType();

   @Deprecated
   int getTypeId();

   byte getLightLevel();

   byte getLightFromSky();

   byte getLightFromBlocks();

   org.bukkit.World getWorld();

   int getX();

   int getY();

   int getZ();

   org.bukkit.Location getLocation();

   org.bukkit.Location getLocation(org.bukkit.Location var1);

   org.bukkit.Chunk getChunk();

   @Deprecated
   void setData(byte var1);

   @Deprecated
   void setData(byte var1, boolean var2);

   void setType(org.bukkit.Material var1);

   void setType(org.bukkit.Material var1, boolean var2);

   @Deprecated
   boolean setTypeId(int var1);

   @Deprecated
   boolean setTypeId(int var1, boolean var2);

   @Deprecated
   boolean setTypeIdAndData(int var1, byte var2, boolean var3);

   BlockFace getFace(Block var1);

   BlockState getState();

   Biome getBiome();

   void setBiome(Biome var1);

   boolean isBlockPowered();

   boolean isBlockIndirectlyPowered();

   boolean isBlockFacePowered(BlockFace var1);

   boolean isBlockFaceIndirectlyPowered(BlockFace var1);

   int getBlockPower(BlockFace var1);

   int getBlockPower();

   boolean isEmpty();

   boolean isLiquid();

   double getTemperature();

   double getHumidity();

   PistonMoveReaction getPistonMoveReaction();

   boolean breakNaturally();

   boolean breakNaturally(ItemStack var1);

   Collection<ItemStack> getDrops();

   Collection<ItemStack> getDrops(ItemStack var1);
}
