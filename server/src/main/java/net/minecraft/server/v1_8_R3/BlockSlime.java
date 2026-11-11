package net.minecraft.server.v1_8_R3;

public class BlockSlime extends BlockHalfTransparent {
   public BlockSlime() {
      super(Material.CLAY, false, MaterialMapColor.c);
      this.a(CreativeModeTab.c);
      this.frictionFactor = 0.8F;
   }

   @Override
   public void fallOn(World var1, BlockPosition var2, Entity var3, float var4) {
      if (var3.isSneaking()) {
         super.fallOn(var1, var2, var3, var4);
      } else {
         var3.e(var4, 0.0F);
      }
   }

   @Override
   public void a(World var1, Entity var2) {
      if (var2.isSneaking()) {
         super.a(var1, var2);
      } else if (var2.motY < 0.0) {
         var2.motY = -var2.motY;
      }
   }

   @Override
   public void a(World var1, BlockPosition var2, Entity var3) {
      if (Math.abs(var3.motY) < 0.1 && !var3.isSneaking()) {
         double var4 = 0.4 + Math.abs(var3.motY) * 0.2;
         var3.motX *= var4;
         var3.motZ *= var4;
      }

      super.a(var1, var2, var3);
   }
}
