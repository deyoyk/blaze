package org.bukkit.material;

import org.bukkit.block.BlockFace;

public class Furnace extends FurnaceAndDispenser {
   public Furnace() {
      super(org.bukkit.Material.FURNACE);
   }

   public Furnace(BlockFace direction) {
      this();
      this.setFacingDirection(direction);
   }

   @Deprecated
   public Furnace(int type) {
      super(type);
   }

   public Furnace(org.bukkit.Material type) {
      super(type);
   }

   @Deprecated
   public Furnace(int type, byte data) {
      super(type, data);
   }

   @Deprecated
   public Furnace(org.bukkit.Material type, byte data) {
      super(type, data);
   }

   public Furnace clone() {
      return (Furnace)super.clone();
   }
}
