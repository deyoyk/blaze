package net.minecraft.server.v1_8_R3;

import lol.deyo.blaze.configs.EnchantConfig;

public class EnchantmentWaterWorker extends Enchantment {
   public EnchantmentWaterWorker(int var1, MinecraftKey var2, int var3) {
      super(var1, var2, var3, EnchantmentSlotType.ARMOR_HEAD);
      this.c("waterWorker");
   }

   @Override
   public int a(int var1) {
      return 1;
   }

   @Override
   public int b(int var1) {
      return this.a(var1) + 40;
   }

   @Override
   public int getEnchantLimit() {
      return (Integer)EnchantConfig.aquaAffinity.getValue();
   }
}
