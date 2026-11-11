package net.techcable.tacospigot;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.server.v1_8_R3.IBlockState;

public class BlockStateRegistry {
   private static final ConcurrentMap<IBlockState<?>, Integer> idsByObj = new ConcurrentHashMap<>();
   private static volatile IBlockState<?>[] byId = new IBlockState[0];
   private static final AtomicInteger nextId = new AtomicInteger();

   private BlockStateRegistry() {
   }

   public static int getId(IBlockState<?> s) {
      return idsByObj.computeIfAbsent(s, state -> {
         int id = nextId.getAndIncrement();
         synchronized (BlockStateRegistry.class) {
            if (id >= byId.length) {
               byId = Arrays.copyOf(byId, id + 1);
            }

            byId[id] = (IBlockState<?>)state;
         }

         return id;
      });
   }

   public static IBlockState<?> getById(int id) {
      if (id < 0) {
         throw new IllegalArgumentException("Negative id: " + id);
      } else {
         return id < byId.length ? byId[id] : null;
      }
   }
}
