package net.minecraft.server.v1_8_R3;

import lol.deyo.blaze.configs.EnchantConfig;

public class EnchantmentLootBonus extends Enchantment {
   protected EnchantmentLootBonus(int var1, MinecraftKey var2, int var3, EnchantmentSlotType var4) {
      super(var1, var2, var3, var4);
      if (var4 == EnchantmentSlotType.DIGGER) {
         this.c("lootBonusDigger");
      } else if (var4 == EnchantmentSlotType.FISHING_ROD) {
         this.c("lootBonusFishing");
      } else {
         this.c("lootBonus");
      }
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
      String var1 = this.name;
      switch (this.name.hashCode()) {
         case 1120872499:
            if (var1.equals("lootBonusFishing")) {
               return (Integer)EnchantConfig.luckOfTheSea.getValue();
            }
            break;
         case 1360876119:
            if (var1.equals("lootBonus")) {
               return (Integer)EnchantConfig.looting.getValue();
            }
            break;
         case 2056750281:
            if (var1.equals("lootBonusDigger")) {
               return (Integer)EnchantConfig.fortune.getValue();
            }
      }

      return -1;
   }

   @Override
   public boolean a(Enchantment var1) {
      return super.a(var1) && var1.id != SILK_TOUCH.id;
   }
}
