package org.bukkit.craftbukkit.v1_8_R3.scheduler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import org.apache.commons.lang.Validate;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitWorker;
import org.github.paperspigot.event.ServerExceptionEvent;
import org.github.paperspigot.exception.ServerSchedulerException;

public class CraftScheduler implements BukkitScheduler {
   private final AtomicInteger ids = new AtomicInteger(1);
   private volatile CraftTask head = new CraftTask();
   private final AtomicReference<CraftTask> tail = new AtomicReference<>(this.head);
   final PriorityQueue<CraftTask> pending = new PriorityQueue<>(10, (o1, o2) -> (int)(o1.getNextRun() - o2.getNextRun()));
   private final List<CraftTask> temp = new ArrayList<>();
   final ConcurrentHashMap<Integer, CraftTask> runners = new ConcurrentHashMap<>();
   private volatile CraftTask currentTask = null;
   volatile int currentTick = -1;
   private final CraftScheduler asyncScheduler;
   private final boolean isAsyncScheduler;

   public CraftScheduler() {
      this(false);
   }

   public CraftScheduler(boolean isAsync) {
      this.isAsyncScheduler = isAsync;
      if (isAsync) {
         this.asyncScheduler = this;
      } else {
         this.asyncScheduler = new CraftAsyncScheduler();
      }
   }

   @Override
   public int scheduleSyncDelayedTask(Plugin plugin, Runnable task) {
      return this.scheduleSyncDelayedTask(plugin, task, 0L);
   }

   @Override
   public BukkitTask runTask(Plugin plugin, Runnable runnable) {
      return this.runTaskLater(plugin, runnable, 0L);
   }

   @Deprecated
   @Override
   public int scheduleAsyncDelayedTask(Plugin plugin, Runnable task) {
      return this.scheduleAsyncDelayedTask(plugin, task, 0L);
   }

   @Override
   public BukkitTask runTaskAsynchronously(Plugin plugin, Runnable runnable) {
      return this.runTaskLaterAsynchronously(plugin, runnable, 0L);
   }

   @Override
   public int scheduleSyncDelayedTask(Plugin plugin, Runnable task, long delay) {
      return this.scheduleSyncRepeatingTask(plugin, task, delay, -1L);
   }

   @Override
   public BukkitTask runTaskLater(Plugin plugin, Runnable runnable, long delay) {
      return this.runTaskTimer(plugin, runnable, delay, -1L);
   }

   @Deprecated
   @Override
   public int scheduleAsyncDelayedTask(Plugin plugin, Runnable task, long delay) {
      return this.scheduleAsyncRepeatingTask(plugin, task, delay, -1L);
   }

   @Override
   public BukkitTask runTaskLaterAsynchronously(Plugin plugin, Runnable runnable, long delay) {
      return this.runTaskTimerAsynchronously(plugin, runnable, delay, -1L);
   }

   @Override
   public int scheduleSyncRepeatingTask(Plugin plugin, Runnable runnable, long delay, long period) {
      return this.runTaskTimer(plugin, runnable, delay, period).getTaskId();
   }

   @Override
   public BukkitTask runTaskTimer(Plugin plugin, Runnable runnable, long delay, long period) {
      validate(plugin, runnable);
      if (delay < 0L) {
         delay = 0L;
      }

      if (period == 0L) {
         period = 1L;
      } else if (period < -1L) {
         period = -1L;
      }

      return this.handle(new CraftTask(plugin, runnable, this.nextId(), period), delay);
   }

   @Deprecated
   @Override
   public int scheduleAsyncRepeatingTask(Plugin plugin, Runnable runnable, long delay, long period) {
      return this.runTaskTimerAsynchronously(plugin, runnable, delay, period).getTaskId();
   }

   @Override
   public BukkitTask runTaskTimerAsynchronously(Plugin plugin, Runnable runnable, long delay, long period) {
      validate(plugin, runnable);
      if (delay < 0L) {
         delay = 0L;
      }

      if (period == 0L) {
         period = 1L;
      } else if (period < -1L) {
         period = -1L;
      }

      return this.handle(new CraftAsyncTask(this.asyncScheduler.runners, plugin, runnable, this.nextId(), period), delay);
   }

