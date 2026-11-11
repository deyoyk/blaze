package lol.deyo.blaze.async;

import lol.deyo.blaze.async.threads.AsyncExecutorThread;
import lol.deyo.blaze.async.threads.TrackerExecutorThread;
import lol.deyo.blaze.async.worker.TaskQueueWorker;
import lol.deyo.blaze.configs.BlazeConfig;
import lol.deyo.blaze.pathsearch.PathSearchThrottlerThread;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class ThreadingManager {
   private final AsyncExecutorThread asyncExecutorThread = new AsyncExecutorThread();
   private final TrackerExecutorThread trackerExecutorThread = new TrackerExecutorThread();
   private final PathSearchThrottlerThread pathSearchExecutorThread = new PathSearchThrottlerThread(BlazeConfig.pathSearchThreads.getValue());

   public void shutdown() {
      this.pathSearchExecutorThread.shutdown();
      TaskQueueWorker files = this.asyncExecutorThread.getFilesWorker();
      ExecutorService service = this.asyncExecutorThread.getExecutor();

      while (files.isActive() && !service.isTerminated()) {
         try {
            service.awaitTermination(10L, TimeUnit.SECONDS);
         } catch (InterruptedException var5) {
         }
      }

      if (!service.isTerminated()) {
         service.shutdownNow();

         try {
            service.awaitTermination(10L, TimeUnit.SECONDS);
         } catch (InterruptedException var4) {
            var4.printStackTrace();
         }
      }
   }

   public AsyncExecutorThread getAsyncExecutor() {
      return this.asyncExecutorThread;
   }

   public TrackerExecutorThread getTrackerExecutor() {
      return this.trackerExecutorThread;
   }

   public PathSearchThrottlerThread getPathSearchExecutor() {
      return this.pathSearchExecutorThread;
   }
}
