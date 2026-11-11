package org.bukkit.entity;

import org.bukkit.inventory.HorseInventory;
import org.bukkit.inventory.InventoryHolder;

public interface Horse extends Animals, Vehicle, InventoryHolder, Tameable {
   Horse.Variant getVariant();

   void setVariant(Horse.Variant var1);

   Horse.Color getColor();

   void setColor(Horse.Color var1);

   Horse.Style getStyle();

   void setStyle(Horse.Style var1);

   boolean isCarryingChest();

   void setCarryingChest(boolean var1);

   int getDomestication();

   void setDomestication(int var1);

   int getMaxDomestication();

   void setMaxDomestication(int var1);

   double getJumpStrength();

   void setJumpStrength(double var1);

   HorseInventory getInventory();

   public static enum Color {
      WHITE,
      CREAMY,
      CHESTNUT,
      BROWN,
      BLACK,
      GRAY,
      DARK_BROWN;
   }

   public static enum Style {
      NONE,
      WHITE,
      WHITEFIELD,
      WHITE_DOTS,
      BLACK_DOTS;
   }

   public static enum Variant {
      HORSE,
      DONKEY,
      MULE,
      UNDEAD_HORSE,
      SKELETON_HORSE;
   }
}
