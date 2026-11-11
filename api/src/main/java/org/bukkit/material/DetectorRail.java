package org.bukkit.material;

public class DetectorRail extends ExtendedRails implements PressureSensor {
   public DetectorRail() {
      super(org.bukkit.Material.DETECTOR_RAIL);
   }

   @Deprecated
   public DetectorRail(int type) {
      super(type);
   }

   public DetectorRail(org.bukkit.Material type) {
      super(type);
   }

   @Deprecated
   public DetectorRail(int type, byte data) {
      super(type, data);
   }

   @Deprecated
   public DetectorRail(org.bukkit.Material type, byte data) {
      super(type, data);
   }

   @Override
   public boolean isPressed() {
      return (this.getData() & 8) == 8;
   }

   public void setPressed(boolean isPressed) {
      this.setData((byte)(isPressed ? this.getData() | 8 : this.getData() & -9));
   }

   public DetectorRail clone() {
      return (DetectorRail)super.clone();
   }
}
