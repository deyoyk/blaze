package net.minecraft.server.v1_8_R3;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.Iterator;
import org.jetbrains.annotations.NotNull;

public class RegistryID<T> implements Registry<T> {
   private final IdentityHashMap<T, Integer> a = new IdentityHashMap<>(0);
   private Object[] b = new Object[16];

   public void a(T var1, int var2) {
      this.a.put(var1, var2);
      if (var2 >= this.b.length) {
         this.b = Arrays.copyOf(this.b, Math.max(this.b.length * 2, var2 + 1));
      }

      this.b[var2] = var1;
   }

   public int b(T var1) {
      Integer var2 = this.a.get(var1);
      return var2 == null ? -1 : var2;
   }

   public T a(int var1) {
      return var1 >= 0 && var1 < this.b.length ? (T)this.b[var1] : null;
   }

   @NotNull
   @Override
   public Iterator<T> iterator() {
      return Iterators.filter(Iterators.forArray((T[])this.b), Predicates.notNull());
   }
}
