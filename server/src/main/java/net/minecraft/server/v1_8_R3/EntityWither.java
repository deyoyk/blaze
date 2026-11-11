package net.minecraft.server.v1_8_R3;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.List;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

public class EntityWither extends EntityMonster implements IRangedEntity {
   private static final Predicate<Entity> bq = new Predicate() {
      public boolean a(Entity entity) {
         return entity instanceof EntityLiving && ((EntityLiving)entity).getMonsterType() != EnumMonsterType.UNDEAD;
      }

      public boolean apply(Object object) {
         return this.a((Entity)object);
      }
   };
   private final float[] a = new float[2];
   private final float[] b = new float[2];
   private final int[] bn = new int[2];
   private final int[] bo = new int[2];
   private int bp;

   public EntityWither(World world) {
      super(world);
      this.setHealth(this.getMaxHealth());
      this.setSize(0.9F, 3.5F);
      this.fireProof = true;
      ((Navigation)this.getNavigation()).d(true);
      this.goalSelector.a(0, new PathfinderGoalFloat(this));
      this.goalSelector.a(2, new PathfinderGoalArrowAttack(this, 1.0, 40, 20.0F));
      this.goalSelector.a(5, new PathfinderGoalRandomStroll(this, 1.0));
      this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
      this.goalSelector.a(7, new PathfinderGoalRandomLookaround(this));
      this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, false));
      this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityInsentient.class, 0, false, false, bq));
      this.b_ = 50;
   }

   public static boolean a(Block block) {
      return block != Blocks.BEDROCK
         && block != Blocks.END_PORTAL
         && block != Blocks.END_PORTAL_FRAME
         && block != Blocks.COMMAND_BLOCK
         && block != Blocks.BARRIER;
   }

   @Override
   protected void h() {
      super.h();
      this.datawatcher.a(17, 0);
      this.datawatcher.a(18, 0);
      this.datawatcher.a(19, 0);
      this.datawatcher.a(20, 0);
   }

   @Override
   public void b(NBTTagCompound nbttagcompound) {
      super.b(nbttagcompound);
      nbttagcompound.setInt("Invul", this.cl());
   }

   @Override
   public void a(NBTTagCompound nbttagcompound) {
      super.a(nbttagcompound);
      this.r(nbttagcompound.getInt("Invul"));
   }

   @Override
   protected String z() {
      return "mob.wither.idle";
   }

   @Override
   protected String bo() {
      return "mob.wither.hurt";
   }

   @Override
   protected String bp() {
      return "mob.wither.death";
   }

   @Override
   public void m() {
      this.motY *= 0.6F;
      if (this.s(0) > 0) {
         Entity entity = this.world.a(this.s(0));
         if (entity != null) {
            if (this.locY < entity.locY || !this.cm() && this.locY < entity.locY + 5.0) {
               if (this.motY < 0.0) {
                  this.motY = 0.0;
               }

               this.motY = this.motY + (0.5 - this.motY) * 0.6F;
            }

            double d3 = entity.locX - this.locX;
            double d0 = entity.locZ - this.locZ;
            double d1 = d3 * d3 + d0 * d0;
            if (d1 > 9.0) {
               double d2 = MathHelper.sqrt(d1);
               this.motX = this.motX + (d3 / d2 * 0.5 - this.motX) * 0.6F;
               this.motZ = this.motZ + (d0 / d2 * 0.5 - this.motZ) * 0.6F;
            }
         }
      }

      if (this.motX * this.motX + this.motZ * this.motZ > 0.05F) {
         this.yaw = (float)MathHelper.b(this.motZ, this.motX) * (180.0F / (float)Math.PI) - 90.0F;
      }

      super.m();

      for (int i = 0; i < 2; i++) {
         int j = this.s(i + 1);
         Entity entity1 = null;
         if (j > 0) {
            entity1 = this.world.a(j);
         }

         if (entity1 != null) {
            double d0 = this.t(i + 1);
            double d1 = this.u(i + 1);
            double d2 = this.v(i + 1);
            double d4 = entity1.locX - d0;
            double d5 = entity1.locY + entity1.getHeadHeight() - d1;
            double d6 = entity1.locZ - d2;
            double d7 = MathHelper.sqrt(d4 * d4 + d6 * d6);
            float f = (float)(MathHelper.b(d6, d4) * 180.0 / (float) Math.PI) - 90.0F;
            float f1 = (float)(-(MathHelper.b(d5, d7) * 180.0 / (float) Math.PI));
            this.a[i] = this.b(this.a[i], f1, 40.0F);
            this.b[i] = this.b(this.b[i], f, 10.0F);
         } else {
            this.b[i] = this.b(this.b[i], this.aI, 10.0F);
         }
      }

      boolean flag = this.cm();

      for (int jx = 0; jx < 3; jx++) {
         double d8 = this.t(jx);
         double d9 = this.u(jx);
         double d10 = this.v(jx);
         this.world
            .addParticle(
               EnumParticle.SMOKE_NORMAL,
               d8 + this.random.nextGaussian() * 0.3F,
               d9 + this.random.nextGaussian() * 0.3F,
               d10 + this.random.nextGaussian() * 0.3F,
               0.0,
               0.0,
               0.0,
               EnumParticle.EMPTY_ARRAY
            );
         if (flag && this.world.random.nextInt(4) == 0) {
            this.world
               .addParticle(
                  EnumParticle.SPELL_MOB,
                  d8 + this.random.nextGaussian() * 0.3F,
                  d9 + this.random.nextGaussian() * 0.3F,
                  d10 + this.random.nextGaussian() * 0.3F,
                  0.7F,
                  0.7F,
                  0.5,
                  EnumParticle.EMPTY_ARRAY
               );
         }
      }

      if (this.cl() > 0) {
         for (int var26 = 0; var26 < 3; var26++) {
            this.world
               .addParticle(
                  EnumParticle.SPELL_MOB,
                  this.locX + this.random.nextGaussian(),
                  this.locY + this.random.nextFloat() * 3.3F,
                  this.locZ + this.random.nextGaussian(),
                  0.7F,
                  0.7F,
                  0.9F,
                  EnumParticle.EMPTY_ARRAY
               );
         }
      }
   }

   @Override
   protected void E() {
      if (this.cl() > 0) {
         int i = this.cl() - 1;
         if (i <= 0) {
            ExplosionPrimeEvent event = new ExplosionPrimeEvent(this.getBukkitEntity(), 7.0F, false);
            this.world.getServer().getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
               this.world
                  .createExplosion(
                     this,
                     this.locX,
                     this.locY + this.getHeadHeight(),
                     this.locZ,
                     event.getRadius(),
                     event.getFire(),
                     this.world.getGameRules().getBoolean("mobGriefing")
                  );
            }

            int viewDistance = this.world.getServer().getViewDistance() * 16;

            for (EntityPlayer player : MinecraftServer.getServer().getPlayerList().players) {
               double deltaX = this.locX - player.locX;
               double deltaZ = this.locZ - player.locZ;
               double distanceSquared = deltaX * deltaX + deltaZ * deltaZ;
               if (this.world.spigotConfig.witherSpawnSoundRadius <= 0
                  || !(distanceSquared > this.world.spigotConfig.witherSpawnSoundRadius * this.world.spigotConfig.witherSpawnSoundRadius)) {
                  if (distanceSquared > viewDistance * viewDistance) {
                     double deltaLength = Math.sqrt(distanceSquared);
                     double relativeX = player.locX + deltaX / deltaLength * viewDistance;
                     double relativeZ = player.locZ + deltaZ / deltaLength * viewDistance;
                     player.playerConnection
                        .sendPacket(new PacketPlayOutWorldEvent(1013, new BlockPosition((int)relativeX, (int)this.locY, (int)relativeZ), 0, true));
                  } else {
                     player.playerConnection
                        .sendPacket(new PacketPlayOutWorldEvent(1013, new BlockPosition((int)this.locX, (int)this.locY, (int)this.locZ), 0, true));
                  }
               }
            }
         }

         this.r(i);
         if (this.ticksLived % 10 == 0) {
            this.heal(10.0F, RegainReason.WITHER_SPAWN);
         }
      } else {
         super.E();

         for (int ix = 1; ix < 3; ix++) {
            if (this.ticksLived >= this.bn[ix - 1]) {
               this.bn[ix - 1] = this.ticksLived + 10 + this.random.nextInt(10);
               if (this.world.getDifficulty() == EnumDifficulty.NORMAL || this.world.getDifficulty() == EnumDifficulty.HARD) {
                  int k = ix - 1;
                  int l = this.bo[ix - 1];
                  this.bo[k] = this.bo[ix - 1] + 1;
                  if (l > 15) {
                     float f = 10.0F;
                     float f1 = 5.0F;
                     double d0 = MathHelper.a(this.random, this.locX - f, this.locX + f);
                     double d1 = MathHelper.a(this.random, this.locY - f1, this.locY + f1);
                     double d2 = MathHelper.a(this.random, this.locZ - f, this.locZ + f);
                     this.a(ix + 1, d0, d1, d2, true);
                     this.bo[ix - 1] = 0;
                  }
               }

               int j = this.s(ix);
               if (j > 0) {
                  Entity entity = this.world.a(j);
                  if (entity == null || !entity.isAlive() || !(this.h(entity) <= 900.0) || !this.hasLineOfSight(entity)) {
                     this.b(ix, 0);
                  } else if (entity instanceof EntityHuman && ((EntityHuman)entity).abilities.isInvulnerable) {
                     this.b(ix, 0);
                  } else {
                     this.a(ix + 1, (EntityLiving)entity);
                     this.bn[ix - 1] = this.ticksLived + 40 + this.random.nextInt(20);
                     this.bo[ix - 1] = 0;
                  }
               } else {
                  List<EntityLiving> list = this.world
                     .a(EntityLiving.class, this.getBoundingBox().grow(20.0, 8.0, 20.0), Predicates.and(bq, IEntitySelector.d));

                  for (int i1 = 0; i1 < 10 && !list.isEmpty(); i1++) {
                     EntityLiving entityliving = list.get(this.random.nextInt(list.size()));
                     if (entityliving != this && entityliving.isAlive() && this.hasLineOfSight(entityliving)) {
                        if (entityliving instanceof EntityHuman) {
                           if (!((EntityHuman)entityliving).abilities.isInvulnerable) {
                              this.b(ix, entityliving.getId());
                           }
                        } else {
                           this.b(ix, entityliving.getId());
                        }
                        break;
                     }

                     list.remove(entityliving);
                  }
               }
            }
         }

         if (this.getGoalTarget() != null) {
            this.b(0, this.getGoalTarget().getId());
         } else {
            this.b(0, 0);
         }

         if (this.bp > 0) {
            this.bp--;
            if (this.bp == 0 && this.world.getGameRules().getBoolean("mobGriefing")) {
               int var19 = MathHelper.floor(this.locY);
               int j = MathHelper.floor(this.locX);
               int j1 = MathHelper.floor(this.locZ);
               boolean flag = false;

               for (int k1 = -1; k1 <= 1; k1++) {
                  for (int l1 = -1; l1 <= 1; l1++) {
                     for (int i2 = 0; i2 <= 3; i2++) {
                        int j2 = j + k1;
                        int k2 = var19 + i2;
                        int l2 = j1 + l1;
                        BlockPosition blockposition = new BlockPosition(j2, k2, l2);
                        Block block = this.world.getType(blockposition).getBlock();
                        if (block.getMaterial() != Material.AIR
                           && a(block)
                           && !CraftEventFactory.callEntityChangeBlockEvent(this, j2, k2, l2, Blocks.AIR, 0).isCancelled()) {
                           flag = this.world.setAir(blockposition, true) || flag;
                        }
                     }
                  }
               }

               if (flag) {
                  this.world.a(null, 1012, new BlockPosition(this), 0);
               }
            }
         }

         if (this.ticksLived % 20 == 0) {
            this.heal(1.0F, RegainReason.REGEN);
         }
      }
   }

   public void n() {
      this.r(220);
      this.setHealth(this.getMaxHealth() / 3.0F);
   }

   @Override
   public void aA() {
   }

   @Override
   public int br() {
      return 4;
   }

   private double t(int i) {
      if (i <= 0) {
         return this.locX;
      } else {
         float f = (this.aI + 180 * (i - 1)) / 180.0F * (float) Math.PI;
         float f1 = MathHelper.cos(f);
         return this.locX + f1 * 1.3;
      }
   }

   private double u(int i) {
      return i <= 0 ? this.locY + 3.0 : this.locY + 2.2;
   }

   private double v(int i) {
      if (i <= 0) {
         return this.locZ;
      } else {
         float f = (this.aI + 180 * (i - 1)) / 180.0F * (float) Math.PI;
         float f1 = MathHelper.sin(f);
         return this.locZ + f1 * 1.3;
      }
   }

   private float b(float f, float f1, float f2) {
      float f3 = MathHelper.g(f1 - f);
      if (f3 > f2) {
         f3 = f2;
      }

      if (f3 < -f2) {
         f3 = -f2;
      }

      return f + f3;
   }

   private void a(int i, EntityLiving entityliving) {
      this.a(i, entityliving.locX, entityliving.locY + entityliving.getHeadHeight() * 0.5, entityliving.locZ, i == 0 && this.random.nextFloat() < 0.001F);
   }

   private void a(int i, double d0, double d1, double d2, boolean flag) {
      this.world.a(null, 1014, new BlockPosition(this), 0);
      double d3 = this.t(i);
      double d4 = this.u(i);
      double d5 = this.v(i);
      double d6 = d0 - d3;
      double d7 = d1 - d4;
      double d8 = d2 - d5;
      EntityWitherSkull entitywitherskull = new EntityWitherSkull(this.world, this, d6, d7, d8);
      if (flag) {
         entitywitherskull.setCharged(true);
      }

      entitywitherskull.locY = d4;
      entitywitherskull.locX = d3;
      entitywitherskull.locZ = d5;
      this.world.addEntity(entitywitherskull);
   }

   @Override
   public void a(EntityLiving entityliving, float f) {
      this.a(0, entityliving);
   }

   @Override
   public boolean damageEntity(DamageSource damagesource, float f) {
      if (this.isInvulnerable(damagesource)) {
         return false;
      } else if (damagesource == DamageSource.DROWN || damagesource.getEntity() instanceof EntityWither) {
         return false;
      } else if (this.cl() > 0 && damagesource != DamageSource.OUT_OF_WORLD) {
         return false;
      } else {
         if (this.cm()) {
            Entity entity = damagesource.i();
            if (entity instanceof EntityArrow) {
               return false;
            }
         }

         Entity entity = damagesource.getEntity();
         if (!(entity instanceof EntityHuman) && entity instanceof EntityLiving && ((EntityLiving)entity).getMonsterType() == this.getMonsterType()) {
            return false;
         } else {
            if (this.bp <= 0) {
               this.bp = 20;
            }

            for (int i = 0; i < this.bo.length; i++) {
               this.bo[i] = this.bo[i] + 3;
            }

            return super.damageEntity(damagesource, f);
         }
      }
   }

   @Override
   protected void dropDeathLoot(boolean flag, int i) {
      EntityItem entityitem = this.a(Items.NETHER_STAR, 1);
      if (entityitem != null) {
         entityitem.u();
      }
   }

   @Override
   protected void D() {
      this.ticksFarFromPlayer = 0;
   }

   @Override
   public void e(float f, float f1) {
   }

   @Override
   public void addEffect(MobEffect mobeffect) {
   }

   @Override
   protected void initAttributes() {
      super.initAttributes();
      this.getAttributeInstance(GenericAttributes.maxHealth).setValue(300.0);
      this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.6F);
      this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(40.0);
   }

   public int cl() {
      return this.datawatcher.getInt(20);
   }

   public void r(int i) {
      this.datawatcher.watch(20, i);
   }

   public int s(int i) {
      return this.datawatcher.getInt(17 + i);
   }

   public void b(int i, int j) {
      this.datawatcher.watch(17 + i, j);
   }

   public boolean cm() {
      return this.getHealth() <= this.getMaxHealth() / 2.0F;
   }

   @Override
   public EnumMonsterType getMonsterType() {
      return EnumMonsterType.UNDEAD;
   }

   @Override
   public void mount(Entity entity) {
      this.vehicle = null;
   }
}
