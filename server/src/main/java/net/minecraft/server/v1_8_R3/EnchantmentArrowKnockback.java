package net.minecraft.server.v1_8_R3;

import lol.deyo.blaze.configs.EnchantConfig;

public class EnchantmentArrowKnockback extends Enchantment {
   public EnchantmentArrowKnockback(int var1, MinecraftKey var2, int var3) {
      super(var1, var2, var3, EnchantmentSlotType.BOW);
      this.c("arrowKnockback");
   }

   @Override
   public int a(int var1) {
      return 12 + (var1 - 1) * 20;
   }

   @Override
   public int b(int var1) {
      return this.a(var1) + 25;
   }

   @Override
   public int getMaxLevel() {
      return Math.max(2, this.getEnchantLimit());
   }

   @Override
   public int getEnchantLimit() {
      return (Integer)EnchantConfig.punch.getValue();
   }
}
