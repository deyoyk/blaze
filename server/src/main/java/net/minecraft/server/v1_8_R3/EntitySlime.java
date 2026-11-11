package net.minecraft.server.v1_8_R3;

import org.bukkit.entity.Slime;
import org.bukkit.event.entity.SlimeSplitEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class EntitySlime extends EntityInsentient implements IMonster {
   public float a;
   public float b;
   public float c;
   private boolean bk;

   public EntitySlime(World world) {
      super(world);
      this.moveController = new EntitySlime.ControllerMoveSlime(this);
      this.goalSelector.a(1, new EntitySlime.PathfinderGoalSlimeRandomJump(this));
      this.goalSelector.a(2, new EntitySlime.PathfinderGoalSlimeNearestPlayer(this));
      this.goalSelector.a(3, new EntitySlime.PathfinderGoalSlimeRandomDirection(this));
      this.goalSelector.a(5, new EntitySlime.PathfinderGoalSlimeIdle(this));
      this.targetSelector.a(1, new PathfinderGoalTargetNearestPlayer(this));
      this.targetSelector.a(3, new PathfinderGoalNearestAttackableTargetInsentient(this, EntityIronGolem.class));
   }

   @Override
   protected void h() {
      super.h();
      this.datawatcher.a(16, (byte)1);
   }

   public void setSize(int i) {
      this.datawatcher.watch(16, (byte)i);
      this.setSize(0.51000005F * i, 0.51000005F * i);
      this.setPosition(this.locX, this.locY, this.locZ);
      this.getAttributeInstance(GenericAttributes.maxHealth).setValue(i * i);
      this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.2F + 0.1F * i);
      this.setHealth(this.getMaxHealth());
      this.b_ = i;
   }

   public int getSize() {
      return this.datawatcher.getByte(16);
   }

   @Override
   public void b(NBTTagCompound nbttagcompound) {
      super.b(nbttagcompound);
      nbttagcompound.setInt("Size", this.getSize() - 1);
      nbttagcompound.setBoolean("wasOnGround", this.bk);
   }

   @Override
   public void a(NBTTagCompound nbttagcompound) {
      super.a(nbttagcompound);
      int i = nbttagcompound.getInt("Size");
      if (i < 0) {
         i = 0;
      }

      this.setSize(i + 1);
      this.bk = nbttagcompound.getBoolean("wasOnGround");
   }

   protected EnumParticle n() {
      return EnumParticle.SLIME;
   }

   protected String ck() {
      return "mob.slime." + (this.getSize() > 1 ? "big" : "small");
   }

   @Override
   public void t_() {
      if (this.world.getDifficulty() == EnumDifficulty.PEACEFUL && this.getSize() > 0) {
         this.dead = true;
      }

      this.b = this.b + (this.a - this.b) * 0.5F;
      this.c = this.b;
      super.t_();
      if (this.onGround && !this.bk) {
         int i = this.getSize();

         for (int j = 0; j < i * 8; j++) {
            float f = this.random.nextFloat() * (float) Math.PI * 2.0F;
            float f1 = this.random.nextFloat() * 0.5F + 0.5F;
            float f2 = MathHelper.sin(f) * i * 0.5F * f1;
            float f3 = MathHelper.cos(f) * i * 0.5F * f1;
            World var10000 = this.world;
            EnumParticle var10001 = this.n();
            double var10002 = this.locX + f2;
            double var10004 = this.locZ + f3;
            var10000.addParticle(var10001, var10002, this.getBoundingBox().b, var10004, 0.0, 0.0, 0.0, EnumParticle.EMPTY_ARRAY);
         }

         if (this.cl()) {
            this.makeSound(this.ck(), this.bB(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) / 0.8F);
         }

         this.a = -0.5F;
      } else if (!this.onGround && this.bk) {
         this.a = 1.0F;
      }

      this.bk = this.onGround;
      this.ch();
   }

   protected void ch() {
      this.a *= 0.6F;
   }

   protected int cg() {
      return this.random.nextInt(20) + 10;
   }

   protected EntitySlime cf() {
      return new EntitySlime(this.world);
   }

   @Override
   public void i(int i) {
      if (i == 16) {
         int j = this.getSize();
         this.setSize(0.51000005F * j, 0.51000005F * j);
         this.yaw = this.aK;
         this.aI = this.aK;
         if (this.V() && this.random.nextInt(20) == 0) {
            this.X();
         }
      }

      super.i(i);
   }

   @Override
   public void die() {
      int i = this.getSize();
      if (i > 1 && this.getHealth() <= 0.0F) {
         int j = 2 + this.random.nextInt(3);
         SlimeSplitEvent event = new SlimeSplitEvent((Slime)this.getBukkitEntity(), j);
         this.world.getServer().getPluginManager().callEvent(event);
         if (event.isCancelled() || event.getCount() <= 0) {
            super.die();
            return;
         }

         j = event.getCount();

         for (int k = 0; k < j; k++) {
            float f = (k % 2 - 0.5F) * i / 4.0F;
            float f1 = (k / 2 - 0.5F) * i / 4.0F;
            EntitySlime entityslime = this.cf();
            if (this.hasCustomName()) {
               entityslime.setCustomName(this.getCustomName());
            }

            if (this.isPersistent()) {
               entityslime.bX();
            }

            entityslime.setSize(i / 2);
            entityslime.setPositionRotation(this.locX + f, this.locY + 0.5, this.locZ + f1, this.random.nextFloat() * 360.0F, 0.0F);
            this.world.addEntity(entityslime, SpawnReason.SLIME_SPLIT);
         }
      }

      super.die();
   }

   @Override
   public void collide(Entity entity) {
      super.collide(entity);
      if (entity instanceof EntityIronGolem && this.ci()) {
         this.e((EntityLiving)entity);
      }
   }

   @Override
   public void d(EntityHuman entityhuman) {
      if (this.ci()) {
         this.e(entityhuman);
      }
   }

   protected void e(EntityLiving entityliving) {
      int i = this.getSize();
      if (this.hasLineOfSight(entityliving) && this.h(entityliving) < 0.6 * i * 0.6 * i && entityliving.damageEntity(DamageSource.mobAttack(this), this.cj())) {
         this.makeSound("mob.attack", 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
         this.a(this, entityliving);
      }
   }

   @Override
   public float getHeadHeight() {
      return 0.625F * this.length;
   }

   protected boolean ci() {
      return this.getSize() > 1;
   }

   protected int cj() {
      return this.getSize();
   }

   @Override
   protected String bo() {
      return "mob.slime." + (this.getSize() > 1 ? "big" : "small");
   }

   @Override
   protected String bp() {
      return "mob.slime." + (this.getSize() > 1 ? "big" : "small");
   }

   @Override
   protected Item getLoot() {
      return this.getSize() == 1 ? Items.SLIME : null;
   }

   @Override
   public boolean bR() {
      int x = MathHelper.floor(this.locX);
      int z = MathHelper.floor(this.locZ);
      Chunk chunk = this.world.getChunkAtWorldCoords(x, z);
      if ((this.world.getWorldData().getType() != WorldType.FLAT || this.random.nextInt(4) == 1) && this.world.getDifficulty() != EnumDifficulty.PEACEFUL) {
         BiomeBase biomebase = this.world.getBiome(x, 0, z);
         if (biomebase == BiomeBase.SWAMPLAND
            && this.locY > 50.0
            && this.locY < 70.0
            && this.random.nextFloat() < 0.5F
            && this.random.nextFloat() < this.world.y()
            && this.world.getLightLevel(MathHelper.floor(this.locX), MathHelper.floor(this.locY), MathHelper.floor(this.locZ)) <= this.random.nextInt(8)) {
            return super.bR();
         }

         boolean isSlimeChunk = this.world.paperSpigotConfig.allChunksAreSlimeChunks || chunk.a(987234911L).nextInt(10) == 0;
         if (this.random.nextInt(10) == 0 && isSlimeChunk && this.locY < 40.0) {
            return super.bR();
         }
      }

      return false;
   }

   @Override
   protected float bB() {
      return 0.4F * this.getSize();
   }

   @Override
   public int bQ() {
      return 0;
   }

   protected boolean cn() {
      return this.getSize() > 0;
   }

   protected boolean cl() {
      return this.getSize() > 2;
   }

   @Override
   protected void bF() {
      this.motY = 0.42F;
      this.ai = true;
   }

   @Override
   public GroupDataEntity prepare(DifficultyDamageScaler difficultydamagescaler, GroupDataEntity groupdataentity) {
      int i = this.random.nextInt(3);
      if (i < 2 && this.random.nextFloat() < 0.5F * difficultydamagescaler.c()) {
         i++;
      }

      int j = 1 << i;
      this.setSize(j);
      return super.prepare(difficultydamagescaler, groupdataentity);
   }

   static class ControllerMoveSlime extends ControllerMove {
      private float g;
      private int h;
      private final EntitySlime i;
      private boolean j;

      public ControllerMoveSlime(EntitySlime entityslime) {
         super(entityslime);
         this.i = entityslime;
      }

      public void a(float f, boolean flag) {
         this.g = f;
         this.j = flag;
      }

      public void a(double d0) {
         this.e = d0;
         this.f = true;
      }

      @Override
      public void c() {
         this.a.yaw = this.a(this.a.yaw, this.g, 30.0F);
         this.a.aK = this.a.yaw;
         this.a.aI = this.a.yaw;
         if (!this.f) {
            this.a.n(0.0F);
         } else {
            this.f = false;
            if (this.a.onGround) {
               this.a.k((float)(this.e * this.a.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue()));
               if (this.h-- <= 0) {
                  this.h = this.i.cg();
                  if (this.j) {
                     this.h /= 3;
                  }

                  this.i.getControllerJump().a();
                  if (this.i.cn()) {
                     this.i.makeSound(this.i.ck(), this.i.bB(), ((this.i.bc().nextFloat() - this.i.bc().nextFloat()) * 0.2F + 1.0F) * 0.8F);
                  }
               } else {
                  this.i.aZ = this.i.ba = 0.0F;
                  this.a.k(0.0F);
               }
            } else {
               this.a.k((float)(this.e * this.a.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue()));
            }
         }
      }
   }

   static class PathfinderGoalSlimeIdle extends PathfinderGoal {
      private final EntitySlime a;

      public PathfinderGoalSlimeIdle(EntitySlime entityslime) {
         this.a = entityslime;
         this.a(5);
      }

      @Override
      public boolean a() {
         return true;
      }

      @Override
      public void e() {
         ((EntitySlime.ControllerMoveSlime)this.a.getControllerMove()).a(1.0);
      }
   }

   static class PathfinderGoalSlimeNearestPlayer extends PathfinderGoal {
      private final EntitySlime a;
      private int b;

      public PathfinderGoalSlimeNearestPlayer(EntitySlime entityslime) {
         this.a = entityslime;
         this.a(2);
      }

      @Override
      public boolean a() {
         EntityLiving entityliving = this.a.getGoalTarget();
         return entityliving != null
            && entityliving.isAlive()
            && (!(entityliving instanceof EntityHuman) || !((EntityHuman)entityliving).abilities.isInvulnerable);
      }

      @Override
      public void c() {
         this.b = 300;
         super.c();
      }

      @Override
      public boolean b() {
         EntityLiving entityliving = this.a.getGoalTarget();
         return entityliving != null
            && entityliving.isAlive()
            && (!(entityliving instanceof EntityHuman) || !((EntityHuman)entityliving).abilities.isInvulnerable)
            && --this.b > 0;
      }

      @Override
      public void e() {
         this.a.a(this.a.getGoalTarget(), 10.0F, 10.0F);
         ((EntitySlime.ControllerMoveSlime)this.a.getControllerMove()).a(this.a.yaw, this.a.ci());
      }
   }

   static class PathfinderGoalSlimeRandomDirection extends PathfinderGoal {
      private final EntitySlime a;
      private float b;
      private int c;

      public PathfinderGoalSlimeRandomDirection(EntitySlime entityslime) {
         this.a = entityslime;
         this.a(2);
      }

      @Override
      public boolean a() {
         return this.a.getGoalTarget() == null && (this.a.onGround || this.a.V() || this.a.ab());
      }

      @Override
      public void e() {
         if (--this.c <= 0) {
            this.c = 40 + this.a.bc().nextInt(60);
            this.b = this.a.bc().nextInt(360);
         }

         ((EntitySlime.ControllerMoveSlime)this.a.getControllerMove()).a(this.b, false);
      }
   }

   static class PathfinderGoalSlimeRandomJump extends PathfinderGoal {
      private final EntitySlime a;

      public PathfinderGoalSlimeRandomJump(EntitySlime entityslime) {
         this.a = entityslime;
         this.a(5);
         ((Navigation)entityslime.getNavigation()).d(true);
      }

      @Override
      public boolean a() {
         return this.a.V() || this.a.ab();
      }

      @Override
      public void e() {
         if (this.a.bc().nextFloat() < 0.8F) {
            this.a.getControllerJump().a();
         }

         ((EntitySlime.ControllerMoveSlime)this.a.getControllerMove()).a(1.2);
      }
   }
}
