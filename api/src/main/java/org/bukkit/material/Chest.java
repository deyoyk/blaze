package org.bukkit.material;

import org.bukkit.block.BlockFace;

public class Chest extends DirectionalContainer {
   public Chest() {
      super(org.bukkit.Material.CHEST);
   }

   public Chest(BlockFace direction) {
      this();
      this.setFacingDirection(direction);
   }

   @Deprecated
   public Chest(int type) {
      super(type);
   }

   public Chest(org.bukkit.Material type) {
      super(type);
   }

   @Deprecated
   public Chest(int type, byte data) {
      super(type, data);
   }

   @Deprecated
   public Chest(org.bukkit.Material type, byte data) {
      super(type, data);
   }

   public Chest clone() {
      return (Chest)super.clone();
   }
}
