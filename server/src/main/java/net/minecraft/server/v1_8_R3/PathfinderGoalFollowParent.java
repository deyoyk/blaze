package net.minecraft.server.v1_8_R3;

import java.util.List;

public class PathfinderGoalFollowParent extends PathfinderGoal {
   EntityAnimal a;
   EntityAnimal b;
   double c;
   private int d;

   public PathfinderGoalFollowParent(EntityAnimal entityanimal, double d0) {
      this.a = entityanimal;
      this.c = d0;
   }

   @Override
   public boolean a() {
      if (this.a.getAge() >= 0) {
         return false;
      } else {
         List<? extends EntityAnimal> list = this.a.world.a((Class<? extends EntityAnimal>)this.a.getClass(), this.a.getBoundingBox().grow(8.0, 4.0, 8.0));
         EntityAnimal entityanimal = null;
         double d0 = Double.MAX_VALUE;

         for (EntityAnimal entityanimal1 : list) {
            if (entityanimal1.getAge() >= 0) {
               double d1 = this.a.h(entityanimal1);
               if (d1 <= d0) {
                  d0 = d1;
                  entityanimal = entityanimal1;
               }
            }
         }

         if (entityanimal == null) {
            return false;
         } else if (d0 < 9.0) {
            return false;
         } else {
            this.b = entityanimal;
            return true;
         }
      }
   }

   @Override
   public boolean b() {
      if (this.a.getAge() >= 0) {
         return false;
      } else if (!this.b.isAlive()) {
         return false;
      } else {
         double d0 = this.a.h(this.b);
         return d0 >= 9.0 && d0 <= 256.0;
      }
   }

   @Override
   public void c() {
      this.d = 0;
   }

   @Override
   public void d() {
      this.b = null;
   }

   @Override
   public void e() {
      if (--this.d <= 0) {
         this.d = 10;
         this.a.getNavigation().a(this.b, this.c);
      }
   }
}
