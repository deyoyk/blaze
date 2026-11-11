package org.bukkit.craftbukkit.v1_8_R3.util;

import java.util.concurrent.ExecutionException;

public abstract class Waitable<T> implements Runnable {
   Throwable t = null;
   T value = (T)null;
   Waitable.Status status = Waitable.Status.WAITING;

   @Override
   public final void run() {
      synchronized (this) {
         if (this.status != Waitable.Status.WAITING) {
            throw new IllegalStateException("Invalid state " + this.status);
         }

         this.status = Waitable.Status.RUNNING;
      }

      try {
         this.value = this.evaluate();
      } catch (Throwable var11) {
         this.t = var11;
      } finally {
         synchronized (this) {
            this.status = Waitable.Status.FINISHED;
            this.notifyAll();
         }
      }
   }

   protected abstract T evaluate();

   public synchronized T get() throws InterruptedException, ExecutionException {
      while (this.status != Waitable.Status.FINISHED) {
         this.wait();
      }

      if (this.t != null) {
         throw new ExecutionException(this.t);
      } else {
         return this.value;
      }
   }

   private static enum Status {
      WAITING,
      RUNNING,
      FINISHED;
   }
}
