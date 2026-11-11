package org.bukkit.util;

import java.lang.reflect.Array;

public class Java15Compat {
   public static <T> T[] Arrays_copyOfRange(T[] original, int start, int end) {
      if (original.length < start || 0 > start) {
         throw new ArrayIndexOutOfBoundsException();
      } else if (start <= end) {
         int length = end - start;
         int copyLength = Math.min(length, original.length - start);
         T[] copy = (T[])Array.newInstance(original.getClass().getComponentType(), length);
         System.arraycopy(original, start, copy, 0, copyLength);
         return copy;
      } else {
         throw new IllegalArgumentException();
      }
   }
}
