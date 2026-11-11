package net.minecraft.server.v1_8_R3;

import java.util.Random;

public class WorldGenEnder extends WorldGenerator {
   private Block a;

   public WorldGenEnder(Block block) {
      this.a = block;
   }

   @Override
   public boolean generate(World world, Random random, BlockPosition blockposition) {
      if (world.isEmpty(blockposition) && world.getType(blockposition.getX(), blockposition.getY() - 1, blockposition.getZ()).getBlock() == this.a) {
         int i = random.nextInt(32) + 6;
         int j = random.nextInt(4) + 1;

         for (int k = blockposition.getX() - j; k <= blockposition.getX() + j; k++) {
            for (int l = blockposition.getZ() - j; l <= blockposition.getZ() + j; l++) {
               int i1 = k - blockposition.getX();
               int j1 = l - blockposition.getZ();
               if (i1 * i1 + j1 * j1 <= j * j + 1 && world.getType(k, blockposition.getY() - 1, l).getBlock() != this.a) {
                  return false;
               }
            }
         }

         for (int var11 = blockposition.getY(); var11 < blockposition.getY() + i && var11 < 256; var11++) {
            for (int lx = blockposition.getX() - j; lx <= blockposition.getX() + j; lx++) {
               for (int i1 = blockposition.getZ() - j; i1 <= blockposition.getZ() + j; i1++) {
                  int j1 = lx - blockposition.getX();
                  int k1 = i1 - blockposition.getZ();
                  if (j1 * j1 + k1 * k1 <= j * j + 1) {
                     world.setTypeAndData(new BlockPosition(lx, var11, i1), Blocks.OBSIDIAN.getBlockData(), 2);
                  }
               }
            }
         }

         EntityEnderCrystal entityendercrystal = new EntityEnderCrystal(world);
         entityendercrystal.setPositionRotation(
            blockposition.getX() + 0.5F, blockposition.getY() + i, blockposition.getZ() + 0.5F, random.nextFloat() * 360.0F, 0.0F
         );
         world.addEntity(entityendercrystal);
         world.setTypeAndData(blockposition.up(i), Blocks.BEDROCK.getBlockData(), 2);
         return true;
      } else {
         return false;
      }
   }
}
