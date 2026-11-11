package org.bukkit.material;

import org.bukkit.block.BlockFace;

public class DirectionalContainer extends MaterialData implements Directional {
   @Deprecated
   public DirectionalContainer(int type) {
      super(type);
   }

   public DirectionalContainer(org.bukkit.Material type) {
      super(type);
   }

   @Deprecated
   public DirectionalContainer(int type, byte data) {
      super(type, data);
   }

   @Deprecated
   public DirectionalContainer(org.bukkit.Material type, byte data) {
      super(type, data);
   }

   @Override
   public void setFacingDirection(BlockFace face) {
      byte data;
      switch (face) {
         case NORTH:
            data = 2;
            break;
         case SOUTH:
            data = 3;
            break;
         case WEST:
            data = 4;
            break;
         case EAST:
         default:
            data = 5;
      }

      this.setData(data);
   }

   @Override
   public BlockFace getFacing() {
      byte data = this.getData();
      switch (data) {
         case 2:
            return BlockFace.NORTH;
         case 3:
            return BlockFace.SOUTH;
         case 4:
            return BlockFace.WEST;
         case 5:
         default:
            return BlockFace.EAST;
      }
   }

   @Override
   public String toString() {
      return super.toString() + " facing " + this.getFacing();
   }

   public DirectionalContainer clone() {
      return (DirectionalContainer)super.clone();
   }
}
