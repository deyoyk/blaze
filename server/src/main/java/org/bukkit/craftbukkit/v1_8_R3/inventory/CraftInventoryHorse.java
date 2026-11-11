package org.bukkit.craftbukkit.v1_8_R3.inventory;

import net.minecraft.server.v1_8_R3.IInventory;
import org.bukkit.inventory.HorseInventory;
import org.bukkit.inventory.ItemStack;

public class CraftInventoryHorse extends CraftInventory implements HorseInventory {
   public CraftInventoryHorse(IInventory inventory) {
      super(inventory);
   }

   @Override
   public ItemStack getSaddle() {
      return this.getItem(0);
   }

   @Override
   public ItemStack getArmor() {
      return this.getItem(1);
   }

   @Override
   public void setSaddle(ItemStack stack) {
      this.setItem(0, stack);
   }

   @Override
   public void setArmor(ItemStack stack) {
      this.setItem(1, stack);
   }
}
