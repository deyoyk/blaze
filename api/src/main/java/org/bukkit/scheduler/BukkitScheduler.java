package org.bukkit.scheduler;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import org.bukkit.plugin.Plugin;

public interface BukkitScheduler {
   int scheduleSyncDelayedTask(Plugin var1, Runnable var2, long var3);

   @Deprecated
   int scheduleSyncDelayedTask(Plugin var1, BukkitRunnable var2, long var3);

   int scheduleSyncDelayedTask(Plugin var1, Runnable var2);

   @Deprecated
   int scheduleSyncDelayedTask(Plugin var1, BukkitRunnable var2);

   int scheduleSyncRepeatingTask(Plugin var1, Runnable var2, long var3, long var5);

   @Deprecated
   int scheduleSyncRepeatingTask(Plugin var1, BukkitRunnable var2, long var3, long var5);

   @Deprecated
   int scheduleAsyncDelayedTask(Plugin var1, Runnable var2, long var3);

   @Deprecated
   int scheduleAsyncDelayedTask(Plugin var1, Runnable var2);

   @Deprecated
   int scheduleAsyncRepeatingTask(Plugin var1, Runnable var2, long var3, long var5);

   <T> Future<T> callSyncMethod(Plugin var1, Callable<T> var2);

   void cancelTask(int var1);

   void cancelTasks(Plugin var1);

   void cancelAllTasks();

   boolean isCurrentlyRunning(int var1);

   boolean isQueued(int var1);

   List<BukkitWorker> getActiveWorkers();

   List<BukkitTask> getPendingTasks();

   BukkitTask runTask(Plugin var1, Runnable var2) throws IllegalArgumentException;

   @Deprecated
   BukkitTask runTask(Plugin var1, BukkitRunnable var2) throws IllegalArgumentException;

   BukkitTask runTaskAsynchronously(Plugin var1, Runnable var2) throws IllegalArgumentException;

   @Deprecated
   BukkitTask runTaskAsynchronously(Plugin var1, BukkitRunnable var2) throws IllegalArgumentException;

   BukkitTask runTaskLater(Plugin var1, Runnable var2, long var3) throws IllegalArgumentException;

   @Deprecated
   BukkitTask runTaskLater(Plugin var1, BukkitRunnable var2, long var3) throws IllegalArgumentException;

   BukkitTask runTaskLaterAsynchronously(Plugin var1, Runnable var2, long var3) throws IllegalArgumentException;

   @Deprecated
   BukkitTask runTaskLaterAsynchronously(Plugin var1, BukkitRunnable var2, long var3) throws IllegalArgumentException;

   BukkitTask runTaskTimer(Plugin var1, Runnable var2, long var3, long var5) throws IllegalArgumentException;

   @Deprecated
   BukkitTask runTaskTimer(Plugin var1, BukkitRunnable var2, long var3, long var5) throws IllegalArgumentException;

   BukkitTask runTaskTimerAsynchronously(Plugin var1, Runnable var2, long var3, long var5) throws IllegalArgumentException;

   @Deprecated
   BukkitTask runTaskTimerAsynchronously(Plugin var1, BukkitRunnable var2, long var3, long var5) throws IllegalArgumentException;
}
