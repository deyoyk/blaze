package com.destroystokyo.paper.event.executor.asm;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;
import org.bukkit.plugin.EventExecutor;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

public class ASMEventExecutorGenerator {
   public static AtomicInteger NEXT_ID = new AtomicInteger(1);

   @NotNull
   public static byte[] generateEventExecutor(@NotNull Method m, @NotNull String name) {
      ClassWriter writer = new ClassWriter(3);
      writer.visit(52, 1, name.replace('.', '/'), null, Type.getInternalName(Object.class), new String[]{Type.getInternalName(EventExecutor.class)});
      GeneratorAdapter methodGenerator = new GeneratorAdapter(writer.visitMethod(1, "<init>", "()V", null, null), 1, "<init>", "()V");
      methodGenerator.loadThis();
      methodGenerator.visitMethodInsn(183, Type.getInternalName(Object.class), "<init>", "()V", false);
      methodGenerator.returnValue();
      methodGenerator.endMethod();
      methodGenerator = new GeneratorAdapter(
         writer.visitMethod(1, "execute", "(Lorg/bukkit/event/Listener;Lorg/bukkit/event/Event;)V", null, null),
         1,
         "execute",
         "(Lorg/bukkit/event/Listener;Lorg/bukkit/event/Listener;)V"
      );
      methodGenerator.loadArg(0);
      methodGenerator.checkCast(Type.getType(m.getDeclaringClass()));
      methodGenerator.loadArg(1);
      methodGenerator.checkCast(Type.getType(m.getParameterTypes()[0]));
      methodGenerator.visitMethodInsn(
         m.getDeclaringClass().isInterface() ? 185 : 182,
         Type.getInternalName(m.getDeclaringClass()),
         m.getName(),
         Type.getMethodDescriptor(m),
         m.getDeclaringClass().isInterface()
      );
      if (m.getReturnType() != void.class) {
         methodGenerator.pop();
      }

      methodGenerator.returnValue();
      methodGenerator.endMethod();
      writer.visitEnd();
      return writer.toByteArray();
   }

   @NotNull
   public static String generateName() {
      int id = NEXT_ID.getAndIncrement();
      return "com.destroystokyo.paper.event.executor.asm.generated.GeneratedEventExecutor" + id;
   }
}
