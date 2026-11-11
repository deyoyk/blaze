package org.github.paperspigot.exception;

import com.google.common.base.Preconditions;
import org.bukkit.scheduler.BukkitTask;

public class ServerSchedulerException extends ServerPluginException {
   private final BukkitTask task;

   public ServerSchedulerException(String message, Throwable cause, BukkitTask task) {
      super(message, cause, task.getOwner());
      this.task = (BukkitTask)Preconditions.checkNotNull(task, "task");
   }

   public ServerSchedulerException(Throwable cause, BukkitTask task) {
      super(cause, task.getOwner());
      this.task = (BukkitTask)Preconditions.checkNotNull(task, "task");
   }

   protected ServerSchedulerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, BukkitTask task) {
      super(message, cause, enableSuppression, writableStackTrace, task.getOwner());
      this.task = (BukkitTask)Preconditions.checkNotNull(task, "task");
   }

   public BukkitTask getTask() {
      return this.task;
   }
}
