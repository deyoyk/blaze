package net.minecraft.server.v1_8_R3;

import java.util.Random;

public class BlockSeaLantern extends Block {
   public BlockSeaLantern(Material var1) {
      super(var1);
      this.a(CreativeModeTab.b);
   }

   @Override
   public int a(Random var1) {
      return 2 + var1.nextInt(2);
   }

   @Override
   public int getDropCount(int var1, Random var2) {
      return MathHelper.clamp(this.a(var2) + var2.nextInt(var1 + 1), 1, 5);
   }

   @Override
   public Item getDropType(IBlockData var1, Random var2, int var3) {
      return Items.PRISMARINE_CRYSTALS;
   }

   @Override
   public MaterialMapColor g(IBlockData var1) {
      return MaterialMapColor.p;
   }

   @Override
   protected boolean I() {
      return true;
   }
}
