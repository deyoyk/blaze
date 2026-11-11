package org.bukkit.material;

import org.bukkit.block.BlockFace;

public class Skull extends MaterialData implements Directional {
   public Skull() {
      super(org.bukkit.Material.SKULL);
   }

   public Skull(BlockFace direction) {
      this();
      this.setFacingDirection(direction);
   }

   @Deprecated
   public Skull(int type) {
      super(type);
   }

   public Skull(org.bukkit.Material type) {
      super(type);
   }

   @Deprecated
   public Skull(int type, byte data) {
      super(type, data);
   }

   @Deprecated
   public Skull(org.bukkit.Material type, byte data) {
      super(type, data);
   }

   @Override
   public void setFacingDirection(BlockFace face) {
      int data;
      switch (face) {
         case SELF:
         default:
            data = 1;
            break;
         case NORTH:
            data = 2;
            break;
         case EAST:
            data = 4;
            break;
         case SOUTH:
            data = 3;
            break;
         case WEST:
            data = 5;
      }

      this.setData((byte)data);
   }

   @Override
   public BlockFace getFacing() {
      int data = this.getData();
      switch (data) {
         case 1:
         default:
            return BlockFace.SELF;
         case 2:
            return BlockFace.NORTH;
         case 3:
            return BlockFace.SOUTH;
         case 4:
            return BlockFace.EAST;
         case 5:
            return BlockFace.WEST;
      }
   }

   @Override
   public String toString() {
      return super.toString() + " facing " + this.getFacing();
   }

   public Skull clone() {
      return (Skull)super.clone();
   }
}
