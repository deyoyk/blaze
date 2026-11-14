package lol.deyo.blaze.anticheat.checks;

import lol.deyo.blaze.anticheat.ACManager;
import lol.deyo.blaze.anticheat.ACPlayer;
import lol.deyo.blaze.anticheat.Check;
import lol.deyo.blaze.anticheat.AnticheatConfig;
import java.util.Deque;

public class AutoClickerCheck extends Check {
   public AutoClickerCheck(ACManager manager) {
      super(manager, "AutoClicker");
   }

   @Override
   public boolean enabled() {
      return (Boolean)AnticheatConfig.combatEnabled.getValue()
         && (Boolean)AnticheatConfig.autoclickerEnabled.getValue();
   }

   @Override
   public void onSwing(ACPlayer data, long nowNanos) {
      int cps = data.getClicksThisSecond();
      int maxCps = (Integer)AnticheatConfig.autoclickerMaxCps.getValue();
      if (cps > maxCps) {
         this.flag(data, 1.0, "cps=" + cps);
      }

      Deque<Long> intervals = data.getClickIntervals();
      if (intervals.size() >= 20) {
         double stddev = stddev(intervals);
         double minEntropy = (Double)AnticheatConfig.autoclickerMinEntropy.getValue();
         if (stddev < minEntropy) {
            this.flag(data, 1.5, String.format("entropy=%.5f", stddev));
         }
      }
   }

   public static double stddev(Deque<Long> intervals) {
      int n = intervals.size();
      if (n < 2) {
         return Double.MAX_VALUE;
      }

      double mean = 0.0;
      for (long interval : intervals) {
         mean += interval;
      }

      mean /= n;
      double variance = 0.0;
      for (long interval : intervals) {
         double d = interval - mean;
         variance += d * d;
      }

      variance /= n - 1;
      return Math.sqrt(variance) / 1000000.0;
   }
}
