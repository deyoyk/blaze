package org.bukkit.inventory.meta;

import java.util.List;

public interface FireworkMeta extends ItemMeta {
   void addEffect(org.bukkit.FireworkEffect var1) throws IllegalArgumentException;

   void addEffects(org.bukkit.FireworkEffect... var1) throws IllegalArgumentException;

   void addEffects(Iterable<org.bukkit.FireworkEffect> var1) throws IllegalArgumentException;

   List<org.bukkit.FireworkEffect> getEffects();

   int getEffectsSize();

   void removeEffect(int var1) throws IndexOutOfBoundsException;

   void clearEffects();

   boolean hasEffects();

   int getPower();

   void setPower(int var1) throws IllegalArgumentException;

   FireworkMeta clone();
}
