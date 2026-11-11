package org.bukkit.inventory.meta;

public interface LeatherArmorMeta extends ItemMeta {
   org.bukkit.Color getColor();

   void setColor(org.bukkit.Color var1);

   LeatherArmorMeta clone();
}
