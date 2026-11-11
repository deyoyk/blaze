package net.minecraft.server.v1_8_R3;

public class PathfinderGoalRestrictOpenDoor extends PathfinderGoal {
   private final EntityCreature a;
   private VillageDoor b;

   public PathfinderGoalRestrictOpenDoor(EntityCreature entitycreature) {
      this.a = entitycreature;
      if (!(entitycreature.getNavigation() instanceof Navigation)) {
         throw new IllegalArgumentException("Unsupported mob type for RestrictOpenDoorGoal");
      }
   }

   @Override
   public boolean a() {
      if (this.a.world.w()) {
         return false;
      } else {
         BlockPosition blockposition = new BlockPosition(this.a);
         Village village = this.a.world.ae().getClosestVillage(blockposition, 16);
         if (village == null) {
            return false;
         } else {
            this.b = village.b(blockposition);
            return this.b != null && this.b.b(blockposition) < 2.25;
         }
      }
   }

   @Override
   public boolean b() {
      return !this.a.world.w() && !this.b.i() && this.b.c(new BlockPosition(this.a));
   }

   @Override
   public void c() {
      ((Navigation)this.a.getNavigation()).b(false);
      ((Navigation)this.a.getNavigation()).c(false);
   }

   @Override
   public void d() {
      ((Navigation)this.a.getNavigation()).b(true);
      ((Navigation)this.a.getNavigation()).c(true);
      this.b = null;
   }

   @Override
   public void e() {
      this.b.b();
   }
}
