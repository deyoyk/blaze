package net.minecraft.server.v1_8_R3;

import com.google.common.collect.ImmutableSet;
import java.util.Collection;

public class BlockStateBoolean extends BlockState<Boolean> {
   private final ImmutableSet<Boolean> a = ImmutableSet.of(Boolean.TRUE, Boolean.FALSE);

   protected BlockStateBoolean(String s) {
      super(s, Boolean.class);
   }

   public int getValueId(Boolean value) {
      return value ? 1 : 0;
   }

   public Boolean getByValueId(int id) {
      switch (id) {
         case 0:
            return false;
         case 1:
            return true;
         default:
            throw new IllegalArgumentException("Invalid id: " + id);
      }
   }

   @Override
   public Collection<Boolean> c() {
      return this.a;
   }

   public static BlockStateBoolean of(String s) {
      return new BlockStateBoolean(s);
   }

   public String a(Boolean obool) {
      return obool.toString();
   }
}
