package net.minecraft.server.v1_8_R3;

import lol.deyo.blaze.configs.EnchantConfig;

public class EnchantmentFire extends Enchantment {
   protected EnchantmentFire(int var1, MinecraftKey var2, int var3) {
      super(var1, var2, var3, EnchantmentSlotType.WEAPON);
      this.c("fire");
   }

   @Override
   public int a(int var1) {
      return 10 + 20 * (var1 - 1);
   }

   @Override
   public int b(int var1) {
      return super.a(var1) + 50;
   }

   @Override
   public int getMaxLevel() {
      return Math.max(2, this.getEnchantLimit());
   }

   @Override
   public int getEnchantLimit() {
      return (Integer)EnchantConfig.fireAspect.getValue();
   }
}
