package org.bukkit.material;

import org.bukkit.block.BlockFace;

public class CocoaPlant extends MaterialData implements Directional, Attachable {
   public CocoaPlant() {
      super(org.bukkit.Material.COCOA);
   }

   @Deprecated
   public CocoaPlant(int type) {
      super(type);
   }

   @Deprecated
   public CocoaPlant(int type, byte data) {
      super(type, data);
   }

   public CocoaPlant(CocoaPlant.CocoaPlantSize sz) {
      this();
      this.setSize(sz);
   }

   public CocoaPlant(CocoaPlant.CocoaPlantSize sz, BlockFace dir) {
      this();
      this.setSize(sz);
      this.setFacingDirection(dir);
   }

   public CocoaPlant.CocoaPlantSize getSize() {
      switch (this.getData() & 12) {
         case 0:
            return CocoaPlant.CocoaPlantSize.SMALL;
         case 4:
            return CocoaPlant.CocoaPlantSize.MEDIUM;
         default:
            return CocoaPlant.CocoaPlantSize.LARGE;
      }
   }

   public void setSize(CocoaPlant.CocoaPlantSize sz) {
      int dat = this.getData() & 3;
      switch (sz) {
         case SMALL:
         default:
            break;
         case MEDIUM:
            dat |= 4;
            break;
         case LARGE:
            dat |= 8;
      }

      this.setData((byte)dat);
   }

   @Override
   public BlockFace getAttachedFace() {
      return this.getFacing().getOppositeFace();
   }

   @Override
   public void setFacingDirection(BlockFace face) {
      int dat = this.getData() & 12;
      switch (face) {
         case SOUTH:
         default:
            break;
         case WEST:
            dat |= 1;
            break;
         case NORTH:
            dat |= 2;
            break;
         case EAST:
            dat |= 3;
      }

      this.setData((byte)dat);
   }

   @Override
   public BlockFace getFacing() {
      switch (this.getData() & 3) {
         case 0:
            return BlockFace.SOUTH;
         case 1:
            return BlockFace.WEST;
         case 2:
            return BlockFace.NORTH;
         case 3:
            return BlockFace.EAST;
         default:
            return null;
      }
   }

   public CocoaPlant clone() {
      return (CocoaPlant)super.clone();
   }

   @Override
   public String toString() {
      return super.toString() + " facing " + this.getFacing() + " " + this.getSize();
   }

   public static enum CocoaPlantSize {
      SMALL,
      MEDIUM,
      LARGE;
   }
}
