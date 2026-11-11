package net.minecraft.server.v1_8_R3;

import lol.deyo.blaze.pathsearch.PositionPathSearchType;

public class PathfinderGoalRandomStroll extends PathfinderGoal {
   private final EntityCreature a;
   private double b;
   private double c;
   private double d;
   private final double e;
   private int f;
   private boolean g;

   public PathfinderGoalRandomStroll(EntityCreature entitycreature, double d0) {
      this(entitycreature, d0, 120);
   }

   public PathfinderGoalRandomStroll(EntityCreature entitycreature, double d0, int i) {
      this.a = entitycreature;
      this.e = d0;
      this.f = i;
      this.a(1);
   }

   @Override
   public boolean a() {
      if (!this.g) {
         if (this.a.bh() >= 100) {
            return false;
         }

         if (this.a.bc().nextInt(this.f) != 0) {
            return false;
         }
      }

      Vec3D vec3d = RandomPositionGenerator.a(this.a, 10, 7);
      if (vec3d == null) {
         return false;
      } else {
         this.b = vec3d.a;
         this.c = vec3d.b;
         this.d = vec3d.c;
         this.g = false;
         return true;
      }
   }

   @Override
   public boolean b() {
      return !this.a.getNavigation().m();
   }

   @Override
   public void c() {
      this.a.getNavigation().a(this.b, this.c, this.d, this.e, PositionPathSearchType.RANDOMSTROLL);
   }

   public void f() {
      this.g = true;
   }

   public void setTimeBetweenMovement(int i) {
      this.f = i;
   }
}
