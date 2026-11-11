package net.minecraft.server.v1_8_R3;

import lol.deyo.blaze.configs.EnchantConfig;
import java.util.Random;

public class EnchantmentDurability extends Enchantment {
   protected EnchantmentDurability(int var1, MinecraftKey var2, int var3) {
      super(var1, var2, var3, EnchantmentSlotType.BREAKABLE);
      this.c("durability");
   }

   @Override
   public int a(int var1) {
      return 5 + (var1 - 1) * 8;
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
      return (Integer)EnchantConfig.unbreaking.getValue();
   }

   @Override
   public boolean canEnchant(ItemStack var1) {
      return var1.e() || super.canEnchant(var1);
   }

   public static boolean a(ItemStack var0, int var1, Random var2) {
      return var0.getItem() instanceof ItemArmor && var2.nextFloat() < 0.6F ? false : var2.nextInt(var1 + 1) > 0;
   }
}
