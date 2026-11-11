package net.minecraft.server.v1_8_R3;

import java.util.Random;

public class BiomeIcePlains extends BiomeBase {
   private boolean aD;
   private WorldGenPackedIce2 aE = new WorldGenPackedIce2();
   private WorldGenPackedIce1 aF = new WorldGenPackedIce1(4);

   public BiomeIcePlains(int var1, boolean var2) {
      super(var1);
      this.aD = var2;
      if (var2) {
         this.ak = Blocks.SNOW.getBlockData();
      }

      this.au.clear();
   }

   @Override
   public void a(World var1, Random var2, BlockPosition var3) {
      if (this.aD) {
         for (int var4 = 0; var4 < 3; var4++) {
            int var5 = var2.nextInt(16) + 8;
            int var6 = var2.nextInt(16) + 8;
            this.aE.generate(var1, var2, var1.getHighestBlockYAt(var3.a(var5, 0, var6)));
         }

         for (int var7 = 0; var7 < 2; var7++) {
            int var8 = var2.nextInt(16) + 8;
            int var9 = var2.nextInt(16) + 8;
            this.aF.generate(var1, var2, var1.getHighestBlockYAt(var3.a(var8, 0, var9)));
         }
      }

      super.a(var1, var2, var3);
   }

   @Override
   public WorldGenTreeAbstract a(Random var1) {
      return new WorldGenTaiga2(false);
   }

   @Override
   protected BiomeBase d(int var1) {
      BiomeBase var2 = new BiomeIcePlains(var1, true)
         .a(13828095, true)
         .a(this.ah + " Spikes")
         .c()
         .a(0.0F, 0.5F)
         .a(new BiomeBase.BiomeTemperature(this.an + 0.1F, this.ao + 0.1F));
      var2.an = this.an + 0.3F;
      var2.ao = this.ao + 0.4F;
      return var2;
   }
}
