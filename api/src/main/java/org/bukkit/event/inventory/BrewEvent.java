package org.bukkit.event.inventory;

import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.inventory.BrewerInventory;

public class BrewEvent extends BlockEvent implements Cancellable {
   private static final HandlerList handlers = new HandlerList();
   private BrewerInventory contents;
   private boolean cancelled;

   public BrewEvent(Block brewer, BrewerInventory contents) {
      super(brewer);
      this.contents = contents;
   }

   public BrewerInventory getContents() {
      return this.contents;
   }

   @Override
   public boolean isCancelled() {
      return this.cancelled;
   }

   @Override
   public void setCancelled(boolean cancel) {
      this.cancelled = cancel;
   }

   @Override
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
