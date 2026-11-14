package lol.deyo.blaze.anticheat.checks;

import lol.deyo.blaze.anticheat.ACManager;
import lol.deyo.blaze.anticheat.ACPlayer;
import lol.deyo.blaze.anticheat.Check;
import lol.deyo.blaze.anticheat.AnticheatConfig;
import java.util.Deque;

public class AutoClickerCheck extends Check {
   private int sustainedLowEntropyWindows;

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
         this.flag(data, cps >= maxCps * 2 ? 3.0 : 1.0, "cps=" + cps);
      }

      Deque<Long> intervals = data.getClickIntervals();
      if (intervals.size() >= 20) {
         double stddev = stddev(intervals);
         double minEntropy = (Double)AnticheatConfig.autoclickerMinEntropy.getValue();
         if (stddev < minEntropy) {
            this.sustainedLowEntropyWindows++;
            if (this.sustainedLowEntropyWindows >= 2) {
               this.flag(data, 1.5, String.format("entropy=%.5f windows=%d", stddev, this.sustainedLowEntropyWindows));
               this.sustainedLowEntropyWindows = 0;
            }
         } else {
            this.sustainedLowEntropyWindows = 0;
         }
      } else {
         this.sustainedLowEntropyWindows = 0;
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
