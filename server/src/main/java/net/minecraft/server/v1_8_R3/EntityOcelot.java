package net.minecraft.server.v1_8_R3;

import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class EntityOcelot extends EntityTameableAnimal {
   public boolean spawnBonus = true;
   private PathfinderGoalAvoidTarget<EntityHuman> bo;
   private final PathfinderGoalTempt bp;

   public EntityOcelot(World world) {
      super(world);
      this.setSize(0.6F, 0.7F);
      ((Navigation)this.getNavigation()).a(true);
      this.goalSelector.a(1, new PathfinderGoalFloat(this));
      this.goalSelector.a(2, this.bm);
      this.goalSelector.a(3, this.bp = new PathfinderGoalTempt(this, 0.6, Items.FISH, true));
      this.goalSelector.a(5, new PathfinderGoalFollowOwner(this, 1.0, 10.0F, 5.0F));
      this.goalSelector.a(6, new PathfinderGoalJumpOnBlock(this, 0.8));
      this.goalSelector.a(7, new PathfinderGoalLeapAtTarget(this, 0.3F));
      this.goalSelector.a(8, new PathfinderGoalOcelotAttack(this));
      this.goalSelector.a(9, new PathfinderGoalBreed(this, 0.8));
      this.goalSelector.a(10, new PathfinderGoalRandomStroll(this, 0.8));
      this.goalSelector.a(11, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 10.0F));
      this.targetSelector.a(1, new PathfinderGoalRandomTargetNonTamed<>(this, EntityChicken.class, false, null));
   }

   @Override
   protected void h() {
      super.h();
      this.datawatcher.a(18, (byte)0);
   }

   @Override
   public void E() {
      if (this.getControllerMove().a()) {
         double d0 = this.getControllerMove().b();
         if (d0 == 0.6) {
            this.setSneaking(true);
            this.setSprinting(false);
         } else if (d0 == 1.33) {
            this.setSneaking(false);
            this.setSprinting(true);
         } else {
            this.setSneaking(false);
            this.setSprinting(false);
         }
      } else {
         this.setSneaking(false);
         this.setSprinting(false);
      }
   }

   @Override
   public boolean isTypeNotPersistent() {
      return !this.isTamed();
   }

   @Override
   protected void initAttributes() {
      super.initAttributes();
      this.getAttributeInstance(GenericAttributes.maxHealth).setValue(10.0);
      this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.3F);
   }

   @Override
   public void e(float f, float f1) {
   }

   @Override
   public void b(NBTTagCompound nbttagcompound) {
      super.b(nbttagcompound);
      nbttagcompound.setInt("CatType", this.getCatType());
   }

   @Override
   public void a(NBTTagCompound nbttagcompound) {
      super.a(nbttagcompound);
      this.setCatType(nbttagcompound.getInt("CatType"));
   }

   @Override
   protected String z() {
      return this.isTamed() ? (this.isInLove() ? "mob.cat.purr" : (this.random.nextInt(4) == 0 ? "mob.cat.purreow" : "mob.cat.meow")) : "";
   }

   @Override
   protected String bo() {
      return "mob.cat.hitt";
   }

   @Override
   protected String bp() {
      return "mob.cat.hitt";
   }

   @Override
   protected float bB() {
      return 0.4F;
   }

   @Override
   protected Item getLoot() {
      return Items.LEATHER;
   }

   @Override
   public boolean r(Entity entity) {
      return entity.damageEntity(DamageSource.mobAttack(this), 3.0F);
   }

   @Override
   protected void dropDeathLoot(boolean flag, int i) {
   }

   @Override
   public boolean a(EntityHuman entityhuman) {
      ItemStack itemstack = entityhuman.inventory.getItemInHand();
      if (this.isTamed()) {
         if (this.e((EntityLiving)entityhuman) && !this.d(itemstack)) {
            this.bm.setSitting(!this.isSitting());
         }
      } else if (this.bp.f() && itemstack != null && itemstack.getItem() == Items.FISH && entityhuman.h(this) < 9.0) {
         if (!entityhuman.abilities.canInstantlyBuild) {
            itemstack.count--;
         }

         if (itemstack.count <= 0) {
            entityhuman.inventory.setItem(entityhuman.inventory.itemInHandIndex, null);
         }

         if (this.random.nextInt(3) == 0 && !CraftEventFactory.callEntityTameEvent(this, entityhuman).isCancelled()) {
            this.setTamed(true);
            this.setCatType(1 + this.world.random.nextInt(3));
            this.setOwnerUUID(entityhuman.getUniqueID().toString());
            this.l(true);
            this.bm.setSitting(true);
            this.world.broadcastEntityEffect(this, (byte)7);
         } else {
            this.l(false);
            this.world.broadcastEntityEffect(this, (byte)6);
         }

         return true;
      }

      return super.a(entityhuman);
   }

   public EntityOcelot b(EntityAgeable entityageable) {
      EntityOcelot entityocelot = new EntityOcelot(this.world);
      if (this.isTamed()) {
         entityocelot.setOwnerUUID(this.getOwnerUUID());
         entityocelot.setTamed(true);
         entityocelot.setCatType(this.getCatType());
      }

      return entityocelot;
   }

   @Override
   public boolean d(ItemStack itemstack) {
      return itemstack != null && itemstack.getItem() == Items.FISH;
   }

   @Override
   public boolean mate(EntityAnimal entityanimal) {
      if (entityanimal == this) {
         return false;
      } else if (!this.isTamed()) {
         return false;
      } else if (!(entityanimal instanceof EntityOcelot)) {
         return false;
      } else {
         EntityOcelot entityocelot = (EntityOcelot)entityanimal;
         return entityocelot.isTamed() && this.isInLove() && entityocelot.isInLove();
      }
   }

   public int getCatType() {
      return this.datawatcher.getByte(18);
   }

   public void setCatType(int i) {
      this.datawatcher.watch(18, (byte)i);
   }

   @Override
   public boolean bR() {
      return this.world.random.nextInt(3) != 0;
   }

   @Override
   public boolean canSpawn() {
      if (this.world.a(this.getBoundingBox(), this)
         && this.world.getCubes(this, this.getBoundingBox()).isEmpty()
         && !this.world.containsLiquid(this.getBoundingBox())) {
         BlockPosition blockposition = new BlockPosition(this.locX, this.getBoundingBox().b, this.locZ);
         if (blockposition.getY() < this.world.F()) {
            return false;
         } else {
            Block block = this.world.getType(blockposition.getX(), blockposition.getY() - 1, blockposition.getZ()).getBlock();
            return block == Blocks.GRASS || block.getMaterial() == Material.LEAVES;
         }
      } else {
         return false;
      }
   }

   @Override
   public String getName() {
      return this.hasCustomName() ? this.getCustomName() : (this.isTamed() ? LocaleI18n.get("entity.Cat.name") : super.getName());
   }

   @Override
   public void setTamed(boolean flag) {
      super.setTamed(flag);
   }

   @Override
   protected void cm() {
      if (this.bo == null) {
         this.bo = new PathfinderGoalAvoidTarget<>(this, EntityHuman.class, 16.0F, 0.8, 1.33);
      }

      this.goalSelector.a(this.bo);
      if (!this.isTamed()) {
         this.goalSelector.a(4, this.bo);
      }
   }

   @Override
   public GroupDataEntity prepare(DifficultyDamageScaler difficultydamagescaler, GroupDataEntity groupdataentity) {
      groupdataentity = super.prepare(difficultydamagescaler, groupdataentity);
      if (this.spawnBonus && this.world.random.nextInt(7) == 0) {
         for (int i = 0; i < 2; i++) {
            EntityOcelot entityocelot = new EntityOcelot(this.world);
            entityocelot.setPositionRotation(this.locX, this.locY, this.locZ, this.yaw, 0.0F);
            entityocelot.setAgeRaw(-24000);
            this.world.addEntity(entityocelot, SpawnReason.OCELOT_BABY);
         }
      }

      return groupdataentity;
   }

   @Override
   public EntityAgeable createChild(EntityAgeable entityageable) {
      return this.b(entityageable);
   }
}
