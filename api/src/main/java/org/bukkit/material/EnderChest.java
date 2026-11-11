package org.bukkit.material;

import org.bukkit.block.BlockFace;

public class EnderChest extends DirectionalContainer {
   public EnderChest() {
      super(org.bukkit.Material.ENDER_CHEST);
   }

   public EnderChest(BlockFace direction) {
      this();
      this.setFacingDirection(direction);
   }

   @Deprecated
   public EnderChest(int type) {
      super(type);
   }

   public EnderChest(org.bukkit.Material type) {
      super(type);
   }

   @Deprecated
   public EnderChest(int type, byte data) {
      super(type, data);
   }

   @Deprecated
   public EnderChest(org.bukkit.Material type, byte data) {
      super(type, data);
   }

   public EnderChest clone() {
      return (EnderChest)super.clone();
   }
}
