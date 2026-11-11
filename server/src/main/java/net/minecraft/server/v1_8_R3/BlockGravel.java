package net.minecraft.server.v1_8_R3;

import java.util.Random;

public class BlockGravel extends BlockFalling {
   @Override
   public Item getDropType(IBlockData var1, Random var2, int var3) {
      if (var3 > 3) {
         var3 = 3;
      }

      return var2.nextInt(10 - var3 * 3) == 0 ? Items.FLINT : Item.getItemOf(this);
   }

   @Override
   public MaterialMapColor g(IBlockData var1) {
      return MaterialMapColor.m;
   }
}
