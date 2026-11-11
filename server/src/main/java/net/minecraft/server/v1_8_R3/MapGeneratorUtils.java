package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public class MapGeneratorUtils {
   public static <K, V> Map<K, V> b(Iterable<K> var0, Iterable<V> var1) {
      return a(var0, var1, Maps.newLinkedHashMap());
   }

   public static <K, V> Map<K, V> a(Iterable<K> var0, Iterable<V> var1, Map<K, V> var2) {
      Iterator var3 = var1.iterator();

      for (K var5 : var0) {
         var2.put(var5, (V)var3.next());
      }

      if (var3.hasNext()) {
         throw new NoSuchElementException();
      } else {
         return var2;
      }
   }
}
