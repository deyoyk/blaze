package org.bukkit.enchantments;

import org.bukkit.inventory.ItemStack;

public class EnchantmentWrapper extends Enchantment {
   public EnchantmentWrapper(int id) {
      super(id);
   }

   public Enchantment getEnchantment() {
      return Enchantment.getById(this.getId());
   }

   @Override
   public int getMaxLevel() {
      return this.getEnchantment().getMaxLevel();
   }

   @Override
   public int getStartLevel() {
      return this.getEnchantment().getStartLevel();
   }

   @Override
   public EnchantmentTarget getItemTarget() {
      return this.getEnchantment().getItemTarget();
   }

   @Override
   public boolean canEnchantItem(ItemStack item) {
      return this.getEnchantment().canEnchantItem(item);
   }

   @Override
   public String getName() {
      return this.getEnchantment().getName();
   }

   @Override
   public boolean conflictsWith(Enchantment other) {
      return this.getEnchantment().conflictsWith(other);
   }
}
