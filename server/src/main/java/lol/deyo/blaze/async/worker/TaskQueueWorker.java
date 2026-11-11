package lol.deyo.blaze.async.worker;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import org.apache.logging.log4j.LogManager;

public class TaskQueueWorker implements Runnable {
   private final ConcurrentLinkedDeque<Runnable> taskQueue = new ConcurrentLinkedDeque<>();
   private final ExecutorService service;
   private volatile boolean isActive = false;

   public TaskQueueWorker(ExecutorService service) {
      this.service = service;
   }

   @Override
   public void run() {
      Runnable task;
      while (this.isActive = (task = this.taskQueue.pollFirst()) != null) {
         try {
            task.run();
         } catch (Exception var3) {
            LogManager.getLogger().error("Thread {} encountered an exception: {}", new Object[]{Thread.currentThread().getName(), var3.getMessage(), var3});
         }
      }
   }

   public void queueTask(Runnable runnable) {
      if (this.service.isTerminated()) {
         runnable.run();
      } else {
         this.taskQueue.addLast(runnable);
         if (!this.isActive) {
            this.isActive = true;
            this.service.execute(this);
         }
      }
   }

   public boolean isActive() {
      if (!this.isActive && !this.taskQueue.isEmpty()) {
         this.isActive = true;
         this.service.execute(this);
      }

      return this.isActive;
   }
}
