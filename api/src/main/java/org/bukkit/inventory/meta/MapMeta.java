package org.bukkit.inventory.meta;

public interface MapMeta extends ItemMeta {
   boolean isScaling();

   void setScaling(boolean var1);

   MapMeta clone();
}
