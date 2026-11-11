package org.bukkit.craftbukkit.v1_8_R3.inventory;

import net.minecraft.server.v1_8_R3.TileEntityBeacon;
import org.bukkit.inventory.BeaconInventory;
import org.bukkit.inventory.ItemStack;

public class CraftInventoryBeacon extends CraftInventory implements BeaconInventory {
   public CraftInventoryBeacon(TileEntityBeacon beacon) {
      super(beacon);
   }

   @Override
   public void setItem(ItemStack item) {
      this.setItem(0, item);
   }

   @Override
   public ItemStack getItem() {
      return this.getItem(0);
   }
}
