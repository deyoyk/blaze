package org.bukkit.craftbukkit.v1_8_R3.chunkio;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.server.v1_8_R3.ChunkProviderServer;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.craftbukkit.v1_8_R3.util.AsynchronousExecutor;
import org.bukkit.craftbukkit.v1_8_R3.util.LongHash;
import org.bukkit.event.world.ChunkLoadEvent;
import org.jetbrains.annotations.NotNull;

class ChunkIOProvider implements AsynchronousExecutor.CallBackProvider<QueuedChunk, net.minecraft.server.v1_8_R3.Chunk, Runnable, RuntimeException> {
   private final AtomicInteger threadNumber = new AtomicInteger(1);

   public net.minecraft.server.v1_8_R3.Chunk callStage1(QueuedChunk queuedChunk) throws RuntimeException {
      try {
         Object[] data = queuedChunk.loader.loadChunk(queuedChunk.world, queuedChunk.x, queuedChunk.z);
         if (data != null) {
            queuedChunk.compound = (NBTTagCompound)data[1];
            return (net.minecraft.server.v1_8_R3.Chunk)data[0];
         } else {
            return null;
         }
      } catch (IOException var3) {
         throw new RuntimeException(var3);
      }
   }

   public void callStage2(QueuedChunk queuedChunk, net.minecraft.server.v1_8_R3.Chunk chunk) throws RuntimeException {
      ChunkProviderServer cps = queuedChunk.provider;
      if (chunk == null) {
         cps.originalGetChunkAt(queuedChunk.x, queuedChunk.z);
      } else {
         queuedChunk.loader.loadEntities(chunk, queuedChunk.compound.getCompound("Level"), queuedChunk.world);
         chunk.setLastSaved(cps.world.getTime());
         cps.chunks.put(LongHash.toLong(queuedChunk.x, queuedChunk.z), chunk);
         chunk.addEntities();
         if (cps.chunkProvider != null) {
            cps.chunkProvider.recreateStructures(chunk, queuedChunk.x, queuedChunk.z);
         }

         org.bukkit.Server server = cps.world.getServer();
         if (server != null) {
            server.getPluginManager().callEvent(new ChunkLoadEvent(chunk.bukkitChunk, false));
         }

         for (int x = -2; x < 3; x++) {
            for (int z = -2; z < 3; z++) {
               if (x != 0 || z != 0) {
                  net.minecraft.server.v1_8_R3.Chunk neighbor = cps.getChunkIfLoaded(chunk.locX + x, chunk.locZ + z);
                  if (neighbor != null) {
                     neighbor.setNeighborLoaded(-x, -z);
                     chunk.setNeighborLoaded(x, z);
                  }
               }
            }
         }

         chunk.loadNearby(cps, cps, queuedChunk.x, queuedChunk.z);
      }
   }

   public void callStage3(QueuedChunk queuedChunk, net.minecraft.server.v1_8_R3.Chunk chunk, Runnable runnable) throws RuntimeException {
      runnable.run();
   }

   @Override
   public Thread newThread(@NotNull Runnable runnable) {
      Thread thread = new Thread(runnable, "Chunk I/O Executor Thread-" + this.threadNumber.getAndIncrement());
      thread.setDaemon(true);
      return thread;
   }
}
