package lol.deyo.blaze.anticheat.checks;

public final class ViolationBuffer {
   private final double decayPerEvent;
   private final double limit;
   private double buffered;

   public ViolationBuffer(double decayPerEvent, double limit) {
      this.decayPerEvent = decayPerEvent;
      this.limit = limit;
   }

   public double add(double amount) {
      double buffered = Math.max(0.0, this.buffered - this.decayPerEvent) + amount;
      this.buffered = Math.min(buffered, this.limit * 2.0);
      return this.buffered;
   }

   public double addClean() {
      this.buffered = Math.max(0.0, this.buffered - this.decayPerEvent);
      return this.buffered;
   }

   public double buffered() {
      return this.buffered;
   }

   public void reset() {
      this.buffered = 0.0;
   }
}