   @Override
   public <T> Future<T> callSyncMethod(Plugin plugin, Callable<T> task) {
      validate(plugin, task);
      CraftFuture<T> future = new CraftFuture<>(task, plugin, this.nextId());
      this.handle(future, 0L);
      return future;
   }

   @Override
   public void cancelTask(final int taskId) {
      if (taskId > 0) {
         if (!this.isAsyncScheduler) {
            this.asyncScheduler.cancelTask(taskId);
         }

         CraftTask task = this.runners.get(taskId);
         if (task != null) {
            task.cancel0();
         }

         task = new CraftTask(new Runnable() {
            @Override
            public void run() {
               if (!this.check(CraftScheduler.this.temp)) {
                  this.check(CraftScheduler.this.pending);
               }
            }

            private boolean check(Iterable<CraftTask> collection) {
               Iterator<CraftTask> tasks = collection.iterator();

               while (tasks.hasNext()) {
                  CraftTask taskx = tasks.next();
                  if (taskx.getTaskId() == taskId) {
                     taskx.cancel0();
                     tasks.remove();
                     if (taskx.isSync()) {
                        CraftScheduler.this.runners.remove(taskId);
                     }

                     return true;
                  }
               }

               return false;
            }
         });
         this.handle(task, 0L);

         for (CraftTask taskPending = this.head.getNext(); taskPending != null; taskPending = taskPending.getNext()) {
            if (taskPending == task) {
               return;
            }

            if (taskPending.getTaskId() == taskId) {
               taskPending.cancel0();
            }
         }
      }
   }

   @Override
   public void cancelTasks(final Plugin plugin) {
      Validate.notNull(plugin, "Cannot cancel tasks of null plugin");
      if (!this.isAsyncScheduler) {
         this.asyncScheduler.cancelTasks(plugin);
      }

      CraftTask task = new CraftTask(new Runnable() {
         @Override
         public void run() {
            this.check(CraftScheduler.this.pending);
            this.check(CraftScheduler.this.temp);
         }

         void check(Iterable<CraftTask> collection) {
            Iterator<CraftTask> tasks = collection.iterator();

            while (tasks.hasNext()) {
               CraftTask taskx = tasks.next();
               if (taskx.getOwner().equals(plugin)) {
                  taskx.cancel0();
                  tasks.remove();
                  if (taskx.isSync()) {
                     CraftScheduler.this.runners.remove(taskx.getTaskId());
                  }
               }
            }
         }
      });
      this.handle(task, 0L);

      for (CraftTask taskPending = this.head.getNext(); taskPending != null; taskPending = taskPending.getNext()) {
         if (taskPending == task) {
            return;
         }

         if (taskPending.getTaskId() != -1 && taskPending.getOwner().equals(plugin)) {
            taskPending.cancel0();
         }
      }

      for (CraftTask runner : this.runners.values()) {
         if (runner.getOwner().equals(plugin)) {
            runner.cancel0();
         }
      }
   }

   @Override
   public void cancelAllTasks() {
      if (!this.isAsyncScheduler) {
         this.asyncScheduler.cancelAllTasks();
      }

      CraftTask task = new CraftTask(() -> {
         Iterator<CraftTask> it = this.runners.values().iterator();

         while (it.hasNext()) {
            CraftTask task1 = it.next();
            task1.cancel0();
            if (task1.isSync()) {
               it.remove();
            }
         }

         this.pending.clear();
         this.temp.clear();
      });
      this.handle(task, 0L);

      for (CraftTask taskPending = this.head.getNext(); taskPending != null && taskPending != task; taskPending = taskPending.getNext()) {
         taskPending.cancel0();
      }

      for (CraftTask runner : this.runners.values()) {
         runner.cancel0();
      }
   }

