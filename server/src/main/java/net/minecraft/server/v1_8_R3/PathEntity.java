package net.minecraft.server.v1_8_R3;

public class PathEntity {
   private final PathPoint[] a;
   private int b;
   private int c;

   public PathEntity(PathPoint[] apathpoint) {
      this.a = apathpoint;
      this.c = apathpoint.length;
   }

   public void a() {
      this.b++;
   }

   public boolean b() {
      return this.b >= this.c;
   }

   public PathPoint c() {
      return this.c > 0 ? this.a[this.c - 1] : null;
   }

   public PathPoint a(int i) {
      return this.a[i];
   }

   public int d() {
      return this.c;
   }

   public void b(int i) {
      this.c = i;
   }

   public int e() {
      return this.b;
   }

   public void c(int i) {
      this.b = i;
   }

   public Vec3D a(Entity entity, int i) {
      double d0 = this.a[i].a + (int)(entity.width + 1.0F) * 0.5;
      double d1 = this.a[i].b;
      double d2 = this.a[i].c + (int)(entity.width + 1.0F) * 0.5;
      return new Vec3D(d0, d1, d2);
   }

   public Vec3D a(Entity entity) {
      return this.a(entity, this.b);
   }

   public boolean a(PathEntity pathentity) {
      if (pathentity == null) {
         return false;
      } else if (pathentity.a.length != this.a.length) {
         return false;
      } else {
         for (int i = 0; i < this.a.length; i++) {
            if (this.a[i].a != pathentity.a[i].a || this.a[i].b != pathentity.a[i].b || this.a[i].c != pathentity.a[i].c) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean b(Vec3D vec3d) {
      PathPoint pathpoint = this.c();
      return pathpoint != null && pathpoint.a == (int)vec3d.a && pathpoint.c == (int)vec3d.c;
   }
}
