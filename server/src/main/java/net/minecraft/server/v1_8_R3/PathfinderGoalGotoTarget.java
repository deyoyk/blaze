package net.minecraft.server.v1_8_R3;

import lol.deyo.blaze.pathsearch.PositionPathSearchType;

public abstract class PathfinderGoalGotoTarget extends PathfinderGoal {
   private final EntityCreature c;
   private final double d;
   protected int a;
   private int e;
   private int f;
   protected BlockPosition b = BlockPosition.ZERO;
   private boolean g;
   private final int h;

   public PathfinderGoalGotoTarget(EntityCreature entitycreature, double d0, int i) {
      this.c = entitycreature;
      this.d = d0;
      this.h = i;
      this.a(5);
   }

   @Override
   public boolean a() {
      if (this.a > 0) {
         this.a--;
         return false;
      } else {
         this.a = 200 + this.c.bc().nextInt(200);
         return this.g();
      }
   }

   @Override
   public boolean b() {
      return this.e >= -this.f && this.e <= 1200 && this.a(this.c.world, this.b);
   }

   @Override
   public void c() {
      this.c.getNavigation().a(this.b.getX() + 0.5, this.b.getY() + 1, this.b.getZ() + 0.5, this.d, PositionPathSearchType.GOTOTARGET);
      this.e = 0;
      this.f = this.c.bc().nextInt(this.c.bc().nextInt(1200) + 1200) + 1200;
   }

   @Override
   public void e() {
      if (this.c.c(this.b.up()) > 1.0) {
         this.g = false;
         this.e++;
         if (this.e % 40 == 0) {
            this.c.getNavigation().a(this.b.getX() + 0.5, this.b.getY() + 1, this.b.getZ() + 0.5, this.d, PositionPathSearchType.GOTOTARGET);
         }
      } else {
         this.g = true;
         this.e--;
      }
   }

   protected boolean f() {
      return this.g;
   }

   private boolean g() {
      BlockPosition blockposition = new BlockPosition(this.c);

      for (int j = 0; j <= 1; j = j > 0 ? -j : 1 - j) {
         for (int k = 0; k < this.h; k++) {
            for (int l = 0; l <= k; l = l > 0 ? -l : 1 - l) {
               for (int i1 = l < k && l > -k ? k : 0; i1 <= k; i1 = i1 > 0 ? -i1 : 1 - i1) {
                  BlockPosition blockposition1 = blockposition.a(l, j - 1, i1);
                  if (this.c.e(blockposition1) && this.a(this.c.world, blockposition1)) {
                     this.b = blockposition1;
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   protected abstract boolean a(World var1, BlockPosition var2);
}
