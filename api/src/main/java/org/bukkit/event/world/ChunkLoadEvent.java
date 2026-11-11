package org.bukkit.event.world;

import org.bukkit.event.HandlerList;

public class ChunkLoadEvent extends ChunkEvent {
   private static final HandlerList handlers = new HandlerList();
   private final boolean newChunk;

   public ChunkLoadEvent(org.bukkit.Chunk chunk, boolean newChunk) {
      super(chunk);
      this.newChunk = newChunk;
   }

   public boolean isNewChunk() {
      return this.newChunk;
   }

   @Override
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
