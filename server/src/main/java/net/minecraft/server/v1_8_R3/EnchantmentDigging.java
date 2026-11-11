package net.minecraft.server.v1_8_R3;

import lol.deyo.blaze.configs.EnchantConfig;

public class EnchantmentDigging extends Enchantment {
   protected EnchantmentDigging(int var1, MinecraftKey var2, int var3) {
      super(var1, var2, var3, EnchantmentSlotType.DIGGER);
      this.c("digging");
   }

   @Override
   public int a(int var1) {
      return 1 + 10 * (var1 - 1);
   }

   @Override
   public int b(int var1) {
      return super.a(var1) + 50;
   }

   @Override
   public int getMaxLevel() {
      return Math.max(5, this.getEnchantLimit());
   }

   @Override
   public int getEnchantLimit() {
      return (Integer)EnchantConfig.efficiency.getValue();
   }

   @Override
   public boolean canEnchant(ItemStack var1) {
      return var1.getItem() == Items.SHEARS || super.canEnchant(var1);
   }
}
