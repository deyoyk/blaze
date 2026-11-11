package net.minecraft.server.v1_8_R3;

public class PathfinderGoalRestrictSun extends PathfinderGoal {
   private final EntityCreature a;

   public PathfinderGoalRestrictSun(EntityCreature entitycreature) {
      this.a = entitycreature;
   }

   @Override
   public boolean a() {
      return this.a.world.w();
   }

   @Override
   public void c() {
      ((Navigation)this.a.getNavigation()).e(true);
   }

   @Override
   public void d() {
      ((Navigation)this.a.getNavigation()).e(false);
   }
}
