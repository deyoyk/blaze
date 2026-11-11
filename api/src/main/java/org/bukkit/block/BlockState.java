package org.bukkit.block;

import org.bukkit.material.MaterialData;
import org.bukkit.metadata.Metadatable;

public interface BlockState extends Metadatable {
   Block getBlock();

   MaterialData getData();

   org.bukkit.Material getType();

   @Deprecated
   int getTypeId();

   byte getLightLevel();

   org.bukkit.World getWorld();

   int getX();

   int getY();

   int getZ();

   org.bukkit.Location getLocation();

   org.bukkit.Location getLocation(org.bukkit.Location var1);

   org.bukkit.Chunk getChunk();

   void setData(MaterialData var1);

   void setType(org.bukkit.Material var1);

   @Deprecated
   boolean setTypeId(int var1);

   boolean update();

   boolean update(boolean var1);

   boolean update(boolean var1, boolean var2);

   @Deprecated
   byte getRawData();

   @Deprecated
   void setRawData(byte var1);

   boolean isPlaced();
}
