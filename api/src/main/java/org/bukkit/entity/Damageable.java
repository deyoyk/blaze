package org.bukkit.entity;

public interface Damageable extends Entity {
   void damage(double var1);

   @Deprecated
   void _INVALID_damage(int var1);

   void damage(double var1, Entity var3);

   @Deprecated
   void _INVALID_damage(int var1, Entity var2);

   double getHealth();

   @Deprecated
   int _INVALID_getHealth();

   void setHealth(double var1);

   @Deprecated
   void _INVALID_setHealth(int var1);

   double getMaxHealth();

   @Deprecated
   int _INVALID_getMaxHealth();

   void setMaxHealth(double var1);

   @Deprecated
   void _INVALID_setMaxHealth(int var1);

   void resetMaxHealth();
}
