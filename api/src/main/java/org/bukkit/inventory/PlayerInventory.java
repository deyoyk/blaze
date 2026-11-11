package org.bukkit.inventory;

import org.bukkit.entity.HumanEntity;

public interface PlayerInventory extends Inventory {
   ItemStack[] getArmorContents();

   ItemStack getHelmet();

   ItemStack getChestplate();

   ItemStack getLeggings();

   ItemStack getBoots();

   @Override
   void setItem(int var1, ItemStack var2);

   void setArmorContents(ItemStack[] var1);

   void setHelmet(ItemStack var1);

   void setChestplate(ItemStack var1);

   void setLeggings(ItemStack var1);

   void setBoots(ItemStack var1);

   ItemStack getItemInHand();

   void setItemInHand(ItemStack var1);

   int getHeldItemSlot();

   void setHeldItemSlot(int var1);

   @Deprecated
   int clear(int var1, int var2);

   HumanEntity getHolder();
}
