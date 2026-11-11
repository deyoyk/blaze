package net.minecraft.server.v1_8_R3;

import java.util.Random;

public class BlockLightStone extends Block {
   public BlockLightStone(Material var1) {
      super(var1);
      this.a(CreativeModeTab.b);
   }

   @Override
   public int getDropCount(int var1, Random var2) {
      return MathHelper.clamp(this.a(var2) + var2.nextInt(var1 + 1), 1, 4);
   }

   @Override
   public int a(Random var1) {
      return 2 + var1.nextInt(3);
   }

   @Override
   public Item getDropType(IBlockData var1, Random var2, int var3) {
      return Items.GLOWSTONE_DUST;
   }

   @Override
   public MaterialMapColor g(IBlockData var1) {
      return MaterialMapColor.d;
   }
}
