package org.bukkit.material;

import org.bukkit.block.BlockFace;

public class Torch extends SimpleAttachableMaterialData {
   public Torch() {
      super(org.bukkit.Material.TORCH);
   }

   @Deprecated
   public Torch(int type) {
      super(type);
   }

   public Torch(org.bukkit.Material type) {
      super(type);
   }

   @Deprecated
   public Torch(int type, byte data) {
      super(type, data);
   }

   @Deprecated
   public Torch(org.bukkit.Material type, byte data) {
      super(type, data);
   }

   @Override
   public BlockFace getAttachedFace() {
      byte data = this.getData();
      switch (data) {
         case 1:
            return BlockFace.WEST;
         case 2:
            return BlockFace.EAST;
         case 3:
            return BlockFace.NORTH;
         case 4:
            return BlockFace.SOUTH;
         case 5:
         default:
            return BlockFace.DOWN;
      }
   }

   @Override
   public void setFacingDirection(BlockFace face) {
      byte data;
      switch (face) {
         case EAST:
            data = 1;
            break;
         case WEST:
            data = 2;
            break;
         case SOUTH:
            data = 3;
            break;
         case NORTH:
            data = 4;
            break;
         case UP:
         default:
            data = 5;
      }

      this.setData(data);
   }

   public Torch clone() {
      return (Torch)super.clone();
   }
}
