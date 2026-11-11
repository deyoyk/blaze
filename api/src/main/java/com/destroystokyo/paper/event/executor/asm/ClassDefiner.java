package com.destroystokyo.paper.event.executor.asm;

import org.jetbrains.annotations.NotNull;

public interface ClassDefiner {
   default boolean isBypassAccessChecks() {
      return false;
   }

   @NotNull
   Class<?> defineClass(@NotNull ClassLoader var1, @NotNull String var2, @NotNull byte[] var3);

   @NotNull
   static ClassDefiner getInstance() {
      return SafeClassDefiner.INSTANCE;
   }
}
