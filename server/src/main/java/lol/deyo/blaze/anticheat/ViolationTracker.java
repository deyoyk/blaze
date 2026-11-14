package lol.deyo.blaze.anticheat;

public final class ViolationTracker {
   private String[] checkNames = new String[16];
   private double[] levels = new double[16];
   private double[] decayPerSecond = new double[16];
   private long[] lastNanos = new long[16];
   private int count;

   public void register(Check check, double decayPerSecond) {
      if (this.count >= this.checkNames.length) {
         String[] names = new String[this.count * 2];
         double[] lv = new double[this.count * 2];
         double[] dec = new double[this.count * 2];
         long[] last = new long[this.count * 2];
         System.arraycopy(this.checkNames, 0, names, 0, this.count);
         System.arraycopy(this.levels, 0, lv, 0, this.count);
         System.arraycopy(this.decayPerSecond, 0, dec, 0, this.count);
         System.arraycopy(this.lastNanos, 0, last, 0, this.count);
         this.checkNames = names;
         this.levels = lv;
         this.decayPerSecond = dec;
         this.lastNanos = last;
      }

      this.checkNames[this.count] = check.getName();
      this.decayPerSecond[this.count] = decayPerSecond;
      this.count++;
   }

   public double levelOf(String checkName) {
      int idx = this.indexOf(checkName);
      return idx < 0 ? 0.0 : this.levels[idx];
   }

   public double penalize(String checkName, double weight, long nowNanos) {
      int idx = this.indexOf(checkName);
      if (idx < 0) {
         return weight;
      }

      double decayed = this.decay(idx, nowNanos);
      double result = decayed + weight;
      this.levels[idx] = result;
      this.lastNanos[idx] = nowNanos;
      return result;
   }

   private double decay(int idx, long nowNanos) {
      double current = this.levels[idx];
      if (current <= 0.0) {
         return 0.0;
      }

      long elapsed = nowNanos - this.lastNanos[idx];
      if (elapsed <= 0L) {
         return current;
      }

      double seconds = elapsed / 1000000000.0;
      double decayed = current - this.decayPerSecond[idx] * seconds;
      return decayed > 0.0 ? decayed : 0.0;
   }

   public void reset() {
      for (int i = 0; i < this.count; i++) {
         this.levels[i] = 0.0;
      }
   }

   public int size() {
      return this.count;
   }

   public double levelAt(int idx) {
      return this.levels[idx];
   }

   public String nameAt(int idx) {
      return this.checkNames[idx];
   }

   private int indexOf(String checkName) {
      for (int i = 0; i < this.count; i++) {
         if (this.checkNames[i].equals(checkName)) {
            return i;
         }
      }

      return -1;
   }
}
