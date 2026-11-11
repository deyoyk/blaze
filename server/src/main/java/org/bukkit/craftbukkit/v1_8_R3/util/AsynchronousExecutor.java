package org.bukkit.craftbukkit.v1_8_R3.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import org.apache.commons.lang.Validate;

public final class AsynchronousExecutor<P, T, C, E extends Throwable> {
   static final AtomicIntegerFieldUpdater STATE_FIELD = AtomicIntegerFieldUpdater.newUpdater(AsynchronousExecutor.Task.class, "state");
   final AsynchronousExecutor.CallBackProvider<P, T, C, E> provider;
   final Queue<AsynchronousExecutor<P, T, C, E>.Task> finished = new ConcurrentLinkedQueue<>();
   final Map<P, AsynchronousExecutor<P, T, C, E>.Task> tasks = new HashMap<>();
   final ThreadPoolExecutor pool;

   private static boolean set(AsynchronousExecutor.Task $this, int expected, int value) {
      return STATE_FIELD.compareAndSet($this, expected, value);
   }

   public AsynchronousExecutor(AsynchronousExecutor.CallBackProvider<P, T, C, E> provider, int coreSize) {
      Validate.notNull(provider, "Provider cannot be null");
      this.provider = provider;
      this.pool = new ThreadPoolExecutor(coreSize, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), provider);
   }

   public void add(P parameter, C callback) {
      AsynchronousExecutor<P, T, C, E>.Task task = this.tasks.get(parameter);
      if (task == null) {
         this.tasks.put(parameter, task = new AsynchronousExecutor.Task(parameter));
         this.pool.execute(task);
      }

      task.callbacks.add(callback);
   }

   public boolean drop(P parameter, C callback) throws IllegalStateException {
      AsynchronousExecutor<P, T, C, E>.Task task = this.tasks.get(parameter);
      if (task == null) {
         return true;
      } else if (!task.callbacks.remove(callback)) {
         throw new IllegalStateException("Unknown " + callback + " for " + parameter);
      } else {
         return task.callbacks.isEmpty() ? task.drop() : false;
      }
   }

   public T get(P parameter) throws E, IllegalStateException {
      AsynchronousExecutor<P, T, C, E>.Task task = this.tasks.get(parameter);
      if (task == null) {
         throw new IllegalStateException("Unknown " + parameter);
      } else {
         return (T)task.get();
      }
   }

   public T getSkipQueue(P parameter) throws E {
      return this.skipQueue(parameter);
   }

   public T getSkipQueue(P parameter, C callback) throws E {
      T object = this.skipQueue(parameter);
      this.provider.callStage3(parameter, object, callback);
      return object;
   }

   public T getSkipQueue(P parameter, C... callbacks) throws E {
      AsynchronousExecutor.CallBackProvider<P, T, C, E> provider = this.provider;
      T object = this.skipQueue(parameter);

      for (C callback : callbacks) {
         provider.callStage3(parameter, object, callback);
      }

      return object;
   }

   public T getSkipQueue(P parameter, Iterable<C> callbacks) throws E {
      AsynchronousExecutor.CallBackProvider<P, T, C, E> provider = this.provider;
      T object = this.skipQueue(parameter);

      for (C callback : callbacks) {
         provider.callStage3(parameter, object, callback);
      }

      return object;
   }

   private T skipQueue(P parameter) throws E {
      AsynchronousExecutor<P, T, C, E>.Task task = this.tasks.get(parameter);
      if (task != null) {
         return (T)task.get();
      } else {
         T object = this.provider.callStage1(parameter);
         this.provider.callStage2(parameter, object);
         return object;
      }
   }

   public void finishActive() throws E {
      Queue<AsynchronousExecutor<P, T, C, E>.Task> finished = this.finished;

      while (!finished.isEmpty()) {
         finished.poll().finish();
      }
   }

   public void setActiveThreads(int coreSize) {
      this.pool.setMaximumPoolSize(coreSize);
   }

   public interface CallBackProvider<P, T, C, E extends Throwable> extends ThreadFactory {
      T callStage1(P var1) throws E;

      void callStage2(P var1, T var2) throws E;

      void callStage3(P var1, T var2, C var3) throws E;
   }

   class Task implements Runnable {
      static final int PENDING = 0;
      static final int STAGE_1_ASYNC = 1;
      static final int STAGE_1_SYNC = 2;
      static final int STAGE_1_COMPLETE = 3;
      static final int FINISHED = 4;
      volatile int state = 0;
      final P parameter;
      T object;
      final List<C> callbacks = new LinkedList();
      E t = (E)null;

      Task(P parameter) {
         this.parameter = parameter;
      }

      @Override
      public void run() {
         if (this.initAsync()) {
            AsynchronousExecutor.this.finished.add(this);
         }
      }

      boolean initAsync() {
         if (AsynchronousExecutor.set(this, 0, 1)) {
            boolean ret = true;

            try {
               this.init();
            } finally {
               if (!AsynchronousExecutor.set(this, 1, 3)) {
                  synchronized (this) {
                     if (this.state != 2) {
                        this.notifyAll();
                     }

                     this.state = 3;
                  }

                  ret = false;
               }
            }

            return ret;
         } else {
            return false;
         }
      }

      void initSync() {
         if (AsynchronousExecutor.set(this, 0, 3)) {
            this.init();
         } else if (AsynchronousExecutor.set(this, 1, 2)) {
            synchronized (this) {
               if (AsynchronousExecutor.set(this, 2, 0)) {
                  while (this.state != 3) {
                     try {
                        this.wait();
                     } catch (InterruptedException var3) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Unable to handle interruption on " + this.parameter, var3);
                     }
                  }
               }
            }
         }
      }

      void init() {
         try {
            this.object = AsynchronousExecutor.this.provider.callStage1(this.parameter);
         } catch (Throwable var2) {
            this.t = (E)var2;
         }
      }

      T get() throws E {
         this.initSync();
         if (this.callbacks.isEmpty()) {
            this.callbacks.add((C)this);
         }

         this.finish();
         return this.object;
      }

      void finish() throws E {
         switch (this.state) {
            case 0:
            case 1:
            case 2:
            default:
               throw new IllegalStateException("Attempting to finish unprepared(" + this.state + ") task(" + this.parameter + ")");
            case 3:
               label59: {
                  try {
                     if (this.t != null) {
                        throw this.t;
                     }

                     if (!this.callbacks.isEmpty()) {
                        AsynchronousExecutor.CallBackProvider<P, T, C, E> provider = AsynchronousExecutor.this.provider;
                        P parameter = this.parameter;
                        T object = this.object;
                        provider.callStage2(parameter, object);

                        for (C callback : this.callbacks) {
                           if (callback != this) {
                              provider.callStage3(parameter, object, callback);
                           }
                        }
                        break label59;
                     }
                  } finally {
                     AsynchronousExecutor.this.tasks.remove(this.parameter);
                     this.state = 4;
                  }

                  return;
               }
            case 4:
         }
      }

      boolean drop() {
         if (AsynchronousExecutor.set(this, 0, 4)) {
            AsynchronousExecutor.this.tasks.remove(this.parameter);
            return true;
         } else {
            return false;
         }
      }
   }
}
