package org.bukkit.material;

public class Crops extends MaterialData {
   public Crops() {
      super(org.bukkit.Material.CROPS);
   }

   public Crops(org.bukkit.CropState state) {
      this();
      this.setState(state);
   }

   @Deprecated
   public Crops(int type) {
      super(type);
   }

   public Crops(org.bukkit.Material type) {
      super(type);
   }

   @Deprecated
   public Crops(int type, byte data) {
      super(type, data);
   }

   @Deprecated
   public Crops(org.bukkit.Material type, byte data) {
      super(type, data);
   }

   public org.bukkit.CropState getState() {
      return org.bukkit.CropState.getByData(this.getData());
   }

   public void setState(org.bukkit.CropState state) {
      this.setData(state.getData());
   }

   @Override
   public String toString() {
      return this.getState() + " " + super.toString();
   }

   public Crops clone() {
      return (Crops)super.clone();
   }
}
