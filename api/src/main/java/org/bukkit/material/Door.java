package org.bukkit.material;

import org.bukkit.block.BlockFace;

public class Door extends MaterialData implements Directional, Openable {
   @Deprecated
   public Door() {
      super(org.bukkit.Material.WOODEN_DOOR);
   }

   @Deprecated
   public Door(int type) {
      super(type);
   }

   public Door(org.bukkit.Material type) {
      super(type);
   }

   public Door(org.bukkit.Material type, BlockFace face) {
      this(type, face, false);
   }

   public Door(org.bukkit.Material type, BlockFace face, boolean isOpen) {
      super(type);
      this.setTopHalf(false);
      this.setFacingDirection(face);
      this.setOpen(isOpen);
   }

   public Door(org.bukkit.Material type, boolean isHingeRight) {
      super(type);
      this.setTopHalf(true);
      this.setHinge(isHingeRight);
   }

   public Door(org.bukkit.TreeSpecies species, BlockFace face) {
      this(getWoodDoorOfSpecies(species), face, false);
   }

   public Door(org.bukkit.TreeSpecies species, BlockFace face, boolean isOpen) {
      this(getWoodDoorOfSpecies(species), face, isOpen);
   }

   public Door(org.bukkit.TreeSpecies species, boolean isHingeRight) {
      this(getWoodDoorOfSpecies(species), isHingeRight);
   }

   @Deprecated
   public Door(int type, byte data) {
      super(type, data);
   }

   @Deprecated
   public Door(org.bukkit.Material type, byte data) {
      super(type, data);
   }

   public static org.bukkit.Material getWoodDoorOfSpecies(org.bukkit.TreeSpecies species) {
      switch (species) {
         case GENERIC:
         default:
            return org.bukkit.Material.WOODEN_DOOR;
         case BIRCH:
            return org.bukkit.Material.BIRCH_DOOR;
         case REDWOOD:
            return org.bukkit.Material.SPRUCE_DOOR;
         case JUNGLE:
            return org.bukkit.Material.JUNGLE_DOOR;
         case ACACIA:
            return org.bukkit.Material.ACACIA_DOOR;
         case DARK_OAK:
            return org.bukkit.Material.DARK_OAK_DOOR;
      }
   }

   @Override
   public boolean isOpen() {
      return (this.getData() & 4) == 4;
   }

   @Override
   public void setOpen(boolean isOpen) {
      this.setData((byte)(isOpen ? this.getData() | 4 : this.getData() & -5));
   }

   public boolean isTopHalf() {
      return (this.getData() & 8) == 8;
   }

   public void setTopHalf(boolean isTopHalf) {
      this.setData((byte)(isTopHalf ? this.getData() | 8 : this.getData() & -9));
   }

   @Deprecated
   public BlockFace getHingeCorner() {
      return BlockFace.SELF;
   }

   @Override
   public String toString() {
      return (this.isTopHalf() ? "TOP" : "BOTTOM") + " half of " + super.toString();
   }

   @Override
   public void setFacingDirection(BlockFace face) {
      byte data = (byte)(this.getData() & 12);
      switch (face) {
         case WEST:
            data = (byte)(data | 0);
            break;
         case NORTH:
            data = (byte)(data | 1);
            break;
         case EAST:
            data = (byte)(data | 2);
            break;
         case SOUTH:
            data = (byte)(data | 3);
      }

      this.setData(data);
   }

   @Override
   public BlockFace getFacing() {
      byte data = (byte)(this.getData() & 3);
      switch (data) {
         case 0:
            return BlockFace.WEST;
         case 1:
            return BlockFace.NORTH;
         case 2:
            return BlockFace.EAST;
         case 3:
            return BlockFace.SOUTH;
         default:
            throw new IllegalStateException("Unknown door facing (data: " + data + ")");
      }
   }

   public boolean getHinge() {
      return (this.getData() & 1) == 1;
   }

   public void setHinge(boolean isHingeRight) {
      this.setData((byte)(isHingeRight ? this.getData() | 1 : this.getData() & -2));
   }

   public Door clone() {
      return (Door)super.clone();
   }
}
