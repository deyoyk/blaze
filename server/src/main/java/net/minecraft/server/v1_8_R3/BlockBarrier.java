package net.minecraft.server.v1_8_R3;

public class BlockBarrier extends Block {
   protected BlockBarrier() {
      super(Material.BANNER);
      this.x();
      this.b(6000001.0F);
      this.K();
      this.t = true;
   }

   @Override
   public int b() {
      return -1;
   }

   @Override
   public boolean c() {
      return false;
   }

   @Override
   public void dropNaturally(World var1, BlockPosition var2, IBlockData var3, float var4, int var5) {
   }
}
