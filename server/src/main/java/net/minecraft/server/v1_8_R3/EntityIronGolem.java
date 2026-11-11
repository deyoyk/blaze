package net.minecraft.server.v1_8_R3;

import com.google.common.base.Predicate;
import lol.deyo.blaze.configs.BlazeConfig;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;

public class EntityIronGolem extends EntityGolem {
   private int b;
   Village a;
   private int c;
   private int bm;

   public EntityIronGolem(World world) {
      super(world);
      this.setSize(1.4F, 2.9F);
      ((Navigation)this.getNavigation()).a(true);
      this.goalSelector.a(1, new PathfinderGoalMeleeAttack(this, 1.0, true));
      this.goalSelector.a(2, new PathfinderGoalMoveTowardsTarget(this, 0.9, 32.0F));
      this.goalSelector.a(3, new PathfinderGoalMoveThroughVillage(this, 0.6, true));
      this.goalSelector.a(4, new PathfinderGoalMoveTowardsRestriction(this, 1.0));
      this.goalSelector.a(5, new PathfinderGoalOfferFlower(this));
      this.goalSelector.a(6, new PathfinderGoalRandomStroll(this, 0.6));
      this.goalSelector.a(7, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
      this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
      this.targetSelector.a(1, new PathfinderGoalDefendVillage(this));
      this.targetSelector.a(2, new PathfinderGoalHurtByTarget(this, false));
      this.targetSelector.a(3, new EntityIronGolem.PathfinderGoalNearestGolemTarget<>(this, EntityInsentient.class, 10, false, true, IMonster.e));
   }

   @Override
   public void e(float f, float f1) {
      if ((Boolean)BlazeConfig.ironGolemFallDamage.getValue()) {
         super.e(f, f1);
      }
   }

   @Override
   protected void h() {
      super.h();
      this.datawatcher.a(16, (byte)0);
   }

   @Override
   protected void E() {
      if (--this.b <= 0) {
         this.b = 70 + this.random.nextInt(50);
         this.a = this.world.ae().getClosestVillage(MathHelper.floor(this.locX), MathHelper.floor(this.locY), MathHelper.floor(this.locZ), 32);
         if (this.a == null) {
            this.cj();
         } else {
            this.a(this.a.a(), (int)(this.a.b() * 0.6F));
         }
      }

      super.E();
   }

   @Override
   protected void initAttributes() {
      super.initAttributes();
      this.getAttributeInstance(GenericAttributes.maxHealth).setValue(100.0);
      this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.25);
   }

   @Override
   protected int j(int i) {
      return i;
   }

   @Override
   protected void s(Entity entity) {
      if (entity instanceof IMonster && !(entity instanceof EntityCreeper) && this.bc().nextInt(20) == 0) {
         this.setGoalTarget((EntityLiving)entity, TargetReason.COLLISION, true);
      }

      super.s(entity);
   }

   @Override
   public void m() {
      super.m();
      if (this.c > 0) {
         this.c--;
      }

      if (this.bm > 0) {
         this.bm--;
      }

      if (this.motX * this.motX + this.motZ * this.motZ > 2.5000003E-7F && this.random.nextInt(5) == 0) {
         int i = MathHelper.floor(this.locX);
         int j = MathHelper.floor(this.locY - 0.2F);
         int k = MathHelper.floor(this.locZ);
         IBlockData iblockdata = this.world.getType(i, j, k);
         Block block = iblockdata.getBlock();
         if (block.getMaterial() != Material.AIR) {
            this.world
               .addParticle(
                  EnumParticle.BLOCK_CRACK,
                  this.locX + (this.random.nextFloat() - 0.5) * this.width,
                  this.getBoundingBox().b + 0.1,
                  this.locZ + (this.random.nextFloat() - 0.5) * this.width,
                  4.0 * (this.random.nextFloat() - 0.5),
                  0.5,
                  (this.random.nextFloat() - 0.5) * 4.0,
                  Block.getCombinedId(iblockdata)
               );
         }
      }
   }

