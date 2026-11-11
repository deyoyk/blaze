package net.minecraft.server.v1_8_R3;

import net.jafama.FastMath;

public class EntityEnderSignal extends Entity {
   private double a;
   private double b;
   private double c;
   private int d;
   private boolean e;

   public EntityEnderSignal(World world) {
      super(world);
      this.setSize(0.25F, 0.25F);
   }

   @Override
   protected void h() {
   }

   public EntityEnderSignal(World world, double d0, double d1, double d2) {
      super(world);
      this.d = 0;
      this.setSize(0.25F, 0.25F);
      this.setPosition(d0, d1, d2);
   }

   public void a(BlockPosition blockposition) {
      double d0 = blockposition.getX();
      int i = blockposition.getY();
      double d1 = blockposition.getZ();
      double d2 = d0 - this.locX;
      double d3 = d1 - this.locZ;
      float f = MathHelper.sqrt(d2 * d2 + d3 * d3);
      if (f > 12.0F) {
         this.a = this.locX + d2 / f * 12.0;
         this.c = this.locZ + d3 / f * 12.0;
         this.b = this.locY + 8.0;
      } else {
         this.a = d0;
         this.b = i;
         this.c = d1;
      }

      this.d = 0;
      this.e = this.random.nextInt(5) > 0;
   }

   @Override
   public void t_() {
      this.P = this.locX;
      this.Q = this.locY;
      this.R = this.locZ;
      super.t_();
      this.locX = this.locX + this.motX;
      this.locY = this.locY + this.motY;
      this.locZ = this.locZ + this.motZ;
      float f = MathHelper.sqrt(this.motX * this.motX + this.motZ * this.motZ);
      this.yaw = (float)(MathHelper.b(this.motX, this.motZ) * 180.0 / (float) Math.PI);
      this.pitch = (float)(MathHelper.b(this.motY, f) * 180.0 / (float) Math.PI);

      while (this.pitch - this.lastPitch < -180.0F) {
         this.lastPitch -= 360.0F;
      }

      while (this.pitch - this.lastPitch >= 180.0F) {
         this.lastPitch += 360.0F;
      }

      while (this.yaw - this.lastYaw < -180.0F) {
         this.lastYaw -= 360.0F;
      }

      while (this.yaw - this.lastYaw >= 180.0F) {
         this.lastYaw += 360.0F;
      }

      this.pitch = this.lastPitch + (this.pitch - this.lastPitch) * 0.2F;
      this.yaw = this.lastYaw + (this.yaw - this.lastYaw) * 0.2F;
      double d0 = this.a - this.locX;
      double d1 = this.c - this.locZ;
      float f1 = (float)Math.sqrt(d0 * d0 + d1 * d1);
      float f2 = (float)MathHelper.b(d1, d0);
      double d2 = f + (f1 - f) * 0.0025;
      if (f1 < 1.0F) {
         d2 *= 0.8;
         this.motY *= 0.8;
      }

      this.motX = FastMath.cos(f2) * d2;
      this.motZ = FastMath.sin(f2) * d2;
      if (this.locY < this.b) {
         this.motY = this.motY + (1.0 - this.motY) * 0.015F;
      } else {
         this.motY = this.motY + (-1.0 - this.motY) * 0.015F;
      }

      float f3 = 0.25F;
      if (this.V()) {
         for (int i = 0; i < 4; i++) {
            this.world
               .addParticle(
                  EnumParticle.WATER_BUBBLE,
                  this.locX - this.motX * f3,
                  this.locY - this.motY * f3,
                  this.locZ - this.motZ * f3,
                  this.motX,
                  this.motY,
                  this.motZ
               );
         }
      } else {
         this.world
            .addParticle(
               EnumParticle.PORTAL,
               this.locX - this.motX * f3 + this.random.nextDouble() * 0.6 - 0.3,
               this.locY - this.motY * f3 - 0.5,
               this.locZ - this.motZ * f3 + this.random.nextDouble() * 0.6 - 0.3,
               this.motX,
               this.motY,
               this.motZ
            );
      }

      this.setPosition(this.locX, this.locY, this.locZ);
      this.d++;
      if (this.d > 80) {
         this.die();
         if (this.e) {
            this.world.addEntity(new EntityItem(this.world, this.locX, this.locY, this.locZ, new ItemStack(Items.ENDER_EYE)));
         } else {
            this.world.triggerEffect(2003, new BlockPosition(this), 0);
         }
      }
   }

   @Override
   public void b(NBTTagCompound nbttagcompound) {
   }

   @Override
   public void a(NBTTagCompound nbttagcompound) {
   }

   @Override
   public float c(float f) {
      return 1.0F;
   }

   @Override
   public boolean aD() {
      return false;
   }
}
