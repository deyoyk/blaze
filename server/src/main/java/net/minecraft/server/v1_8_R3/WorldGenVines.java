package net.minecraft.server.v1_8_R3;

import java.util.Random;

public class WorldGenVines extends WorldGenerator {
   @Override
   public boolean generate(World world, Random random, BlockPosition blockposition) {
      while (blockposition.getY() < 128) {
         if (world.isEmpty(blockposition)) {
            EnumDirection[] var7;
            for (EnumDirection enumdirection : var7 = EnumDirection.EnumDirectionLimit.HORIZONTAL.a()) {
               if (Blocks.VINE.canPlace(world, blockposition, enumdirection)) {
                  IBlockData iblockdata = Blocks.VINE
                     .getBlockData()
                     .set(BlockVine.NORTH, enumdirection == EnumDirection.NORTH)
                     .set(BlockVine.EAST, enumdirection == EnumDirection.EAST)
                     .set(BlockVine.SOUTH, enumdirection == EnumDirection.SOUTH)
                     .set(BlockVine.WEST, enumdirection == EnumDirection.WEST);
                  world.setTypeAndData(blockposition, iblockdata, 2);
                  break;
               }
            }
         } else {
            blockposition = blockposition.a(random.nextInt(4) - random.nextInt(4), 0, random.nextInt(4) - random.nextInt(4));
         }

         blockposition = blockposition.up();
      }

      return true;
   }
}
