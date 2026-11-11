package org.bukkit.material;

import org.bukkit.block.BlockFace;

public class PistonBaseMaterial extends MaterialData implements Directional, Redstone {
   @Deprecated
   public PistonBaseMaterial(int type) {
      super(type);
   }

   public PistonBaseMaterial(org.bukkit.Material type) {
      super(type);
   }

   @Deprecated
   public PistonBaseMaterial(int type, byte data) {
      super(type, data);
   }

   @Deprecated
   public PistonBaseMaterial(org.bukkit.Material type, byte data) {
      super(type, data);
   }

   @Override
   public void setFacingDirection(BlockFace face) {
      byte data = (byte)(this.getData() & 8);
      switch (face) {
         case UP:
            data = (byte)(data | 1);
            break;
         case NORTH:
            data = (byte)(data | 2);
            break;
         case SOUTH:
            data = (byte)(data | 3);
            break;
         case WEST:
            data = (byte)(data | 4);
            break;
         case EAST:
            data = (byte)(data | 5);
      }

      this.setData(data);
   }

   @Override
   public BlockFace getFacing() {
      byte dir = (byte)(this.getData() & 7);
      switch (dir) {
         case 0:
            return BlockFace.DOWN;
         case 1:
            return BlockFace.UP;
         case 2:
            return BlockFace.NORTH;
         case 3:
            return BlockFace.SOUTH;
         case 4:
            return BlockFace.WEST;
         case 5:
            return BlockFace.EAST;
         default:
            return BlockFace.SELF;
      }
   }

   @Override
   public boolean isPowered() {
      return (this.getData() & 8) == 8;
   }

   public void setPowered(boolean powered) {
      this.setData((byte)(powered ? this.getData() | 8 : this.getData() & -9));
   }

   public boolean isSticky() {
      return this.getItemType() == org.bukkit.Material.PISTON_STICKY_BASE;
   }

   public PistonBaseMaterial clone() {
      return (PistonBaseMaterial)super.clone();
   }
}
