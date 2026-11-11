package org.bukkit.material;

public class RedstoneWire extends MaterialData implements Redstone {
   public RedstoneWire() {
      super(org.bukkit.Material.REDSTONE_WIRE);
   }

   @Deprecated
   public RedstoneWire(int type) {
      super(type);
   }

   public RedstoneWire(org.bukkit.Material type) {
      super(type);
   }

   @Deprecated
   public RedstoneWire(int type, byte data) {
      super(type, data);
   }

   @Deprecated
   public RedstoneWire(org.bukkit.Material type, byte data) {
      super(type, data);
   }

   @Override
   public boolean isPowered() {
      return this.getData() > 0;
   }

   @Override
   public String toString() {
      return super.toString() + " " + (this.isPowered() ? "" : "NOT ") + "POWERED";
   }

   public RedstoneWire clone() {
      return (RedstoneWire)super.clone();
   }
}
