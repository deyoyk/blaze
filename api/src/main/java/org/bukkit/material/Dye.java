package org.bukkit.material;

public class Dye extends MaterialData implements Colorable {
   public Dye() {
      super(org.bukkit.Material.INK_SACK);
   }

   @Deprecated
   public Dye(int type) {
      super(type);
   }

   public Dye(org.bukkit.Material type) {
      super(type);
   }

   @Deprecated
   public Dye(int type, byte data) {
      super(type, data);
   }

   @Deprecated
   public Dye(org.bukkit.Material type, byte data) {
      super(type, data);
   }

   public Dye(org.bukkit.DyeColor color) {
      super(org.bukkit.Material.INK_SACK, color.getDyeData());
   }

   @Override
   public org.bukkit.DyeColor getColor() {
      return org.bukkit.DyeColor.getByDyeData(this.getData());
   }

   @Override
   public void setColor(org.bukkit.DyeColor color) {
      this.setData(color.getDyeData());
   }

   @Override
   public String toString() {
      return this.getColor() + " DYE(" + this.getData() + ")";
   }

   public Dye clone() {
      return (Dye)super.clone();
   }
}
