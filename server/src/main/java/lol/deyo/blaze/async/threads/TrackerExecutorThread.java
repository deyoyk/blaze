package lol.deyo.blaze.async.threads;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lol.deyo.blaze.configs.BlazeConfig;
import lol.deyo.blaze.optimizations.ObjectMapList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import net.minecraft.server.v1_8_R3.EntityTrackerEntry;
import net.minecraft.server.v1_8_R3.MinecraftServer;

public class TrackerExecutorThread {
   private final int trackerThreads;
   private final ExecutorService trackerExecutor;

   public TrackerExecutorThread() {
      ThreadFactory trackerFactory = new ThreadFactoryBuilder().setNameFormat("entity-tracker-%d").build();
      this.trackerThreads = 4;
      this.trackerExecutor = Executors.newFixedThreadPool(this.trackerThreads - 1, trackerFactory);
   }

   public void updatePlayers(ObjectMapList<EntityTrackerEntry> c) {
      boolean parallelizeUpdate = false;
      if (BlazeConfig.dynamicParalellisedTracking.getValue()) {
         for (EntityTrackerEntry entityTrackerEntry : c) {
            if (entityTrackerEntry.trackedPlayers.size() >= BlazeConfig.playersNeededParalellisedTracking.getValue()) {
               parallelizeUpdate = true;
               break;
            }
         }
      }

      if (!parallelizeUpdate) {
         for (EntityTrackerEntry entityTrackerEntryx : c) {
            entityTrackerEntryx.update(3);
         }
      } else {
         int offset = 0;
         CountDownLatch latch = new CountDownLatch(this.trackerThreads);

         for (int i = 1; i <= this.trackerThreads; i++) {
            int localOffset = offset++;
            int finalTrackerThread = i;
            Runnable runnable = () -> {
               int i1 = localOffset;

               while (i1 < c.size()) {
                  c.get(i1).update(finalTrackerThread - 1);
                  i1 += this.trackerThreads;
               }

               latch.countDown();
            };
            if (i < this.trackerThreads) {
               this.trackerExecutor.execute(runnable);
            } else {
               runnable.run();
            }
         }

         try {
            latch.await();
         } catch (Exception var9) {
            MinecraftServer.LOGGER.error(var9);
         }
      }
   }
}
