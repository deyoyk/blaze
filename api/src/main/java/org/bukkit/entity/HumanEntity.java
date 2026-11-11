package org.bukkit.entity;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.permissions.Permissible;

public interface HumanEntity extends LivingEntity, AnimalTamer, Permissible, InventoryHolder {
   @Override
   String getName();

   PlayerInventory getInventory();

   Inventory getEnderChest();

   boolean setWindowProperty(InventoryView.Property var1, int var2);

   InventoryView getOpenInventory();

   InventoryView openInventory(Inventory var1);

   InventoryView openWorkbench(org.bukkit.Location var1, boolean var2);

   InventoryView openEnchanting(org.bukkit.Location var1, boolean var2);

   void openInventory(InventoryView var1);

   void closeInventory();

   ItemStack getItemInHand();

   void setItemInHand(ItemStack var1);

   ItemStack getItemOnCursor();

   void setItemOnCursor(ItemStack var1);

   boolean isSleeping();

   int getSleepTicks();

   org.bukkit.GameMode getGameMode();

   void setGameMode(org.bukkit.GameMode var1);

   boolean isBlocking();

   int getExpToLevel();
}
