package net.minecraft.server.v1_8_R3;

import java.util.Random;

public class WorldGenSwampTree extends WorldGenTreeAbstract {
   private static final IBlockData a = Blocks.LOG.getBlockData().set(BlockLog1.VARIANT, BlockWood.EnumLogVariant.OAK);
   private static final IBlockData b = Blocks.LEAVES
      .getBlockData()
      .set(BlockLeaves1.VARIANT, BlockWood.EnumLogVariant.OAK)
      .set(BlockLeaves1.CHECK_DECAY, Boolean.FALSE);

   public WorldGenSwampTree() {
      super(false);
   }

   @Override
   public boolean generate(World world, Random random, BlockPosition blockposition) {
      int i = random.nextInt(4) + 5;

      while (world.getType(blockposition.getX(), blockposition.getY() - 1, blockposition.getZ()).getBlock().getMaterial() == Material.WATER) {
         blockposition = blockposition.down();
      }

      boolean flag = true;
      if (blockposition.getY() >= 1 && blockposition.getY() + i + 1 <= 256) {
         for (int l = blockposition.getY(); l <= blockposition.getY() + 1 + i; l++) {
            byte b0 = 1;
            if (l == blockposition.getY()) {
               b0 = 0;
            }

            if (l >= blockposition.getY() + 1 + i - 2) {
               b0 = 3;
            }

            for (int j = blockposition.getX() - b0; j <= blockposition.getX() + b0 && flag; j++) {
               for (int k = blockposition.getZ() - b0; k <= blockposition.getZ() + b0 && flag; k++) {
                  if (l >= 0 && l < 256) {
                     Block block = world.getType(j, l, k).getBlock();
                     if (block.getMaterial() != Material.AIR && block.getMaterial() != Material.LEAVES) {
                        if (block != Blocks.WATER && block != Blocks.FLOWING_WATER) {
                           flag = false;
                        } else if (l > blockposition.getY()) {
                           flag = false;
                        }
                     }
                  } else {
                     flag = false;
                  }
               }
            }
         }

         if (!flag) {
            return false;
         } else {
            Block block1 = world.getType(blockposition.getX(), blockposition.getY() - 1, blockposition.getZ()).getBlock();
            if ((block1 == Blocks.GRASS || block1 == Blocks.DIRT) && blockposition.getY() < 256 - i - 1) {
               this.a(world, blockposition.down());

               for (int j1 = blockposition.getY() - 3 + i; j1 <= blockposition.getY() + i; j1++) {
                  int k1 = j1 - (blockposition.getY() + i);
                  int j = 2 - k1 / 2;

                  for (int kx = blockposition.getX() - j; kx <= blockposition.getX() + j; kx++) {
                     int l1 = kx - blockposition.getX();

                     for (int i1 = blockposition.getZ() - j; i1 <= blockposition.getZ() + j; i1++) {
                        int i2 = i1 - blockposition.getZ();
                        if (Math.abs(l1) != j || Math.abs(i2) != j || random.nextInt(2) != 0 && k1 != 0) {
                           BlockPosition blockposition1 = new BlockPosition(kx, j1, i1);
                           if (!world.getType(blockposition1).getBlock().o()) {
                              this.a(world, blockposition1, b);
                           }
                        }
                     }
                  }
               }

               for (int var26 = 0; var26 < i; var26++) {
                  Block block2 = world.getType(blockposition.up(var26)).getBlock();
                  if (block2.getMaterial() == Material.AIR
                     || block2.getMaterial() == Material.LEAVES
                     || block2 == Blocks.FLOWING_WATER
                     || block2 == Blocks.WATER) {
                     this.a(world, blockposition.up(var26), a);
                  }
               }

               for (int var27 = blockposition.getY() - 3 + i; var27 <= blockposition.getY() + i; var27++) {
                  int k1 = var27 - (blockposition.getY() + i);
                  int j = 2 - k1 / 2;
                  BlockPosition.MutableBlockPosition blockposition_mutableblockposition1 = new BlockPosition.MutableBlockPosition();

                  for (int l1 = blockposition.getX() - j; l1 <= blockposition.getX() + j; l1++) {
                     for (int i1x = blockposition.getZ() - j; i1x <= blockposition.getZ() + j; i1x++) {
                        blockposition_mutableblockposition1.c(l1, var27, i1x);
                        if (world.getType(blockposition_mutableblockposition1).getBlock().getMaterial() == Material.LEAVES) {
                           BlockPosition blockposition2 = blockposition_mutableblockposition1.west();
                           BlockPosition blockposition1 = blockposition_mutableblockposition1.east();
                           BlockPosition blockposition3 = blockposition_mutableblockposition1.north();
                           BlockPosition blockposition4 = blockposition_mutableblockposition1.south();
                           if (random.nextInt(4) == 0 && world.getType(blockposition2).getBlock().getMaterial() == Material.AIR) {
                              this.a(world, blockposition2, BlockVine.EAST);
                           }

                           if (random.nextInt(4) == 0 && world.getType(blockposition1).getBlock().getMaterial() == Material.AIR) {
                              this.a(world, blockposition1, BlockVine.WEST);
                           }

                           if (random.nextInt(4) == 0 && world.getType(blockposition3).getBlock().getMaterial() == Material.AIR) {
                              this.a(world, blockposition3, BlockVine.SOUTH);
                           }

                           if (random.nextInt(4) == 0 && world.getType(blockposition4).getBlock().getMaterial() == Material.AIR) {
                              this.a(world, blockposition4, BlockVine.NORTH);
                           }
                        }
                     }
                  }
               }

               return true;
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   private void a(World world, BlockPosition blockposition, BlockStateBoolean blockstateboolean) {
      IBlockData iblockdata = Blocks.VINE.getBlockData().set(blockstateboolean, true);
      this.a(world, blockposition, iblockdata);
      int i = 4;

      for (BlockPosition var6 = blockposition.down(); world.getType(var6).getBlock().getMaterial() == Material.AIR && i > 0; i--) {
         this.a(world, var6, iblockdata);
         var6 = var6.down();
      }
   }
}
