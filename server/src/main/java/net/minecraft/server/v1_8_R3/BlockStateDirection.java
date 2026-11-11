package net.minecraft.server.v1_8_R3;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import java.util.Collection;

public class BlockStateDirection extends BlockStateEnum<EnumDirection> {
   protected BlockStateDirection(String var1, Collection<EnumDirection> var2) {
      super(var1, EnumDirection.class, var2);
   }

   public static BlockStateDirection of(String var0) {
      return of(var0, Predicates.alwaysTrue());
   }

   public static BlockStateDirection of(String var0, Predicate<EnumDirection> var1) {
      return a(var0, Collections2.filter(Lists.newArrayList(EnumDirection.values()), var1));
   }

   public static BlockStateDirection a(String var0, Collection<EnumDirection> var1) {
      return new BlockStateDirection(var0, var1);
   }
}
