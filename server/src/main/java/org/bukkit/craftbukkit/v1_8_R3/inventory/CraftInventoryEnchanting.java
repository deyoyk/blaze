package org.bukkit.craftbukkit.v1_8_R3.inventory;

import net.minecraft.server.v1_8_R3.InventorySubcontainer;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.ItemStack;

public class CraftInventoryEnchanting extends CraftInventory implements EnchantingInventory {
   public CraftInventoryEnchanting(InventorySubcontainer inventory) {
      super(inventory);
   }

   @Override
   public void setItem(ItemStack item) {
      this.setItem(0, item);
   }

   @Override
   public ItemStack getItem() {
      return this.getItem(0);
   }

   public InventorySubcontainer getInventory() {
      return (InventorySubcontainer)this.inventory;
   }

   @Override
   public void setSecondary(ItemStack item) {
      this.setItem(1, item);
   }

   @Override
   public ItemStack getSecondary() {
      return this.getItem(1);
   }
}
