package lol.deyo.blaze.optimizations;

import it.unimi.dsi.fastutil.shorts.Short2IntOpenHashMap;
import java.util.Arrays;

public final class SectionTickList {
   private final Short2IntOpenHashMap map = new Short2IntOpenHashMap(0);
   private static final short[] EMPTY_LIST = new short[0];
   private short[] byIndex;
   private int count;

   public SectionTickList() {
      this.map.defaultReturnValue(Integer.MIN_VALUE);
      this.byIndex = EMPTY_LIST;
   }

   public int size() {
      return this.count;
   }

   public short getRaw(int index) {
      return this.byIndex[index];
   }

   public boolean add(short value) {
      int count = this.count;
      int currIndex = this.map.putIfAbsent(value, count);
      if (currIndex != Integer.MIN_VALUE) {
         return false;
      } else {
         short[] list = this.byIndex;
         if (list.length == count) {
            list = this.byIndex = Arrays.copyOf(list, (int)Math.max(4L, count * 2L));
         }

         list[count] = value;
         this.count = count + 1;
         return true;
      }
   }

   public boolean remove(short value) {
      int index = this.map.remove(value);
      if (index == Integer.MIN_VALUE) {
         return false;
      } else {
         int endIndex = --this.count;
         short end = this.byIndex[endIndex];
         if (index != endIndex) {
            this.map.put(end, index);
         }

         this.byIndex[index] = end;
         this.byIndex[endIndex] = 0;
         return true;
      }
   }

   public void clear() {
      this.count = 0;
      this.map.clear();
   }
}
