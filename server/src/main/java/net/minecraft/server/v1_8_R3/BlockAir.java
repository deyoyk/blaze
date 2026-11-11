package net.minecraft.server.v1_8_R3;

public class BlockAir extends Block {
   protected BlockAir() {
      super(Material.AIR);
   }

   @Override
   public int b() {
      return -1;
   }

   @Override
   public AxisAlignedBB a(World var1, BlockPosition var2, IBlockData var3) {
      return null;
   }

   @Override
   public boolean c() {
      return false;
   }

   @Override
   public boolean a(IBlockData var1, boolean var2) {
      return false;
   }

   @Override
   public void dropNaturally(World var1, BlockPosition var2, IBlockData var3, float var4, int var5) {
   }

   @Override
   public boolean a(World var1, BlockPosition var2) {
      return true;
   }
}
