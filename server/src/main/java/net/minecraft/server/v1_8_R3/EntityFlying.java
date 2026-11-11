package net.minecraft.server.v1_8_R3;

public abstract class EntityFlying extends EntityInsentient {
   public EntityFlying(World world) {
      super(world);
   }

   @Override
   public void e(float f, float f1) {
   }

   @Override
   protected void a(double d0, boolean flag, Block block, BlockPosition blockposition) {
   }

   @Override
   public void g(float f, float f1) {
      if (this.V()) {
         this.a(f, f1, 0.02F);
         this.move(this.motX, this.motY, this.motZ);
         this.motX *= 0.8F;
         this.motY *= 0.8F;
         this.motZ *= 0.8F;
      } else if (this.ab()) {
         this.a(f, f1, 0.02F);
         this.move(this.motX, this.motY, this.motZ);
         this.motX *= 0.5;
         this.motY *= 0.5;
         this.motZ *= 0.5;
      } else {
         float f2 = 0.91F;
         if (this.onGround) {
            f2 = this.world.getType(MathHelper.floor(this.locX), MathHelper.floor(this.boundingBox.b) - 1, MathHelper.floor(this.locZ)).getBlock().frictionFactor
               * 0.91F;
         }

         float f3 = 0.16277136F / (f2 * f2 * f2);
         this.a(f, f1, this.onGround ? 0.1F * f3 : 0.02F);
         f2 = 0.91F;
         if (this.onGround) {
            f2 = this.world.getType(MathHelper.floor(this.locX), MathHelper.floor(this.boundingBox.b) - 1, MathHelper.floor(this.locZ)).getBlock().frictionFactor
               * 0.91F;
         }

         this.move(this.motX, this.motY, this.motZ);
         this.motX *= f2;
         this.motY *= f2;
         this.motZ *= f2;
      }

      this.aA = this.aB;
      double d0 = this.locX - this.lastX;
      double d1 = this.locZ - this.lastZ;
      float f4 = MathHelper.sqrt(d0 * d0 + d1 * d1) * 4.0F;
      if (f4 > 1.0F) {
         f4 = 1.0F;
      }

      this.aB = this.aB + (f4 - this.aB) * 0.4F;
      this.aC = this.aC + this.aB;
   }

   @Override
   public boolean k_() {
      return false;
   }
}
