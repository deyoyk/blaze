package net.minecraft.server.v1_8_R3;

import lol.deyo.blaze.configs.EnchantConfig;

public class EnchantmentLure extends Enchantment {
   protected EnchantmentLure(int var1, MinecraftKey var2, int var3, EnchantmentSlotType var4) {
      super(var1, var2, var3, var4);
      this.c("fishingSpeed");
   }

   @Override
   public int a(int var1) {
      return 15 + (var1 - 1) * 9;
   }

   @Override
   public int b(int var1) {
      return super.a(var1) + 50;
   }

   @Override
   public int getMaxLevel() {
      return Math.max(3, this.getEnchantLimit());
   }

   @Override
   public int getEnchantLimit() {
      return (Integer)EnchantConfig.lure.getValue();
   }
}
