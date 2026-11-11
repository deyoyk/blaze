package org.bukkit.block;

import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class DoubleChest implements InventoryHolder {
   private final DoubleChestInventory inventory;

   public DoubleChest(DoubleChestInventory chest) {
      this.inventory = chest;
   }

   @Override
   public Inventory getInventory() {
      return this.inventory;
   }

   public InventoryHolder getLeftSide() {
      return this.inventory.getLeftSide().getHolder();
   }

   public InventoryHolder getRightSide() {
      return this.inventory.getRightSide().getHolder();
   }

   public org.bukkit.Location getLocation() {
      return new org.bukkit.Location(this.getWorld(), this.getX(), this.getY(), this.getZ());
   }

   public org.bukkit.World getWorld() {
      return ((Chest)this.getLeftSide()).getWorld();
   }

   public double getX() {
      return 0.5 * (((Chest)this.getLeftSide()).getX() + ((Chest)this.getRightSide()).getX());
   }

   public double getY() {
      return 0.5 * (((Chest)this.getLeftSide()).getY() + ((Chest)this.getRightSide()).getY());
   }

   public double getZ() {
      return 0.5 * (((Chest)this.getLeftSide()).getZ() + ((Chest)this.getRightSide()).getZ());
   }
}
