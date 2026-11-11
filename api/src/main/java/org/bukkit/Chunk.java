package org.bukkit;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;

public interface Chunk {
   int getX();

   int getZ();

   int getEntityCount();

   org.bukkit.World getWorld();

   Block getBlock(int var1, int var2, int var3);

   org.bukkit.ChunkSnapshot getChunkSnapshot();

   org.bukkit.ChunkSnapshot getChunkSnapshot(boolean var1, boolean var2, boolean var3);

   Entity[] getEntities();

   BlockState[] getTileEntities();

   boolean isLoaded();

   boolean load(boolean var1);

   boolean load();

   boolean unload(boolean var1, boolean var2);

   boolean unload(boolean var1);

   boolean unload();

   lol.deyo.blaze.api.chunk.ChunkSnapshot takeSnapshot();

   void restoreSnapshot(lol.deyo.blaze.api.chunk.ChunkSnapshot var1);
}
