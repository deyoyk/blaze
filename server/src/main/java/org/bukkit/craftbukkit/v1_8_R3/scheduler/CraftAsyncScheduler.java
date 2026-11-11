package org.bukkit.craftbukkit.v1_8_R3.scheduler;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.bukkit.plugin.Plugin;
import org.github.paperspigot.ServerSchedulerReportingWrapper;

public class CraftAsyncScheduler extends CraftScheduler {
   private final ThreadPoolExecutor executor = new ThreadPoolExecutor(
      4, Integer.MAX_VALUE, 30L, TimeUnit.SECONDS, new SynchronousQueue<>(), new ThreadFactoryBuilder().setNameFormat("Craft Scheduler Thread - %1$d").build()
   );
   private final Executor management = Executors.newSingleThreadExecutor(
      new ThreadFactoryBuilder().setNameFormat("Craft Async Scheduler Management Thread").build()
   );
   private final List<CraftTask> temp = new ArrayList<>();

   CraftAsyncScheduler() {
      super(true);
      this.executor.allowCoreThreadTimeOut(true);
      this.executor.prestartAllCoreThreads();
   }

   @Override
   public void cancelTask(int taskId) {
      this.management.execute(() -> this.removeTask(taskId));
   }

   private synchronized void removeTask(int taskId) {
      this.parsePending();
      this.pending.removeIf(task -> {
         if (task.getTaskId() == taskId) {
            task.cancel0();
            return true;
         } else {
            return false;
         }
      });
   }

   @Override
   public void mainThreadHeartbeat(int currentTick) {
      this.currentTick = currentTick;
      this.management.execute(() -> this.runTasks(currentTick));
   }

   private synchronized void runTasks(int currentTick) {
      this.parsePending();

      for (; !this.pending.isEmpty() && this.pending.peek().getNextRun() <= currentTick; this.parsePending()) {
         CraftTask task = this.pending.remove();
         if (this.executeTask(task)) {
            long period = task.getPeriod();
            if (period > 0L) {
               task.setNextRun(currentTick + period);
               this.temp.add(task);
            }
         }
      }

      this.pending.addAll(this.temp);
      this.temp.clear();
   }

   private boolean executeTask(CraftTask task) {
      if (isValid(task)) {
         this.runners.put(task.getTaskId(), task);
         this.executor.execute(new ServerSchedulerReportingWrapper(task));
         return true;
      } else {
         return false;
      }
   }

   @Override
   public synchronized void cancelTasks(Plugin plugin) {
      this.parsePending();
      Iterator<CraftTask> iterator = this.pending.iterator();

      while (iterator.hasNext()) {
         CraftTask task = iterator.next();
         if (task.getTaskId() != -1 && (plugin == null || task.getOwner().equals(plugin))) {
            task.cancel0();
            iterator.remove();
         }
      }
   }

   static boolean isValid(CraftTask runningTask) {
      return runningTask.getPeriod() >= -1L;
   }
}
