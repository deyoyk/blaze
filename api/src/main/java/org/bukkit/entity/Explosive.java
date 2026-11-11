package org.bukkit.entity;

public interface Explosive extends Entity {
   void setYield(float var1);

   float getYield();

   void setIsIncendiary(boolean var1);

   boolean isIncendiary();
}
