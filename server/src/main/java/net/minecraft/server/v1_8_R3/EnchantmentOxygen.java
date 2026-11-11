package net.minecraft.server.v1_8_R3;

import lol.deyo.blaze.configs.EnchantConfig;

public class EnchantmentOxygen extends Enchantment {
   public EnchantmentOxygen(int var1, MinecraftKey var2, int var3) {
      super(var1, var2, var3, EnchantmentSlotType.ARMOR_HEAD);
      this.c("oxygen");
   }

   @Override
   public int a(int var1) {
      return 10 * var1;
   }

   @Override
   public int b(int var1) {
      return this.a(var1) + 30;
   }

   @Override
   public int getMaxLevel() {
      return Math.max(3, this.getEnchantLimit());
   }

   @Override
   public int getEnchantLimit() {
      return (Integer)EnchantConfig.waterBreathing.getValue();
   }
}
