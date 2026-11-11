package net.minecraft.server.v1_8_R3;

public class RegistryDefault<K, V> extends RegistrySimple<K, V> {
   private final V a;

   public RegistryDefault(V var1) {
      this.a = (V)var1;
   }

   @Override
   public V get(K var1) {
      Object var2 = super.get((K)var1);
      return (V)(var2 == null ? this.a : var2);
   }
}
