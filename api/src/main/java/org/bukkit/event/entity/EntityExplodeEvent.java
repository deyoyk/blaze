package org.bukkit.event.entity;

import java.util.List;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class EntityExplodeEvent extends EntityEvent implements Cancellable {
   private static final HandlerList handlers = new HandlerList();
   private boolean cancel;
   private final org.bukkit.Location location;
   private final List<Block> blocks;
   private float yield;

   public EntityExplodeEvent(Entity what, org.bukkit.Location location, List<Block> blocks, float yield) {
      super(what);
      this.location = location;
      this.blocks = blocks;
      this.yield = yield;
      this.cancel = false;
   }

   @Override
   public boolean isCancelled() {
      return this.cancel;
   }

   @Override
   public void setCancelled(boolean cancel) {
      this.cancel = cancel;
   }

   public List<Block> blockList() {
      return this.blocks;
   }

   public org.bukkit.Location getLocation() {
      return this.location;
   }

   public float getYield() {
      return this.yield;
   }

   public void setYield(float yield) {
      this.yield = yield;
   }

   @Override
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
