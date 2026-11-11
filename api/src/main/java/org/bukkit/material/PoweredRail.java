package org.bukkit.material;

public class PoweredRail extends ExtendedRails implements Redstone {
   public PoweredRail() {
      super(org.bukkit.Material.POWERED_RAIL);
   }

   @Deprecated
   public PoweredRail(int type) {
      super(type);
   }

   public PoweredRail(org.bukkit.Material type) {
      super(type);
   }

   @Deprecated
   public PoweredRail(int type, byte data) {
      super(type, data);
   }

   @Deprecated
   public PoweredRail(org.bukkit.Material type, byte data) {
      super(type, data);
   }

   @Override
   public boolean isPowered() {
      return (this.getData() & 8) == 8;
   }

   public void setPowered(boolean isPowered) {
      this.setData((byte)(isPowered ? this.getData() | 8 : this.getData() & -9));
   }

   public PoweredRail clone() {
      return (PoweredRail)super.clone();
   }
}
