package net.minecraft.server.v1_8_R3;

public class PathfinderGoalFollowOwner extends PathfinderGoal {
   private final EntityTameableAnimal d;
   private EntityLiving e;
   World a;
   private final double f;
   private final NavigationAbstract g;
   private int h;
   float b;
   float c;

   public PathfinderGoalFollowOwner(EntityTameableAnimal entitytameableanimal, double d0, float f, float f1) {
      this.d = entitytameableanimal;
      this.a = entitytameableanimal.world;
      this.f = d0;
      this.g = entitytameableanimal.getNavigation();
      this.c = f;
      this.b = f1;
      this.a(3);
      if (!(entitytameableanimal.getNavigation() instanceof Navigation)) {
         throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
      }
   }

   @Override
   public boolean a() {
      EntityLiving entityliving = this.d.getOwner();
      if (entityliving == null) {
         return false;
      } else if (entityliving instanceof EntityHuman && ((EntityHuman)entityliving).isSpectator()) {
         return false;
      } else if (this.d.isSitting()) {
         return false;
      } else if (this.d.h(entityliving) < this.c * this.c) {
         return false;
      } else {
         this.e = entityliving;
         return true;
      }
   }

   @Override
   public boolean b() {
      return !this.g.m() && this.d.h(this.e) > this.b * this.b && !this.d.isSitting();
   }

   @Override
   public void c() {
      this.h = 0;
      ((Navigation)this.d.getNavigation()).a(false);
   }

   @Override
   public void d() {
      this.e = null;
      this.g.n();
      ((Navigation)this.d.getNavigation()).a(true);
   }

   private boolean a(BlockPosition blockposition) {
      IBlockData iblockdata = this.a.getType(blockposition);
      Block block = iblockdata.getBlock();
      return block == Blocks.AIR || !block.d();
   }

   @Override
   public void e() {
      this.d.getControllerLook().a(this.e, 10.0F, this.d.bQ());
      if (!this.d.isSitting() && --this.h <= 0) {
         this.h = 10;
         if (!this.g.a(this.e, this.f) && !this.d.cc() && this.d.h(this.e) >= 144.0) {
            int i = MathHelper.floor(this.e.locX) - 2;
            int j = MathHelper.floor(this.e.locZ) - 2;
            int k = MathHelper.floor(this.e.getBoundingBox().b);

            for (int l = 0; l <= 4; l++) {
               for (int i1 = 0; i1 <= 4; i1++) {
                  if ((l < 1 || i1 < 1 || l > 3 || i1 > 3)
                     && World.a(this.a, new BlockPosition(i + l, k - 1, j + i1))
                     && this.a(new BlockPosition(i + l, k, j + i1))
                     && this.a(new BlockPosition(i + l, k + 1, j + i1))) {
                     this.d.setPositionRotation(i + l + 0.5F, k, j + i1 + 0.5F, this.d.yaw, this.d.pitch);
                     this.g.n();
                     return;
                  }
               }
            }
         }
      }
   }
}
