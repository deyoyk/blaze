package net.minecraft.server.v1_8_R3;

public class GenLayerMushroomShore extends GenLayer {
   public GenLayerMushroomShore(long var1, GenLayer var3) {
      super(var1);
      this.a = var3;
   }

   @Override
   public int[] a(int var1, int var2, int var3, int var4) {
      int[] var5 = this.a.a(var1 - 1, var2 - 1, var3 + 2, var4 + 2);
      int[] var6 = IntCache.a(var3 * var4);

      for (int var7 = 0; var7 < var4; var7++) {
         for (int var8 = 0; var8 < var3; var8++) {
            this.a(var8 + var1, var7 + var2);
            int var9 = var5[var8 + 1 + (var7 + 1) * (var3 + 2)];
            BiomeBase var10 = BiomeBase.getBiome(var9);
            if (var9 == BiomeBase.MUSHROOM_ISLAND.id) {
               int var11 = var5[var8 + 1 + (var7 + 1 - 1) * (var3 + 2)];
               int var12 = var5[var8 + 1 + 1 + (var7 + 1) * (var3 + 2)];
               int var13 = var5[var8 + 1 - 1 + (var7 + 1) * (var3 + 2)];
               int var14 = var5[var8 + 1 + (var7 + 1 + 1) * (var3 + 2)];
               if (var11 != BiomeBase.OCEAN.id && var12 != BiomeBase.OCEAN.id && var13 != BiomeBase.OCEAN.id && var14 != BiomeBase.OCEAN.id) {
                  var6[var8 + var7 * var3] = var9;
               } else {
                  var6[var8 + var7 * var3] = BiomeBase.MUSHROOM_SHORE.id;
               }
            } else if (var10 != null && var10.l() == BiomeJungle.class) {
               int var17 = var5[var8 + 1 + (var7 + 1 - 1) * (var3 + 2)];
               int var20 = var5[var8 + 1 + 1 + (var7 + 1) * (var3 + 2)];
               int var23 = var5[var8 + 1 - 1 + (var7 + 1) * (var3 + 2)];
               int var26 = var5[var8 + 1 + (var7 + 1 + 1) * (var3 + 2)];
               if (!this.c(var17) || !this.c(var20) || !this.c(var23) || !this.c(var26)) {
                  var6[var8 + var7 * var3] = BiomeBase.JUNGLE_EDGE.id;
               } else if (!b(var17) && !b(var20) && !b(var23) && !b(var26)) {
                  var6[var8 + var7 * var3] = var9;
               } else {
                  var6[var8 + var7 * var3] = BiomeBase.BEACH.id;
               }
            } else if (var9 == BiomeBase.EXTREME_HILLS.id || var9 == BiomeBase.EXTREME_HILLS_PLUS.id || var9 == BiomeBase.SMALL_MOUNTAINS.id) {
               this.a(var5, var6, var8, var7, var3, var9, BiomeBase.STONE_BEACH.id);
            } else if (var10 != null && var10.j()) {
               this.a(var5, var6, var8, var7, var3, var9, BiomeBase.COLD_BEACH.id);
            } else if (var9 == BiomeBase.MESA.id || var9 == BiomeBase.MESA_PLATEAU_F.id) {
               int var16 = var5[var8 + 1 + (var7 + 1 - 1) * (var3 + 2)];
               int var19 = var5[var8 + 1 + 1 + (var7 + 1) * (var3 + 2)];
               int var22 = var5[var8 + 1 - 1 + (var7 + 1) * (var3 + 2)];
               int var25 = var5[var8 + 1 + (var7 + 1 + 1) * (var3 + 2)];
               if (b(var16) || b(var19) || b(var22) || b(var25)) {
                  var6[var8 + var7 * var3] = var9;
               } else if (this.d(var16) && this.d(var19) && this.d(var22) && this.d(var25)) {
                  var6[var8 + var7 * var3] = var9;
               } else {
                  var6[var8 + var7 * var3] = BiomeBase.DESERT.id;
               }
            } else if (var9 != BiomeBase.OCEAN.id && var9 != BiomeBase.DEEP_OCEAN.id && var9 != BiomeBase.RIVER.id && var9 != BiomeBase.SWAMPLAND.id) {
               int var15 = var5[var8 + 1 + (var7 + 1 - 1) * (var3 + 2)];
               int var18 = var5[var8 + 1 + 1 + (var7 + 1) * (var3 + 2)];
               int var21 = var5[var8 + 1 - 1 + (var7 + 1) * (var3 + 2)];
               int var24 = var5[var8 + 1 + (var7 + 1 + 1) * (var3 + 2)];
               if (!b(var15) && !b(var18) && !b(var21) && !b(var24)) {
                  var6[var8 + var7 * var3] = var9;
               } else {
                  var6[var8 + var7 * var3] = BiomeBase.BEACH.id;
               }
            } else {
               var6[var8 + var7 * var3] = var9;
            }
         }
      }

      return var6;
   }

   private void a(int[] var1, int[] var2, int var3, int var4, int var5, int var6, int var7) {
      if (b(var6)) {
         var2[var3 + var4 * var5] = var6;
      } else {
         int var8 = var1[var3 + 1 + (var4 + 1 - 1) * (var5 + 2)];
         int var9 = var1[var3 + 1 + 1 + (var4 + 1) * (var5 + 2)];
         int var10 = var1[var3 + 1 - 1 + (var4 + 1) * (var5 + 2)];
         int var11 = var1[var3 + 1 + (var4 + 1 + 1) * (var5 + 2)];
         if (!b(var8) && !b(var9) && !b(var10) && !b(var11)) {
            var2[var3 + var4 * var5] = var6;
         } else {
            var2[var3 + var4 * var5] = var7;
         }
      }
   }

   private boolean c(int var1) {
      return BiomeBase.getBiome(var1) != null && BiomeBase.getBiome(var1).l() == BiomeJungle.class
         ? true
         : var1 == BiomeBase.JUNGLE_EDGE.id
            || var1 == BiomeBase.JUNGLE.id
            || var1 == BiomeBase.JUNGLE_HILLS.id
            || var1 == BiomeBase.FOREST.id
            || var1 == BiomeBase.TAIGA.id
            || b(var1);
   }

   private boolean d(int var1) {
      return BiomeBase.getBiome(var1) instanceof BiomeMesa;
   }
}
