package org.bukkit.material;

import org.bukkit.block.BlockFace;

public class Rails extends MaterialData {
   public Rails() {
      super(org.bukkit.Material.RAILS);
   }

   @Deprecated
   public Rails(int type) {
      super(type);
   }

   public Rails(org.bukkit.Material type) {
      super(type);
   }

   @Deprecated
   public Rails(int type, byte data) {
      super(type, data);
   }

   @Deprecated
   public Rails(org.bukkit.Material type, byte data) {
      super(type, data);
   }

   public boolean isOnSlope() {
      byte d = this.getConvertedData();
      return d == 2 || d == 3 || d == 4 || d == 5;
   }

   public boolean isCurve() {
      byte d = this.getConvertedData();
      return d == 6 || d == 7 || d == 8 || d == 9;
   }

   public BlockFace getDirection() {
      byte d = this.getConvertedData();
      switch (d) {
         case 0:
         default:
            return BlockFace.SOUTH;
         case 1:
            return BlockFace.EAST;
         case 2:
            return BlockFace.EAST;
         case 3:
            return BlockFace.WEST;
         case 4:
            return BlockFace.NORTH;
         case 5:
            return BlockFace.SOUTH;
         case 6:
            return BlockFace.NORTH_WEST;
         case 7:
            return BlockFace.NORTH_EAST;
         case 8:
            return BlockFace.SOUTH_EAST;
         case 9:
            return BlockFace.SOUTH_WEST;
      }
   }

   @Override
   public String toString() {
      return super.toString() + " facing " + this.getDirection() + (this.isCurve() ? " on a curve" : (this.isOnSlope() ? " on a slope" : ""));
   }

   @Deprecated
   protected byte getConvertedData() {
      return this.getData();
   }

   public void setDirection(BlockFace face, boolean isOnSlope) {
      switch (face) {
         case EAST:
            this.setData((byte)(isOnSlope ? 2 : 1));
            break;
         case WEST:
            this.setData((byte)(isOnSlope ? 3 : 1));
            break;
         case NORTH:
            this.setData((byte)(isOnSlope ? 4 : 0));
            break;
         case SOUTH:
            this.setData((byte)(isOnSlope ? 5 : 0));
            break;
         case NORTH_WEST:
            this.setData((byte)6);
            break;
         case NORTH_EAST:
            this.setData((byte)7);
            break;
         case SOUTH_EAST:
            this.setData((byte)8);
            break;
         case SOUTH_WEST:
            this.setData((byte)9);
      }
   }

   public Rails clone() {
      return (Rails)super.clone();
   }
}
