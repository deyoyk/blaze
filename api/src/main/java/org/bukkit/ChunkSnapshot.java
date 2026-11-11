package org.bukkit;

import org.bukkit.block.Biome;

public interface ChunkSnapshot {
   int getX();

   int getZ();

   String getWorldName();

   @Deprecated
   int getBlockTypeId(int var1, int var2, int var3);

   @Deprecated
   int getBlockData(int var1, int var2, int var3);

   int getBlockSkyLight(int var1, int var2, int var3);

   int getBlockEmittedLight(int var1, int var2, int var3);

   int getHighestBlockYAt(int var1, int var2);

   Biome getBiome(int var1, int var2);

   double getRawBiomeTemperature(int var1, int var2);

   double getRawBiomeRainfall(int var1, int var2);

   long getCaptureFullTime();

   boolean isSectionEmpty(int var1);
}
