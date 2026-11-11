package org.bukkit.inventory;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;

public interface Inventory extends Iterable<ItemStack> {
   int getSize();

   int getMaxStackSize();

   void setMaxStackSize(int var1);

   String getName();

   ItemStack getItem(int var1);

   void setItem(int var1, ItemStack var2);

   HashMap<Integer, ItemStack> addItem(ItemStack... var1) throws IllegalArgumentException;

   HashMap<Integer, ItemStack> removeItem(ItemStack... var1) throws IllegalArgumentException;

   ItemStack[] getContents();

   void setContents(ItemStack[] var1) throws IllegalArgumentException;

   @Deprecated
   boolean contains(int var1);

   boolean contains(org.bukkit.Material var1) throws IllegalArgumentException;

   boolean contains(ItemStack var1);

   @Deprecated
   boolean contains(int var1, int var2);

   boolean contains(org.bukkit.Material var1, int var2) throws IllegalArgumentException;

   boolean contains(ItemStack var1, int var2);

   boolean containsAtLeast(ItemStack var1, int var2);

   @Deprecated
   HashMap<Integer, ? extends ItemStack> all(int var1);

   HashMap<Integer, ? extends ItemStack> all(org.bukkit.Material var1) throws IllegalArgumentException;

   HashMap<Integer, ? extends ItemStack> all(ItemStack var1);

   @Deprecated
   int first(int var1);

   int first(org.bukkit.Material var1) throws IllegalArgumentException;

   int first(ItemStack var1);

   int firstEmpty();

   @Deprecated
   void remove(int var1);

   void remove(org.bukkit.Material var1) throws IllegalArgumentException;

   void remove(ItemStack var1);

   void clear(int var1);

   void clear();

   List<HumanEntity> getViewers();

   String getTitle();

   InventoryType getType();

   InventoryHolder getHolder();

   ListIterator<ItemStack> iterator();

   ListIterator<ItemStack> iterator(int var1);
}
