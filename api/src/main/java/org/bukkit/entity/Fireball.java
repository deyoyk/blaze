package org.bukkit.entity;

import org.bukkit.util.Vector;

public interface Fireball extends Projectile, Explosive {
   void setDirection(Vector var1);

   Vector getDirection();
}
