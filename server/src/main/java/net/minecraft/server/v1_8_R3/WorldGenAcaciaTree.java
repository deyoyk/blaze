package net.minecraft.server.v1_8_R3;

import java.util.Random;

public class WorldGenAcaciaTree extends WorldGenTreeAbstract {
   private static final IBlockData a = Blocks.LOG2.getBlockData().set(BlockLog2.VARIANT, BlockWood.EnumLogVariant.ACACIA);
   private static final IBlockData b = Blocks.LEAVES2
      .getBlockData()
      .set(BlockLeaves2.VARIANT, BlockWood.EnumLogVariant.ACACIA)
      .set(BlockLeaves.CHECK_DECAY, Boolean.FALSE);

   public WorldGenAcaciaTree(boolean flag) {
      super(flag);
   }

   @Override
   public boolean generate(World world, Random random, BlockPosition blockposition) {
      int i = random.nextInt(3) + random.nextInt(3) + 5;
      boolean flag = true;
      if (blockposition.getY() >= 1 && blockposition.getY() + i + 1 <= 256) {
         for (int l = blockposition.getY(); l <= blockposition.getY() + 1 + i; l++) {
            byte b0 = 1;
            if (l == blockposition.getY()) {
               b0 = 0;
            }

            if (l >= blockposition.getY() + 1 + i - 2) {
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
            if ((block == Blocks.GRASS || block == Blocks.DIRT) && blockposition.getY() < 256 - i - 1) {
               this.a(world, blockposition.down());
               EnumDirection enumdirection = EnumDirection.EnumDirectionLimit.HORIZONTAL.a(random);
               int i1 = i - random.nextInt(4) - 1;
               int j = 3 - random.nextInt(3);
               int kx = blockposition.getX();
               int j1 = blockposition.getZ();
               int k1 = 0;

               for (int i2 = 0; i2 < i; i2++) {
                  int l1 = blockposition.getY() + i2;
                  if (i2 >= i1 && j > 0) {
                     kx += enumdirection.getAdjacentX();
                     j1 += enumdirection.getAdjacentZ();
                     j--;
                  }

                  Material material = world.getType(kx, l1, j1).getBlock().getMaterial();
                  if (material == Material.AIR || material == Material.LEAVES) {
                     this.b(world, new BlockPosition(kx, l1, j1));
                     k1 = l1;
                  }
               }

               BlockPosition blockposition2 = new BlockPosition(kx, k1, j1);

               for (int l1x = -3; l1x <= 3; l1x++) {
                  for (int j2 = -3; j2 <= 3; j2++) {
                     if (Math.abs(l1x) != 3 || Math.abs(j2) != 3) {
                        this.c(world, blockposition2.a(l1x, 0, j2));
                     }
                  }
               }

               blockposition2 = blockposition2.up();

               for (int var28 = -1; var28 <= 1; var28++) {
                  for (int j2x = -1; j2x <= 1; j2x++) {
                     this.c(world, blockposition2.a(var28, 0, j2x));
                  }
               }

               this.c(world, blockposition2.east(2));
               this.c(world, blockposition2.west(2));
               this.c(world, blockposition2.south(2));
               this.c(world, blockposition2.north(2));
               kx = blockposition.getX();
               j1 = blockposition.getZ();
               EnumDirection enumdirection1 = EnumDirection.EnumDirectionLimit.HORIZONTAL.a(random);
               if (enumdirection1 != enumdirection) {
                  int var29 = i1 - random.nextInt(2) - 1;
                  int j2x = 1 + random.nextInt(3);
                  k1 = 0;

                  for (int l2 = var29; l2 < i && j2x > 0; j2x--) {
                     if (l2 >= 1) {
                        int k2 = blockposition.getY() + l2;
                        kx += enumdirection1.getAdjacentX();
                        j1 += enumdirection1.getAdjacentZ();
                        Material material1 = world.getType(kx, k2, j1).getBlock().getMaterial();
                        if (material1 == Material.AIR || material1 == Material.LEAVES) {
                           this.b(world, new BlockPosition(kx, k2, j1));
                           k1 = k2;
                        }
                     }

                     l2++;
                  }

                  if (k1 > 0) {
                     BlockPosition blockposition4 = new BlockPosition(kx, k1, j1);

                     for (int k2 = -2; k2 <= 2; k2++) {
                        for (int i3 = -2; i3 <= 2; i3++) {
                           if (Math.abs(k2) != 2 || Math.abs(i3) != 2) {
                              this.c(world, blockposition4.a(k2, 0, i3));
                           }
                        }
                     }

                     blockposition4 = blockposition4.up();

                     for (int var36 = -1; var36 <= 1; var36++) {
                        for (int i3x = -1; i3x <= 1; i3x++) {
                           this.c(world, blockposition4.a(var36, 0, i3x));
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

   private void b(World world, BlockPosition blockposition) {
      this.a(world, blockposition, a);
   }

   private void c(World world, BlockPosition blockposition) {
      Material material = world.getType(blockposition).getBlock().getMaterial();
      if (material == Material.AIR || material == Material.LEAVES) {
         this.a(world, blockposition, b);
      }
   }
}
