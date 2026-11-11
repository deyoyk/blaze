package org.bukkit.event.block;

import org.bukkit.block.Block;
import org.bukkit.event.HandlerList;

public class BlockCanBuildEvent extends BlockEvent {
   private static final HandlerList handlers = new HandlerList();
   protected boolean buildable;
   @Deprecated
   protected int material;

   @Deprecated
   public BlockCanBuildEvent(Block block, int id, boolean canBuild) {
      super(block);
      this.buildable = canBuild;
      this.material = id;
   }

   public boolean isBuildable() {
      return this.buildable;
   }

   public void setBuildable(boolean cancel) {
      this.buildable = cancel;
   }

   public org.bukkit.Material getMaterial() {
      return org.bukkit.Material.getMaterial(this.material);
   }

   @Deprecated
   public int getMaterialId() {
      return this.material;
   }

   @Override
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
