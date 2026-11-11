package org.bukkit.inventory.meta;

import java.util.Map;
import org.bukkit.enchantments.Enchantment;

public interface EnchantmentStorageMeta extends ItemMeta {
   boolean hasStoredEnchants();

   boolean hasStoredEnchant(Enchantment var1);

   int getStoredEnchantLevel(Enchantment var1);

   Map<Enchantment, Integer> getStoredEnchants();

   boolean addStoredEnchant(Enchantment var1, int var2, boolean var3);

   boolean removeStoredEnchant(Enchantment var1) throws IllegalArgumentException;

   boolean hasConflictingStoredEnchant(Enchantment var1);

   EnchantmentStorageMeta clone();
}
