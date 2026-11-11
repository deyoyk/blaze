package lol.deyo.blaze.async.factory;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import org.jetbrains.annotations.NotNull;

public class NamePriorityThreadFactory implements ThreadFactory {
   private String name = "Spigot_Async-Thread";
   private final int priority;
   private int idCounter;
   private boolean isDaemon;

   public NamePriorityThreadFactory(int priority) {
      this.priority = Math.min(Math.max(priority, 1), 10);
   }

   public NamePriorityThreadFactory(int priority, String name) {
      this(priority);
      this.name = name;
   }

   public NamePriorityThreadFactory setDaemon(boolean daemon) {
      this.isDaemon = daemon;
      return this;
   }

   @Override
   public Thread newThread(@NotNull Runnable runnable) {
      Thread thread = Executors.defaultThreadFactory().newThread(runnable);
      thread.setPriority(this.priority);
      thread.setName(this.name + "-" + this.idCounter++);
      thread.setDaemon(this.isDaemon);
      return thread;
   }
}
