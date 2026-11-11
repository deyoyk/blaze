package org.bukkit.map;

import java.util.List;

public interface MapView {
   @Deprecated
   short getId();

   boolean isVirtual();

   MapView.Scale getScale();

   void setScale(MapView.Scale var1);

   int getCenterX();

   int getCenterZ();

   void setCenterX(int var1);

   void setCenterZ(int var1);

   org.bukkit.World getWorld();

   void setWorld(org.bukkit.World var1);

   List<MapRenderer> getRenderers();

   void addRenderer(MapRenderer var1);

   boolean removeRenderer(MapRenderer var1);

   public static enum Scale {
      CLOSEST(0),
      CLOSE(1),
      NORMAL(2),
      FAR(3),
      FARTHEST(4);

      private byte value;

      private Scale(int value) {
         this.value = (byte)value;
      }

      @Deprecated
      public static MapView.Scale valueOf(byte value) {
         switch (value) {
            case 0:
               return CLOSEST;
            case 1:
               return CLOSE;
            case 2:
               return NORMAL;
            case 3:
               return FAR;
            case 4:
               return FARTHEST;
            default:
               return null;
         }
      }

      @Deprecated
      public byte getValue() {
         return this.value;
      }
   }
}
