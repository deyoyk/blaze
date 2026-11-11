package org.bukkit.material;

import org.bukkit.block.BlockFace;

public abstract class SimpleAttachableMaterialData extends MaterialData implements Attachable {
   @Deprecated
   public SimpleAttachableMaterialData(int type) {
      super(type);
   }

   public SimpleAttachableMaterialData(int type, BlockFace direction) {
      this(type);
      this.setFacingDirection(direction);
   }

   public SimpleAttachableMaterialData(org.bukkit.Material type, BlockFace direction) {
      this(type);
      this.setFacingDirection(direction);
   }

   public SimpleAttachableMaterialData(org.bukkit.Material type) {
      super(type);
   }

   @Deprecated
   public SimpleAttachableMaterialData(int type, byte data) {
      super(type, data);
   }

   @Deprecated
   public SimpleAttachableMaterialData(org.bukkit.Material type, byte data) {
      super(type, data);
   }

   @Override
   public BlockFace getFacing() {
      BlockFace attachedFace = this.getAttachedFace();
      return attachedFace == null ? null : attachedFace.getOppositeFace();
   }

   @Override
   public String toString() {
      return super.toString() + " facing " + this.getFacing();
   }

   public SimpleAttachableMaterialData clone() {
      return (SimpleAttachableMaterialData)super.clone();
   }
}
