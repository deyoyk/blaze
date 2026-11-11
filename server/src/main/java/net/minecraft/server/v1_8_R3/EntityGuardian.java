package net.minecraft.server.v1_8_R3;

import com.google.common.base.Predicate;
import net.jafama.FastMath;

public class EntityGuardian extends EntityMonster {
   private int bp;
   public PathfinderGoalRandomStroll goalRandomStroll;

   public EntityGuardian(World world) {
      super(world);
      this.b_ = 10;
      this.setSize(0.85F, 0.85F);
      this.goalSelector.a(4, new EntityGuardian.PathfinderGoalGuardianAttack(this));
      PathfinderGoalMoveTowardsRestriction pathfindergoalmovetowardsrestriction;
      this.goalSelector.a(5, pathfindergoalmovetowardsrestriction = new PathfinderGoalMoveTowardsRestriction(this, 1.0));
      this.goalSelector.a(7, this.goalRandomStroll = new PathfinderGoalRandomStroll(this, 1.0, 80));
      this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
      this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityGuardian.class, 12.0F, 0.01F));
      this.goalSelector.a(9, new PathfinderGoalRandomLookaround(this));
      this.goalRandomStroll.a(3);
      pathfindergoalmovetowardsrestriction.a(3);
      this.targetSelector
         .a(
            1,
            new PathfinderGoalNearestAttackableTarget<>(
               this, EntityLiving.class, 10, true, false, new EntityGuardian.EntitySelectorGuardianTargetHumanSquid(this)
            )
         );
      this.moveController = new EntityGuardian.ControllerMoveGuardian(this);
   }

   @Override
   public void initAttributes() {
      super.initAttributes();
      this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(6.0);
      this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.5);
      this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(16.0);
      this.getAttributeInstance(GenericAttributes.maxHealth).setValue(30.0);
   }

   @Override
   public void a(NBTTagCompound nbttagcompound) {
      super.a(nbttagcompound);
      this.setElder(nbttagcompound.getBoolean("Elder"));
   }

   @Override
   public void b(NBTTagCompound nbttagcompound) {
      super.b(nbttagcompound);
      nbttagcompound.setBoolean("Elder", this.isElder());
   }

   @Override
   protected NavigationAbstract b(World world) {
      return new NavigationGuardian(this, world);
   }

   @Override
   protected void h() {
      super.h();
      this.datawatcher.a(16, 0);
      this.datawatcher.a(17, 0);
   }

   private boolean a(int i) {
      return (this.datawatcher.getInt(16) & i) != 0;
   }

   private void a(int i, boolean flag) {
      int j = this.datawatcher.getInt(16);
      if (flag) {
         this.datawatcher.watch(16, j | i);
      } else {
         this.datawatcher.watch(16, j & ~i);
      }
   }

   public boolean n() {
      return this.a(2);
   }

   private void l(boolean flag) {
      this.a(2, flag);
   }

   public int cm() {
      return this.isElder() ? 60 : 80;
   }

   public boolean isElder() {
      return this.a(4);
   }

   public void setElder(boolean flag) {
      this.a(4, flag);
      if (flag) {
         this.setSize(1.9975F, 1.9975F);
         this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.3F);
         this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(8.0);
         this.getAttributeInstance(GenericAttributes.maxHealth).setValue(80.0);
         this.bX();
         this.goalRandomStroll.setTimeBetweenMovement(400);
      }
   }

   private void b(int i) {
      this.datawatcher.watch(17, i);
   }

   public boolean cp() {
      return this.datawatcher.getInt(17) != 0;
   }

   public EntityLiving cq() {
      return !this.cp() ? null : this.getGoalTarget();
   }

   @Override
   public void i(int i) {
      super.i(i);
      if (i == 16) {
         if (this.isElder() && this.width < 1.0F) {
            this.setSize(1.9975F, 1.9975F);
         }
      } else if (i == 17) {
         this.bp = 0;
      }
   }

   @Override
   public int w() {
      return 160;
   }

   @Override
   protected String z() {
      return !this.V() ? "mob.guardian.land.idle" : (this.isElder() ? "mob.guardian.elder.idle" : "mob.guardian.idle");
   }

   @Override
   protected String bo() {
      return !this.V() ? "mob.guardian.land.hit" : (this.isElder() ? "mob.guardian.elder.hit" : "mob.guardian.hit");
   }

   @Override
   protected String bp() {
      return !this.V() ? "mob.guardian.land.death" : (this.isElder() ? "mob.guardian.elder.death" : "mob.guardian.death");
   }

   @Override
   protected boolean s_() {
      return false;
   }

   @Override
   public float getHeadHeight() {
      return this.length * 0.5F;
   }

   @Override
   public float a(BlockPosition blockposition) {
      return this.world.getType(blockposition).getBlock().getMaterial() == Material.WATER ? 10.0F + this.world.o(blockposition) - 0.5F : super.a(blockposition);
   }

   @Override
   public void m() {
      if (this.inWater) {
         this.setAirTicks(300);
      } else if (this.onGround) {
         this.motY += 0.5;
         this.motX = this.motX + (this.random.nextFloat() * 2.0F - 1.0F) * 0.4F;
         this.motZ = this.motZ + (this.random.nextFloat() * 2.0F - 1.0F) * 0.4F;
         this.yaw = this.random.nextFloat() * 360.0F;
         this.onGround = false;
         this.ai = true;
      }

      if (this.cp()) {
         this.yaw = this.aK;
      }

      super.m();
   }

   public float q(float f) {
      return (this.bp + f) / this.cm();
   }

   @Override
   protected void E() {
      super.E();
      if (this.isElder()) {
         if ((this.ticksLived + this.getId()) % 1200 == 0) {
            MobEffectList mobeffectlist = MobEffectList.SLOWER_DIG;

            for (EntityPlayer entityplayer : this.world.b(EntityPlayer.class, new Predicate<Object>() {
               public boolean a(EntityPlayer entityplayer) {
                  return EntityGuardian.this.h(entityplayer) < 2500.0 && entityplayer.playerInteractManager.c();
               }

               public boolean apply(Object object) {
                  return this.a((EntityPlayer)object);
               }
            })) {
               if (!entityplayer.hasEffect(mobeffectlist)
                  || entityplayer.getEffect(mobeffectlist).getAmplifier() < 2
                  || entityplayer.getEffect(mobeffectlist).getDuration() < 1200) {
                  entityplayer.playerConnection.sendPacket(new PacketPlayOutGameStateChange(10, 0.0F));
                  entityplayer.addEffect(new MobEffect(mobeffectlist.id, 6000, 2));
               }
            }
         }

         if (!this.ck()) {
            this.a(new BlockPosition(this), 16);
         }
      }
   }

   @Override
   protected void dropDeathLoot(boolean flag, int i) {
      int j = this.random.nextInt(3) + this.random.nextInt(i + 1);
      if (j > 0) {
         this.a(new ItemStack(Items.PRISMARINE_SHARD, j, 0), 1.0F);
      }

      if (this.random.nextInt(3 + i) > 1) {
         this.a(new ItemStack(Items.FISH, 1, ItemFish.EnumFish.COD.a()), 1.0F);
      } else if (this.random.nextInt(3 + i) > 1) {
         this.a(new ItemStack(Items.PRISMARINE_CRYSTALS, 1, 0), 1.0F);
      }

      if (flag && this.isElder()) {
         this.a(new ItemStack(Blocks.SPONGE, 1, 1), 1.0F);
      }
   }

   @Override
   protected void getRareDrop() {
      ItemStack itemstack = WeightedRandom.a(this.random, EntityFishingHook.j()).a(this.random);
      this.a(itemstack, 1.0F);
   }

   @Override
   protected boolean n_() {
      return true;
   }

   @Override
   public boolean canSpawn() {
      return this.world.a(this.getBoundingBox(), this) && this.world.getCubes(this, this.getBoundingBox()).isEmpty();
   }

   @Override
   public boolean bR() {
      return (this.random.nextInt(20) == 0 || !this.world.j(new BlockPosition(this))) && super.bR();
   }

   @Override
   public boolean damageEntity(DamageSource damagesource, float f) {
      if (!this.n() && !damagesource.isMagic() && damagesource.i() instanceof EntityLiving) {
         EntityLiving entityliving = (EntityLiving)damagesource.i();
         if (!damagesource.isExplosion()) {
            entityliving.damageEntity(DamageSource.a(this), 2.0F);
            entityliving.makeSound("damage.thorns", 0.5F, 1.0F);
         }
      }

      this.goalRandomStroll.f();
      return super.damageEntity(damagesource, f);
   }

   @Override
   public int bQ() {
      return 180;
   }

   @Override
   public void g(float f, float f1) {
      if (this.bM()) {
         if (this.V()) {
            this.a(f, f1, 0.1F);
            this.move(this.motX, this.motY, this.motZ);
            this.motX *= 0.9F;
            this.motY *= 0.9F;
            this.motZ *= 0.9F;
            if (!this.n() && this.getGoalTarget() == null) {
               this.motY -= 0.005;
            }
         } else {
            super.g(f, f1);
         }
      } else {
         super.g(f, f1);
      }
   }

   static class ControllerMoveGuardian extends ControllerMove {
      private final EntityGuardian g;

      public ControllerMoveGuardian(EntityGuardian entityguardian) {
         super(entityguardian);
         this.g = entityguardian;
      }

      @Override
      public void c() {
         if (this.f && !this.g.getNavigation().m()) {
            double d0 = this.b - this.g.locX;
            double d1 = this.c - this.g.locY;
            double d2 = this.d - this.g.locZ;
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;
            d3 = MathHelper.sqrt(d3);
            d1 /= d3;
            float f = (float)(MathHelper.b(d2, d0) * 180.0 / (float) Math.PI) - 90.0F;
            this.g.yaw = this.a(this.g.yaw, f, 30.0F);
            this.g.aI = this.g.yaw;
            float f1 = (float)(this.e * this.g.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue());
            this.g.k(this.g.bI() + (f1 - this.g.bI()) * 0.125F);
            double d4 = FastMath.sin((this.g.ticksLived + this.g.getId()) * 0.5) * 0.05;
            double d5 = FastMath.cos(this.g.yaw * (float) Math.PI / 180.0F);
            double d6 = FastMath.sin(this.g.yaw * (float) Math.PI / 180.0F);
            this.g.motX += d4 * d5;
            this.g.motZ += d4 * d6;
            d4 = FastMath.sin((this.g.ticksLived + this.g.getId()) * 0.75) * 0.05;
            this.g.motY += d4 * (d6 + d5) * 0.25;
            this.g.motY = this.g.motY + this.g.bI() * d1 * 0.1;
            ControllerLook controllerlook = this.g.getControllerLook();
            double d7 = this.g.locX + d0 / d3 * 2.0;
            double d8 = this.g.getHeadHeight() + this.g.locY + d1 / d3;
            double d9 = this.g.locZ + d2 / d3 * 2.0;
            double d10 = controllerlook.e();
            double d11 = controllerlook.f();
            double d12 = controllerlook.g();
            if (!controllerlook.b()) {
               d10 = d7;
               d11 = d8;
               d12 = d9;
            }

            this.g.getControllerLook().a(d10 + (d7 - d10) * 0.125, d11 + (d8 - d11) * 0.125, d12 + (d9 - d12) * 0.125, 10.0F, 40.0F);
            this.g.l(true);
         } else {
            this.g.k(0.0F);
            this.g.l(false);
         }
      }
   }

   static class EntitySelectorGuardianTargetHumanSquid implements Predicate<EntityLiving> {
      private final EntityGuardian a;

      public EntitySelectorGuardianTargetHumanSquid(EntityGuardian entityguardian) {
         this.a = entityguardian;
      }

      public boolean a(EntityLiving entityliving) {
         return (entityliving instanceof EntityHuman || entityliving instanceof EntitySquid) && entityliving.h(this.a) > 9.0;
      }

      public boolean apply(EntityLiving object) {
         return this.a(object);
      }
   }

   static class PathfinderGoalGuardianAttack extends PathfinderGoal {
      private final EntityGuardian a;
      private int b;

      public PathfinderGoalGuardianAttack(EntityGuardian entityguardian) {
         this.a = entityguardian;
         this.a(3);
      }

      @Override
      public boolean a() {
         EntityLiving entityliving = this.a.getGoalTarget();
         return entityliving != null && entityliving.isAlive();
      }

      @Override
      public boolean b() {
         return super.b() && (this.a.isElder() || this.a.h(this.a.getGoalTarget()) > 9.0);
      }

      @Override
      public void c() {
         this.b = -10;
         this.a.getNavigation().n();
         this.a.getControllerLook().a(this.a.getGoalTarget(), 90.0F, 90.0F);
         this.a.ai = true;
      }

      @Override
      public void d() {
         this.a.b(0);
         this.a.setGoalTarget(null);
         this.a.goalRandomStroll.f();
      }

      @Override
      public void e() {
         EntityLiving entityliving = this.a.getGoalTarget();
         this.a.getNavigation().n();
         this.a.getControllerLook().a(entityliving, 90.0F, 90.0F);
         if (!this.a.hasLineOfSight(entityliving)) {
            this.a.setGoalTarget(null);
         } else {
            this.b++;
            if (this.b == 0) {
               this.a.b(this.a.getGoalTarget().getId());
               this.a.world.broadcastEntityEffect(this.a, (byte)21);
            } else if (this.b >= this.a.cm()) {
               float f = 1.0F;
               if (this.a.world.getDifficulty() == EnumDifficulty.HARD) {
                  f += 2.0F;
               }

               if (this.a.isElder()) {
                  f += 2.0F;
               }

               entityliving.damageEntity(DamageSource.b(this.a, this.a), f);
               entityliving.damageEntity(DamageSource.mobAttack(this.a), (float)this.a.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).getValue());
               this.a.setGoalTarget(null);
            }

            super.e();
         }
      }
   }
}
