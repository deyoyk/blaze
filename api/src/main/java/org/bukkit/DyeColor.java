package org.bukkit;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.Map;

public enum DyeColor {
   WHITE(0, 15, org.bukkit.Color.WHITE, org.bukkit.Color.fromRGB(15790320)),
   ORANGE(1, 14, org.bukkit.Color.fromRGB(14188339), org.bukkit.Color.fromRGB(15435844)),
   MAGENTA(2, 13, org.bukkit.Color.fromRGB(11685080), org.bukkit.Color.fromRGB(12801229)),
   LIGHT_BLUE(3, 12, org.bukkit.Color.fromRGB(6724056), org.bukkit.Color.fromRGB(6719955)),
   YELLOW(4, 11, org.bukkit.Color.fromRGB(15066419), org.bukkit.Color.fromRGB(14602026)),
   LIME(5, 10, org.bukkit.Color.fromRGB(8375321), org.bukkit.Color.fromRGB(4312372)),
   PINK(6, 9, org.bukkit.Color.fromRGB(15892389), org.bukkit.Color.fromRGB(14188952)),
   GRAY(7, 8, org.bukkit.Color.fromRGB(5000268), org.bukkit.Color.fromRGB(4408131)),
   SILVER(8, 7, org.bukkit.Color.fromRGB(10066329), org.bukkit.Color.fromRGB(11250603)),
   CYAN(9, 6, org.bukkit.Color.fromRGB(5013401), org.bukkit.Color.fromRGB(2651799)),
   PURPLE(10, 5, org.bukkit.Color.fromRGB(8339378), org.bukkit.Color.fromRGB(8073150)),
   BLUE(11, 4, org.bukkit.Color.fromRGB(3361970), org.bukkit.Color.fromRGB(2437522)),
   BROWN(12, 3, org.bukkit.Color.fromRGB(6704179), org.bukkit.Color.fromRGB(5320730)),
   GREEN(13, 2, org.bukkit.Color.fromRGB(6717235), org.bukkit.Color.fromRGB(3887386)),
   RED(14, 1, org.bukkit.Color.fromRGB(10040115), org.bukkit.Color.fromRGB(11743532)),
   BLACK(15, 0, org.bukkit.Color.fromRGB(1644825), org.bukkit.Color.fromRGB(1973019));

   private final byte woolData;
   private final byte dyeData;
   private final org.bukkit.Color color;
   private final org.bukkit.Color firework;
   private static final org.bukkit.DyeColor[] BY_WOOL_DATA = values();
   private static final org.bukkit.DyeColor[] BY_DYE_DATA = values();
   private static final Map<org.bukkit.Color, org.bukkit.DyeColor> BY_COLOR;
   private static final Map<org.bukkit.Color, org.bukkit.DyeColor> BY_FIREWORK;

   private DyeColor(int woolData, int dyeData, org.bukkit.Color color, org.bukkit.Color firework) {
      this.woolData = (byte)woolData;
      this.dyeData = (byte)dyeData;
      this.color = color;
      this.firework = firework;
   }

   @Deprecated
   public byte getData() {
      return this.getWoolData();
   }

   @Deprecated
   public byte getWoolData() {
      return this.woolData;
   }

   @Deprecated
   public byte getDyeData() {
      return this.dyeData;
   }

   public org.bukkit.Color getColor() {
      return this.color;
   }

   public org.bukkit.Color getFireworkColor() {
      return this.firework;
   }

   @Deprecated
   public static org.bukkit.DyeColor getByData(byte data) {
      return getByWoolData(data);
   }

   @Deprecated
   public static org.bukkit.DyeColor getByWoolData(byte data) {
      int i = 255 & data;
      return i >= BY_WOOL_DATA.length ? null : BY_WOOL_DATA[i];
   }

   @Deprecated
   public static org.bukkit.DyeColor getByDyeData(byte data) {
      int i = 255 & data;
      return i >= BY_DYE_DATA.length ? null : BY_DYE_DATA[i];
   }

   public static org.bukkit.DyeColor getByColor(org.bukkit.Color color) {
      return BY_COLOR.get(color);
   }

   public static org.bukkit.DyeColor getByFireworkColor(org.bukkit.Color color) {
      return BY_FIREWORK.get(color);
   }

   static {
      Builder<org.bukkit.Color, org.bukkit.DyeColor> byColor = ImmutableMap.builder();
      Builder<org.bukkit.Color, org.bukkit.DyeColor> byFirework = ImmutableMap.builder();

      for (org.bukkit.DyeColor color : values()) {
         BY_WOOL_DATA[color.woolData & 255] = color;
         BY_DYE_DATA[color.dyeData & 255] = color;
         byColor.put(color.getColor(), color);
         byFirework.put(color.getFireworkColor(), color);
      }

      BY_COLOR = byColor.build();
      BY_FIREWORK = byFirework.build();
   }
}
