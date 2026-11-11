package net.minecraft.server.v1_8_R3;

import java.util.Random;

public class WorldGenTaigaStructure extends WorldGenerator {
   private final Block a;
   private final int b;

   public WorldGenTaigaStructure(Block block, int i) {
      super(false);
      this.a = block;
      this.b = i;
   }

   @Override
   public boolean generate(World world, Random random, BlockPosition blockposition) {
      while (blockposition.getY() > 3) {
         Block block = world.getType(blockposition.getX(), blockposition.getY() - 1, blockposition.getZ()).getBlock();
         if (block.getMaterial() == Material.AIR || block != Blocks.GRASS && block != Blocks.DIRT && block != Blocks.STONE) {
            blockposition = blockposition.down();
            continue;
         }
         break;
      }

      if (blockposition.getY() <= 3) {
         return false;
      } else {
         int i = this.b;

         for (int j = 0; i >= 0 && j < 3; j++) {
            int k = i + random.nextInt(2);
            int l = i + random.nextInt(2);
            int i1 = i + random.nextInt(2);
            float f = (k + l + i1) * 0.333F + 0.5F;

            for (BlockPosition blockposition1 : BlockPosition.a(blockposition.a(-k, -l, -i1), blockposition.a(k, l, i1))) {
               if (blockposition1.i(blockposition) <= f * f) {
                  world.setTypeAndData(blockposition1, this.a.getBlockData(), 4);
               }
            }

            blockposition = blockposition.a(-(i + 1) + random.nextInt(2 + i * 2), -random.nextInt(2), -(i + 1) + random.nextInt(2 + i * 2));
         }

         return true;
      }
   }
}
