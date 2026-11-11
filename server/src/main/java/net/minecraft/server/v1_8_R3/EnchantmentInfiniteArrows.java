package net.minecraft.server.v1_8_R3;

import lol.deyo.blaze.configs.EnchantConfig;

public class EnchantmentInfiniteArrows extends Enchantment {
   public EnchantmentInfiniteArrows(int var1, MinecraftKey var2, int var3) {
      super(var1, var2, var3, EnchantmentSlotType.BOW);
      this.c("arrowInfinite");
   }

   @Override
   public int a(int var1) {
      return 20;
   }

   @Override
   public int b(int var1) {
      return 50;
   }

   @Override
   public int getEnchantLimit() {
      return (Integer)EnchantConfig.infinity.getValue();
   }
}
