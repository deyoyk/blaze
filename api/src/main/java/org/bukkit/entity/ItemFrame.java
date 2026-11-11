package org.bukkit.entity;

import org.bukkit.inventory.ItemStack;

public interface ItemFrame extends Hanging {
   ItemStack getItem();

   void setItem(ItemStack var1);

   org.bukkit.Rotation getRotation();

   void setRotation(org.bukkit.Rotation var1) throws IllegalArgumentException;
}
