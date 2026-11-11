package net.minecraft.server.v1_8_R3;

public class PathfinderGoalOfferFlower extends PathfinderGoal {
   private final EntityIronGolem a;
   private EntityVillager b;
   private int c;

   public PathfinderGoalOfferFlower(EntityIronGolem entityirongolem) {
      this.a = entityirongolem;
      this.a(3);
   }

   @Override
   public boolean a() {
      if (!this.a.world.w()) {
         return false;
      } else if (this.a.bc().nextInt(8000) != 0) {
         return false;
      } else {
         this.b = (EntityVillager)this.a.world.a(EntityVillager.class, this.a.getBoundingBox().grow(6.0, 2.0, 6.0), (Entity)this.a);
         return this.b != null;
      }
   }

   @Override
   public boolean b() {
      return this.c > 0;
   }

   @Override
   public void c() {
      this.c = 400;
      this.a.a(true);
   }

   @Override
   public void d() {
      this.a.a(false);
      this.b = null;
   }

   @Override
   public void e() {
      this.a.getControllerLook().a(this.b, 30.0F, 30.0F);
      this.c--;
   }
}
