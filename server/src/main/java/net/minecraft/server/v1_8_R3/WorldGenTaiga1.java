package net.minecraft.server.v1_8_R3;

import java.util.Random;

public class WorldGenTaiga1 extends WorldGenTreeAbstract {
   private static final IBlockData a = Blocks.LOG.getBlockData().set(BlockLog1.VARIANT, BlockWood.EnumLogVariant.SPRUCE);
   private static final IBlockData b = Blocks.LEAVES
      .getBlockData()
      .set(BlockLeaves1.VARIANT, BlockWood.EnumLogVariant.SPRUCE)
      .set(BlockLeaves.CHECK_DECAY, Boolean.FALSE);

   public WorldGenTaiga1() {
      super(false);
   }

   @Override
   public boolean generate(World world, Random random, BlockPosition blockposition) {
      int i = random.nextInt(5) + 7;
      int j = i - random.nextInt(2) - 3;
      int k = i - j;
      int l = 1 + random.nextInt(k + 1);
      boolean flag = true;
      if (blockposition.getY() >= 1 && blockposition.getY() + i + 1 <= 256) {
         for (int l1 = blockposition.getY(); l1 <= blockposition.getY() + 1 + i && flag; l1++) {
            int k1;
            if (l1 - blockposition.getY() < j) {
               k1 = 0;
            } else {
               k1 = l;
            }

            for (int i1 = blockposition.getX() - k1; i1 <= blockposition.getX() + k1 && flag; i1++) {
               for (int j1 = blockposition.getZ() - k1; j1 <= blockposition.getZ() + k1 && flag; j1++) {
                  if (l1 < 0 || l1 >= 256) {
                     flag = false;
                  } else if (!this.a(world.getType(i1, l1, j1).getBlock())) {
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
               int k1 = 0;

               for (int i2 = blockposition.getY() + i; i2 >= blockposition.getY() + j; i2--) {
                  for (int i1 = blockposition.getX() - k1; i1 <= blockposition.getX() + k1; i1++) {
                     int j1x = i1 - blockposition.getX();

                     for (int j2 = blockposition.getZ() - k1; j2 <= blockposition.getZ() + k1; j2++) {
                        int k2 = j2 - blockposition.getZ();
                        if ((Math.abs(j1x) != k1 || Math.abs(k2) != k1 || k1 <= 0) && !world.getType(i1, i2, j2).getBlock().o()) {
                           this.a(world, new BlockPosition(i1, i2, j2), b);
                        }
                     }
                  }

                  if (k1 >= 1 && i2 == blockposition.getY() + j + 1) {
                     k1--;
                  } else if (k1 < l) {
                     k1++;
                  }
               }

               for (int var20 = 0; var20 < i - 1; var20++) {
                  Block block1 = world.getType(blockposition.up(var20)).getBlock();
                  if (block1.getMaterial() == Material.AIR || block1.getMaterial() == Material.LEAVES) {
                     this.a(world, blockposition.up(var20), a);
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
