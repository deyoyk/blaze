package net.minecraft.server.v1_8_R3;

import java.util.Random;

public class WorldGenForest extends WorldGenTreeAbstract {
   private static final IBlockData a = Blocks.LOG.getBlockData().set(BlockLog1.VARIANT, BlockWood.EnumLogVariant.BIRCH);
   private static final IBlockData b = Blocks.LEAVES
      .getBlockData()
      .set(BlockLeaves1.VARIANT, BlockWood.EnumLogVariant.BIRCH)
      .set(BlockLeaves1.CHECK_DECAY, Boolean.FALSE);
   private final boolean c;

   public WorldGenForest(boolean flag, boolean flag1) {
      super(flag);
      this.c = flag1;
   }

   @Override
   public boolean generate(World world, Random random, BlockPosition blockposition) {
      int i = random.nextInt(3) + 5;
      if (this.c) {
         i += random.nextInt(7);
      }

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
            if ((block == Blocks.GRASS || block == Blocks.DIRT || block == Blocks.FARMLAND) && blockposition.getY() < 256 - i - 1) {
               this.a(world, blockposition.down());

               for (int i1 = blockposition.getY() - 3 + i; i1 <= blockposition.getY() + i; i1++) {
                  int j1 = i1 - (blockposition.getY() + i);
                  int j = 1 - j1 / 2;

                  for (int kx = blockposition.getX() - j; kx <= blockposition.getX() + j; kx++) {
                     int k1 = kx - blockposition.getX();

                     for (int l1 = blockposition.getZ() - j; l1 <= blockposition.getZ() + j; l1++) {
                        int i2 = l1 - blockposition.getZ();
                        if (Math.abs(k1) != j || Math.abs(i2) != j || random.nextInt(2) != 0 && j1 != 0) {
                           Block block1 = world.getType(kx, i1, l1).getBlock();
                           if (block1.getMaterial() == Material.AIR || block1.getMaterial() == Material.LEAVES) {
                              this.a(world, new BlockPosition(kx, i1, l1), b);
                           }
                        }
                     }
                  }
               }

               for (int var19 = 0; var19 < i; var19++) {
                  Block block2 = world.getType(blockposition.up(var19)).getBlock();
                  if (block2.getMaterial() == Material.AIR || block2.getMaterial() == Material.LEAVES) {
                     this.a(world, blockposition.up(var19), a);
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
}
