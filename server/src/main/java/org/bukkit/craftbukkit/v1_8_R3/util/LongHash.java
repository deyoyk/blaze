package org.bukkit.craftbukkit.v1_8_R3.util;

public class LongHash {
   public static long toLong(int msw, int lsw) {
      return ((long)msw << 32) + lsw - -2147483648L;
   }

   public static int msw(long l) {
      return (int)(l >> 32);
   }

   public static int lsw(long l) {
      return (int)(l & 4294967295L) + Integer.MIN_VALUE;
   }
}
