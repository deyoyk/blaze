package org.bukkit.material;

public class WoodenStep extends MaterialData {
   public WoodenStep() {
      super(org.bukkit.Material.WOOD_STEP);
   }

   @Deprecated
   public WoodenStep(int type) {
      super(type);
   }

   public WoodenStep(org.bukkit.TreeSpecies species) {
      this();
      this.setSpecies(species);
   }

   public WoodenStep(org.bukkit.TreeSpecies species, boolean inv) {
      this();
      this.setSpecies(species);
      this.setInverted(inv);
   }

   @Deprecated
   public WoodenStep(int type, byte data) {
      super(type, data);
   }

   @Deprecated
   public WoodenStep(org.bukkit.Material type, byte data) {
      super(type, data);
   }

   public org.bukkit.TreeSpecies getSpecies() {
      return org.bukkit.TreeSpecies.getByData((byte)(this.getData() & 3));
   }

   public void setSpecies(org.bukkit.TreeSpecies species) {
      this.setData((byte)(this.getData() & 12 | species.getData()));
   }

   public boolean isInverted() {
      return (this.getData() & 8) != 0;
   }

   public void setInverted(boolean inv) {
      int dat = this.getData() & 7;
      if (inv) {
         dat |= 8;
      }

      this.setData((byte)dat);
   }

   public WoodenStep clone() {
      return (WoodenStep)super.clone();
   }

   @Override
   public String toString() {
      return super.toString() + " " + this.getSpecies() + (this.isInverted() ? " inverted" : "");
   }
}
