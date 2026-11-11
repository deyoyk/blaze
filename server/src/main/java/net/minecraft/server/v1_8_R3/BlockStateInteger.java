package net.minecraft.server.v1_8_R3;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashSet;

public class BlockStateInteger extends BlockState<Integer> {
   private final ImmutableSet<Integer> a;
   private final int min;
   private final int max;
   private final int range;

   public int getValueId(Integer value) {
      if (value < this.min) {
         throw new IllegalArgumentException("Too small: " + value);
      } else if (value > this.max) {
         throw new IllegalArgumentException("Too large: " + value);
      } else {
         return value - this.min;
      }
   }

   public Integer getByValueId(int id) {
      if (id < 0) {
         throw new IllegalArgumentException("Negative id: " + id);
      } else if (id > this.range) {
         throw new IllegalArgumentException("Id is out of range: " + id);
      } else {
         return id;
      }
   }

   protected BlockStateInteger(String s, int i, int j) {
      super(s, Integer.class);
      this.min = i;
      this.max = j;
      this.range = this.max - this.min;
      if (i < 0) {
         throw new IllegalArgumentException("Min value of " + s + " must be 0 or greater");
      } else if (j <= i) {
         throw new IllegalArgumentException("Max value of " + s + " must be greater than min (" + i + ")");
      } else {
         HashSet<Integer> hashset = Sets.newHashSet();

         for (int k = i; k <= j; k++) {
            hashset.add(k);
         }

         this.a = ImmutableSet.copyOf(hashset);
      }
   }

   @Override
   public Collection<Integer> c() {
      return this.a;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (object == null || this.getClass() != object.getClass()) {
         return false;
      } else if (!super.equals(object)) {
         return false;
      } else {
         BlockStateInteger blockstateinteger = (BlockStateInteger)object;
         return this.a.equals(blockstateinteger.a);
      }
   }

   @Override
   public int hashCode() {
      int i = super.hashCode();
      return 31 * i + this.a.hashCode();
   }

   public static BlockStateInteger of(String s, int i, int j) {
      return new BlockStateInteger(s, i, j);
   }

   public String a(Integer integer) {
      return integer.toString();
   }
}
