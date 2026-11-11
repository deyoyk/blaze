package net.minecraft.server.v1_8_R3;

import java.util.Random;

public class BlockWeb extends Block {
   public BlockWeb() {
      super(Material.WEB);
      this.a(CreativeModeTab.c);
   }

   @Override
   public void a(World var1, BlockPosition var2, IBlockData var3, Entity var4) {
      var4.aA();
   }

   @Override
   public boolean c() {
      return false;
   }

   @Override
   public AxisAlignedBB a(World var1, BlockPosition var2, IBlockData var3) {
      return null;
   }

   @Override
   public boolean d() {
      return false;
   }

   @Override
   public Item getDropType(IBlockData var1, Random var2, int var3) {
      return Items.STRING;
   }

   @Override
   protected boolean I() {
      return true;
   }
}
