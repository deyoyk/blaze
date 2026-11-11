package net.minecraft.server.v1_8_R3;

import java.util.Random;

public class BiomeJungle extends BiomeBase {
   private boolean aD;
   private static final IBlockData aE = Blocks.LOG.getBlockData().set(BlockLog1.VARIANT, BlockWood.EnumLogVariant.JUNGLE);
   private static final IBlockData aF = Blocks.LEAVES
      .getBlockData()
      .set(BlockLeaves1.VARIANT, BlockWood.EnumLogVariant.JUNGLE)
      .set(BlockLeaves.CHECK_DECAY, false);
   private static final IBlockData aG = Blocks.LEAVES
      .getBlockData()
      .set(BlockLeaves1.VARIANT, BlockWood.EnumLogVariant.OAK)
      .set(BlockLeaves.CHECK_DECAY, false);

   public BiomeJungle(int var1, boolean var2) {
      super(var1);
      this.aD = var2;
      if (var2) {
         this.as.A = 2;
      } else {
         this.as.A = 50;
      }

      this.as.C = 25;
      this.as.B = 4;
      if (!var2) {
         this.at.add(new BiomeBase.BiomeMeta(EntityOcelot.class, 2, 1, 1));
      }

      this.au.add(new BiomeBase.BiomeMeta(EntityChicken.class, 10, 4, 4));
   }

   @Override
   public WorldGenTreeAbstract a(Random var1) {
      if (var1.nextInt(10) == 0) {
         return this.aB;
      } else if (var1.nextInt(2) == 0) {
         return new WorldGenGroundBush(aE, aG);
      } else {
         return (WorldGenTreeAbstract)(!this.aD && var1.nextInt(3) == 0
            ? new WorldGenJungleTree(false, 10, 20, aE, aF)
            : new WorldGenTrees(false, 4 + var1.nextInt(7), aE, aF, true));
      }
   }

   @Override
   public WorldGenerator b(Random var1) {
      return var1.nextInt(4) == 0 ? new WorldGenGrass(BlockLongGrass.EnumTallGrassType.FERN) : new WorldGenGrass(BlockLongGrass.EnumTallGrassType.GRASS);
   }

   @Override
   public void a(World var1, Random var2, BlockPosition var3) {
      super.a(var1, var2, var3);
      int var4 = var2.nextInt(16) + 8;
      int var5 = var2.nextInt(16) + 8;
      int var6 = var2.nextInt(var1.getHighestBlockYAt(var3.a(var4, 0, var5)).getY() * 2);
      new WorldGenMelon().generate(var1, var2, var3.a(var4, var6, var5));
      WorldGenVines var9 = new WorldGenVines();

      for (int var10 = 0; var10 < 50; var10++) {
         var6 = var2.nextInt(16) + 8;
         short var7 = 128;
         int var8 = var2.nextInt(16) + 8;
         var9.generate(var1, var2, var3.a(var6, 128, var8));
      }
   }
}
