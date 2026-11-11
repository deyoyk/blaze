package net.minecraft.server.v1_8_R3;

public class PathfinderGoalTempt extends PathfinderGoal {
   private final EntityCreature a;
   private final double b;
   private double c;
   private double d;
   private double e;
   private double f;
   private double g;
   private EntityHuman h;
   private int i;
   private boolean j;
   private final Item k;
   private final boolean l;
   private boolean m;

   public PathfinderGoalTempt(EntityCreature entitycreature, double d0, Item item, boolean flag) {
      this.a = entitycreature;
      this.b = d0;
      this.k = item;
      this.l = flag;
      this.a(3);
      if (!(entitycreature.getNavigation() instanceof Navigation)) {
         throw new IllegalArgumentException("Unsupported mob type for TemptGoal");
      }
   }

   @Override
   public boolean a() {
      if (this.i > 0) {
         this.i--;
         return false;
      } else {
         this.h = this.a.world.findNearbyPlayer(this.a, 10.0);
         if (this.h == null) {
            return false;
         } else {
            ItemStack itemstack = this.h.bZ();
            return itemstack != null && itemstack.getItem() == this.k;
         }
      }
   }

   @Override
   public boolean b() {
      if (this.l) {
         if (this.a.h(this.h) < 36.0) {
            if (this.h.e(this.c, this.d, this.e) > 0.010000000000000002) {
               return false;
            }

            if (Math.abs(this.h.pitch - this.f) > 5.0 || Math.abs(this.h.yaw - this.g) > 5.0) {
               return false;
            }
         } else {
            this.c = this.h.locX;
            this.d = this.h.locY;
            this.e = this.h.locZ;
         }

         this.f = this.h.pitch;
         this.g = this.h.yaw;
      }

      return this.a();
   }

   @Override
   public void c() {
      this.c = this.h.locX;
      this.d = this.h.locY;
      this.e = this.h.locZ;
      this.j = true;
      this.m = ((Navigation)this.a.getNavigation()).e();
      ((Navigation)this.a.getNavigation()).a(false);
   }

   @Override
   public void d() {
      this.h = null;
      this.a.getNavigation().n();
      this.i = 100;
      this.j = false;
      ((Navigation)this.a.getNavigation()).a(this.m);
   }

   @Override
   public void e() {
      this.a.getControllerLook().a(this.h, 30.0F, this.a.bQ());
      if (this.a.h(this.h) < 6.25) {
         this.a.getNavigation().n();
      } else {
         this.a.getNavigation().a(this.h, this.b);
      }
   }

   public boolean f() {
      return this.j;
   }
}
