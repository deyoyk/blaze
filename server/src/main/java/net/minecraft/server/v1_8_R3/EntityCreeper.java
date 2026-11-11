package net.minecraft.server.v1_8_R3;

import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.CreeperPowerEvent.PowerCause;

public class EntityCreeper extends EntityMonster {
   private int fuseTicks;
   private int maxFuseTicks = 30;
   private int explosionRadius = 3;
   private int bn = 0;
   private int record = -1;

   public EntityCreeper(World world) {
      super(world);
      this.goalSelector.a(1, new PathfinderGoalFloat(this));
      this.goalSelector.a(2, new PathfinderGoalSwell(this));
      this.goalSelector.a(3, new PathfinderGoalAvoidTarget<>(this, EntityOcelot.class, 6.0F, 1.0, 1.2));
      this.goalSelector.a(4, new PathfinderGoalMeleeAttack(this, 1.0, false));
      this.goalSelector.a(5, new PathfinderGoalRandomStroll(this, 0.8));
      this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
      this.goalSelector.a(6, new PathfinderGoalRandomLookaround(this));
      this.targetSelector.a(1, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, true));
      this.targetSelector.a(2, new PathfinderGoalHurtByTarget(this, false));
   }

   @Override
   protected void initAttributes() {
      super.initAttributes();
      this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.25);
   }

   @Override
   public int aE() {
      return this.getGoalTarget() == null ? 3 : 3 + (int)(this.getHealth() - 1.0F);
   }

   @Override
   public void e(float f, float f1) {
      super.e(f, f1);
      this.fuseTicks = (int)(this.fuseTicks + f * 1.5F);
      if (this.fuseTicks > this.maxFuseTicks - 5) {
         this.fuseTicks = this.maxFuseTicks - 5;
      }
   }

   @Override
   protected void h() {
      super.h();
      this.datawatcher.a(16, (byte)-1);
      this.datawatcher.a(17, (byte)0);
      this.datawatcher.a(18, (byte)0);
   }

   @Override
   public void b(NBTTagCompound nbttagcompound) {
      super.b(nbttagcompound);
      if (this.datawatcher.getByte(17) == 1) {
         nbttagcompound.setBoolean("powered", true);
      }

      nbttagcompound.setShort("Fuse", (short)this.maxFuseTicks);
      nbttagcompound.setByte("ExplosionRadius", (byte)this.explosionRadius);
      nbttagcompound.setBoolean("ignited", this.cn());
   }

   @Override
   public void a(NBTTagCompound nbttagcompound) {
      super.a(nbttagcompound);
      this.datawatcher.watch(17, (byte)(nbttagcompound.getBoolean("powered") ? 1 : 0));
      if (nbttagcompound.hasKeyOfType("Fuse", 99)) {
         this.maxFuseTicks = nbttagcompound.getShort("Fuse");
      }

      if (nbttagcompound.hasKeyOfType("ExplosionRadius", 99)) {
         this.explosionRadius = nbttagcompound.getByte("ExplosionRadius");
      }

      if (nbttagcompound.getBoolean("ignited")) {
         this.co();
      }
   }

   @Override
   public void t_() {
      if (this.isAlive()) {
         if (this.cn()) {
            this.a(1);
         }

         int i = this.cm();
         if (i > 0 && this.fuseTicks == 0) {
            this.makeSound("creeper.primed", 1.0F, 0.5F);
         }

         this.fuseTicks += i;
         if (this.fuseTicks < 0) {
            this.fuseTicks = 0;
         }

         if (this.fuseTicks >= this.maxFuseTicks) {
            this.fuseTicks = this.maxFuseTicks;
            this.cr();
         }
      }

      super.t_();
   }

   @Override
   protected String bo() {
      return "mob.creeper.say";
   }

   @Override
   protected String bp() {
      return "mob.creeper.death";
   }

   @Override
   public void die(DamageSource damagesource) {
      if (damagesource.getEntity() instanceof EntitySkeleton) {
         int i = Item.getId(Items.RECORD_13);
         int j = Item.getId(Items.RECORD_WAIT);
         this.record = i + this.random.nextInt(j - i + 1);
      } else if (damagesource.getEntity() instanceof EntityCreeper
         && damagesource.getEntity() != this
         && ((EntityCreeper)damagesource.getEntity()).isPowered()
         && ((EntityCreeper)damagesource.getEntity()).cp()) {
         ((EntityCreeper)damagesource.getEntity()).cq();
         this.headDrop = new ItemStack(Items.SKULL, 1, 4);
      }

      super.die(damagesource);
   }

   @Override
   protected void dropDeathLoot(boolean flag, int i) {
      super.dropDeathLoot(flag, i);
      if (this.record != -1) {
         this.a(Item.getById(this.record), 1);
         this.record = -1;
      }
   }

   @Override
   public boolean r(Entity entity) {
      return true;
   }

   public boolean isPowered() {
      return this.datawatcher.getByte(17) == 1;
   }

   public void setPowered(boolean powered) {
      if (!powered) {
         this.datawatcher.watch(17, (byte)0);
      } else {
         this.datawatcher.watch(17, (byte)1);
      }
   }

   @Override
   protected Item getLoot() {
      return Items.GUNPOWDER;
   }

   public int cm() {
      return this.datawatcher.getByte(16);
   }

   public void a(int i) {
      this.datawatcher.watch(16, (byte)i);
   }

   @Override
   public void onLightningStrike(EntityLightning entitylightning) {
      super.onLightningStrike(entitylightning);
      if (!CraftEventFactory.callCreeperPowerEvent(this, entitylightning, PowerCause.LIGHTNING).isCancelled()) {
         this.setPowered(true);
      }
   }

   @Override
   protected boolean a(EntityHuman entityhuman) {
      ItemStack itemstack = entityhuman.inventory.getItemInHand();
      if (itemstack != null && itemstack.getItem() == Items.FLINT_AND_STEEL) {
         this.world.makeSound(this.locX + 0.5, this.locY + 0.5, this.locZ + 0.5, "fire.ignite", 1.0F, this.random.nextFloat() * 0.4F + 0.8F);
         entityhuman.bw();
         this.co();
         itemstack.damage(1, entityhuman);
         return true;
      } else {
         return super.a(entityhuman);
      }
   }

   private void cr() {
      boolean flag = this.world.getGameRules().getBoolean("mobGriefing");
      float f = this.isPowered() ? 2.0F : 1.0F;
      ExplosionPrimeEvent event = new ExplosionPrimeEvent(this.getBukkitEntity(), this.explosionRadius * f, false);
      this.world.getServer().getPluginManager().callEvent(event);
      if (!event.isCancelled()) {
         this.world.createExplosion(this, this.locX, this.locY, this.locZ, event.getRadius(), event.getFire(), flag);
         this.die();
      } else {
         this.fuseTicks = 0;
      }
   }

   public boolean cn() {
      return this.datawatcher.getByte(18) != 0;
   }

   public void co() {
      this.datawatcher.watch(18, (byte)1);
   }

   public boolean cp() {
      return this.bn < 1 && this.world.getGameRules().getBoolean("doMobLoot");
   }

   public void cq() {
      this.bn++;
   }
}
