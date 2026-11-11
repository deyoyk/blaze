package org.bukkit.material;

import org.bukkit.block.BlockFace;

public class Pumpkin extends MaterialData implements Directional {
   public Pumpkin() {
      super(org.bukkit.Material.PUMPKIN);
   }

   public Pumpkin(BlockFace direction) {
      this();
      this.setFacingDirection(direction);
   }

   @Deprecated
   public Pumpkin(int type) {
      super(type);
   }

   public Pumpkin(org.bukkit.Material type) {
      super(type);
   }

   @Deprecated
   public Pumpkin(int type, byte data) {
      super(type, data);
   }

   @Deprecated
   public Pumpkin(org.bukkit.Material type, byte data) {
      super(type, data);
   }

   public boolean isLit() {
      return this.getItemType() == org.bukkit.Material.JACK_O_LANTERN;
   }

   @Override
   public void setFacingDirection(BlockFace face) {
      byte data;
      switch (face) {
         case NORTH:
            data = 0;
            break;
         case EAST:
            data = 1;
            break;
         case SOUTH:
            data = 2;
            break;
         case WEST:
         default:
            data = 3;
      }

      this.setData(data);
   }

   @Override
   public BlockFace getFacing() {
      byte data = this.getData();
      switch (data) {
         case 0:
            return BlockFace.NORTH;
         case 1:
            return BlockFace.EAST;
         case 2:
            return BlockFace.SOUTH;
         case 3:
         default:
            return BlockFace.EAST;
      }
   }

   @Override
   public String toString() {
      return super.toString() + " facing " + this.getFacing() + " " + (this.isLit() ? "" : "NOT ") + "LIT";
   }

   public Pumpkin clone() {
      return (Pumpkin)super.clone();
   }
}
