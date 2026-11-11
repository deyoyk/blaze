package org.bukkit.inventory;

public interface HorseInventory extends Inventory {
   ItemStack getSaddle();

   ItemStack getArmor();

   void setSaddle(ItemStack var1);

   void setArmor(ItemStack var1);
}
