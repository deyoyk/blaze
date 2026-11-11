package org.bukkit.entity;

public interface TNTPrimed extends Explosive {
   void setFuseTicks(int var1);

   int getFuseTicks();

   Entity getSource();

   org.bukkit.Location getSourceLoc();
}
