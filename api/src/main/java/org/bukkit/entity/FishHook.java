package org.bukkit.entity;

public interface FishHook extends Projectile {
   double getBiteChance();

   void setBiteChance(double var1) throws IllegalArgumentException;
}
