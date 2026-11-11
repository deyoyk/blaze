package net.minecraft.server.v1_8_R3;

public class Vec3D {
   public double a;
   public double b;
   public double c;

   public Vec3D(double paramDouble1, double paramDouble2, double paramDouble3) {
      if (paramDouble1 == -0.0) {
         paramDouble1 = 0.0;
      }

      if (paramDouble2 == -0.0) {
         paramDouble2 = 0.0;
      }

      if (paramDouble3 == -0.0) {
         paramDouble3 = 0.0;
      }

      this.a = paramDouble1;
      this.b = paramDouble2;
      this.c = paramDouble3;
   }

   public Vec3D(BaseBlockPosition paramBaseBlockPosition) {
      this(paramBaseBlockPosition.getX(), paramBaseBlockPosition.getY(), paramBaseBlockPosition.getZ());
   }

   public Vec3D a() {
      double d = MathHelper.sqrt(this.a * this.a + this.b * this.b + this.c * this.c);
      return d < 1.0E-4 ? new Vec3D(0.0, 0.0, 0.0) : new Vec3D(this.a / d, this.b / d, this.c / d);
   }

   public double b(Vec3D paramVec3D) {
      return this.a * paramVec3D.a + this.b * paramVec3D.b + this.c * paramVec3D.c;
   }

   public Vec3D d(Vec3D paramVec3D) {
      return this.a(paramVec3D.a, paramVec3D.b, paramVec3D.c);
   }

   public Vec3D a(double paramDouble1, double paramDouble2, double paramDouble3) {
      return this.add(-paramDouble1, -paramDouble2, -paramDouble3);
   }

   public Vec3D e(Vec3D paramVec3D) {
      return this.add(paramVec3D.a, paramVec3D.b, paramVec3D.c);
   }

   public Vec3D add(double paramDouble1, double paramDouble2, double paramDouble3) {
      return new Vec3D(this.a + paramDouble1, this.b + paramDouble2, this.c + paramDouble3);
   }

   public double distanceSquared(Vec3D paramVec3D) {
      double d1 = paramVec3D.a - this.a;
      double d2 = paramVec3D.b - this.b;
      double d3 = paramVec3D.c - this.c;
      return d1 * d1 + d2 * d2 + d3 * d3;
   }

   public double b() {
      return MathHelper.sqrt(this.a * this.a + this.b * this.b + this.c * this.c);
   }

   public Vec3D a(Vec3D paramVec3D, double paramDouble) {
      double d1 = paramVec3D.a - this.a;
      double d2 = paramVec3D.b - this.b;
      double d3 = paramVec3D.c - this.c;
      if (d1 * d1 < 1.0E-7F) {
         return null;
      } else {
         double d4 = (paramDouble - this.a) / d1;
         return !(d4 < 0.0) && !(d4 > 1.0) ? new Vec3D(this.a + d1 * d4, this.b + d2 * d4, this.c + d3 * d4) : null;
      }
   }

   public Vec3D b(Vec3D paramVec3D, double paramDouble) {
      double d1 = paramVec3D.a - this.a;
      double d2 = paramVec3D.b - this.b;
      double d3 = paramVec3D.c - this.c;
      if (d2 * d2 < 1.0E-7F) {
         return null;
      } else {
         double d4 = (paramDouble - this.b) / d2;
         return !(d4 < 0.0) && !(d4 > 1.0) ? new Vec3D(this.a + d1 * d4, this.b + d2 * d4, this.c + d3 * d4) : null;
      }
   }

   public Vec3D c(Vec3D paramVec3D, double paramDouble) {
      double d1 = paramVec3D.a - this.a;
      double d2 = paramVec3D.b - this.b;
      double d3 = paramVec3D.c - this.c;
      if (d3 * d3 < 1.0E-7F) {
         return null;
      } else {
         double d4 = (paramDouble - this.c) / d3;
         return !(d4 < 0.0) && !(d4 > 1.0) ? new Vec3D(this.a + d1 * d4, this.b + d2 * d4, this.c + d3 * d4) : null;
      }
   }

   @Override
   public String toString() {
      return "(" + this.a + ", " + this.b + ", " + this.c + ")";
   }

   public Vec3D a(float paramFloat) {
      float f1 = MathHelper.cos(paramFloat);
      float f2 = MathHelper.sin(paramFloat);
      double d2 = this.b * f1 + this.c * f2;
      double d3 = this.c * f1 - this.b * f2;
      return new Vec3D(this.a, d2, d3);
   }

   public Vec3D b(float paramFloat) {
      float f1 = MathHelper.cos(paramFloat);
      float f2 = MathHelper.sin(paramFloat);
      return new Vec3D(this.a * f1 + this.c * f2, this.b, this.c * f1 - this.a * f2);
   }

   public static final class MutableVec3D extends Vec3D {
      public MutableVec3D() {
         super(0.0, 0.0, 0.0);
      }

      public MutableVec3D(int i, int j, int k) {
         super(0.0, 0.0, 0.0);
         this.setX(i);
         this.setY(j);
         this.setZ(k);
      }

      public void setX(double a) {
         this.a = a;
      }

      public void setY(double b) {
         this.b = b;
      }

      public void setZ(double c) {
         this.c = c;
      }

      public Vec3D.MutableVec3D add(double x, double y, double z) {
         return this.c(this.a + x, this.b + y, this.c + z);
      }

      public Vec3D.MutableVec3D a() {
         double d = MathHelper.sqrt(this.a * this.a + this.b * this.b + this.c * this.c);
         if (d < 1.0E-4) {
            this.c(0.0, 0.0, 0.0);
         }

         this.c(this.a / d, this.b / d, this.c / d);
         return this;
      }

      public Vec3D.MutableVec3D setValues(double x, double y, double z) {
         return this.c(x, y, z);
      }

      public Vec3D.MutableVec3D c(double i, double j, double k) {
         this.setX(i);
         this.setY(j);
         this.setZ(k);
         return this;
      }
   }
}
