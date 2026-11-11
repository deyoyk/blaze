package net.minecraft.server.v1_8_R3;

public class EntityBlaze extends EntityMonster {
   private float a = 0.5F;
   private int b;

   public EntityBlaze(World world) {
      super(world);
      this.fireProof = true;
      this.b_ = 10;
      this.goalSelector.a(4, new EntityBlaze.PathfinderGoalBlazeFireball(this));
      this.goalSelector.a(5, new PathfinderGoalMoveTowardsRestriction(this, 1.0));
      this.goalSelector.a(7, new PathfinderGoalRandomStroll(this, 1.0));
      this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
      this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
      this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, true));
      this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, true));
   }

   @Override
   protected void initAttributes() {
      super.initAttributes();
      this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(6.0);
      this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.23F);
      this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(48.0);
   }

   @Override
   protected void h() {
      super.h();
      this.datawatcher.a(16, (byte)0);
   }

   @Override
   protected String z() {
      return "mob.blaze.breathe";
   }

   @Override
   protected String bo() {
      return "mob.blaze.hit";
   }

   @Override
   protected String bp() {
      return "mob.blaze.death";
   }

   @Override
   public float c(float f) {
      return 1.0F;
   }

   @Override
   public void m() {
      if (!this.onGround && this.motY < 0.0) {
         this.motY *= 0.6;
      }

      super.m();
   }

   @Override
   protected void E() {
      if (this.U()) {
         this.damageEntity(DamageSource.DROWN, 1.0F);
      }

      this.b--;
      if (this.b <= 0) {
         this.b = 100;
         this.a = 0.5F + (float)this.random.nextGaussian() * 3.0F;
      }

      EntityLiving entityliving = this.getGoalTarget();
      if (entityliving != null && entityliving.locY + entityliving.getHeadHeight() > this.locY + this.getHeadHeight() + this.a) {
         this.motY = this.motY + (0.3F - this.motY) * 0.3F;
         this.ai = true;
      }

      super.E();
   }

   @Override
   public void e(float f, float f1) {
   }

   @Override
   protected Item getLoot() {
      return Items.BLAZE_ROD;
   }

   @Override
   public boolean isBurning() {
      return this.n();
   }

   @Override
   protected void dropDeathLoot(boolean flag, int i) {
      if (flag) {
         int j = this.random.nextInt(2 + i);

         for (int k = 0; k < j; k++) {
            this.a(Items.BLAZE_ROD, 1);
         }
      }
   }

   public boolean n() {
      return (this.datawatcher.getByte(16) & 1) != 0;
   }

   public void a(boolean flag) {
      byte b0 = this.datawatcher.getByte(16);
      if (flag) {
         b0 = (byte)(b0 | 1);
      } else {
         b0 = (byte)(b0 & -2);
      }

      this.datawatcher.watch(16, b0);
   }

   @Override
   protected boolean n_() {
      return true;
   }

   static class PathfinderGoalBlazeFireball extends PathfinderGoal {
      private final EntityBlaze a;
      private int b;
      private int c;

      public PathfinderGoalBlazeFireball(EntityBlaze entityblaze) {
         this.a = entityblaze;
         this.a(3);
      }

      @Override
      public boolean a() {
         EntityLiving entityliving = this.a.getGoalTarget();
         return entityliving != null && entityliving.isAlive();
      }

      @Override
      public void c() {
         this.b = 0;
      }

      @Override
      public void d() {
         this.a.a(false);
      }

      @Override
      public void e() {
         this.c--;
         EntityLiving entityliving = this.a.getGoalTarget();
         double d0 = this.a.h(entityliving);
         if (d0 < 4.0) {
            if (this.c <= 0) {
               this.c = 20;
               this.a.r(entityliving);
            }

            this.a.getControllerMove().a(entityliving.locX, entityliving.locY, entityliving.locZ, 1.0);
         } else if (d0 < 256.0) {
            double d1 = entityliving.locX - this.a.locX;
            double d2 = entityliving.getBoundingBox().b + entityliving.length / 2.0F - (this.a.locY + this.a.length / 2.0F);
            double d3 = entityliving.locZ - this.a.locZ;
            if (this.c <= 0) {
               this.b++;
               if (this.b == 1) {
                  this.c = 60;
                  this.a.a(true);
               } else if (this.b <= 4) {
                  this.c = 6;
               } else {
                  this.c = 100;
                  this.b = 0;
                  this.a.a(false);
               }

               if (this.b > 1) {
                  float f = MathHelper.c(MathHelper.sqrt(d0)) * 0.5F;
                  this.a.world.a(null, 1009, new BlockPosition((int)this.a.locX, (int)this.a.locY, (int)this.a.locZ), 0);

                  for (int i = 0; i < 1; i++) {
                     EntitySmallFireball entitysmallfireball = new EntitySmallFireball(
                        this.a.world, this.a, d1 + this.a.bc().nextGaussian() * f, d2, d3 + this.a.bc().nextGaussian() * f
                     );
                     entitysmallfireball.locY = this.a.locY + this.a.length / 2.0F + 0.5;
                     this.a.world.addEntity(entitysmallfireball);
                  }
               }
            }

            this.a.getControllerLook().a(entityliving, 10.0F, 10.0F);
         } else {
            this.a.getNavigation().n();
            this.a.getControllerMove().a(entityliving.locX, entityliving.locY, entityliving.locZ, 1.0);
         }

         super.e();
      }
   }
}
