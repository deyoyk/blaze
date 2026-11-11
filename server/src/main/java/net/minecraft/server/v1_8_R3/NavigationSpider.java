package net.minecraft.server.v1_8_R3;

public class NavigationSpider extends Navigation {
   private BlockPosition f;

   public NavigationSpider(EntityInsentient var1, World var2) {
      super(var1, var2);
   }

   @Override
   public PathEntity a(BlockPosition var1) {
      this.f = var1;
      return super.a(var1);
   }

   @Override
   public PathEntity a(Entity var1) {
      this.f = new BlockPosition(var1);
      return super.a(var1);
   }

   @Override
   public boolean a(Entity var1, double var2) {
      PathEntity var4 = this.a(var1);
      if (var4 != null) {
         return this.a(var4, var2);
      } else {
         this.f = new BlockPosition(var1);
         this.e = var2;
         return true;
      }
   }

   @Override
   public void k() {
      if (!this.m()) {
         super.k();
      } else {
         if (this.f != null) {
            double var1 = this.b.width * this.b.width;
            if (!(this.b.c(this.f) < var1)
               && (!(this.b.locY > this.f.getY()) || !(this.b.c(new BlockPosition(this.f.getX(), MathHelper.floor(this.b.locY), this.f.getZ())) < var1))) {
               this.b.getControllerMove().a(this.f.getX(), this.f.getY(), this.f.getZ(), this.e);
            } else {
               this.f = null;
            }
         }
      }
   }
}
