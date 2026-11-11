package org.bukkit.entity;

import org.bukkit.inventory.ItemStack;

public interface Item extends Entity {
   ItemStack getItemStack();

   void setItemStack(ItemStack var1);

   int getPickupDelay();

   void setPickupDelay(int var1);

   Entity getOwner();

   void setOwner(Entity var1);
}
