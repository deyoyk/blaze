package net.minecraft.server.v1_8_R3;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.Iterator;
import java.util.Map;

public class RegistryMaterials<K, V> extends RegistrySimple<K, V> implements Registry<V> {
   protected final RegistryID<V> a = new RegistryID<>();
   protected final Map<V, K> b = ((BiMap)this.c).inverse();

   public void a(int var1, K var2, V var3) {
      this.a.a((V)var3, var1);
      this.a((K)var2, (V)var3);
   }

   @Override
   protected Map<K, V> b() {
      return HashBiMap.create();
   }

   @Override
   public V get(K var1) {
      return super.get((K)var1);
   }

   public K c(V var1) {
      return this.b.get(var1);
   }

   @Override
   public boolean d(K var1) {
      return super.d((K)var1);
   }

   public int b(V var1) {
      return this.a.b((V)var1);
   }

   public V a(int var1) {
      return this.a.a(var1);
   }

   @Override
   public Iterator<V> iterator() {
      return this.a.iterator();
   }
}
