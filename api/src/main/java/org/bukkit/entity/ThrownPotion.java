package org.bukkit.entity;

import java.util.Collection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public interface ThrownPotion extends Projectile {
   Collection<PotionEffect> getEffects();

   ItemStack getItem();

   void setItem(ItemStack var1);
}
