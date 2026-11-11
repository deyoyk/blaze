package org.bukkit.plugin;

import com.destroystokyo.paper.event.executor.MethodHandleEventExecutor;
import com.destroystokyo.paper.event.executor.StaticMethodHandleEventExecutor;
import com.destroystokyo.paper.event.executor.asm.ASMEventExecutorGenerator;
import com.destroystokyo.paper.event.executor.asm.ClassDefiner;
import com.google.common.base.Preconditions;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public interface EventExecutor {
   ConcurrentMap<Method, Class<? extends EventExecutor>> eventExecutorMap = new ConcurrentHashMap<Method, Class<? extends EventExecutor>>() {
      @NotNull
      public Class<? extends EventExecutor> computeIfAbsent(
         @NotNull Method key, @NotNull Function<? super Method, ? extends Class<? extends EventExecutor>> mappingFunction
      ) {
         Class<? extends EventExecutor> executorClass = this.get(key);
         if (executorClass != null) {
            return executorClass;
         } else {
            synchronized (key) {
               executorClass = this.get(key);
               return executorClass != null ? executorClass : super.computeIfAbsent(key, mappingFunction);
            }
         }
      }
   };

   void execute(Listener var1, Event var2) throws EventException;

   @NotNull
   static EventExecutor create(@NotNull Method m, @NotNull Class<? extends Event> eventClass) {
      Preconditions.checkNotNull(m, "Null method");
      Preconditions.checkArgument(m.getParameterCount() != 0, "Incorrect number of arguments %s", new Object[]{m.getParameterCount()});
      Preconditions.checkArgument(
         m.getParameterTypes()[0] == eventClass, "First parameter %s doesn't match event class %s", new Object[]{m.getParameterTypes()[0], eventClass}
      );
      ClassDefiner definer = ClassDefiner.getInstance();
      if (Modifier.isStatic(m.getModifiers())) {
         return new StaticMethodHandleEventExecutor(eventClass, m);
      } else if (definer.isBypassAccessChecks() || Modifier.isPublic(m.getDeclaringClass().getModifiers()) && Modifier.isPublic(m.getModifiers())) {
         Class<? extends EventExecutor> executorClass = eventExecutorMap.computeIfAbsent(m, __ -> {
            String name = ASMEventExecutorGenerator.generateName();
            byte[] classData = ASMEventExecutorGenerator.generateEventExecutor(m, name);
            return definer.defineClass(m.getDeclaringClass().getClassLoader(), name, classData).asSubclass(EventExecutor.class);
         });

         try {
            EventExecutor asmExecutor = executorClass.newInstance();
            return (listener, event) -> {
               if (eventClass.isInstance(event)) {
                  asmExecutor.execute(listener, event);
               }
            };
         } catch (IllegalAccessException | InstantiationException var5) {
            throw new AssertionError("Unable to initialize generated event executor", var5);
         }
      } else {
         return new MethodHandleEventExecutor(eventClass, m);
      }
   }
}
