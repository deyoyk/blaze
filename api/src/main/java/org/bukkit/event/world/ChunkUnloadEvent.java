package org.bukkit.event.world;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class ChunkUnloadEvent extends ChunkEvent implements Cancellable {
   private static final HandlerList handlers = new HandlerList();
   private boolean cancel = false;

   public ChunkUnloadEvent(org.bukkit.Chunk chunk) {
      super(chunk);
   }

   @Override
   public boolean isCancelled() {
      return this.cancel;
   }

   @Override
   public void setCancelled(boolean cancel) {
      this.cancel = cancel;
   }

   @Override
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
