package com.destroystokyo.paper.event.executor;

import com.destroystokyo.paper.util.SneakyThrow;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.jetbrains.annotations.NotNull;

public class MethodHandleEventExecutor implements EventExecutor {
   private final Class<? extends Event> eventClass;
   private final MethodHandle handle;

   public MethodHandleEventExecutor(@NotNull Class<? extends Event> eventClass, @NotNull MethodHandle handle) {
      this.eventClass = eventClass;
      this.handle = handle;
   }

   public MethodHandleEventExecutor(@NotNull Class<? extends Event> eventClass, @NotNull Method m) {
      this.eventClass = eventClass;

      try {
         m.setAccessible(true);
         this.handle = MethodHandles.lookup().unreflect(m);
      } catch (IllegalAccessException var4) {
         throw new AssertionError("Unable to set accessible", var4);
      }
   }

   public void execute(@NotNull Listener listener, @NotNull Event event) throws EventException {
      if (this.eventClass.isInstance(event)) {
         try {
            this.handle.invoke((Listener)listener, (Event)event);
         } catch (Throwable var4) {
            SneakyThrow.sneaky(var4);
         }
      }
   }
}
