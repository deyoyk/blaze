package net.minecraft.server.v1_8_R3;

import java.util.Random;

public class BiomePlains extends BiomeBase {
   protected boolean aD;

   protected BiomePlains(int var1) {
      super(var1);
      this.a(0.8F, 0.4F);
      this.a(e);
      this.au.add(new BiomeBase.BiomeMeta(EntityHorse.class, 5, 2, 6));
      this.as.A = -999;
      this.as.B = 4;
      this.as.C = 10;
   }

   @Override
   public BlockFlowers.EnumFlowerVarient a(Random var1, BlockPosition var2) {
      double var3 = af.a(var2.getX() / 200.0, var2.getZ() / 200.0);
      if (var3 < -0.8) {
         int var6 = var1.nextInt(4);
         switch (var6) {
            case 0:
               return BlockFlowers.EnumFlowerVarient.ORANGE_TULIP;
            case 1:
               return BlockFlowers.EnumFlowerVarient.RED_TULIP;
            case 2:
               return BlockFlowers.EnumFlowerVarient.PINK_TULIP;
            case 3:
            default:
               return BlockFlowers.EnumFlowerVarient.WHITE_TULIP;
         }
      } else if (var1.nextInt(3) > 0) {
         int var5 = var1.nextInt(3);
         if (var5 == 0) {
            return BlockFlowers.EnumFlowerVarient.POPPY;
         } else {
            return var5 == 1 ? BlockFlowers.EnumFlowerVarient.HOUSTONIA : BlockFlowers.EnumFlowerVarient.OXEYE_DAISY;
         }
      } else {
         return BlockFlowers.EnumFlowerVarient.DANDELION;
      }
   }

   @Override
   public void a(World var1, Random var2, BlockPosition var3) {
      double var4 = af.a((var3.getX() + 8) / 200.0, (var3.getZ() + 8) / 200.0);
      if (var4 < -0.8) {
         this.as.B = 15;
         this.as.C = 5;
      } else {
         this.as.B = 4;
         this.as.C = 10;
         ag.a(BlockTallPlant.EnumTallFlowerVariants.GRASS);

         for (int var6 = 0; var6 < 7; var6++) {
            int var7 = var2.nextInt(16) + 8;
            int var8 = var2.nextInt(16) + 8;
            int var9 = var2.nextInt(var1.getHighestBlockYAt(var3.a(var7, 0, var8)).getY() + 32);
            ag.generate(var1, var2, var3.a(var7, var9, var8));
         }
      }

      if (this.aD) {
         ag.a(BlockTallPlant.EnumTallFlowerVariants.SUNFLOWER);

         for (int var10 = 0; var10 < 10; var10++) {
            int var11 = var2.nextInt(16) + 8;
            int var12 = var2.nextInt(16) + 8;
            int var13 = var2.nextInt(var1.getHighestBlockYAt(var3.a(var11, 0, var12)).getY() + 32);
            ag.generate(var1, var2, var3.a(var11, var13, var12));
         }
      }

      super.a(var1, var2, var3);
   }

   @Override
   protected BiomeBase d(int var1) {
      BiomePlains var2 = new BiomePlains(var1);
      var2.a("Sunflower Plains");
      var2.aD = true;
      var2.b(9286496);
      var2.aj = 14273354;
      return var2;
   }
}
