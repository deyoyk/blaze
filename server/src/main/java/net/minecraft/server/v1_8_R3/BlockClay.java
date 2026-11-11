package net.minecraft.server.v1_8_R3;

import java.util.Random;

public class BlockClay extends Block {
   public BlockClay() {
      super(Material.CLAY);
      this.a(CreativeModeTab.b);
   }

   @Override
   public Item getDropType(IBlockData var1, Random var2, int var3) {
      return Items.CLAY_BALL;
   }

   @Override
   public int a(Random var1) {
      return 4;
   }
}
