package net.minecraft.server.v1_8_R3;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import org.apache.logging.log4j.Logger;

public class SystemUtils {
   public static <V> V a(FutureTask<V> var0, Logger var1) {
      try {
         var0.run();
         return (V)var0.get();
      } catch (ExecutionException var3) {
         var1.fatal("Error executing task", var3);
      } catch (InterruptedException var4) {
         var1.fatal("Error executing task", var4);
      }

      return null;
   }
}