   @Override
   public boolean a(Class<? extends EntityLiving> oclass) {
      return (!this.isPlayerCreated() || !EntityHuman.class.isAssignableFrom(oclass)) && oclass != EntityCreeper.class && super.a(oclass);
   }

   @Override
   public void b(NBTTagCompound nbttagcompound) {
      super.b(nbttagcompound);
      nbttagcompound.setBoolean("PlayerCreated", this.isPlayerCreated());
   }

   @Override
   public void a(NBTTagCompound nbttagcompound) {
      super.a(nbttagcompound);
      this.setPlayerCreated(nbttagcompound.getBoolean("PlayerCreated"));
   }

   @Override
   public boolean r(Entity entity) {
      this.c = 10;
      this.world.broadcastEntityEffect(this, (byte)4);
      boolean flag = entity.damageEntity(DamageSource.mobAttack(this), 7 + this.random.nextInt(15));
      if (flag) {
         entity.motY += 0.4F;
         this.a(this, entity);
      }

      this.makeSound("mob.irongolem.throw", 1.0F, 1.0F);
      return flag;
   }

   public Village n() {
      return this.a;
   }

   public void a(boolean flag) {
      this.bm = flag ? 400 : 0;
      this.world.broadcastEntityEffect(this, (byte)11);
   }

   @Override
   protected String bo() {
      return "mob.irongolem.hit";
   }

   @Override
   protected String bp() {
      return "mob.irongolem.death";
   }

   @Override
   protected void a(BlockPosition blockposition, Block block) {
      this.makeSound("mob.irongolem.walk", 1.0F, 1.0F);
   }

   @Override
   protected void dropDeathLoot(boolean flag, int i) {
      int j = this.random.nextInt(3);

      for (int k = 0; k < j; k++) {
         this.a(Item.getItemOf(Blocks.RED_FLOWER), 1, BlockFlowers.EnumFlowerVarient.POPPY.b());
      }

      int var6 = 3 + this.random.nextInt(3);

      for (int l = 0; l < var6; l++) {
         this.a(Items.IRON_INGOT, 1);
      }
   }

   public int cm() {
      return this.bm;
   }

   public boolean isPlayerCreated() {
      return (this.datawatcher.getByte(16) & 1) != 0;
   }

   public void setPlayerCreated(boolean flag) {
      byte b0 = this.datawatcher.getByte(16);
      if (flag) {
         this.datawatcher.watch(16, (byte)(b0 | 1));
      } else {
         this.datawatcher.watch(16, (byte)(b0 & -2));
      }
   }

   @Override
   public void die(DamageSource damagesource) {
      if (!this.isPlayerCreated() && this.killer != null && this.a != null) {
         this.a.a(this.killer.getName(), -5);
      }

      super.die(damagesource);
   }

   static class PathfinderGoalNearestGolemTarget<T extends EntityLiving> extends PathfinderGoalNearestAttackableTarget<T> {
      public PathfinderGoalNearestGolemTarget(
         final EntityCreature entitycreature, Class<T> oclass, int i, boolean flag, boolean flag1, final Predicate<? super T> predicate
      ) {
         super(entitycreature, oclass, i, flag, flag1, predicate);
         this.c = new Predicate() {
            public boolean a(T t0) {
               if (predicate != null && !predicate.apply(t0)) {
                  return false;
               } else if (t0 instanceof EntityCreeper) {
                  return false;
               } else {
                  if (t0 instanceof EntityHuman) {
                     double d0 = PathfinderGoalNearestGolemTarget.this.f();
                     if (t0.isSneaking()) {
                        d0 *= 0.8F;
                     }

                     if (t0.isInvisible()) {
                        float f = ((EntityHuman)t0).bY();
                        if (f < 0.1F) {
                           f = 0.1F;
                        }

                        d0 *= 0.7F * f;
                     }

                     if (t0.g(entitycreature) > d0) {
                        return false;
                     }
                  }

                  return PathfinderGoalNearestGolemTarget.this.a(t0, false);
               }
            }

            public boolean apply(Object object) {
               return this.a((T)object);
            }
         };
      }
   }
}
