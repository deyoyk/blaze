package org.bukkit.material;

public class Wool extends MaterialData implements Colorable {
   public Wool() {
      super(org.bukkit.Material.WOOL);
   }

   public Wool(org.bukkit.DyeColor color) {
      this();
      this.setColor(color);
   }

   @Deprecated
   public Wool(int type) {
      super(type);
   }

   public Wool(org.bukkit.Material type) {
      super(type);
   }

   @Deprecated
   public Wool(int type, byte data) {
      super(type, data);
   }

   @Deprecated
   public Wool(org.bukkit.Material type, byte data) {
      super(type, data);
   }

   @Override
   public org.bukkit.DyeColor getColor() {
      return org.bukkit.DyeColor.getByWoolData(this.getData());
   }

   @Override
   public void setColor(org.bukkit.DyeColor color) {
      this.setData(color.getWoolData());
   }

   @Override
   public String toString() {
      return this.getColor() + " " + super.toString();
   }

   public Wool clone() {
      return (Wool)super.clone();
   }
}
