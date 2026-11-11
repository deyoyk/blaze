package org.bukkit.event.world;

import org.bukkit.event.HandlerList;

public class ChunkPopulateEvent extends ChunkEvent {
   private static final HandlerList handlers = new HandlerList();

   public ChunkPopulateEvent(org.bukkit.Chunk chunk) {
      super(chunk);
   }

   @Override
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
