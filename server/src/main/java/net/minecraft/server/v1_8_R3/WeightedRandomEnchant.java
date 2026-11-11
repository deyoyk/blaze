package net.minecraft.server.v1_8_R3;

public class WeightedRandomEnchant extends WeightedRandom.WeightedRandomChoice {
   public final Enchantment enchantment;
   public final int level;

   public WeightedRandomEnchant(Enchantment var1, int var2) {
      super(var1.getRandomWeight());
      this.enchantment = var1;
      this.level = var2;
   }
}
