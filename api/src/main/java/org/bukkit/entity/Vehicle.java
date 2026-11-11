package org.bukkit.entity;

import org.bukkit.util.Vector;

public interface Vehicle extends Entity {
   @Override
   Vector getVelocity();

   @Override
   void setVelocity(Vector var1);
}