   @Override
   public boolean isCurrentlyRunning(int taskId) {
      if (!this.isAsyncScheduler && this.asyncScheduler.isCurrentlyRunning(taskId)) {
         return true;
      } else {
         CraftTask task = this.runners.get(taskId);
         if (task == null) {
            return false;
         } else if (task.isSync()) {
            return task == this.currentTask;
         } else {
            CraftAsyncTask asyncTask = (CraftAsyncTask)task;
            synchronized (asyncTask.getWorkers()) {
               return !asyncTask.getWorkers().isEmpty();
            }
         }
      }
   }

   @Override
   public boolean isQueued(int taskId) {
      if (taskId <= 0) {
         return false;
      } else if (!this.isAsyncScheduler && this.asyncScheduler.isQueued(taskId)) {
         return true;
      } else {
         for (CraftTask task = this.head.getNext(); task != null; task = task.getNext()) {
            if (task.getTaskId() == taskId) {
               if (task.getPeriod() >= -1L) {
                  return true;
               }

               return false;
            }
         }

         CraftTask taskx = this.runners.get(taskId);
         return taskx != null && taskx.getPeriod() >= -1L;
      }
   }

   @Override
   public List<BukkitWorker> getActiveWorkers() {
      if (!this.isAsyncScheduler) {
         return this.asyncScheduler.getActiveWorkers();
      } else {
         ArrayList<BukkitWorker> workers = new ArrayList<>();

         for (CraftTask taskObj : this.runners.values()) {
            if (!taskObj.isSync()) {
               CraftAsyncTask task = (CraftAsyncTask)taskObj;
               synchronized (task.getWorkers()) {
                  workers.addAll(task.getWorkers());
               }
            }
         }

         return workers;
      }
   }

   @Override
   public List<BukkitTask> getPendingTasks() {
      ArrayList<CraftTask> truePending = new ArrayList<>();

      for (CraftTask task = this.head.getNext(); task != null; task = task.getNext()) {
         if (task.getTaskId() != -1) {
            truePending.add(task);
         }
      }

      ArrayList<BukkitTask> pending = new ArrayList<>();

      for (CraftTask taskx : this.runners.values()) {
         if (taskx.getPeriod() >= -1L) {
            pending.add(taskx);
         }
      }

      for (CraftTask taskxx : truePending) {
         if (taskxx.getPeriod() >= -1L && !pending.contains(taskxx)) {
            pending.add(taskxx);
         }
      }

      if (!this.isAsyncScheduler) {
         pending.addAll(this.asyncScheduler.getPendingTasks());
      }

      return pending;
   }

   public void mainThreadHeartbeat(int currentTick) {
      if (!this.isAsyncScheduler) {
         this.asyncScheduler.mainThreadHeartbeat(currentTick);
      }

      this.currentTick = currentTick;
      List<CraftTask> temp = this.temp;
      this.parsePending();

      while (this.isReady(currentTick)) {
         CraftTask task = this.pending.remove();
         if (task.getPeriod() < -1L) {
            if (task.isSync()) {
               this.runners.remove(task.getTaskId(), task);
            }

            this.parsePending();
         } else {
            if (task.isSync()) {
               this.currentTask = task;

               try {
                  task.run();
               } catch (Throwable var9) {
                  String msg = String.format("Task #%s for %s generated an exception", task.getTaskId(), task.getOwner().getDescription().getFullName());
                  task.getOwner().getLogger().log(Level.WARNING, msg, var9);
                  task.getOwner().getServer().getPluginManager().callEvent(new ServerExceptionEvent(new ServerSchedulerException(msg, var9, task)));
               } finally {
                  this.currentTask = null;
               }

               this.parsePending();
            } else {
               task.getOwner().getLogger().log(Level.SEVERE, "Unexpected Async Task in the Sync Scheduler. Report this to Paper");
            }

            long period = task.getPeriod();
            if (period > 0L) {
               task.setNextRun(currentTick + period);
               temp.add(task);
            } else if (task.isSync()) {
               this.runners.remove(task.getTaskId());
            }
         }
      }

      this.pending.addAll(temp);
      temp.clear();
   }

