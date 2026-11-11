package org.bukkit.material;

import org.bukkit.block.BlockFace;

public class Stairs extends MaterialData implements Directional {
   @Deprecated
   public Stairs(int type) {
      super(type);
   }

   public Stairs(org.bukkit.Material type) {
      super(type);
   }

   @Deprecated
   public Stairs(int type, byte data) {
      super(type, data);
   }

   @Deprecated
   public Stairs(org.bukkit.Material type, byte data) {
      super(type, data);
   }

   public BlockFace getAscendingDirection() {
      byte data = this.getData();
      switch (data & 3) {
         case 0:
         default:
            return BlockFace.EAST;
         case 1:
            return BlockFace.WEST;
         case 2:
            return BlockFace.SOUTH;
         case 3:
            return BlockFace.NORTH;
      }
   }

   public BlockFace getDescendingDirection() {
      return this.getAscendingDirection().getOppositeFace();
   }

   @Override
   public void setFacingDirection(BlockFace face) {
      byte data;
      switch (face) {
         case NORTH:
            data = 3;
            break;
         case SOUTH:
            data = 2;
            break;
         case EAST:
         default:
            data = 0;
            break;
         case WEST:
            data = 1;
      }

      this.setData((byte)(this.getData() & 12 | data));
   }

   @Override
   public BlockFace getFacing() {
      return this.getDescendingDirection();
   }

   public boolean isInverted() {
      return (this.getData() & 4) != 0;
   }

   public void setInverted(boolean inv) {
      int dat = this.getData() & 3;
      if (inv) {
         dat |= 4;
      }

      this.setData((byte)dat);
   }

   @Override
   public String toString() {
      return super.toString() + " facing " + this.getFacing() + (this.isInverted() ? " inverted" : "");
   }

   public Stairs clone() {
      return (Stairs)super.clone();
   }
}
