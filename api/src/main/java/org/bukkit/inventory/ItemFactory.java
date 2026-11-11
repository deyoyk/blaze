package org.bukkit.inventory;

import org.bukkit.inventory.meta.ItemMeta;

public interface ItemFactory {
   ItemMeta getItemMeta(org.bukkit.Material var1);

   boolean isApplicable(ItemMeta var1, ItemStack var2) throws IllegalArgumentException;

   boolean isApplicable(ItemMeta var1, org.bukkit.Material var2) throws IllegalArgumentException;

   boolean equals(ItemMeta var1, ItemMeta var2) throws IllegalArgumentException;

   ItemMeta asMetaFor(ItemMeta var1, ItemStack var2) throws IllegalArgumentException;

   ItemMeta asMetaFor(ItemMeta var1, org.bukkit.Material var2) throws IllegalArgumentException;

   org.bukkit.Color getDefaultLeatherColor();
}
