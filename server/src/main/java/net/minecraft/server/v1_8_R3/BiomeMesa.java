package net.minecraft.server.v1_8_R3;

import java.util.Arrays;
import java.util.Random;
import net.jafama.FastMath;

public class BiomeMesa extends BiomeBase {
   private IBlockData[] aD;
   private long aE;
   private NoiseGenerator3 aF;
   private NoiseGenerator3 aG;
   private NoiseGenerator3 aH;
   private final boolean aI;
   private final boolean aJ;

   public BiomeMesa(int i, boolean flag, boolean flag1) {
      super(i);
      this.aI = flag;
      this.aJ = flag1;
      this.b();
      this.a(2.0F, 0.0F);
      this.au.clear();
      this.ak = Blocks.SAND.getBlockData().set(BlockSand.VARIANT, BlockSand.EnumSandVariant.RED_SAND);
      this.al = Blocks.STAINED_HARDENED_CLAY.getBlockData();
      this.as.A = -999;
      this.as.D = 20;
      this.as.F = 3;
      this.as.G = 5;
      this.as.B = 0;
      this.au.clear();
      if (flag1) {
         this.as.A = 5;
      }
   }

   @Override
   public WorldGenTreeAbstract a(Random random) {
      return this.aA;
   }

   @Override
   public void a(World world, Random random, BlockPosition blockposition) {
      super.a(world, random, blockposition);
   }

   @Override
   public void a(World world, Random random, ChunkSnapshot chunksnapshot, int i, int j, double d0) {
      if (this.aD == null || this.aE != world.getSeed()) {
         this.a(world.getSeed());
      }

      if (this.aF == null || this.aG == null || this.aE != world.getSeed()) {
         Random random1 = new Random(this.aE);
         this.aF = new NoiseGenerator3(random1, 4);
         this.aG = new NoiseGenerator3(random1, 1);
      }

      this.aE = world.getSeed();
      double d1 = 0.0;
      if (this.aI) {
         int k = (i & -16) + (j & 15);
         int l = (j & -16) + (i & 15);
         double d2 = Math.min(Math.abs(d0), this.aF.a(k * 0.25, l * 0.25));
         if (d2 > 0.0) {
            double d3 = 0.001953125;
            double d4 = Math.abs(this.aG.a(k * d3, l * d3));
            d1 = d2 * d2 * 2.5;
            double d5 = Math.ceil(d4 * 50.0) + 14.0;
            if (d1 > d5) {
               d1 = d5;
            }

            d1 += 64.0;
         }
      }

      int k = i & 15;
      int l = j & 15;
      int i1 = world.F();
      IBlockData iblockdata = Blocks.STAINED_HARDENED_CLAY.getBlockData();
      IBlockData iblockdata1 = this.al;
      int j1 = (int)(d0 / 3.0 + 3.0 + random.nextDouble() * 0.25);
      boolean flag = FastMath.cos(d0 / 3.0 * Math.PI) > 0.0;
      int k1 = -1;
      boolean flag1 = false;

      for (int l1 = 255; l1 >= 0; l1--) {
         if (chunksnapshot.a(l, l1, k).getBlock().getMaterial() == Material.AIR && l1 < (int)d1) {
            chunksnapshot.a(l, l1, k, Blocks.STONE.getBlockData());
         }

         if (l1 <= (world.paperSpigotConfig.generateFlatBedrock ? 0 : random.nextInt(5))) {
            chunksnapshot.a(l, l1, k, Blocks.BEDROCK.getBlockData());
         } else {
            IBlockData iblockdata2 = chunksnapshot.a(l, l1, k);
            if (iblockdata2.getBlock().getMaterial() == Material.AIR) {
               k1 = -1;
            } else if (iblockdata2.getBlock() == Blocks.STONE) {
               if (k1 == -1) {
                  flag1 = false;
                  if (j1 <= 0) {
                     iblockdata = null;
                     iblockdata1 = Blocks.STONE.getBlockData();
                  } else if (l1 >= i1 - 4 && l1 <= i1 + 1) {
                     iblockdata = Blocks.STAINED_HARDENED_CLAY.getBlockData();
                     iblockdata1 = this.al;
                  }

                  if (l1 < i1 && (iblockdata == null || iblockdata.getBlock().getMaterial() == Material.AIR)) {
                     iblockdata = Blocks.WATER.getBlockData();
                  }

                  k1 = j1 + Math.max(0, l1 - i1);
                  if (l1 >= i1 - 1) {
                     if (!this.aJ || l1 <= 86 + j1 * 2) {
                        if (l1 > i1 + 3 + j1) {
                           IBlockData iblockdata3;
                           if (l1 < 64 || l1 > 127) {
                              iblockdata3 = Blocks.STAINED_HARDENED_CLAY.getBlockData().set(BlockCloth.COLOR, EnumColor.ORANGE);
                           } else if (flag) {
                              iblockdata3 = Blocks.HARDENED_CLAY.getBlockData();
                           } else {
                              iblockdata3 = this.a(i, l1, j);
                           }

                           chunksnapshot.a(l, l1, k, iblockdata3);
                        } else {
                           chunksnapshot.a(l, l1, k, this.ak);
                           flag1 = true;
                        }
                     } else if (flag) {
                        chunksnapshot.a(l, l1, k, Blocks.DIRT.getBlockData().set(BlockDirt.VARIANT, BlockDirt.EnumDirtVariant.COARSE_DIRT));
                     } else {
                        chunksnapshot.a(l, l1, k, Blocks.GRASS.getBlockData());
                     }
                  } else {
                     chunksnapshot.a(l, l1, k, iblockdata1);
                     if (iblockdata1.getBlock() == Blocks.STAINED_HARDENED_CLAY) {
                        chunksnapshot.a(l, l1, k, iblockdata1.getBlock().getBlockData().set(BlockCloth.COLOR, EnumColor.ORANGE));
                     }
                  }
               } else if (k1 > 0) {
                  k1--;
                  if (flag1) {
                     chunksnapshot.a(l, l1, k, Blocks.STAINED_HARDENED_CLAY.getBlockData().set(BlockCloth.COLOR, EnumColor.ORANGE));
                  } else {
                     IBlockData iblockdata3 = this.a(i, l1, j);
                     chunksnapshot.a(l, l1, k, iblockdata3);
                  }
               }
            }
         }
      }
   }

