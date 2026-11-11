package net.minecraft.server.v1_8_R3;

public class PathfinderGoalMeleeAttack extends PathfinderGoal {
   protected EntityCreature b;
   int c;
   double d;
   boolean e;
   PathEntity f;
   Class<? extends Entity> g;
   private int h;
   private double i;
   private double j;
   private double k;

   public PathfinderGoalMeleeAttack(EntityCreature entitycreature, Class<? extends Entity> oclass, double d0, boolean flag) {
      this(entitycreature, d0, flag);
      this.g = oclass;
   }

   public PathfinderGoalMeleeAttack(EntityCreature entitycreature, double d0, boolean flag) {
      this.b = entitycreature;
      this.d = d0;
      this.e = flag;
      this.a(3);
   }

   @Override
   public boolean a() {
      EntityLiving entityliving = this.b.getGoalTarget();
      if (entityliving == null) {
         return false;
      } else if (!entityliving.isAlive()) {
         return false;
      } else if (this.g != null && !this.g.isAssignableFrom(entityliving.getClass())) {
         return false;
      } else {
         this.f = this.b.getNavigation().a(entityliving);
         return this.f != null;
      }
   }

   @Override
   public boolean b() {
      EntityLiving entityliving = this.b.getGoalTarget();
      return entityliving != null && entityliving.isAlive() && (!this.e ? !this.b.getNavigation().m() : this.b.e(new BlockPosition(entityliving)));
   }

   @Override
   public void c() {
      this.b.getNavigation().a(this.f, this.d);
      this.h = 0;
   }

   @Override
   public void d() {
      this.b.getNavigation().n();
   }

   @Override
   public void e() {
      EntityLiving entityliving = this.b.getGoalTarget();
      this.b.getControllerLook().a(entityliving, 30.0F, 30.0F);
      double d0 = this.b.e(entityliving.locX, entityliving.getBoundingBox().b, entityliving.locZ);
      double d1 = this.a(entityliving);
      this.h--;
      if ((this.e || this.b.getEntitySenses().a(entityliving))
         && this.h <= 0
         && (this.i == 0.0 && this.j == 0.0 && this.k == 0.0 || entityliving.e(this.i, this.j, this.k) >= 1.0 || this.b.bc().nextFloat() < 0.05F)) {
         this.i = entityliving.locX;
         this.j = entityliving.getBoundingBox().b;
         this.k = entityliving.locZ;
         this.h = 4 + this.b.bc().nextInt(7);
         if (d0 > 1024.0) {
            this.h += 10;
         } else if (d0 > 256.0) {
            this.h += 5;
         }

         if (!this.b.getNavigation().a(entityliving, this.d)) {
            this.h += 15;
         }
      }

      this.c = Math.max(this.c - 1, 0);
      if (d0 <= d1 && this.c == 0) {
         this.c = 20;
         if (this.b.bA() != null) {
            this.b.bw();
         }

         this.b.r(entityliving);
      }
   }

   protected double a(EntityLiving entityliving) {
      return this.b.width * 2.0F * this.b.width * 2.0F + entityliving.width;
   }
}
