package org.bukkit.inventory;

import org.bukkit.block.Furnace;

public interface FurnaceInventory extends Inventory {
   ItemStack getResult();

   ItemStack getFuel();

   ItemStack getSmelting();

   void setFuel(ItemStack var1);

   void setResult(ItemStack var1);

   void setSmelting(ItemStack var1);

   Furnace getHolder();
}
