package net.minecraft.server.v1_8_R3;

import lol.deyo.blaze.configs.EnchantConfig;

public class EnchantmentSilkTouch extends Enchantment {
   protected EnchantmentSilkTouch(int var1, MinecraftKey var2, int var3) {
      super(var1, var2, var3, EnchantmentSlotType.DIGGER);
      this.c("untouching");
   }

   @Override
   public int a(int var1) {
      return 15;
   }

   @Override
   public int b(int var1) {
      return super.a(var1) + 50;
   }

   @Override
   public boolean a(Enchantment var1) {
      return super.a(var1) && var1.id != LOOT_BONUS_BLOCKS.id;
   }

   @Override
   public boolean canEnchant(ItemStack var1) {
      return var1.getItem() == Items.SHEARS || super.canEnchant(var1);
   }

   @Override
   public int getEnchantLimit() {
      return (Integer)EnchantConfig.silkTouch.getValue();
   }
}
