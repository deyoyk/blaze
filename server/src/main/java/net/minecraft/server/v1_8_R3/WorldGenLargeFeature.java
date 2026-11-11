package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

public class WorldGenLargeFeature extends StructureGenerator {
   private static final List<BiomeBase> d = Arrays.asList(
      BiomeBase.DESERT, BiomeBase.DESERT_HILLS, BiomeBase.JUNGLE, BiomeBase.JUNGLE_HILLS, BiomeBase.SWAMPLAND
   );
   private final List<BiomeBase.BiomeMeta> f = Lists.newArrayList();
   private int g = 32;
   private final int h = 8;

   public WorldGenLargeFeature() {
      this.f.add(new BiomeBase.BiomeMeta(EntityWitch.class, 1, 1, 1));
   }

   public WorldGenLargeFeature(Map<String, String> map) {
      this();

      for (Entry<String, String> entry : map.entrySet()) {
         if (entry.getKey().equals("distance")) {
            this.g = MathHelper.a(entry.getValue(), this.g, this.h + 1);
         }
      }
   }

   @Override
   public String a() {
      return "Temple";
   }

   @Override
   protected boolean a(int i, int j) {
      int k = i;
      int l = j;
      if (i < 0) {
         i -= this.g - 1;
      }

      if (j < 0) {
         j -= this.g - 1;
      }

      int i1 = i / this.g;
      int j1 = j / this.g;
      Random random = this.c.a(i1, j1, this.c.spigotConfig.largeFeatureSeed);
      i1 *= this.g;
      j1 *= this.g;
      i1 += random.nextInt(this.g - this.h);
      j1 += random.nextInt(this.g - this.h);
      if (k == i1 && l == j1) {
         BiomeBase biomebase = this.c.getWorldChunkManager().getBiome(k * 16 + 8, l * 16 + 8);
         if (biomebase == null) {
            return false;
         }

         for (BiomeBase biomebase1 : d) {
            if (biomebase == biomebase1) {
               return true;
            }
         }
      }

      return false;
   }

   @Override
   protected StructureStart b(int i, int j) {
      return new WorldGenLargeFeature.WorldGenLargeFeatureStart(this.c, this.b, i, j);
   }

   public boolean a(BlockPosition blockposition) {
      StructureStart structurestart = this.c(blockposition);
      if (structurestart instanceof WorldGenLargeFeature.WorldGenLargeFeatureStart && !structurestart.a.isEmpty()) {
         StructurePiece structurepiece = structurestart.a.getFirst();
         return structurepiece instanceof WorldGenRegistration.WorldGenWitchHut;
      } else {
         return false;
      }
   }

   public List<BiomeBase.BiomeMeta> b() {
      return this.f;
   }

   public static class WorldGenLargeFeatureStart extends StructureStart {
      public WorldGenLargeFeatureStart() {
      }

      public WorldGenLargeFeatureStart(World world, Random random, int i, int j) {
         super(i, j);
         BiomeBase biomebase = world.getBiome(i * 16 + 8, 0, j * 16 + 8);
         if (biomebase == BiomeBase.JUNGLE || biomebase == BiomeBase.JUNGLE_HILLS) {
            this.a.add(new WorldGenRegistration.WorldGenJungleTemple(random, i * 16, j * 16));
         } else if (biomebase == BiomeBase.SWAMPLAND) {
            this.a.add(new WorldGenRegistration.WorldGenWitchHut(random, i * 16, j * 16));
         } else if (biomebase == BiomeBase.DESERT || biomebase == BiomeBase.DESERT_HILLS) {
            this.a.add(new WorldGenRegistration.WorldGenPyramidPiece(random, i * 16, j * 16));
         }

         this.c();
      }
   }
}