   private void a(long i) {
      this.aD = new IBlockData[64];
      Arrays.fill(this.aD, Blocks.HARDENED_CLAY.getBlockData());
      Random random = new Random(i);
      this.aH = new NoiseGenerator3(random, 1);

      for (int j = 0; j < 64; j++) {
         j += random.nextInt(5) + 1;
         if (j < 64) {
            this.aD[j] = Blocks.STAINED_HARDENED_CLAY.getBlockData().set(BlockCloth.COLOR, EnumColor.ORANGE);
         }
      }

      int var13 = random.nextInt(4) + 2;

      for (int k = 0; k < var13; k++) {
         int l = random.nextInt(3) + 1;
         int i1 = random.nextInt(64);

         for (int j1 = 0; i1 + j1 < 64 && j1 < l; j1++) {
            this.aD[i1 + j1] = Blocks.STAINED_HARDENED_CLAY.getBlockData().set(BlockCloth.COLOR, EnumColor.YELLOW);
         }
      }

      int var14 = random.nextInt(4) + 2;

      for (int l = 0; l < var14; l++) {
         int i1 = random.nextInt(3) + 2;
         int j1 = random.nextInt(64);

         for (int k1 = 0; j1 + k1 < 64 && k1 < i1; k1++) {
            this.aD[j1 + k1] = Blocks.STAINED_HARDENED_CLAY.getBlockData().set(BlockCloth.COLOR, EnumColor.BROWN);
         }
      }

      int var16 = random.nextInt(4) + 2;

      for (int i1 = 0; i1 < var16; i1++) {
         int j1 = random.nextInt(3) + 1;
         int k1 = random.nextInt(64);

         for (int l1 = 0; k1 + l1 < 64 && l1 < j1; l1++) {
            this.aD[k1 + l1] = Blocks.STAINED_HARDENED_CLAY.getBlockData().set(BlockCloth.COLOR, EnumColor.RED);
         }
      }

      int var19 = random.nextInt(3) + 3;
      int j1 = 0;

      for (int k1 = 0; k1 < var19; k1++) {
         byte b0 = 1;
         j1 += random.nextInt(16) + 4;

         for (int i2 = 0; j1 + i2 < 64 && i2 < b0; i2++) {
            this.aD[j1 + i2] = Blocks.STAINED_HARDENED_CLAY.getBlockData().set(BlockCloth.COLOR, EnumColor.WHITE);
            if (j1 + i2 > 1 && random.nextBoolean()) {
               this.aD[j1 + i2 - 1] = Blocks.STAINED_HARDENED_CLAY.getBlockData().set(BlockCloth.COLOR, EnumColor.SILVER);
            }

            if (j1 + i2 < 63 && random.nextBoolean()) {
               this.aD[j1 + i2 + 1] = Blocks.STAINED_HARDENED_CLAY.getBlockData().set(BlockCloth.COLOR, EnumColor.SILVER);
            }
         }
      }
   }

   private IBlockData a(int i, int j, int k) {
      int l = (int)FastMath.round(this.aH.a(i / 512.0, i / 512.0) * 2.0);
      return this.aD[(j + l + 64) % 64];
   }

   @Override
   protected BiomeBase d(int i) {
      boolean flag = this.id == BiomeBase.MESA.id;
      BiomeMesa biomemesa = new BiomeMesa(i, flag, this.aJ);
      if (!flag) {
         biomemesa.a(g);
         biomemesa.a(this.ah + " M");
      } else {
         biomemesa.a(this.ah + " (Bryce)");
      }

      biomemesa.a(this.ai, true);
      return biomemesa;
   }
}
