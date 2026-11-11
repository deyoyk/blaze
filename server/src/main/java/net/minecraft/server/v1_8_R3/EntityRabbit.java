package net.minecraft.server.v1_8_R3;

public class EntityRabbit extends EntityAnimal {
   private EntityRabbit.PathfinderGoalRabbitAvoidTarget<EntityWolf> bm;
   private int bo = 0;
   private int bp = 0;
   private boolean bq = false;
   private boolean br = false;
   private int bs = 0;
   private EntityRabbit.EnumRabbitState bt = EntityRabbit.EnumRabbitState.HOP;
   private int bu = 0;

   public EntityRabbit(World world) {
      super(world);
      this.setSize(0.6F, 0.7F);
      this.g = new EntityRabbit.ControllerJumpRabbit(this);
      this.moveController = new EntityRabbit.ControllerMoveRabbit(this);
      ((Navigation)this.getNavigation()).a(true);
      this.initializePathFinderGoals();
      this.b(0.0);
   }

   public void initializePathFinderGoals() {
      this.navigation.a(2.5F);
      this.goalSelector.a(1, new PathfinderGoalFloat(this));
      this.goalSelector.a(1, new EntityRabbit.PathfinderGoalRabbitPanic(this, 1.33));
      this.goalSelector.a(2, new PathfinderGoalTempt(this, 1.0, Items.CARROT, false));
      this.goalSelector.a(2, new PathfinderGoalTempt(this, 1.0, Items.GOLDEN_CARROT, false));
      this.goalSelector.a(2, new PathfinderGoalTempt(this, 1.0, Item.getItemOf(Blocks.YELLOW_FLOWER), false));
      this.goalSelector.a(3, new PathfinderGoalBreed(this, 0.8));
      this.goalSelector.a(5, new EntityRabbit.PathfinderGoalEatCarrots(this));
      this.goalSelector.a(5, new PathfinderGoalRandomStroll(this, 0.6));
      this.goalSelector.a(11, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 10.0F));
      this.bm = new EntityRabbit.PathfinderGoalRabbitAvoidTarget<>(this, EntityWolf.class, 16.0F, 1.33, 1.33);
      this.goalSelector.a(4, this.bm);
   }

   @Override
   protected float bE() {
      return this.moveController.a() && this.moveController.e() > this.locY + 0.5 ? 0.5F : this.bt.b();
   }

   public void a(EntityRabbit.EnumRabbitState entityrabbit_enumrabbitstate) {
      this.bt = entityrabbit_enumrabbitstate;
   }

   public void b(double d0) {
      this.getNavigation().a(d0);
      this.moveController.a(this.moveController.d(), this.moveController.e(), this.moveController.f(), d0);
   }

   public void a(boolean flag, EntityRabbit.EnumRabbitState entityrabbit_enumrabbitstate) {
      super.i(flag);
      if (!flag) {
         if (this.bt == EntityRabbit.EnumRabbitState.ATTACK) {
            this.bt = EntityRabbit.EnumRabbitState.HOP;
         }
      } else {
         this.b(1.5 * entityrabbit_enumrabbitstate.a());
         this.makeSound(this.cm(), this.bB(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * 0.8F);
      }

      this.bq = flag;
   }

   public void b(EntityRabbit.EnumRabbitState entityrabbit_enumrabbitstate) {
      this.a(true, entityrabbit_enumrabbitstate);
      this.bp = entityrabbit_enumrabbitstate.d();
      this.bo = 0;
   }

   public boolean cl() {
      return this.bq;
   }

   @Override
   protected void h() {
      super.h();
      this.datawatcher.a(18, (byte)0);
   }

   @Override
   public void E() {
      if (this.moveController.b() > 0.8) {
         this.a(EntityRabbit.EnumRabbitState.SPRINT);
      } else if (this.bt != EntityRabbit.EnumRabbitState.ATTACK) {
         this.a(EntityRabbit.EnumRabbitState.HOP);
      }

      if (this.bs > 0) {
         this.bs--;
      }

      if (this.bu > 0) {
         this.bu = this.bu - this.random.nextInt(3);
         if (this.bu < 0) {
            this.bu = 0;
         }
      }

      if (this.onGround) {
         if (!this.br) {
            this.a(false, EntityRabbit.EnumRabbitState.NONE);
            this.cw();
         }

         if (this.getRabbitType() == 99 && this.bs == 0) {
            EntityLiving entityliving = this.getGoalTarget();
            if (entityliving != null && this.h(entityliving) < 16.0) {
               this.a(entityliving.locX, entityliving.locZ);
               this.moveController.a(entityliving.locX, entityliving.locY, entityliving.locZ, this.moveController.b());
               this.b(EntityRabbit.EnumRabbitState.ATTACK);
               this.br = true;
            }
         }

         EntityRabbit.ControllerJumpRabbit entityrabbit_controllerjumprabbit = (EntityRabbit.ControllerJumpRabbit)this.g;
         if (!entityrabbit_controllerjumprabbit.c()) {
            if (this.moveController.a() && this.bs == 0) {
               PathEntity pathentity = this.navigation.j();
               Vec3D vec3d = new Vec3D(this.moveController.d(), this.moveController.e(), this.moveController.f());
               if (pathentity != null && pathentity.e() < pathentity.d()) {
                  vec3d = pathentity.a(this);
               }

               this.a(vec3d.a, vec3d.c);
               this.b(this.bt);
            }
         } else if (!entityrabbit_controllerjumprabbit.d()) {
            this.ct();
         }
      }

      this.br = this.onGround;
   }

   @Override
   public void Y() {
   }

   private void a(double d0, double d1) {
      this.yaw = (float)(MathHelper.b(d1 - this.locZ, d0 - this.locX) * 180.0 / (float) Math.PI) - 90.0F;
   }

   private void ct() {
      ((EntityRabbit.ControllerJumpRabbit)this.g).a(true);
   }

   private void cu() {
      ((EntityRabbit.ControllerJumpRabbit)this.g).a(false);
   }

   private void cv() {
      this.bs = this.co();
   }

   private void cw() {
      this.cv();
      this.cu();
   }

   @Override
   public void m() {
      super.m();
      if (this.bo != this.bp) {
         if (this.bo == 0) {
            this.world.broadcastEntityEffect(this, (byte)1);
         }

         this.bo++;
      } else if (this.bp != 0) {
         this.bo = 0;
         this.bp = 0;
      }
   }

   @Override
   protected void initAttributes() {
      super.initAttributes();
      this.getAttributeInstance(GenericAttributes.maxHealth).setValue(10.0);
      this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.3F);
   }

   @Override
   public void b(NBTTagCompound nbttagcompound) {
      super.b(nbttagcompound);
      nbttagcompound.setInt("RabbitType", this.getRabbitType());
      nbttagcompound.setInt("MoreCarrotTicks", this.bu);
   }

   @Override
   public void a(NBTTagCompound nbttagcompound) {
      super.a(nbttagcompound);
      this.setRabbitType(nbttagcompound.getInt("RabbitType"));
      this.bu = nbttagcompound.getInt("MoreCarrotTicks");
   }

   protected String cm() {
      return "mob.rabbit.hop";
   }

   @Override
   protected String z() {
      return "mob.rabbit.idle";
   }

   @Override
   protected String bo() {
      return "mob.rabbit.hurt";
   }

   @Override
   protected String bp() {
      return "mob.rabbit.death";
   }

   @Override
   public boolean r(Entity entity) {
      if (this.getRabbitType() == 99) {
         this.makeSound("mob.attack", 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
         return entity.damageEntity(DamageSource.mobAttack(this), 8.0F);
      } else {
         return entity.damageEntity(DamageSource.mobAttack(this), 3.0F);
      }
   }

   @Override
   public int br() {
      return this.getRabbitType() == 99 ? 8 : super.br();
   }

   @Override
   public boolean damageEntity(DamageSource damagesource, float f) {
      return !this.isInvulnerable(damagesource) && super.damageEntity(damagesource, f);
   }

   @Override
   protected void getRareDrop() {
      this.a(new ItemStack(Items.RABBIT_FOOT, 1), 0.0F);
   }

   @Override
   protected void dropDeathLoot(boolean flag, int i) {
      int j = this.random.nextInt(2) + this.random.nextInt(1 + i);

      for (int k = 0; k < j; k++) {
         this.a(Items.RABBIT_HIDE, 1);
      }

      j = this.random.nextInt(2);

      for (int var6 = 0; var6 < j; var6++) {
         if (this.isBurning()) {
            this.a(Items.COOKED_RABBIT, 1);
         } else {
            this.a(Items.RABBIT, 1);
         }
      }
   }

   private boolean a(Item item) {
      return item == Items.CARROT || item == Items.GOLDEN_CARROT || item == Item.getItemOf(Blocks.YELLOW_FLOWER);
   }

   public EntityRabbit b(EntityAgeable entityageable) {
      EntityRabbit entityrabbit = new EntityRabbit(this.world);
      if (entityageable instanceof EntityRabbit) {
         entityrabbit.setRabbitType(this.random.nextBoolean() ? this.getRabbitType() : ((EntityRabbit)entityageable).getRabbitType());
      }

      return entityrabbit;
   }

   @Override
   public boolean d(ItemStack itemstack) {
      return itemstack != null && this.a(itemstack.getItem());
   }

   public int getRabbitType() {
      return this.datawatcher.getByte(18);
   }

   public void setRabbitType(int i) {
      if (i == 99) {
         this.goalSelector.a(this.bm);
         this.goalSelector.a(4, new EntityRabbit.PathfinderGoalKillerRabbitMeleeAttack(this));
         this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, false));
         this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, true));
         this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityWolf.class, true));
         if (!this.hasCustomName()) {
            this.setCustomName(LocaleI18n.get("entity.KillerBunny.name"));
         }
      }

      this.datawatcher.watch(18, (byte)i);
   }

   @Override
   public GroupDataEntity prepare(DifficultyDamageScaler difficultydamagescaler, GroupDataEntity groupdataentity) {
      GroupDataEntity object = super.prepare(difficultydamagescaler, groupdataentity);
      int i = this.random.nextInt(6);
      boolean flag = false;
      if (object instanceof EntityRabbit.GroupDataRabbit) {
         i = ((EntityRabbit.GroupDataRabbit)object).a;
         flag = true;
      } else {
         object = new EntityRabbit.GroupDataRabbit(i);
      }

      this.setRabbitType(i);
      if (flag) {
         this.setAgeRaw(-24000);
      }

      return object;
   }

   private boolean cx() {
      return this.bu == 0;
   }

   protected int co() {
      return this.bt.c();
   }

   protected void cp() {
      this.world
         .addParticle(
            EnumParticle.BLOCK_DUST,
            this.locX + this.random.nextFloat() * this.width * 2.0F - this.width,
            this.locY + 0.5 + this.random.nextFloat() * this.length,
            this.locZ + this.random.nextFloat() * this.width * 2.0F - this.width,
            0.0,
            0.0,
            0.0,
            Block.getCombinedId(Blocks.CARROTS.fromLegacyData(7))
         );
      this.bu = 100;
   }

   @Override
   public EntityAgeable createChild(EntityAgeable entityageable) {
      return this.b(entityageable);
   }

   public static class ControllerJumpRabbit extends ControllerJump {
      private final EntityRabbit c;
      private boolean d = false;

      public ControllerJumpRabbit(EntityRabbit entityrabbit) {
         super(entityrabbit);
         this.c = entityrabbit;
      }

      public boolean c() {
         return this.a;
      }

      public boolean d() {
         return this.d;
      }

      public void a(boolean flag) {
         this.d = flag;
      }

      @Override
      public void b() {
         if (this.a) {
            this.c.b(EntityRabbit.EnumRabbitState.STEP);
            this.a = false;
         }
      }
   }

   static class ControllerMoveRabbit extends ControllerMove {
      private final EntityRabbit g;

      public ControllerMoveRabbit(EntityRabbit entityrabbit) {
         super(entityrabbit);
         this.g = entityrabbit;
      }

      @Override
      public void c() {
         if (this.g.onGround && !this.g.cl()) {
            this.g.b(0.0);
         }

         super.c();
      }
   }

   static enum EnumRabbitState {
      NONE(0.0F, 0.0F, 30, 1),
      HOP(0.8F, 0.2F, 20, 10),
      STEP(1.0F, 0.45F, 14, 14),
      SPRINT(1.75F, 0.4F, 1, 8),
      ATTACK(2.0F, 0.7F, 7, 8);

      private final float f;
      private final float g;
      private final int h;
      private final int i;

      private EnumRabbitState(float f, float f1, int i, int j) {
         this.f = f;
         this.g = f1;
         this.h = i;
         this.i = j;
      }

      public float a() {
         return this.f;
      }

      public float b() {
         return this.g;
      }

      public int c() {
         return this.h;
      }

      public int d() {
         return this.i;
      }
   }

   public static class GroupDataRabbit implements GroupDataEntity {
      public int a;

      public GroupDataRabbit(int i) {
         this.a = i;
      }
   }

   static class PathfinderGoalEatCarrots extends PathfinderGoalGotoTarget {
      private final EntityRabbit c;
      private boolean d;
      private boolean e = false;

      public PathfinderGoalEatCarrots(EntityRabbit entityrabbit) {
         super(entityrabbit, 0.7F, 16);
         this.c = entityrabbit;
      }

      @Override
      public boolean a() {
         if (this.a <= 0) {
            if (!this.c.world.getGameRules().getBoolean("mobGriefing")) {
               return false;
            }

            this.e = false;
            this.d = this.c.cx();
         }

         return super.a();
      }

      @Override
      public boolean b() {
         return this.e && super.b();
      }

      @Override
      public void c() {
         super.c();
      }

      @Override
      public void d() {
         super.d();
      }

      @Override
      public void e() {
         super.e();
         this.c.getControllerLook().a(this.b.getX() + 0.5, this.b.getY() + 1, this.b.getZ() + 0.5, 10.0F, this.c.bQ());
         if (this.f()) {
            World world = this.c.world;
            BlockPosition blockposition = this.b.up();
            IBlockData iblockdata = world.getType(blockposition);
            if (this.e && iblockdata.getBlock() instanceof BlockCarrots && iblockdata.get(BlockCarrots.AGE) == 7) {
               world.setTypeAndData(blockposition, Blocks.AIR.getBlockData(), 2);
               world.setAir(blockposition, true);
               this.c.cp();
            }

            this.e = false;
            this.a = 10;
         }
      }

      @Override
      protected boolean a(World world, BlockPosition blockposition) {
         Block block = world.getType(blockposition).getBlock();
         if (block == Blocks.FARMLAND) {
            blockposition = blockposition.up();
            IBlockData iblockdata = world.getType(blockposition);
            block = iblockdata.getBlock();
            if (block instanceof BlockCarrots && iblockdata.get(BlockCarrots.AGE) == 7 && this.d && !this.e) {
               this.e = true;
               return true;
            }
         }

         return false;
      }
   }

   static class PathfinderGoalKillerRabbitMeleeAttack extends PathfinderGoalMeleeAttack {
      public PathfinderGoalKillerRabbitMeleeAttack(EntityRabbit entityrabbit) {
         super(entityrabbit, EntityLiving.class, 1.4, true);
      }

      @Override
      protected double a(EntityLiving entityliving) {
         return 4.0F + entityliving.width;
      }
   }

   static class PathfinderGoalRabbitAvoidTarget<T extends Entity> extends PathfinderGoalAvoidTarget<T> {
      public PathfinderGoalRabbitAvoidTarget(EntityRabbit entityrabbit, Class<T> oclass, float f, double d0, double d1) {
         super(entityrabbit, oclass, f, d0, d1);
      }
   }

   static class PathfinderGoalRabbitPanic extends PathfinderGoalPanic {
      private final EntityRabbit b;

      public PathfinderGoalRabbitPanic(EntityRabbit entityrabbit, double d0) {
         super(entityrabbit, d0);
         this.b = entityrabbit;
      }

      @Override
      public void e() {
         super.e();
         this.b.b(this.a);
      }
   }
}
