package org.bukkit.craftbukkit.v1_8_R3;

import net.minecraft.server.v1_8_R3.BiomeBase;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBlock;

public class CraftChunkSnapshot implements org.bukkit.ChunkSnapshot {
   private final int x;
   private final int z;
   private final String worldname;
   private final short[][] blockids;
   private final byte[][] blockdata;
   private final byte[][] skylight;
   private final byte[][] emitlight;
   private final boolean[] empty;
   private final int[] hmap;
   private final long captureFulltime;
   private final BiomeBase[] biome;
   private final double[] biomeTemp;
   private final double[] biomeRain;

   CraftChunkSnapshot(
      int x,
      int z,
      String wname,
      long wtime,
      short[][] sectionBlockIDs,
      byte[][] sectionBlockData,
      byte[][] sectionSkyLights,
      byte[][] sectionEmitLights,
      boolean[] sectionEmpty,
      int[] hmap,
      BiomeBase[] biome,
      double[] biomeTemp,
      double[] biomeRain
   ) {
      this.x = x;
      this.z = z;
      this.worldname = wname;
      this.captureFulltime = wtime;
      this.blockids = sectionBlockIDs;
      this.blockdata = sectionBlockData;
      this.skylight = sectionSkyLights;
      this.emitlight = sectionEmitLights;
      this.empty = sectionEmpty;
      this.hmap = hmap;
      this.biome = biome;
      this.biomeTemp = biomeTemp;
      this.biomeRain = biomeRain;
   }

   @Override
   public int getX() {
      return this.x;
   }

   @Override
   public int getZ() {
      return this.z;
   }

   @Override
   public String getWorldName() {
      return this.worldname;
   }

   @Override
   public final int getBlockTypeId(int x, int y, int z) {
      return this.blockids[y >> 4][(y & 15) << 8 | z << 4 | x];
   }

   @Override
   public final int getBlockData(int x, int y, int z) {
      int off = (y & 15) << 7 | z << 3 | x >> 1;
      return this.blockdata[y >> 4][off] >> ((x & 1) << 2) & 15;
   }

   @Override
   public final int getBlockSkyLight(int x, int y, int z) {
      int off = (y & 15) << 7 | z << 3 | x >> 1;
      return this.skylight[y >> 4][off] >> ((x & 1) << 2) & 15;
   }

   @Override
   public final int getBlockEmittedLight(int x, int y, int z) {
      int off = (y & 15) << 7 | z << 3 | x >> 1;
      return this.emitlight[y >> 4][off] >> ((x & 1) << 2) & 15;
   }

   @Override
   public final int getHighestBlockYAt(int x, int z) {
      return this.hmap[z << 4 | x];
   }

   @Override
   public final Biome getBiome(int x, int z) {
      return CraftBlock.biomeBaseToBiome(this.biome[z << 4 | x]);
   }

   @Override
   public final double getRawBiomeTemperature(int x, int z) {
      return this.biomeTemp[z << 4 | x];
   }

   @Override
   public final double getRawBiomeRainfall(int x, int z) {
      return this.biomeRain[z << 4 | x];
   }

   @Override
   public final long getCaptureFullTime() {
      return this.captureFulltime;
   }

   @Override
   public final boolean isSectionEmpty(int sy) {
      return this.empty[sy];
   }
}
