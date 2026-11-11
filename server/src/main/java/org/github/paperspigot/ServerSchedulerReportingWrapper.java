package org.github.paperspigot;

import com.google.common.base.Preconditions;
import org.bukkit.craftbukkit.v1_8_R3.scheduler.CraftTask;
import org.github.paperspigot.event.ServerExceptionEvent;
import org.github.paperspigot.exception.ServerSchedulerException;

public class ServerSchedulerReportingWrapper implements Runnable {
   private final CraftTask internalTask;

   public ServerSchedulerReportingWrapper(CraftTask internalTask) {
      this.internalTask = (CraftTask)Preconditions.checkNotNull(internalTask, "internalTask");
   }

   @Override
   public void run() {
      try {
         this.internalTask.run();
      } catch (RuntimeException var2) {
         this.internalTask.getOwner().getServer().getPluginManager().callEvent(new ServerExceptionEvent(new ServerSchedulerException(var2, this.internalTask)));
         throw var2;
      } catch (Throwable var3) {
         this.internalTask.getOwner().getServer().getPluginManager().callEvent(new ServerExceptionEvent(new ServerSchedulerException(var3, this.internalTask)));
      }
   }

   public CraftTask getInternalTask() {
      return this.internalTask;
   }
}
