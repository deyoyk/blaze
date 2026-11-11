package org.bukkit;

public interface WorldBorder {
   void reset();

   double getSize();

   void setSize(double var1);

   void setSize(double var1, long var3);

   org.bukkit.Location getCenter();

   void setCenter(double var1, double var3);

   void setCenter(org.bukkit.Location var1);

   double getDamageBuffer();

   void setDamageBuffer(double var1);

   double getDamageAmount();

   void setDamageAmount(double var1);

   int getWarningTime();

   void setWarningTime(int var1);

   int getWarningDistance();

   void setWarningDistance(int var1);
}
