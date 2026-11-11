package net.minecraft.server.v1_8_R3;

import lol.deyo.blaze.configs.EnchantConfig;

public class EnchantmentProtection extends Enchantment {
   private static final String[] E = new String[]{"all", "fire", "fall", "explosion", "projectile"};
   private static final int[] F = new int[]{1, 10, 5, 5, 3};
   private static final int[] G = new int[]{11, 8, 6, 8, 6};
   private static final int[] H = new int[]{20, 12, 10, 12, 15};
   public final int a;

   public EnchantmentProtection(int paramInt1, MinecraftKey paramMinecraftKey, int paramInt2, int paramInt3) {
      super(paramInt1, paramMinecraftKey, paramInt2, EnchantmentSlotType.ARMOR);
      this.a = paramInt3;
      if (paramInt3 == 2) {
         this.slot = EnchantmentSlotType.ARMOR_FEET;
      }
   }

   @Override
   public int a(int paramInt) {
      return F[this.a] + (paramInt - 1) * G[this.a];
   }

   @Override
   public int b(int paramInt) {
      return this.a(paramInt) + H[this.a];
   }

   @Override
   public int getMaxLevel() {
      return Math.max(4, this.getEnchantLimit());
   }

   @Override
   public int getEnchantLimit() {
      switch (this.a) {
         case 0:
            return (Integer)EnchantConfig.protectionAll.getValue();
         case 1:
            return (Integer)EnchantConfig.protectionFire.getValue();
         case 2:
            return (Integer)EnchantConfig.protectionFall.getValue();
         case 3:
            return (Integer)EnchantConfig.protectionExplosion.getValue();
         case 4:
            return (Integer)EnchantConfig.protectionProjectile.getValue();
         default:
            return -1;
      }
   }

   @Override
   public int a(int paramInt, DamageSource paramDamageSource) {
      if (paramDamageSource.ignoresInvulnerability()) {
         return 0;
      } else {
         float f = (6 + paramInt * paramInt) / 3.0F;
         if (this.a == 0) {
            return MathHelper.d(f * 0.75F);
         } else if (this.a == 1 && paramDamageSource.o()) {
            return MathHelper.d(f * 1.25F);
         } else if (this.a == 2 && paramDamageSource == DamageSource.FALL) {
            return MathHelper.d(f * 2.5F);
         } else if (this.a == 3 && paramDamageSource.isExplosion()) {
            return MathHelper.d(f * 1.5F);
         } else {
            return this.a == 4 && paramDamageSource.a() ? MathHelper.d(f * 1.5F) : 0;
         }
      }
   }

   @Override
   public String a() {
      return "enchantment.protect." + E[this.a];
   }

   @Override
   public boolean a(Enchantment paramEnchantment) {
      if (paramEnchantment instanceof EnchantmentProtection) {
         EnchantmentProtection enchantmentProtection = (EnchantmentProtection)paramEnchantment;
         return enchantmentProtection.a == this.a ? false : this.a == 2 || enchantmentProtection.a == 2;
      } else {
         return super.a(paramEnchantment);
      }
   }

   public static int a(Entity paramEntity, int paramInt) {
      int i = EnchantmentManager.a(Enchantment.PROTECTION_FIRE.id, paramEntity.getEquipment());
      if (i > 0) {
         paramInt -= MathHelper.d(paramInt * i * 0.15F);
      }

      return paramInt;
   }

   public static double a(Entity paramEntity, double paramDouble) {
      int i = EnchantmentManager.a(Enchantment.PROTECTION_EXPLOSIONS.id, paramEntity.getEquipment());
      if (i > 0) {
         paramDouble -= MathHelper.floor(paramDouble * (i * 0.15F));
      }

      return paramDouble;
   }
}
