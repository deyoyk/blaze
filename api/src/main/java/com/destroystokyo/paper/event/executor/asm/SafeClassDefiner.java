package com.destroystokyo.paper.event.executor.asm;

import com.google.common.base.Preconditions;
import com.google.common.collect.MapMaker;
import java.util.concurrent.ConcurrentMap;
import org.jetbrains.annotations.NotNull;

public class SafeClassDefiner implements ClassDefiner {
   static final SafeClassDefiner INSTANCE = new SafeClassDefiner();
   private final ConcurrentMap<ClassLoader, SafeClassDefiner.GeneratedClassLoader> loaders = new MapMaker().weakKeys().makeMap();

   private SafeClassDefiner() {
   }

   @NotNull
   @Override
   public Class<?> defineClass(@NotNull ClassLoader parentLoader, @NotNull String name, @NotNull byte[] data) {
      SafeClassDefiner.GeneratedClassLoader loader = this.loaders.computeIfAbsent(parentLoader, SafeClassDefiner.GeneratedClassLoader::new);
      synchronized (loader.getClassLoadingLock(name)) {
         Preconditions.checkState(!loader.hasClass(name), "%s already defined", new Object[]{name});
         Class<?> c = loader.define(name, data);

         assert c.getName().equals(name);

         return c;
      }
   }

   private static class GeneratedClassLoader extends ClassLoader {
      protected GeneratedClassLoader(@NotNull ClassLoader parent) {
         super(parent);
      }

      private Class<?> define(@NotNull String name, byte[] data) {
         synchronized (this.getClassLoadingLock(name)) {
            assert !this.hasClass(name);

            Class<?> c = this.defineClass(name, data, 0, data.length);
            this.resolveClass(c);
            return c;
         }
      }

      @NotNull
      @Override
      public Object getClassLoadingLock(@NotNull String name) {
         return super.getClassLoadingLock(name);
      }

      public boolean hasClass(@NotNull String name) {
         synchronized (this.getClassLoadingLock(name)) {
            boolean var10000;
            try {
               Class.forName(name);
               var10000 = true;
            } catch (ClassNotFoundException var5) {
               return false;
            }

            return var10000;
         }
      }

      static {
         ClassLoader.registerAsParallelCapable();
      }
   }
}
