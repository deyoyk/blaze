package net.minecraft.server.v1_8_R3;

import java.util.Random;

public class WorldGenLakes extends WorldGenerator {
   private final Block a;

   public WorldGenLakes(Block block) {
      this.a = block;
   }

   @Override
   public boolean generate(World world, Random random, BlockPosition blockposition) {
      blockposition = blockposition.a(-8, 0, -8);

      while (blockposition.getY() > 5 && world.isEmpty(blockposition)) {
         blockposition = blockposition.down();
      }

      if (blockposition.getY() <= 4) {
         return false;
      } else {
         blockposition = blockposition.down(4);
         boolean[] aboolean = new boolean[2048];
         int i = random.nextInt(4) + 4;

         for (int j = 0; j < i; j++) {
            double d0 = random.nextDouble() * 6.0 + 3.0;
            double d1 = random.nextDouble() * 4.0 + 2.0;
            double d2 = random.nextDouble() * 6.0 + 3.0;
            double d3 = random.nextDouble() * (16.0 - d0 - 2.0) + 1.0 + d0 / 2.0;
            double d4 = random.nextDouble() * (8.0 - d1 - 4.0) + 2.0 + d1 / 2.0;
            double d5 = random.nextDouble() * (16.0 - d2 - 2.0) + 1.0 + d2 / 2.0;

            for (int k = 1; k < 15; k++) {
               for (int l = 1; l < 15; l++) {
                  for (int i1 = 1; i1 < 7; i1++) {
                     double d6 = (k - d3) / (d0 / 2.0);
                     double d7 = (i1 - d4) / (d1 / 2.0);
                     double d8 = (l - d5) / (d2 / 2.0);
                     double d9 = d6 * d6 + d7 * d7 + d8 * d8;
                     if (d9 < 1.0) {
                        aboolean[(k * 16 + l) * 8 + i1] = true;
                     }
                  }
               }
            }
         }

         for (int var32 = 0; var32 < 16; var32++) {
            for (int k1 = 0; k1 < 16; k1++) {
               for (int j1 = 0; j1 < 8; j1++) {
                  boolean flag = !aboolean[(var32 * 16 + k1) * 8 + j1]
                     && (
                        var32 < 15 && aboolean[((var32 + 1) * 16 + k1) * 8 + j1]
                           || var32 > 0 && aboolean[((var32 - 1) * 16 + k1) * 8 + j1]
                           || k1 < 15 && aboolean[(var32 * 16 + k1 + 1) * 8 + j1]
                           || k1 > 0 && aboolean[(var32 * 16 + (k1 - 1)) * 8 + j1]
                           || j1 < 7 && aboolean[(var32 * 16 + k1) * 8 + j1 + 1]
                           || j1 > 0 && aboolean[(var32 * 16 + k1) * 8 + (j1 - 1)]
                     );
                  if (flag) {
                     Material material = world.getType(blockposition.a(var32, j1, k1)).getBlock().getMaterial();
                     if (j1 >= 4 && material.isLiquid()) {
                        return false;
                     }

                     if (j1 < 4 && !material.isBuildable() && world.getType(blockposition.a(var32, j1, k1)).getBlock() != this.a) {
                        return false;
                     }
                  }
               }
            }
         }

         for (int var33 = 0; var33 < 16; var33++) {
            for (int k1 = 0; k1 < 16; k1++) {
               for (int j1x = 0; j1x < 8; j1x++) {
                  if (aboolean[(var33 * 16 + k1) * 8 + j1x]) {
                     world.setTypeAndData(blockposition.a(var33, j1x, k1), j1x >= 4 ? Blocks.AIR.getBlockData() : this.a.getBlockData(), 2);
                  }
               }
            }
         }

         for (int var34 = 0; var34 < 16; var34++) {
            for (int k1 = 0; k1 < 16; k1++) {
               for (int j1xx = 4; j1xx < 8; j1xx++) {
                  if (aboolean[(var34 * 16 + k1) * 8 + j1xx]) {
                     BlockPosition blockposition1 = blockposition.a(var34, j1xx - 1, k1);
                     if (world.getType(blockposition1).getBlock() == Blocks.DIRT && world.b(EnumSkyBlock.SKY, blockposition.a(var34, j1xx, k1)) > 0) {
                        BiomeBase biomebase = world.getBiome(blockposition1);
                        if (biomebase.ak.getBlock() == Blocks.MYCELIUM) {
                           world.setTypeAndData(blockposition1, Blocks.MYCELIUM.getBlockData(), 2);
                        } else {
                           world.setTypeAndData(blockposition1, Blocks.GRASS.getBlockData(), 2);
                        }
                     }
                  }
               }
            }
         }

         if (this.a.getMaterial() == Material.LAVA) {
            for (int var35 = 0; var35 < 16; var35++) {
               for (int k1 = 0; k1 < 16; k1++) {
                  for (int j1xxx = 0; j1xxx < 8; j1xxx++) {
                     boolean flag = !aboolean[(var35 * 16 + k1) * 8 + j1xxx]
                        && (
                           var35 < 15 && aboolean[((var35 + 1) * 16 + k1) * 8 + j1xxx]
                              || var35 > 0 && aboolean[((var35 - 1) * 16 + k1) * 8 + j1xxx]
                              || k1 < 15 && aboolean[(var35 * 16 + k1 + 1) * 8 + j1xxx]
                              || k1 > 0 && aboolean[(var35 * 16 + (k1 - 1)) * 8 + j1xxx]
                              || j1xxx < 7 && aboolean[(var35 * 16 + k1) * 8 + j1xxx + 1]
                              || j1xxx > 0 && aboolean[(var35 * 16 + k1) * 8 + (j1xxx - 1)]
                        );
                     if (flag
                        && (j1xxx < 4 || random.nextInt(2) != 0)
                        && world.getType(blockposition.a(var35, j1xxx, k1)).getBlock().getMaterial().isBuildable()) {
                        world.setTypeAndData(blockposition.a(var35, j1xxx, k1), Blocks.STONE.getBlockData(), 2);
                     }
                  }
               }
            }
         }

         if (this.a.getMaterial() == Material.WATER) {
            for (int var36 = 0; var36 < 16; var36++) {
               for (int k1 = 0; k1 < 16; k1++) {
                  byte b0 = 4;
                  if (world.v(blockposition.a(var36, b0, k1))) {
                     world.setTypeAndData(blockposition.a(var36, b0, k1), Blocks.ICE.getBlockData(), 2);
                  }
               }
            }
         }

         return true;
      }
   }
}
