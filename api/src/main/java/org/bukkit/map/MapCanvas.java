package org.bukkit.map;

import java.awt.Image;

public interface MapCanvas {
   MapView getMapView();

   MapCursorCollection getCursors();

   void setCursors(MapCursorCollection var1);

   void setPixel(int var1, int var2, byte var3);

   byte getPixel(int var1, int var2);

   byte getBasePixel(int var1, int var2);

   void drawImage(int var1, int var2, Image var3);

   void drawText(int var1, int var2, MapFont var3, String var4);
}