   private void addTask(CraftTask task) {
      AtomicReference<CraftTask> tail = this.tail;
      CraftTask tailTask = tail.get();

      while (!tail.compareAndSet(tailTask, task)) {
         tailTask = tail.get();
      }

      tailTask.setNext(task);
   }

   protected CraftTask handle(CraftTask task, long delay) {
      if (!this.isAsyncScheduler && !task.isSync()) {
         this.asyncScheduler.handle(task, delay);
         return task;
      } else {
         task.setNextRun(this.currentTick + delay);
         this.addTask(task);
         return task;
      }
   }

   private static void validate(Plugin plugin, Object task) {
      Validate.notNull(plugin, "Plugin cannot be null");
      Validate.notNull(task, "Task cannot be null");
      if (!plugin.isEnabled()) {
         throw new IllegalPluginAccessException("Plugin attempted to register task while disabled");
      }
   }

   private int nextId() {
      return this.ids.incrementAndGet();
   }

   void parsePending() {
      CraftTask head = this.head;
      CraftTask task = head.getNext();

      CraftTask lastTask;
      for (lastTask = head; task != null; task = task.getNext()) {
         if (task.getTaskId() == -1) {
            task.run();
         } else if (task.getPeriod() >= -1L) {
            this.pending.add(task);
            this.runners.put(task.getTaskId(), task);
         }

         lastTask = task;
      }

      task = head;

      while (task != lastTask) {
         head = task.getNext();
         task.setNext(null);
         task = head;
      }

      this.head = lastTask;
   }

   private boolean isReady(int currentTick) {
      return !this.pending.isEmpty() && this.pending.peek().getNextRun() <= currentTick;
   }

   @Override
   public String toString() {
      return "";
   }

   @Deprecated
   @Override
   public int scheduleSyncDelayedTask(Plugin plugin, BukkitRunnable task, long delay) {
      return this.scheduleSyncDelayedTask(plugin, (Runnable)task, delay);
   }

   @Deprecated
   @Override
   public int scheduleSyncDelayedTask(Plugin plugin, BukkitRunnable task) {
      return this.scheduleSyncDelayedTask(plugin, (Runnable)task);
   }

   @Deprecated
   @Override
   public int scheduleSyncRepeatingTask(Plugin plugin, BukkitRunnable task, long delay, long period) {
      return this.scheduleSyncRepeatingTask(plugin, (Runnable)task, delay, period);
   }

   @Deprecated
   @Override
   public BukkitTask runTask(Plugin plugin, BukkitRunnable task) throws IllegalArgumentException {
      return this.runTask(plugin, (Runnable)task);
   }

   @Deprecated
   @Override
   public BukkitTask runTaskAsynchronously(Plugin plugin, BukkitRunnable task) throws IllegalArgumentException {
      return this.runTaskAsynchronously(plugin, (Runnable)task);
   }

   @Deprecated
   @Override
   public BukkitTask runTaskLater(Plugin plugin, BukkitRunnable task, long delay) throws IllegalArgumentException {
      return this.runTaskLater(plugin, (Runnable)task, delay);
   }

   @Deprecated
   @Override
   public BukkitTask runTaskLaterAsynchronously(Plugin plugin, BukkitRunnable task, long delay) throws IllegalArgumentException {
      return this.runTaskLaterAsynchronously(plugin, (Runnable)task, delay);
   }

   @Deprecated
   @Override
   public BukkitTask runTaskTimer(Plugin plugin, BukkitRunnable task, long delay, long period) throws IllegalArgumentException {
      return this.runTaskTimer(plugin, (Runnable)task, delay, period);
   }

   @Deprecated
   @Override
   public BukkitTask runTaskTimerAsynchronously(Plugin plugin, BukkitRunnable task, long delay, long period) throws IllegalArgumentException {
      return this.runTaskTimerAsynchronously(plugin, (Runnable)task, delay, period);
   }
}
