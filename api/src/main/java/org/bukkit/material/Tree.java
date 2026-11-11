package org.bukkit.material;

import org.bukkit.block.BlockFace;

public class Tree extends MaterialData {
   public Tree() {
      super(org.bukkit.Material.LOG);
   }

   public Tree(org.bukkit.TreeSpecies species) {
      this();
      this.setSpecies(species);
   }

   public Tree(org.bukkit.TreeSpecies species, BlockFace dir) {
      this();
      this.setSpecies(species);
      this.setDirection(dir);
   }

   @Deprecated
   public Tree(int type) {
      super(type);
   }

   public Tree(org.bukkit.Material type) {
      super(type);
   }

   @Deprecated
   public Tree(int type, byte data) {
      super(type, data);
   }

   @Deprecated
   public Tree(org.bukkit.Material type, byte data) {
      super(type, data);
   }

   public org.bukkit.TreeSpecies getSpecies() {
      return org.bukkit.TreeSpecies.getByData((byte)(this.getData() & 3));
   }

   public void setSpecies(org.bukkit.TreeSpecies species) {
      this.setData((byte)(this.getData() & 12 | species.getData()));
   }

   public BlockFace getDirection() {
      switch (this.getData() >> 2 & 3) {
         case 0:
         default:
            return BlockFace.UP;
         case 1:
            return BlockFace.WEST;
         case 2:
            return BlockFace.NORTH;
         case 3:
            return BlockFace.SELF;
      }
   }

   public void setDirection(BlockFace dir) {
      int dat;
      switch (dir) {
         case UP:
         case DOWN:
         default:
            dat = 0;
            break;
         case WEST:
         case EAST:
            dat = 1;
            break;
         case NORTH:
         case SOUTH:
            dat = 2;
            break;
         case SELF:
            dat = 3;
      }

      this.setData((byte)(this.getData() & 3 | dat << 2));
   }

   @Override
   public String toString() {
      return this.getSpecies() + " " + this.getDirection() + " " + super.toString();
   }

   public Tree clone() {
      return (Tree)super.clone();
   }
}
