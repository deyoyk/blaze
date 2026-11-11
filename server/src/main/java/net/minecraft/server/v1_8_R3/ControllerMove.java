package net.minecraft.server.v1_8_R3;

public class ControllerMove {
   protected EntityInsentient a;
   protected double b;
   protected double c;
   protected double d;
   protected double e;
   protected boolean f;

   public ControllerMove(EntityInsentient var1) {
      this.a = var1;
      this.b = var1.locX;
      this.c = var1.locY;
      this.d = var1.locZ;
   }

   public boolean a() {
      return this.f;
   }

   public double b() {
      return this.e;
   }

   public void a(double var1, double var3, double var5, double var7) {
      this.b = var1;
      this.c = var3;
      this.d = var5;
      this.e = var7;
      this.f = true;
   }

   public void c() {
      this.a.n(0.0F);
      if (this.f) {
         this.f = false;
         int var1 = MathHelper.floor(this.a.getBoundingBox().b + 0.5);
         double var2 = this.b - this.a.locX;
         double var4 = this.d - this.a.locZ;
         double var6 = this.c - var1;
         double var8 = var2 * var2 + var6 * var6 + var4 * var4;
         if (!(var8 < 2.5000003E-7F)) {
            float var10 = (float)(MathHelper.b(var4, var2) * 180.0 / (float) Math.PI) - 90.0F;
            this.a.yaw = this.a(this.a.yaw, var10, 30.0F);
            this.a.k((float)(this.e * this.a.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue()));
            if (var6 > 0.0 && var2 * var2 + var4 * var4 < 1.0) {
               this.a.getControllerJump().a();
            }
         }
      }
   }

   protected float a(float var1, float var2, float var3) {
      float var4 = MathHelper.g(var2 - var1);
      if (var4 > var3) {
         var4 = var3;
      }

      if (var4 < -var3) {
         var4 = -var3;
      }

      float var5 = var1 + var4;
      if (var5 < 0.0F) {
         var5 += 360.0F;
      } else if (var5 > 360.0F) {
         var5 -= 360.0F;
      }

      return var5;
   }

   public double d() {
      return this.b;
   }

   public double e() {
      return this.c;
   }

   public double f() {
      return this.d;
   }
}
