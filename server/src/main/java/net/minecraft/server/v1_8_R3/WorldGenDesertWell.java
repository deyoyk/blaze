package net.minecraft.server.v1_8_R3;

import com.google.common.base.Predicates;
import java.util.Random;

public class WorldGenDesertWell extends WorldGenerator {
   private static final BlockStatePredicate a = BlockStatePredicate.a(Blocks.SAND).a(BlockSand.VARIANT, Predicates.equalTo(BlockSand.EnumSandVariant.SAND));
   private final IBlockData b = Blocks.STONE_SLAB
      .getBlockData()
      .set(BlockDoubleStepAbstract.VARIANT, BlockDoubleStepAbstract.EnumStoneSlabVariant.SAND)
      .set(BlockStepAbstract.HALF, BlockStepAbstract.EnumSlabHalf.BOTTOM);
   private final IBlockData c = Blocks.SANDSTONE.getBlockData();
   private final IBlockData d = Blocks.FLOWING_WATER.getBlockData();

   @Override
   public boolean generate(World world, Random random, BlockPosition blockposition) {
      while (world.isEmpty(blockposition) && blockposition.getY() > 2) {
         blockposition = blockposition.down();
      }

      if (!a.a(world.getType(blockposition))) {
         return false;
      } else {
         for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
               if (world.isEmpty(blockposition.a(i, -1, j)) && world.isEmpty(blockposition.a(i, -2, j))) {
                  return false;
               }
            }
         }

         for (int var8 = -1; var8 <= 0; var8++) {
            for (int jx = -2; jx <= 2; jx++) {
               for (int k = -2; k <= 2; k++) {
                  world.setTypeAndData(blockposition.a(jx, var8, k), this.c, 2);
               }
            }
         }

         world.setTypeAndData(blockposition, this.d, 2);

         for (EnumDirection enumdirection : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
            world.setTypeAndData(blockposition.shift(enumdirection), this.d, 2);
         }

         for (int var9 = -2; var9 <= 2; var9++) {
            for (int jx = -2; jx <= 2; jx++) {
               if (var9 == -2 || var9 == 2 || jx == -2 || jx == 2) {
                  world.setTypeAndData(blockposition.a(var9, 1, jx), this.c, 2);
               }
            }
         }

         world.setTypeAndData(blockposition.a(2, 1, 0), this.b, 2);
         world.setTypeAndData(blockposition.a(-2, 1, 0), this.b, 2);
         world.setTypeAndData(blockposition.a(0, 1, 2), this.b, 2);
         world.setTypeAndData(blockposition.a(0, 1, -2), this.b, 2);

         for (int var10 = -1; var10 <= 1; var10++) {
            for (int jxx = -1; jxx <= 1; jxx++) {
               if (var10 == 0 && jxx == 0) {
                  world.setTypeAndData(blockposition.a(var10, 4, jxx), this.c, 2);
               } else {
                  world.setTypeAndData(blockposition.a(var10, 4, jxx), this.b, 2);
               }
            }
         }

         for (int var11 = 1; var11 <= 3; var11++) {
            world.setTypeAndData(blockposition.a(-1, var11, -1), this.c, 2);
            world.setTypeAndData(blockposition.a(-1, var11, 1), this.c, 2);
            world.setTypeAndData(blockposition.a(1, var11, -1), this.c, 2);
            world.setTypeAndData(blockposition.a(1, var11, 1), this.c, 2);
         }

         return true;
      }
   }
}
