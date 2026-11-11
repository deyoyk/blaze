package org.bukkit.craftbukkit.v1_8_R3.scheduler;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import org.apache.commons.lang.UnhandledException;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitWorker;

class CraftAsyncTask extends CraftTask {
   private final LinkedList<BukkitWorker> workers = new LinkedList<>();
   private final Map<Integer, CraftTask> runners;

   CraftAsyncTask(Map<Integer, CraftTask> runners, Plugin plugin, Runnable task, int id, long delay) {
      super(plugin, task, id, delay);
      this.runners = runners;
   }

   @Override
   public boolean isSync() {
      return false;
   }

   @Override
   public void run() {
      final Thread thread = Thread.currentThread();
      synchronized (this.workers) {
         if (this.getPeriod() == -2) {
            return;
         }

         this.workers.add(new BukkitWorker() {
            @Override
            public Thread getThread() {
               return thread;
            }

            @Override
            public int getTaskId() {
               return CraftAsyncTask.this.getTaskId();
            }

            @Override
            public Plugin getOwner() {
               return CraftAsyncTask.this.getOwner();
            }
         });
      }

      Throwable thrown = null;

      try {
         super.run();
      } catch (Throwable var12) {
         thrown = var12;
         throw new UnhandledException(
            String.format("Plugin %s generated an exception while executing task %s", this.getOwner().getDescription().getFullName(), this.getTaskId()),
            thrown
         );
      } finally {
         synchronized (this.workers) {
            try {
               final Iterator<BukkitWorker> workers = this.workers.iterator();
               boolean removed = false;

               while (workers.hasNext()) {
                  if (workers.next().getThread() == thread) {
                     workers.remove();
                     removed = true;
                     break;
                  }
               }

               if (!removed) {
                  throw new IllegalStateException(
                     String.format("Unable to remove worker %s on task %s for %s", thread.getName(), this.getTaskId(), this.getOwner().getDescription().getFullName()),
                     thrown
                  );
               }
            } finally {
               if (this.getPeriod() < 0 && this.workers.isEmpty()) {
                  this.runners.remove(this.getTaskId());
               }
            }
         }
      }
   }
   LinkedList<BukkitWorker> getWorkers() {
      return this.workers;
   }

   @Override
   boolean cancel0() {
      synchronized (this.workers) {
         this.setPeriod(-2L);
         if (this.workers.isEmpty()) {
            this.runners.remove(this.getTaskId());
         }

         return true;
      }
   }
}
