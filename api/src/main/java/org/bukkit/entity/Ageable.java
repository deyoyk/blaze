package org.bukkit.entity;

public interface Ageable extends Creature {
   int getAge();

   void setAge(int var1);

   void setAgeLock(boolean var1);

   boolean getAgeLock();

   void setBaby();

   void setAdult();

   boolean isAdult();

   boolean canBreed();

   void setBreed(boolean var1);
}
