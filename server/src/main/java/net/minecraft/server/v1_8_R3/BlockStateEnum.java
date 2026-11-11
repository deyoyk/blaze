package net.minecraft.server.v1_8_R3;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;

public class BlockStateEnum<T extends Enum<T> & INamable> extends BlockState<T> {
   private final ImmutableSet<T> a;
   private final Map<String, T> b = Maps.newHashMap();

   protected BlockStateEnum(String paramString, Class<T> paramClass, Collection<T> paramCollection) {
      super(paramString, paramClass);
      this.a = ImmutableSet.copyOf(paramCollection);

      for (Enum<T> enum_ : paramCollection) {
         String str = ((INamable)enum_).getName();
         if (this.b.containsKey(str)) {
            throw new IllegalArgumentException("Multiple values have the same name '" + str + "'");
         }

         this.b.put(str, (T)enum_);
      }
   }

   @Override
   public Collection<T> c() {
      return this.a;
   }

   public String a(T paramT) {
      return paramT.getName();
   }

   public int getValueId(T value) {
      return value.ordinal();
   }

   public T getByValueId(int id) {
      Enum[] values = this.b().getEnumConstants();
      if (id >= 0 && id < values.length) {
         return (T)values[id];
      } else {
         throw new IllegalArgumentException("Invalid id: " + id);
      }
   }

   public static <T extends Enum<T> & INamable> BlockStateEnum<T> of(String paramString, Class<T> paramClass) {
      return a(paramString, paramClass, Predicates.alwaysTrue());
   }

   public static <T extends Enum<T> & INamable> BlockStateEnum<T> a(String paramString, Class<T> paramClass, Predicate<T> paramPredicate) {
      return a(paramString, paramClass, Collections2.filter(Lists.newArrayList(paramClass.getEnumConstants()), paramPredicate));
   }

   public static <T extends Enum<T> & INamable> BlockStateEnum<T> of(String paramString, Class<T> paramClass, T... paramVarArgs) {
      return a(paramString, paramClass, Lists.newArrayList(paramVarArgs));
   }

   public static <T extends Enum<T> & INamable> BlockStateEnum<T> a(String paramString, Class<T> paramClass, Collection<T> paramCollection) {
      return new BlockStateEnum<>(paramString, paramClass, paramCollection);
   }
}
