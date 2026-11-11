package net.minecraft.server.v1_8_R3;

import java.util.List;

public interface IChunkProvider {
   boolean isChunkLoaded(int var1, int var2);

   Chunk getOrCreateChunk(int var1, int var2);

   default Chunk getOrCreateChunk(long hash, int i, int j) {
      return this.getOrCreateChunk(i, j);
   }

   Chunk getChunkAt(BlockPosition var1);

   void getChunkAt(IChunkProvider var1, int var2, int var3);

   boolean a(IChunkProvider var1, Chunk var2, int var3, int var4);

   boolean saveChunks(boolean var1, IProgressUpdate var2);

   boolean unloadChunks();

   boolean canSave();

   String getName();

   List<BiomeBase.BiomeMeta> getMobsFor(EnumCreatureType var1, BlockPosition var2);

   BlockPosition findNearestMapFeature(World var1, String var2, BlockPosition var3);

   int getLoadedChunks();

   void recreateStructures(Chunk var1, int var2, int var3);

   void c();
}
