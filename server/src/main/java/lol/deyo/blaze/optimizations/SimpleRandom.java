package lol.deyo.blaze.optimizations;

import java.util.Random;

public final class SimpleRandom extends Random {
   private static final long MULTIPLIER = 25214903917L;
   private static final long ADDEND = 11L;
   private static final int BITS = 48;
   private static final long MASK = 281474976710655L;
   private long value;

   public SimpleRandom(long seed) {
      super(0L);
      this.value = seed;
   }

   @Override
   public void setSeed(long seed) {
      this.value = (seed ^ 25214903917L) & 281474976710655L;
   }

   private long advanceSeed() {
      return this.value = this.value * 25214903917L + 11L & 281474976710655L;
   }

   @Override
   public int next(int bits) {
      return (int)(this.advanceSeed() >>> 48 - bits);
   }

   @Override
   public int nextInt() {
      long seed = this.advanceSeed();
      return (int)(seed >>> 16);
   }

   @Override
   public int nextInt(int bound) {
      long value = this.advanceSeed() >>> 16;
      return (int)(value * bound >>> 32);
   }
}
