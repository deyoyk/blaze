package org.bukkit.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.material.MaterialData;

public abstract class ChunkGenerator {
   @Deprecated
   public byte[] generate(org.bukkit.World world, Random random, int x, int z) {
      throw new UnsupportedOperationException(
         "Custom generator is missing required methods: generate(), generateBlockSections() and generateExtBlockSections()"
      );
   }

   @Deprecated
   public short[][] generateExtBlockSections(org.bukkit.World world, Random random, int x, int z, ChunkGenerator.BiomeGrid biomes) {
      return (short[][])null;
   }

   @Deprecated
   public byte[][] generateBlockSections(org.bukkit.World world, Random random, int x, int z, ChunkGenerator.BiomeGrid biomes) {
      return (byte[][])null;
   }

   public ChunkGenerator.ChunkData generateChunkData(org.bukkit.World world, Random random, int x, int z, ChunkGenerator.BiomeGrid biome) {
      return null;
   }

   protected final ChunkGenerator.ChunkData createChunkData(org.bukkit.World world) {
      return org.bukkit.Bukkit.getServer().createChunkData(world);
   }

   public boolean canSpawn(org.bukkit.World world, int x, int z) {
      Block highest = world.getBlockAt(x, world.getHighestBlockYAt(x, z), z);
      switch (world.getEnvironment()) {
         case NETHER:
            return true;
         case THE_END:
            return highest.getType() != org.bukkit.Material.AIR
               && highest.getType() != org.bukkit.Material.WATER
               && highest.getType() != org.bukkit.Material.LAVA;
         case NORMAL:
         default:
            return highest.getType() == org.bukkit.Material.SAND || highest.getType() == org.bukkit.Material.GRAVEL;
      }
   }

   public List<BlockPopulator> getDefaultPopulators(org.bukkit.World world) {
      return new ArrayList<>();
   }

   public org.bukkit.Location getFixedSpawnLocation(org.bukkit.World world, Random random) {
      return null;
   }

   public interface BiomeGrid {
      Biome getBiome(int var1, int var2);

      void setBiome(int var1, int var2, Biome var3);
   }

   public interface ChunkData {
      int getMaxHeight();

      void setBlock(int var1, int var2, int var3, org.bukkit.Material var4);

      void setBlock(int var1, int var2, int var3, MaterialData var4);

      void setRegion(int var1, int var2, int var3, int var4, int var5, int var6, org.bukkit.Material var7);

      void setRegion(int var1, int var2, int var3, int var4, int var5, int var6, MaterialData var7);

      org.bukkit.Material getType(int var1, int var2, int var3);

      MaterialData getTypeAndData(int var1, int var2, int var3);

      @Deprecated
      void setRegion(int var1, int var2, int var3, int var4, int var5, int var6, int var7);

      @Deprecated
      void setRegion(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8);

      @Deprecated
      void setBlock(int var1, int var2, int var3, int var4);

      @Deprecated
      void setBlock(int var1, int var2, int var3, int var4, byte var5);

      @Deprecated
      int getTypeId(int var1, int var2, int var3);

      @Deprecated
      byte getData(int var1, int var2, int var3);
   }
}
