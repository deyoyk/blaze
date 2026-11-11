package net.minecraft.server.v1_8_R3;

import net.jafama.FastMath;

public class PathfinderGoalRandomLookaround extends PathfinderGoal {
   private final EntityInsentient a;
   private double b;
   private double c;
   private int d;

   public PathfinderGoalRandomLookaround(EntityInsentient entityinsentient) {
      this.a = entityinsentient;
      this.a(3);
   }

   @Override
   public boolean a() {
      return this.a.bc().nextFloat() < 0.02F;
   }

   @Override
   public boolean b() {
      return this.d >= 0;
   }

   @Override
   public void c() {
      double d0 = (Math.PI * 2) * this.a.bc().nextDouble();
      this.b = FastMath.cos(d0);
      this.c = FastMath.sin(d0);
      this.d = 20 + this.a.bc().nextInt(20);
   }

   @Override
   public void e() {
      this.d--;
      this.a.getControllerLook().a(this.a.locX + this.b, this.a.locY + this.a.getHeadHeight(), this.a.locZ + this.c, 10.0F, this.a.bQ());
   }
}
