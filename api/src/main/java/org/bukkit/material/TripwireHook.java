package org.bukkit.material;

import org.bukkit.block.BlockFace;

public class TripwireHook extends SimpleAttachableMaterialData implements Redstone {
   public TripwireHook() {
      super(org.bukkit.Material.TRIPWIRE_HOOK);
   }

   @Deprecated
   public TripwireHook(int type) {
      super(type);
   }

   @Deprecated
   public TripwireHook(int type, byte data) {
      super(type, data);
   }

   public TripwireHook(BlockFace dir) {
      this();
      this.setFacingDirection(dir);
   }

   public boolean isConnected() {
      return (this.getData() & 4) != 0;
   }

   public void setConnected(boolean connected) {
      int dat = this.getData() & 11;
      if (connected) {
         dat |= 4;
      }

      this.setData((byte)dat);
   }

   public boolean isActivated() {
      return (this.getData() & 8) != 0;
   }

   public void setActivated(boolean act) {
      int dat = this.getData() & 7;
      if (act) {
         dat |= 8;
      }

      this.setData((byte)dat);
   }

   @Override
   public void setFacingDirection(BlockFace face) {
      int dat = this.getData() & 12;
      switch (face) {
         case WEST:
            dat |= 1;
            break;
         case NORTH:
            dat |= 2;
            break;
         case EAST:
            dat |= 3;
         case SOUTH:
      }

      this.setData((byte)dat);
   }

   @Override
   public BlockFace getAttachedFace() {
      switch (this.getData() & 3) {
         case 0:
            return BlockFace.NORTH;
         case 1:
            return BlockFace.EAST;
         case 2:
            return BlockFace.SOUTH;
         case 3:
            return BlockFace.WEST;
         default:
            return null;
      }
   }

   @Override
   public boolean isPowered() {
      return this.isActivated();
   }

   public TripwireHook clone() {
      return (TripwireHook)super.clone();
   }

   @Override
   public String toString() {
      return super.toString() + " facing " + this.getFacing() + (this.isActivated() ? " Activated" : "") + (this.isConnected() ? " Connected" : "");
   }
}
