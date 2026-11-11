package lol.deyo.blaze.async.threads;

import lol.deyo.blaze.async.factory.NamePriorityThreadFactory;
import lol.deyo.blaze.async.worker.TaskQueueWorker;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AsyncExecutorThread {
   private final ExecutorService asyncExecutor;
   private final TaskQueueWorker files;
   private final TaskQueueWorker heads;
   private final TaskQueueWorker chat;

   public AsyncExecutorThread() {
      int priority = Thread.currentThread().getPriority() - 1;
      this.asyncExecutor = Executors.newCachedThreadPool(new NamePriorityThreadFactory(priority, "Spigot_Async-Executor").setDaemon(true));
      this.files = new TaskQueueWorker(this.asyncExecutor);
      this.heads = new TaskQueueWorker(this.asyncExecutor);
      this.chat = new TaskQueueWorker(this.asyncExecutor);
   }

   public void queueFileTask(Runnable runnable) {
      this.files.queueTask(runnable);
   }

   public void queueHeadLoading(Runnable runnable) {
      this.heads.queueTask(runnable);
   }

   public void queueChat(Runnable runnable) {
      this.chat.queueTask(runnable);
   }

   public TaskQueueWorker getFilesWorker() {
      return this.files;
   }

   public void execute(Runnable runnable) {
      this.asyncExecutor.execute(runnable);
   }

   public Future<?> submit(Runnable runnable) {
      return this.asyncExecutor.submit(runnable);
   }

   public ExecutorService getExecutor() {
      return this.asyncExecutor;
   }
}
