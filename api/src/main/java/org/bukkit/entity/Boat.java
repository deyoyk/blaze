package org.bukkit.entity;

public interface Boat extends Vehicle {
   double getMaxSpeed();

   void setMaxSpeed(double var1);

   double getOccupiedDeceleration();

   void setOccupiedDeceleration(double var1);

   double getUnoccupiedDeceleration();

   void setUnoccupiedDeceleration(double var1);

   boolean getWorkOnLand();

   void setWorkOnLand(boolean var1);
}
