package lol.deyo.blaze.anticheat.checks;

import lol.deyo.blaze.anticheat.ACManager;
import lol.deyo.blaze.anticheat.ACPlayer;
import lol.deyo.blaze.anticheat.AnticheatConfig;
import lol.deyo.blaze.anticheat.Check;
import java.util.concurrent.TimeUnit;

public class TimerCheck extends Check {
   private static final double NANOS_PER_TICK = TimeUnit.MILLISECONDS.toNanos(50);
   private static final double MIN_WINDOW_TICKS = 20.0;
   private static final double DECAY_PER_CLEAN_WINDOW = 0.3;
   private static final double MAX_PER_WINDOW = 2.0;
   private static final double MAX_BUFFER = 6.0;
   private static final double FLAG_WEIGHT_BASE = 0.5;
   private static final double FLAG_WEIGHT_PER_EXCESS = 10.0;
   private static final double MAX_FLAG_WEIGHT = 3.0;

   private final ViolationBuffer buffer = new ViolationBuffer(DECAY_PER_CLEAN_WINDOW, MAX_BUFFER);

   public TimerCheck(ACManager manager) {
      super(manager, "Timer");
   }

   @Override
   public boolean enabled() {
      return (Boolean)AnticheatConfig.movementEnabled.getValue()
         && (Boolean)AnticheatConfig.timerEnabled.getValue();
   }

   @Override
   public void onFlying(ACPlayer data, long nowNanos) {
      long packets = data.getTimerPacketsSinceBalance();
      long elapsed = data.getTimerElapsedNanosSinceBalance();
      double elapsedTicks = elapsed / NANOS_PER_TICK;
      if (elapsedTicks < MIN_WINDOW_TICKS) {
         return;
      }

      double rate = packets / elapsedTicks;
      double maxRate = (Double)AnticheatConfig.timerMaxRate.getValue();
      if (rate > maxRate && packets > 40) {
         double excess = rate - maxRate;
         double buffered = this.buffer.add(Math.min(excess * MIN_WINDOW_TICKS, MAX_PER_WINDOW));
         if (buffered > (Double)AnticheatConfig.timerMaxBalanceTicks.getValue()) {
            if (this.flag(data,
               Math.min(MAX_FLAG_WEIGHT, FLAG_WEIGHT_BASE + buffered * FLAG_WEIGHT_PER_EXCESS),
               String.format("rate=%.3f packets=%d buffered=%.1f", rate, packets, buffered))) {
               data.resetTimerBalance();
               this.buffer.reset();
            }
         }
      } else {
         this.buffer.addClean();
         if (rate <= 1.0) {
            data.resetTimerBalance();
         }
      }
   }
}
