package lol.deyo.blaze.anticheat.checks;

import lol.deyo.blaze.anticheat.ACManager;
import lol.deyo.blaze.anticheat.ACPlayer;
import lol.deyo.blaze.anticheat.Check;
import lol.deyo.blaze.anticheat.AnticheatConfig;
import java.util.concurrent.TimeUnit;

public class TimerCheck extends Check {
   private static final double NANOS_PER_TICK = TimeUnit.MILLISECONDS.toNanos(50);

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
      if (elapsed < NANOS_PER_TICK * 20) {
         return;
      }

      double rate = packets / (elapsed / NANOS_PER_TICK);
      double maxRate = (Double)AnticheatConfig.timerMaxRate.getValue();
      if (rate > maxRate && packets > 40) {
         double excess = rate - maxRate;
         if (this.flag(data, Math.min(3.0, 0.5 + excess * 10.0), String.format("rate=%.3f packets=%d", rate, packets))) {
            data.resetTimerBalance();
         }
      } else if (rate <= 1.0) {
         data.resetTimerBalance();
      }
   }
}
