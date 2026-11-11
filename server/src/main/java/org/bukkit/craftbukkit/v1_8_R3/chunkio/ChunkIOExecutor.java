package org.bukkit.craftbukkit.v1_8_R3.chunkio;

import lol.deyo.blaze.configs.BlazeConfig;
import net.minecraft.server.v1_8_R3.ChunkProviderServer;
import net.minecraft.server.v1_8_R3.ChunkRegionLoader;
import org.bukkit.craftbukkit.v1_8_R3.util.AsynchronousExecutor;

public class ChunkIOExecutor {
   private static final int BASE_THREADS = (Integer)BlazeConfig.chunkLoadingThreads.getValue();
   private static final AsynchronousExecutor<QueuedChunk, net.minecraft.server.v1_8_R3.Chunk, Runnable, RuntimeException> instance = new AsynchronousExecutor<>(
      new ChunkIOProvider(), BASE_THREADS
   );

   public static net.minecraft.server.v1_8_R3.Chunk syncChunkLoad(
      net.minecraft.server.v1_8_R3.World world, ChunkRegionLoader loader, ChunkProviderServer provider, int x, int z
   ) {
      return instance.getSkipQueue(new QueuedChunk(x, z, loader, world, provider));
   }

   public static void queueChunkLoad(
      net.minecraft.server.v1_8_R3.World world, ChunkRegionLoader loader, ChunkProviderServer provider, int x, int z, Runnable runnable
   ) {
      instance.add(new QueuedChunk(x, z, loader, world, provider), runnable);
   }

   public static void dropQueuedChunkLoad(net.minecraft.server.v1_8_R3.World world, int x, int z, Runnable runnable) {
      instance.drop(new QueuedChunk(x, z, null, world, null), runnable);
   }

   public static void adjustPoolSize(int players) {
      instance.setActiveThreads(
         Math.max(
            (Integer)BlazeConfig.chunkLoadingThreads.getValue(),
            (int)Math.ceil((double)players / ((Integer)BlazeConfig.playersPerThread.getValue()).intValue())
         )
      );
   }

   public static void tick() {
      instance.finishActive();
   }
}
