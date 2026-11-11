package org.bukkit.entity;

public interface Tameable {
   boolean isTamed();

   void setTamed(boolean var1);

   AnimalTamer getOwner();

   void setOwner(AnimalTamer var1);
}
