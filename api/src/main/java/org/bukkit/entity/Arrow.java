package org.bukkit.entity;

public interface Arrow extends Projectile {
   int getKnockbackStrength();

   void setKnockbackStrength(int var1);

   boolean isCritical();

   void setCritical(boolean var1);

   Arrow.Spigot spigot();

   public static class Spigot extends Entity.Spigot {
      public double getDamage() {
         throw new UnsupportedOperationException("Not supported yet.");
      }

      public void setDamage(double damage) {
         throw new UnsupportedOperationException("Not supported yet.");
      }
   }
}
