package net.minecraft.server.v1_8_R3;

import java.util.Random;

public class BiomeOcean extends BiomeBase {
   public BiomeOcean(int var1) {
      super(var1);
      this.au.clear();
   }

   @Override
   public BiomeBase.EnumTemperature m() {
      return BiomeBase.EnumTemperature.OCEAN;
   }

   @Override
   public void a(World var1, Random var2, ChunkSnapshot var3, int var4, int var5, double var6) {
      super.a(var1, var2, var3, var4, var5, var6);
   }
}
