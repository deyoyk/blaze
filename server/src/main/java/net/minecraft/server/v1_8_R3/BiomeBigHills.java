package net.minecraft.server.v1_8_R3;

import java.util.Random;

public class BiomeBigHills extends BiomeBase {
   private final WorldGenerator aD = new WorldGenMinable(
      Blocks.MONSTER_EGG.getBlockData().set(BlockMonsterEggs.VARIANT, BlockMonsterEggs.EnumMonsterEggVarient.STONE), 9
   );
   private final WorldGenTaiga2 aE = new WorldGenTaiga2(false);
   private final int aG = 1;
   private final int aH = 2;
   private int aI = 0;

   protected BiomeBigHills(int i, boolean flag) {
      super(i);
      if (flag) {
         this.as.A = 3;
         this.aI = this.aG;
      }
   }

   @Override
   public WorldGenTreeAbstract a(Random random) {
      return (WorldGenTreeAbstract)(random.nextInt(3) > 0 ? this.aE : super.a(random));
   }

   @Override
   public void a(World world, Random random, BlockPosition blockposition) {
      super.a(world, random, blockposition);
      int i = (int)(3.0 * (Double)world.generatorConfig.emeraldMultiplier.getValue() + random.nextInt(6));

      for (int j = 0; j < i; j++) {
         int k = random.nextInt(16);
         int l = random.nextInt(28) + 4;
         int i1 = random.nextInt(16);
         BlockPosition blockposition1 = blockposition.a(k, l, i1);
         if (world.getType(blockposition1).getBlock() == Blocks.STONE) {
            world.setTypeAndData(blockposition1, Blocks.EMERALD_ORE.getBlockData(), 2);
         }
      }

      for (int var10 = 0; var10 < 7; var10++) {
         int var11 = random.nextInt(16);
         int k = random.nextInt(64);
         int l = random.nextInt(16);
         this.aD.generate(world, random, blockposition.a(var11, k, l));
      }
   }

   @Override
   public void a(World world, Random random, ChunkSnapshot chunksnapshot, int i, int j, double d0) {
      this.ak = Blocks.GRASS.getBlockData();
      this.al = Blocks.DIRT.getBlockData();
      if ((d0 < -1.0 || d0 > 2.0) && this.aI == this.aH) {
         this.ak = Blocks.GRAVEL.getBlockData();
         this.al = Blocks.GRAVEL.getBlockData();
      } else if (d0 > 1.0 && this.aI != this.aG) {
         this.ak = Blocks.STONE.getBlockData();
         this.al = Blocks.STONE.getBlockData();
      }

      this.b(world, random, chunksnapshot, i, j, d0);
   }

   private BiomeBigHills b(BiomeBase biomebase) {
      this.aI = this.aH;
      this.a(biomebase.ai, true);
      this.a(biomebase.ah + " M");
      this.a(new BiomeBase.BiomeTemperature(biomebase.an, biomebase.ao));
      this.a(biomebase.temperature, biomebase.humidity);
      return this;
   }

   @Override
   protected BiomeBase d(int i) {
      return new BiomeBigHills(i, false).b(this);
   }
}
