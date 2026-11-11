package org.bukkit.event.world;

public abstract class ChunkEvent extends WorldEvent {
   protected org.bukkit.Chunk chunk;

   protected ChunkEvent(org.bukkit.Chunk chunk) {
      super(chunk.getWorld());
      this.chunk = chunk;
   }

   public org.bukkit.Chunk getChunk() {
      return this.chunk;
   }
}
