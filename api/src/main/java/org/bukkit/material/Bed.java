package org.bukkit.material;

import org.bukkit.block.BlockFace;

public class Bed extends MaterialData implements Directional {
   public Bed() {
      super(org.bukkit.Material.BED_BLOCK);
   }

   public Bed(BlockFace direction) {
      this();
      this.setFacingDirection(direction);
   }

   @Deprecated
   public Bed(int type) {
      super(type);
   }

   public Bed(org.bukkit.Material type) {
      super(type);
   }

   @Deprecated
   public Bed(int type, byte data) {
      super(type, data);
   }

   @Deprecated
   public Bed(org.bukkit.Material type, byte data) {
      super(type, data);
   }

   public boolean isHeadOfBed() {
      return (this.getData() & 8) == 8;
   }

   public void setHeadOfBed(boolean isHeadOfBed) {
      this.setData((byte)(isHeadOfBed ? this.getData() | 8 : this.getData() & -9));
   }

   @Override
   public void setFacingDirection(BlockFace face) {
      byte data;
      switch (face) {
         case SOUTH:
            data = 0;
            break;
         case WEST:
            data = 1;
            break;
         case NORTH:
            data = 2;
            break;
         case EAST:
         default:
            data = 3;
      }

      if (this.isHeadOfBed()) {
         data = (byte)(data | 8);
      }

      this.setData(data);
   }

   @Override
   public BlockFace getFacing() {
      byte data = (byte)(this.getData() & 7);
      switch (data) {
         case 0:
            return BlockFace.SOUTH;
         case 1:
            return BlockFace.WEST;
         case 2:
            return BlockFace.NORTH;
         case 3:
         default:
            return BlockFace.EAST;
      }
   }

   @Override
   public String toString() {
      return (this.isHeadOfBed() ? "HEAD" : "FOOT") + " of " + super.toString() + " facing " + this.getFacing();
   }

   public Bed clone() {
      return (Bed)super.clone();
   }
}
