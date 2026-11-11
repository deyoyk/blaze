package net.minecraft.server.v1_8_R3;

import java.util.Random;

public class BlockMelon extends Block {
   protected BlockMelon() {
      super(Material.PUMPKIN, MaterialMapColor.u);
      this.a(CreativeModeTab.b);
   }

   @Override
   public Item getDropType(IBlockData var1, Random var2, int var3) {
      return Items.MELON;
   }

   @Override
   public int a(Random var1) {
      return 3 + var1.nextInt(5);
   }

   @Override
   public int getDropCount(int var1, Random var2) {
      return Math.min(9, this.a(var2) + var2.nextInt(1 + var1));
   }
}
