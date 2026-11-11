package org.bukkit;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

@SerializableAs("Color")
public final class Color implements ConfigurationSerializable {
   private static final int BIT_MASK = 255;
   public static final org.bukkit.Color WHITE = fromRGB(16777215);
   public static final org.bukkit.Color SILVER = fromRGB(12632256);
   public static final org.bukkit.Color GRAY = fromRGB(8421504);
   public static final org.bukkit.Color BLACK = fromRGB(0);
   public static final org.bukkit.Color RED = fromRGB(16711680);
   public static final org.bukkit.Color MAROON = fromRGB(8388608);
   public static final org.bukkit.Color YELLOW = fromRGB(16776960);
   public static final org.bukkit.Color OLIVE = fromRGB(8421376);
   public static final org.bukkit.Color LIME = fromRGB(65280);
   public static final org.bukkit.Color GREEN = fromRGB(32768);
   public static final org.bukkit.Color AQUA = fromRGB(65535);
   public static final org.bukkit.Color TEAL = fromRGB(32896);
   public static final org.bukkit.Color BLUE = fromRGB(255);
   public static final org.bukkit.Color NAVY = fromRGB(128);
   public static final org.bukkit.Color FUCHSIA = fromRGB(16711935);
   public static final org.bukkit.Color PURPLE = fromRGB(8388736);
   public static final org.bukkit.Color ORANGE = fromRGB(16753920);
   private final byte red;
   private final byte green;
   private final byte blue;

   public static org.bukkit.Color fromRGB(int red, int green, int blue) throws IllegalArgumentException {
      return new org.bukkit.Color(red, green, blue);
   }

   public static org.bukkit.Color fromBGR(int blue, int green, int red) throws IllegalArgumentException {
      return new org.bukkit.Color(red, green, blue);
   }

   public static org.bukkit.Color fromRGB(int rgb) throws IllegalArgumentException {
      Validate.isTrue(rgb >> 24 == 0, "Extrenuous data in: ", rgb);
      return fromRGB(rgb >> 16 & 0xFF, rgb >> 8 & 0xFF, rgb >> 0 & 0xFF);
   }

   public static org.bukkit.Color fromBGR(int bgr) throws IllegalArgumentException {
      Validate.isTrue(bgr >> 24 == 0, "Extrenuous data in: ", bgr);
      return fromBGR(bgr >> 16 & 0xFF, bgr >> 8 & 0xFF, bgr >> 0 & 0xFF);
   }

   private Color(int red, int green, int blue) {
      Validate.isTrue(red >= 0 && red <= 255, "Red is not between 0-255: ", red);
      Validate.isTrue(green >= 0 && green <= 255, "Green is not between 0-255: ", green);
      Validate.isTrue(blue >= 0 && blue <= 255, "Blue is not between 0-255: ", blue);
      this.red = (byte)red;
      this.green = (byte)green;
      this.blue = (byte)blue;
   }

   public int getRed() {
      return 0xFF & this.red;
   }

   public org.bukkit.Color setRed(int red) {
      return fromRGB(red, this.getGreen(), this.getBlue());
   }

   public int getGreen() {
      return 0xFF & this.green;
   }

   public org.bukkit.Color setGreen(int green) {
      return fromRGB(this.getRed(), green, this.getBlue());
   }

   public int getBlue() {
      return 0xFF & this.blue;
   }

   public org.bukkit.Color setBlue(int blue) {
      return fromRGB(this.getRed(), this.getGreen(), blue);
   }

   public int asRGB() {
      return this.getRed() << 16 | this.getGreen() << 8 | this.getBlue() << 0;
   }

   public int asBGR() {
      return this.getBlue() << 16 | this.getGreen() << 8 | this.getRed() << 0;
   }

   public org.bukkit.Color mixDyes(org.bukkit.DyeColor... colors) {
      Validate.noNullElements(colors, "Colors cannot be null");
      org.bukkit.Color[] toPass = new org.bukkit.Color[colors.length];

      for (int i = 0; i < colors.length; i++) {
         toPass[i] = colors[i].getColor();
      }

      return this.mixColors(toPass);
   }

   public org.bukkit.Color mixColors(org.bukkit.Color... colors) {
      Validate.noNullElements(colors, "Colors cannot be null");
      int totalRed = this.getRed();
      int totalGreen = this.getGreen();
      int totalBlue = this.getBlue();
      int totalMax = Math.max(Math.max(totalRed, totalGreen), totalBlue);

      for (org.bukkit.Color color : colors) {
         totalRed += color.getRed();
         totalGreen += color.getGreen();
         totalBlue += color.getBlue();
         totalMax += Math.max(Math.max(color.getRed(), color.getGreen()), color.getBlue());
      }

      float averageRed = totalRed / (colors.length + 1);
      float averageGreen = totalGreen / (colors.length + 1);
      float averageBlue = totalBlue / (colors.length + 1);
      float averageMax = totalMax / (colors.length + 1);
      float maximumOfAverages = Math.max(Math.max(averageRed, averageGreen), averageBlue);
      float gainFactor = averageMax / maximumOfAverages;
      return fromRGB((int)(averageRed * gainFactor), (int)(averageGreen * gainFactor), (int)(averageBlue * gainFactor));
   }

   @Override
   public boolean equals(Object o) {
      if (!(o instanceof org.bukkit.Color)) {
         return false;
      } else {
         org.bukkit.Color that = (org.bukkit.Color)o;
         return this.blue == that.blue && this.green == that.green && this.red == that.red;
      }
   }

   @Override
   public int hashCode() {
      return this.asRGB() ^ org.bukkit.Color.class.hashCode();
   }

   @Override
   public Map<String, Object> serialize() {
      return ImmutableMap.of("RED", this.getRed(), "BLUE", this.getBlue(), "GREEN", this.getGreen());
   }

   public static org.bukkit.Color deserialize(Map<String, Object> map) {
      return fromRGB(asInt("RED", map), asInt("GREEN", map), asInt("BLUE", map));
   }

   private static int asInt(String string, Map<String, Object> map) {
      Object value = map.get(string);
      if (value == null) {
         throw new IllegalArgumentException(string + " not in map " + map);
      } else if (!(value instanceof Number)) {
         throw new IllegalArgumentException(string + 40 + value + ") is not a number");
      } else {
         return ((Number)value).intValue();
      }
   }

   @Override
   public String toString() {
      return "Color:[rgb0x"
         + Integer.toHexString(this.getRed()).toUpperCase()
         + Integer.toHexString(this.getGreen()).toUpperCase()
         + Integer.toHexString(this.getBlue()).toUpperCase()
         + "]";
   }
}
