package net.minecraft.server.v1_8_R3;

import lol.deyo.blaze.pathsearch.PositionPathSearchType;

public class PathfinderGoalMoveTowardsRestriction extends PathfinderGoal {
   private final EntityCreature a;
   private double b;
   private double c;
   private double d;
   private final double e;

   public PathfinderGoalMoveTowardsRestriction(EntityCreature entitycreature, double d0) {
      this.a = entitycreature;
      this.e = d0;
      this.a(1);
   }

   @Override
   public boolean a() {
      if (this.a.cg()) {
         return false;
      } else {
         BlockPosition blockposition = this.a.ch();
         Vec3D vec3d = RandomPositionGenerator.a(this.a, 16, 7, new Vec3D(blockposition.getX(), blockposition.getY(), blockposition.getZ()));
         if (vec3d == null) {
            return false;
         } else {
            this.b = vec3d.a;
            this.c = vec3d.b;
            this.d = vec3d.c;
            return true;
         }
      }
   }

   @Override
   public boolean b() {
      return !this.a.getNavigation().m();
   }

   @Override
   public void c() {
      this.a.getNavigation().a(this.b, this.c, this.d, this.e, PositionPathSearchType.MOVETOWARDSRESTRICTION);
   }
}
