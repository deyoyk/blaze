package net.minecraft.server.v1_8_R3;

import java.util.Random;

public class WorldGenTrees extends WorldGenTreeAbstract {
   private static final IBlockData a = Blocks.LOG.getBlockData().set(BlockLog1.VARIANT, BlockWood.EnumLogVariant.OAK);
   private static final IBlockData b = Blocks.LEAVES
      .getBlockData()
      .set(BlockLeaves1.VARIANT, BlockWood.EnumLogVariant.OAK)
      .set(BlockLeaves.CHECK_DECAY, Boolean.FALSE);
   private final int c;
   private final boolean d;
   private final IBlockData e;
   private final IBlockData f;

   public WorldGenTrees(boolean flag) {
      this(flag, 4, a, b, false);
   }

   public WorldGenTrees(boolean flag, int i, IBlockData iblockdata, IBlockData iblockdata1, boolean flag1) {
      super(flag);
      this.c = i;
      this.e = iblockdata;
      this.f = iblockdata1;
      this.d = flag1;
   }

   @Override
   public boolean generate(World world, Random random, BlockPosition blockposition) {
      int yHeight = random.nextInt(3) + this.c;
      boolean flag = true;
      if (blockposition.getY() >= 1 && blockposition.getY() + yHeight + 1 <= 256) {
         for (int l = blockposition.getY(); l <= blockposition.getY() + 1 + yHeight; l++) {
            byte b0 = 1;
            if (l == blockposition.getY()) {
               b0 = 0;
            }

            if (l >= blockposition.getY() + 1 + yHeight - 2) {
               b0 = 2;
            }

            for (int j = blockposition.getX() - b0; j <= blockposition.getX() + b0 && flag; j++) {
               for (int k = blockposition.getZ() - b0; k <= blockposition.getZ() + b0 && flag; k++) {
                  if (l < 0 || l >= 256) {
                     flag = false;
                  } else if (!this.a(world.getType(j, l, k).getBlock())) {
                     flag = false;
                  }
               }
            }
         }

         if (!flag) {
            return false;
         } else {
            Block block = world.getType(blockposition.getX(), blockposition.getY() - 1, blockposition.getZ()).getBlock();
            if ((block == Blocks.GRASS || block == Blocks.DIRT || block == Blocks.FARMLAND) && blockposition.getY() < 256 - yHeight - 1) {
               this.a(world, blockposition.down());
               byte b0x = 3;
               byte b1 = 0;

               for (int j = blockposition.getY() - b0x + yHeight; j <= blockposition.getY() + yHeight; j++) {
                  int kx = j - (blockposition.getY() + yHeight);
                  int i1 = b1 + 1 - kx / 2;

                  for (int l1 = blockposition.getX() - i1; l1 <= blockposition.getX() + i1; l1++) {
                     int j1 = l1 - blockposition.getX();

                     for (int k1 = blockposition.getZ() - i1; k1 <= blockposition.getZ() + i1; k1++) {
                        int i2 = k1 - blockposition.getZ();
                        if (Math.abs(j1) != i1 || Math.abs(i2) != i1 || random.nextInt(2) != 0 && kx != 0) {
                           BlockPosition blockposition1 = new BlockPosition(l1, j, k1);
                           Block block1 = world.getType(blockposition1).getBlock();
                           if (block1.getMaterial() == Material.AIR
                              || block1.getMaterial() == Material.LEAVES
                              || block1.getMaterial() == Material.REPLACEABLE_PLANT) {
                              this.a(world, blockposition1, this.f);
                           }
                        }
                     }
                  }
               }

               for (int var21 = 0; var21 < yHeight; var21++) {
                  Block block2 = world.getType(blockposition.up(var21)).getBlock();
                  if (block2.getMaterial() == Material.AIR || block2.getMaterial() == Material.LEAVES || block2.getMaterial() == Material.REPLACEABLE_PLANT) {
                     this.a(world, blockposition.up(var21), this.e);
                     if (this.d && var21 > 0) {
                        if (random.nextInt(3) > 0 && world.isEmpty(blockposition.a(-1, var21, 0))) {
                           this.a(world, blockposition.a(-1, var21, 0), BlockVine.EAST);
                        }

                        if (random.nextInt(3) > 0 && world.isEmpty(blockposition.a(1, var21, 0))) {
                           this.a(world, blockposition.a(1, var21, 0), BlockVine.WEST);
                        }

                        if (random.nextInt(3) > 0 && world.isEmpty(blockposition.a(0, var21, -1))) {
                           this.a(world, blockposition.a(0, var21, -1), BlockVine.SOUTH);
                        }

                        if (random.nextInt(3) > 0 && world.isEmpty(blockposition.a(0, var21, 1))) {
                           this.a(world, blockposition.a(0, var21, 1), BlockVine.NORTH);
                        }
                     }
                  }
               }

               if (this.d) {
                  for (int var22 = blockposition.getY() - 3 + yHeight; var22 <= blockposition.getY() + yHeight; var22++) {
                     int kx = var22 - (blockposition.getY() + yHeight);
                     int i1 = 2 - kx / 2;
                     BlockPosition.MutableBlockPosition blockposition_mutableblockposition1 = new BlockPosition.MutableBlockPosition();

                     for (int j1 = blockposition.getX() - i1; j1 <= blockposition.getX() + i1; j1++) {
                        for (int k1x = blockposition.getZ() - i1; k1x <= blockposition.getZ() + i1; k1x++) {
                           blockposition_mutableblockposition1.c(j1, var22, k1x);
                           if (world.getType(blockposition_mutableblockposition1).getBlock().getMaterial() == Material.LEAVES) {
                              BlockPosition blockposition2 = blockposition_mutableblockposition1.west();
                              BlockPosition blockposition1 = blockposition_mutableblockposition1.east();
                              BlockPosition blockposition3 = blockposition_mutableblockposition1.north();
                              BlockPosition blockposition4 = blockposition_mutableblockposition1.south();
                              if (random.nextInt(4) == 0 && world.getType(blockposition2).getBlock().getMaterial() == Material.AIR) {
                                 this.b(world, blockposition2, BlockVine.EAST);
                              }

                              if (random.nextInt(4) == 0 && world.getType(blockposition1).getBlock().getMaterial() == Material.AIR) {
                                 this.b(world, blockposition1, BlockVine.WEST);
                              }

                              if (random.nextInt(4) == 0 && world.getType(blockposition3).getBlock().getMaterial() == Material.AIR) {
                                 this.b(world, blockposition3, BlockVine.SOUTH);
                              }

                              if (random.nextInt(4) == 0 && world.getType(blockposition4).getBlock().getMaterial() == Material.AIR) {
                                 this.b(world, blockposition4, BlockVine.NORTH);
                              }
                           }
                        }
                     }
                  }

                  if (random.nextInt(5) == 0 && yHeight > 5) {
                     for (int var23 = 0; var23 < 2; var23++) {
                        for (EnumDirection enumdirection : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
                           if (random.nextInt(4 - var23) == 0) {
                              EnumDirection enumdirection1 = enumdirection.opposite();
                              this.a(
                                 world,
                                 random.nextInt(3),
                                 blockposition.a(enumdirection1.getAdjacentX(), yHeight - 5 + var23, enumdirection1.getAdjacentZ()),
                                 enumdirection
                              );
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

   private void a(World world, int i, BlockPosition blockposition, EnumDirection enumdirection) {
      this.a(world, blockposition, Blocks.COCOA.getBlockData().set(BlockCocoa.AGE, i).set(BlockCocoa.FACING, enumdirection));
   }

   private void a(World world, BlockPosition blockposition, BlockStateBoolean blockstateboolean) {
      this.a(world, blockposition, Blocks.VINE.getBlockData().set(blockstateboolean, true));
   }

   private void b(World world, BlockPosition blockposition, BlockStateBoolean blockstateboolean) {
      this.a(world, blockposition, blockstateboolean);
      int i = 4;

      for (BlockPosition var5 = blockposition.down(); world.getType(var5).getBlock().getMaterial() == Material.AIR && i > 0; i--) {
         this.a(world, var5, blockstateboolean);
         var5 = var5.down();
      }
   }
}
