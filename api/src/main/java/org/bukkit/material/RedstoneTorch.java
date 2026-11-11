package org.bukkit.material;

public class RedstoneTorch extends Torch implements Redstone {
   public RedstoneTorch() {
      super(org.bukkit.Material.REDSTONE_TORCH_ON);
   }

   @Deprecated
   public RedstoneTorch(int type) {
      super(type);
   }

   public RedstoneTorch(org.bukkit.Material type) {
      super(type);
   }

   @Deprecated
   public RedstoneTorch(int type, byte data) {
      super(type, data);
   }

   @Deprecated
   public RedstoneTorch(org.bukkit.Material type, byte data) {
      super(type, data);
   }

   @Override
   public boolean isPowered() {
      return this.getItemType() == org.bukkit.Material.REDSTONE_TORCH_ON;
   }

   @Override
   public String toString() {
      return super.toString() + " " + (this.isPowered() ? "" : "NOT ") + "POWERED";
   }

   public RedstoneTorch clone() {
      return (RedstoneTorch)super.clone();
   }
}
