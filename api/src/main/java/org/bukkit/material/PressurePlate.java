package org.bukkit.material;

public class PressurePlate extends MaterialData implements PressureSensor {
   public PressurePlate() {
      super(org.bukkit.Material.WOOD_PLATE);
   }

   @Deprecated
   public PressurePlate(int type) {
      super(type);
   }

   public PressurePlate(org.bukkit.Material type) {
      super(type);
   }

   @Deprecated
   public PressurePlate(int type, byte data) {
      super(type, data);
   }

   @Deprecated
   public PressurePlate(org.bukkit.Material type, byte data) {
      super(type, data);
   }

   @Override
   public boolean isPressed() {
      return this.getData() == 1;
   }

   @Override
   public String toString() {
      return super.toString() + (this.isPressed() ? " PRESSED" : "");
   }

   public PressurePlate clone() {
      return (PressurePlate)super.clone();
   }
}
