package net.minecraft.server.v1_8_R3;

import java.util.Random;

public class WorldGenForestTree extends WorldGenTreeAbstract {
   private static final IBlockData a = Blocks.LOG2.getBlockData().set(BlockLog2.VARIANT, BlockWood.EnumLogVariant.DARK_OAK);
   private static final IBlockData b = Blocks.LEAVES2
      .getBlockData()
      .set(BlockLeaves2.VARIANT, BlockWood.EnumLogVariant.DARK_OAK)
      .set(BlockLeaves.CHECK_DECAY, Boolean.FALSE);

   public WorldGenForestTree(boolean flag) {
      super(flag);
   }

   @Override
   public boolean generate(World world, Random random, BlockPosition blockposition) {
      int i = random.nextInt(3) + random.nextInt(2) + 6;
      int j = blockposition.getX();
      int k = blockposition.getY();
      int l = blockposition.getZ();
      if (k >= 1 && k + i + 1 < 256) {
         BlockPosition blockposition1 = blockposition.down();
         Block block = world.getType(blockposition1).getBlock();
         if (block != Blocks.GRASS && block != Blocks.DIRT) {
            return false;
         } else if (!this.a(world, blockposition, i)) {
            return false;
         } else {
            this.a(world, blockposition1);
            this.a(world, blockposition1.east());
            this.a(world, blockposition1.south());
            this.a(world, blockposition1.south().east());
            EnumDirection enumdirection = EnumDirection.EnumDirectionLimit.HORIZONTAL.a(random);
            int i1 = i - random.nextInt(4);
            int j1 = 2 - random.nextInt(3);
            int k1 = j;
            int l1 = l;
            int i2 = k + i - 1;

            for (int j2 = 0; j2 < i; j2++) {
               if (j2 >= i1 && j1 > 0) {
                  k1 += enumdirection.getAdjacentX();
                  l1 += enumdirection.getAdjacentZ();
                  j1--;
               }

               int k2 = k + j2;
               BlockPosition blockposition2 = new BlockPosition(k1, k2, l1);
               Material material = world.getType(blockposition2).getBlock().getMaterial();
               if (material == Material.AIR || material == Material.LEAVES) {
                  this.b(world, blockposition2);
                  this.b(world, blockposition2.east());
                  this.b(world, blockposition2.south());
                  this.b(world, blockposition2.east().south());
               }
            }

            for (int var21 = -2; var21 <= 0; var21++) {
               for (int k2 = -2; k2 <= 0; k2++) {
                  byte b0 = -1;
                  this.a(world, k1 + var21, i2 + b0, l1 + k2);
                  this.a(world, 1 + k1 - var21, i2 + b0, l1 + k2);
                  this.a(world, k1 + var21, i2 + b0, 1 + l1 - k2);
                  this.a(world, 1 + k1 - var21, i2 + b0, 1 + l1 - k2);
                  if ((var21 > -2 || k2 > -1) && (var21 != -1 || k2 != -2)) {
                     byte b1 = 1;
                     this.a(world, k1 + var21, i2 + b1, l1 + k2);
                     this.a(world, 1 + k1 - var21, i2 + b1, l1 + k2);
                     this.a(world, k1 + var21, i2 + b1, 1 + l1 - k2);
                     this.a(world, 1 + k1 - var21, i2 + b1, 1 + l1 - k2);
                  }
               }
            }

            if (random.nextBoolean()) {
               this.a(world, k1, i2 + 2, l1);
               this.a(world, k1 + 1, i2 + 2, l1);
               this.a(world, k1 + 1, i2 + 2, l1 + 1);
               this.a(world, k1, i2 + 2, l1 + 1);
            }

            for (int var22 = -3; var22 <= 4; var22++) {
               for (int k2x = -3; k2x <= 4; k2x++) {
                  if ((var22 != -3 || k2x != -3)
                     && (var22 != -3 || k2x != 4)
                     && (var22 != 4 || k2x != -3)
                     && (var22 != 4 || k2x != 4)
                     && (Math.abs(var22) < 3 || Math.abs(k2x) < 3)) {
                     this.a(world, k1 + var22, i2, l1 + k2x);
                  }
               }
            }

            for (int var23 = -1; var23 <= 2; var23++) {
               for (int k2xx = -1; k2xx <= 2; k2xx++) {
                  if ((var23 < 0 || var23 > 1 || k2xx < 0 || k2xx > 1) && random.nextInt(3) <= 0) {
                     int l2 = random.nextInt(3) + 2;

                     for (int i3 = 0; i3 < l2; i3++) {
                        this.b(world, new BlockPosition(j + var23, i2 - i3 - 1, l + k2xx));
                     }

                     for (int var31 = -1; var31 <= 1; var31++) {
                        for (int j3 = -1; j3 <= 1; j3++) {
                           this.a(world, k1 + var23 + var31, i2, l1 + k2xx + j3);
                        }
                     }

                     for (int var32 = -2; var32 <= 2; var32++) {
                        for (int j3 = -2; j3 <= 2; j3++) {
                           if (Math.abs(var32) != 2 || Math.abs(j3) != 2) {
                              this.a(world, k1 + var23 + var32, i2 - 1, l1 + k2xx + j3);
                           }
                        }
                     }
                  }
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   private boolean a(World world, BlockPosition blockposition, int i) {
      int j = blockposition.getX();
      int k = blockposition.getY();
      int l = blockposition.getZ();

      for (int i1 = 0; i1 <= i + 1; i1++) {
         byte b0 = 1;
         if (i1 == 0) {
            b0 = 0;
         }

         if (i1 >= i - 1) {
            b0 = 2;
         }

         for (int j1 = -b0; j1 <= b0; j1++) {
            for (int k1 = -b0; k1 <= b0; k1++) {
               if (!this.a(world.getType(j + j1, k + i1, l + k1).getBlock())) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   private void b(World world, BlockPosition blockposition) {
      if (this.a(world.getType(blockposition).getBlock())) {
         this.a(world, blockposition, a);
      }
   }

   private void a(World world, int i, int j, int k) {
      BlockPosition blockposition = new BlockPosition(i, j, k);
      Block block = world.getType(blockposition).getBlock();
      if (block.getMaterial() == Material.AIR) {
         this.a(world, blockposition, b);
      }
   }
}
