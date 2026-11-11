package org.bukkit.inventory.meta;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;

public interface ItemMeta extends Cloneable, ConfigurationSerializable {
   boolean hasDisplayName();

   String getDisplayName();

   void setDisplayName(String var1);

   boolean hasLore();

   List<String> getLore();

   void setLore(List<String> var1);

   boolean hasEnchants();

   boolean hasEnchant(Enchantment var1);

   int getEnchantLevel(Enchantment var1);

   Map<Enchantment, Integer> getEnchants();

   boolean addEnchant(Enchantment var1, int var2, boolean var3);

   boolean removeEnchant(Enchantment var1);

   boolean hasConflictingEnchant(Enchantment var1);

   void addItemFlags(ItemFlag... var1);

   void removeItemFlags(ItemFlag... var1);

   Set<ItemFlag> getItemFlags();

   boolean hasItemFlag(ItemFlag var1);

   ItemMeta clone();

   ItemMeta.Spigot spigot();

   public static class Spigot {
      public void setUnbreakable(boolean unbreakable) {
         throw new UnsupportedOperationException("Not supported yet.");
      }

      public boolean isUnbreakable() {
         throw new UnsupportedOperationException("Not supported yet.");
      }
   }
}
