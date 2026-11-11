package org.bukkit.entity;

public interface Wolf extends Animals, Tameable {
   boolean isAngry();

   void setAngry(boolean var1);

   boolean isSitting();

   void setSitting(boolean var1);

   org.bukkit.DyeColor getCollarColor();

   void setCollarColor(org.bukkit.DyeColor var1);
}
