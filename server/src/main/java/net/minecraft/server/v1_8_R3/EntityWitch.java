package net.minecraft.server.v1_8_R3;

import java.util.List;
import java.util.UUID;

public class EntityWitch extends EntityMonster implements IRangedEntity {
   private static final UUID a = UUID.fromString("5CD17E52-A79A-43D3-A529-90FDE04B181E");
   private static final AttributeModifier b = new AttributeModifier(a, "Drinking speed penalty", -0.25, 0).a(false);
   private static final Item[] c = new Item[]{
      Items.GLOWSTONE_DUST, Items.SUGAR, Items.REDSTONE, Items.SPIDER_EYE, Items.GLASS_BOTTLE, Items.GUNPOWDER, Items.STICK, Items.STICK
   };
   private int bm;

   public EntityWitch(World world) {
      super(world);
      this.setSize(0.6F, 1.95F);
      this.goalSelector.a(1, new PathfinderGoalFloat(this));
      this.goalSelector.a(2, new PathfinderGoalArrowAttack(this, 1.0, 60, 10.0F));
      this.goalSelector.a(2, new PathfinderGoalRandomStroll(this, 1.0));
      this.goalSelector.a(3, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
      this.goalSelector.a(3, new PathfinderGoalRandomLookaround(this));
      this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, false));
      this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, true));
   }

   @Override
   protected void h() {
      super.h();
      this.getDataWatcher().a(21, (byte)0);
   }

   @Override
   protected String z() {
      return null;
   }

   @Override
   protected String bo() {
      return null;
   }

   @Override
   protected String bp() {
      return null;
   }

   public void a(boolean flag) {
      this.getDataWatcher().watch(21, (byte)(flag ? 1 : 0));
   }

   public boolean n() {
      return this.getDataWatcher().getByte(21) == 1;
   }

   @Override
   protected void initAttributes() {
      super.initAttributes();
      this.getAttributeInstance(GenericAttributes.maxHealth).setValue(26.0);
      this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.25);
   }

   @Override
   public void m() {
      if (this.n()) {
         if (this.bm-- <= 0) {
            this.a(false);
            ItemStack itemstack = this.bA();
            this.setEquipment(0, null);
            if (itemstack != null && itemstack.getItem() == Items.POTION) {
               List<MobEffect> list = Items.POTION.h(itemstack);
               if (list != null) {
                  for (MobEffect mobeffect : list) {
                     this.addEffect(new MobEffect(mobeffect));
                  }
               }
            }

            this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).c(b);
         }
      } else {
         short short0 = -1;
         if (this.random.nextFloat() < 0.15F && this.a(Material.WATER) && !this.hasEffect(MobEffectList.WATER_BREATHING)) {
            short0 = 8237;
         } else if (this.random.nextFloat() < 0.15F && this.isBurning() && !this.hasEffect(MobEffectList.FIRE_RESISTANCE)) {
            short0 = 16307;
         } else if (this.random.nextFloat() < 0.05F && this.getHealth() < this.getMaxHealth()) {
            short0 = 16341;
         } else if (this.random.nextFloat() < 0.25F
            && this.getGoalTarget() != null
            && !this.hasEffect(MobEffectList.FASTER_MOVEMENT)
            && this.getGoalTarget().h(this) > 121.0) {
            short0 = 16274;
         } else if (this.random.nextFloat() < 0.25F
            && this.getGoalTarget() != null
            && !this.hasEffect(MobEffectList.FASTER_MOVEMENT)
            && this.getGoalTarget().h(this) > 121.0) {
            short0 = 16274;
         }

         if (short0 > -1) {
            this.setEquipment(0, new ItemStack(Items.POTION, 1, short0));
            this.bm = this.bA().l();
            this.a(true);
            AttributeInstance attributeinstance = this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED);
            attributeinstance.c(b);
            attributeinstance.b(b);
         }
      }

      if (this.random.nextFloat() < 7.5E-4F) {
         this.world.broadcastEntityEffect(this, (byte)15);
      }

      super.m();
   }

   @Override
   protected float applyMagicModifier(DamageSource damagesource, float f) {
      f = super.applyMagicModifier(damagesource, f);
      if (damagesource.getEntity() == this) {
         f = 0.0F;
      }

      if (damagesource.isMagic()) {
         f = (float)(f * 0.15);
      }

      return f;
   }

   @Override
   protected void dropDeathLoot(boolean flag, int i) {
      int j = this.random.nextInt(3) + 1;

      for (int k = 0; k < j; k++) {
         int l = this.random.nextInt(3);
         Item item = c[this.random.nextInt(c.length)];
         if (i > 0) {
            l += this.random.nextInt(i + 1);
         }

         for (int i1 = 0; i1 < l; i1++) {
            this.a(item, 1);
         }
      }
   }

   @Override
   public void a(EntityLiving entityliving, float f) {
      if (!this.n()) {
         EntityPotion entitypotion = new EntityPotion(this.world, this, 32732);
         double d0 = entityliving.locY + entityliving.getHeadHeight() - 1.1F;
         entitypotion.pitch -= -20.0F;
         double d1 = entityliving.locX + entityliving.motX - this.locX;
         double d2 = d0 - this.locY;
         double d3 = entityliving.locZ + entityliving.motZ - this.locZ;
         float f1 = MathHelper.sqrt(d1 * d1 + d3 * d3);
         if (f1 >= 8.0F && !entityliving.hasEffect(MobEffectList.SLOWER_MOVEMENT)) {
            entitypotion.setPotionValue(32698);
         } else if (entityliving.getHealth() >= 8.0F && !entityliving.hasEffect(MobEffectList.POISON)) {
            entitypotion.setPotionValue(32660);
         } else if (f1 <= 3.0F && !entityliving.hasEffect(MobEffectList.WEAKNESS) && this.random.nextFloat() < 0.25F) {
            entitypotion.setPotionValue(32696);
         }

         entitypotion.shoot(d1, d2 + f1 * 0.2F, d3, 0.75F, 8.0F);
         this.world.addEntity(entitypotion);
      }
   }

   @Override
   public float getHeadHeight() {
      return 1.62F;
   }
}
