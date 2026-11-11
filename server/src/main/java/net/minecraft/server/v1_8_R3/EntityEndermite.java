package net.minecraft.server.v1_8_R3;

public class EntityEndermite extends EntityMonster {
   private int a = 0;
   private boolean b = false;

   public EntityEndermite(World world) {
      super(world);
      this.b_ = 3;
      this.setSize(0.4F, 0.3F);
      this.goalSelector.a(1, new PathfinderGoalFloat(this));
      this.goalSelector.a(2, new PathfinderGoalMeleeAttack(this, EntityHuman.class, 1.0, false));
      this.goalSelector.a(3, new PathfinderGoalRandomStroll(this, 1.0));
      this.goalSelector.a(7, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
      this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
      this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, true));
      this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, true));
   }

   @Override
   public float getHeadHeight() {
      return 0.1F;
   }

   @Override
   protected void initAttributes() {
      super.initAttributes();
      this.getAttributeInstance(GenericAttributes.maxHealth).setValue(8.0);
      this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.25);
      this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(2.0);
   }

   @Override
   protected boolean s_() {
      return false;
   }

   @Override
   protected String z() {
      return "mob.silverfish.say";
   }

   @Override
   protected String bo() {
      return "mob.silverfish.hit";
   }

   @Override
   protected String bp() {
      return "mob.silverfish.kill";
   }

   @Override
   protected void a(BlockPosition blockposition, Block block) {
      this.makeSound("mob.silverfish.step", 0.15F, 1.0F);
   }

   @Override
   protected Item getLoot() {
      return null;
   }

   @Override
   public void a(NBTTagCompound nbttagcompound) {
      super.a(nbttagcompound);
      this.a = nbttagcompound.getInt("Lifetime");
      this.b = nbttagcompound.getBoolean("PlayerSpawned");
   }

   @Override
   public void b(NBTTagCompound nbttagcompound) {
      super.b(nbttagcompound);
      nbttagcompound.setInt("Lifetime", this.a);
      nbttagcompound.setBoolean("PlayerSpawned", this.b);
   }

   @Override
   public void t_() {
      this.aI = this.yaw;
      super.t_();
   }

   public boolean n() {
      return this.b;
   }

   public void a(boolean flag) {
      this.b = flag;
   }

   @Override
   public void m() {
      super.m();
      if (!this.isPersistent()) {
         this.a++;
      }

      if (this.a >= 2400) {
         this.die();
      }
   }

   @Override
   protected boolean n_() {
      return true;
   }

   @Override
   public boolean bR() {
      if (super.bR()) {
         EntityHuman entityhuman = this.world.findNearbyPlayer(this, 5.0);
         return entityhuman == null;
      } else {
         return false;
      }
   }

   @Override
   public EnumMonsterType getMonsterType() {
      return EnumMonsterType.ARTHROPOD;
   }
}
