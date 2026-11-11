package org.bukkit.inventory;

import org.bukkit.entity.Entity;

public interface EntityEquipment {
   ItemStack getItemInHand();

   void setItemInHand(ItemStack var1);

   ItemStack getHelmet();

   void setHelmet(ItemStack var1);

   ItemStack getChestplate();

   void setChestplate(ItemStack var1);

   ItemStack getLeggings();

   void setLeggings(ItemStack var1);

   ItemStack getBoots();

   void setBoots(ItemStack var1);

   ItemStack[] getArmorContents();

   void setArmorContents(ItemStack[] var1);

   void clear();

   float getItemInHandDropChance();

   void setItemInHandDropChance(float var1);

   float getHelmetDropChance();

   void setHelmetDropChance(float var1);

   float getChestplateDropChance();

   void setChestplateDropChance(float var1);

   float getLeggingsDropChance();

   void setLeggingsDropChance(float var1);

   float getBootsDropChance();

   void setBootsDropChance(float var1);

   Entity getHolder();
}
