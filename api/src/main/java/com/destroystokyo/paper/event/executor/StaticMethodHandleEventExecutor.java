package com.destroystokyo.paper.event.executor;

import com.destroystokyo.paper.util.SneakyThrow;
import com.google.common.base.Preconditions;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.jetbrains.annotations.NotNull;

public class StaticMethodHandleEventExecutor implements EventExecutor {
   private final Class<? extends Event> eventClass;
   private final MethodHandle handle;

   public StaticMethodHandleEventExecutor(@NotNull Class<? extends Event> eventClass, @NotNull Method m) {
      Preconditions.checkArgument(Modifier.isStatic(m.getModifiers()), "Not a static method: %s", new Object[]{m});
      Preconditions.checkArgument(true, "eventClass is null");
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
            this.handle.invoke((Event)event);
         } catch (Throwable var4) {
            SneakyThrow.sneaky(var4);
         }
      }
   }
}
