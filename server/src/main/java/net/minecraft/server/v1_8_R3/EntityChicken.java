package net.minecraft.server.v1_8_R3;

public class EntityChicken extends EntityAnimal {
   public float bm;
   public float bo;
   public float bp;
   public float bq;
   public float br = 1.0F;
   public int bs;
   public boolean bt;

   public EntityChicken(World world) {
      super(world);
      this.setSize(0.4F, 0.7F);
      this.bs = this.random.nextInt(6000) + 6000;
      this.goalSelector.a(0, new PathfinderGoalFloat(this));
      this.goalSelector.a(1, new PathfinderGoalPanic(this, 1.4));
      this.goalSelector.a(2, new PathfinderGoalBreed(this, 1.0));
      this.goalSelector.a(3, new PathfinderGoalTempt(this, 1.0, Items.WHEAT_SEEDS, false));
      this.goalSelector.a(4, new PathfinderGoalFollowParent(this, 1.1));
      this.goalSelector.a(5, new PathfinderGoalRandomStroll(this, 1.0));
      this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
      this.goalSelector.a(7, new PathfinderGoalRandomLookaround(this));
   }

   @Override
   public float getHeadHeight() {
      return this.length;
   }

   @Override
   protected void initAttributes() {
      super.initAttributes();
      this.getAttributeInstance(GenericAttributes.maxHealth).setValue(4.0);
      this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.25);
   }

   @Override
   public void m() {
      if (this.isChickenJockey()) {
         this.persistent = !this.isTypeNotPersistent();
      }

      super.m();
      this.bq = this.bm;
      this.bp = this.bo;
      this.bo = (float)(this.bo + (this.onGround ? -1 : 4) * 0.3);
      this.bo = MathHelper.a(this.bo, 0.0F, 1.0F);
      if (!this.onGround && this.br < 1.0F) {
         this.br = 1.0F;
      }

      this.br = (float)(this.br * 0.9);
      if (!this.onGround && this.motY < 0.0) {
         this.motY *= 0.6;
      }

      this.bm = this.bm + this.br * 2.0F;
      if (!this.isBaby() && !this.isChickenJockey() && --this.bs <= 0) {
         this.makeSound("mob.chicken.plop", 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
         this.a(Items.EGG, 1);
         this.bs = this.random.nextInt(6000) + 6000;
      }
   }

   @Override
   public void e(float f, float f1) {
   }

   @Override
   protected String z() {
      return "mob.chicken.say";
   }

   @Override
   protected String bo() {
      return "mob.chicken.hurt";
   }

   @Override
   protected String bp() {
      return "mob.chicken.hurt";
   }

   @Override
   protected void a(BlockPosition blockposition, Block block) {
      this.makeSound("mob.chicken.step", 0.15F, 1.0F);
   }

   @Override
   protected Item getLoot() {
      return Items.FEATHER;
   }

   @Override
   protected void dropDeathLoot(boolean flag, int i) {
      int j = this.random.nextInt(3) + this.random.nextInt(1 + i);

      for (int k = 0; k < j; k++) {
         this.a(Items.FEATHER, 1);
      }

      if (this.isBurning()) {
         this.a(Items.COOKED_CHICKEN, 1);
      } else {
         this.a(Items.CHICKEN, 1);
      }
   }

   public EntityChicken b(EntityAgeable entityageable) {
      return new EntityChicken(this.world);
   }

   @Override
   public boolean d(ItemStack itemstack) {
      return itemstack != null && itemstack.getItem() == Items.WHEAT_SEEDS;
   }

   @Override
   public void a(NBTTagCompound nbttagcompound) {
      super.a(nbttagcompound);
      this.bt = nbttagcompound.getBoolean("IsChickenJockey");
      if (nbttagcompound.hasKey("EggLayTime")) {
         this.bs = nbttagcompound.getInt("EggLayTime");
      }
   }

   @Override
   protected int getExpValue(EntityHuman entityhuman) {
      return this.isChickenJockey() ? 10 : super.getExpValue(entityhuman);
   }

   @Override
   public void b(NBTTagCompound nbttagcompound) {
      super.b(nbttagcompound);
      nbttagcompound.setBoolean("IsChickenJockey", this.bt);
      nbttagcompound.setInt("EggLayTime", this.bs);
   }

   @Override
   public boolean isTypeNotPersistent() {
      return this.isChickenJockey() && this.passenger == null;
   }

   @Override
   public void al() {
      super.al();
      this.passenger
         .setPosition(
            this.locX + 0.1 * MathHelper.sin(this.aI * (float) Math.PI / 180.0F),
            this.locY + this.length * 0.5F + this.passenger.am() + 0.0,
            this.locZ - 0.1 * MathHelper.cos(this.aI * (float) Math.PI / 180.0F)
         );
      if (this.passenger instanceof EntityLiving) {
         ((EntityLiving)this.passenger).aI = this.aI;
      }
   }

   public boolean isChickenJockey() {
      return this.bt;
   }

   public void l(boolean flag) {
      this.bt = flag;
   }

   @Override
   public EntityAgeable createChild(EntityAgeable entityageable) {
      return this.b(entityageable);
   }
}
