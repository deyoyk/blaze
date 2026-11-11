package org.bukkit.inventory.meta;

public interface FireworkEffectMeta extends ItemMeta {
   void setEffect(org.bukkit.FireworkEffect var1);

   boolean hasEffect();

   org.bukkit.FireworkEffect getEffect();

   FireworkEffectMeta clone();
}
