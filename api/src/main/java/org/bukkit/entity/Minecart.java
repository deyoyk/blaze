package org.bukkit.entity;

import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

public interface Minecart extends Vehicle {
   @Deprecated
   void _INVALID_setDamage(int var1);

   void setDamage(double var1);

   @Deprecated
   int _INVALID_getDamage();

   double getDamage();

   double getMaxSpeed();

   void setMaxSpeed(double var1);

   boolean isSlowWhenEmpty();

   void setSlowWhenEmpty(boolean var1);

   Vector getFlyingVelocityMod();

   void setFlyingVelocityMod(Vector var1);

   Vector getDerailedVelocityMod();

   void setDerailedVelocityMod(Vector var1);

   void setDisplayBlock(MaterialData var1);

   MaterialData getDisplayBlock();

   void setDisplayBlockOffset(int var1);

   int getDisplayBlockOffset();
}
