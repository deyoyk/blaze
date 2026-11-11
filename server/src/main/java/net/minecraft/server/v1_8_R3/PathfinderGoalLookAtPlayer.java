package net.minecraft.server.v1_8_R3;

public class PathfinderGoalLookAtPlayer extends PathfinderGoal {
   protected EntityInsentient a;
   protected Entity b;
   protected float c;
   private int e;
   private final float f;
   protected Class<? extends Entity> d;

   public PathfinderGoalLookAtPlayer(EntityInsentient entityinsentient, Class<? extends Entity> oclass, float f) {
      this.a = entityinsentient;
      this.d = oclass;
      this.c = f;
      this.f = 0.02F;
      this.a(2);
   }

   public PathfinderGoalLookAtPlayer(EntityInsentient entityinsentient, Class<? extends Entity> oclass, float f, float f1) {
      this.a = entityinsentient;
      this.d = oclass;
      this.c = f;
      this.f = f1;
      this.a(2);
   }

   @Override
   public boolean a() {
      if (this.a.bc().nextFloat() >= this.f) {
         return false;
      } else {
         if (this.a.getGoalTarget() != null) {
            this.b = this.a.getGoalTarget();
         }

         if (this.d == EntityHuman.class) {
            this.b = this.a.world.findNearbyPlayer(this.a, this.c);
         } else {
            this.b = this.a.world.a(this.d, this.a.getBoundingBox().grow(this.c, 3.0, this.c), this.a);
         }

         return this.b != null;
      }
   }

   @Override
   public boolean b() {
      return this.b.isAlive() && !(this.a.h(this.b) > this.c * this.c) && this.e > 0;
   }

   @Override
   public void c() {
      this.e = 40 + this.a.bc().nextInt(40);
   }

   @Override
   public void d() {
      this.b = null;
   }

   @Override
   public void e() {
      this.a.getControllerLook().a(this.b.locX, this.b.locY + this.b.getHeadHeight(), this.b.locZ, 10.0F, this.a.bQ());
      this.e--;
   }
}
