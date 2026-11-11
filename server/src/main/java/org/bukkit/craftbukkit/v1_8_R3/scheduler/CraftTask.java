package org.bukkit.craftbukkit.v1_8_R3.scheduler;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class CraftTask implements BukkitTask, Runnable {
   private volatile CraftTask next = null;
   private volatile long period;
   private long nextRun;
   public final Runnable task;
   private final Plugin plugin;
   private final int id;

   CraftTask() {
      this(null, null, -1, -1L);
   }

   CraftTask(Runnable task) {
      this(null, task, -1, -1L);
   }

   CraftTask(Plugin plugin, Runnable task, int id, long period) {
      this.plugin = plugin;
      this.task = task;
      this.id = id;
      this.period = period;
   }

   @Override
   public final int getTaskId() {
      return this.id;
   }

   @Override
   public final Plugin getOwner() {
      return this.plugin;
   }

   @Override
   public boolean isSync() {
      return true;
   }

   @Override
   public void run() {
      this.task.run();
   }

   long getPeriod() {
      return this.period;
   }

   void setPeriod(long period) {
      this.period = period;
   }

   long getNextRun() {
      return this.nextRun;
   }

   void setNextRun(long nextRun) {
      this.nextRun = nextRun;
   }

   CraftTask getNext() {
      return this.next;
   }

   void setNext(CraftTask next) {
      this.next = next;
   }

   Class<? extends Runnable> getTaskClass() {
      return (Class<? extends Runnable>)this.task.getClass();
   }

   @Override
   public void cancel() {
      org.bukkit.Bukkit.getScheduler().cancelTask(this.id);
   }

   boolean cancel0() {
      this.setPeriod(-2L);
      return true;
   }
}
