package org.bukkit.inventory;

public interface EnchantingInventory extends Inventory {
   void setItem(ItemStack var1);

   ItemStack getItem();

   void setSecondary(ItemStack var1);

   ItemStack getSecondary();
}
