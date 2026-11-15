package lol.deyo.blaze.anticheat.checks;

import lol.deyo.blaze.anticheat.ACManager;
import lol.deyo.blaze.anticheat.ACPlayer;
import lol.deyo.blaze.anticheat.AnticheatConfig;
import lol.deyo.blaze.anticheat.Check;
import java.util.Deque;

public class AutoClickerCheck extends Check {
   private int sustainedLowEntropyWindows;
   private int sustainedLowBitsWindows;
   private int equalDelayFlags;
   private int consistencyFlags;

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

         this.checkEqualDelay(data, intervals);
         this.checkIntervalBits(data, intervals);
         this.checkConsistency(data, intervals);
      } else {
         this.sustainedLowEntropyWindows = 0;
         this.sustainedLowBitsWindows = 0;
         this.equalDelayFlags = 0;
      }
   }

   private void checkConsistency(ACPlayer data, Deque<Long> intervals) {
      int n = intervals.size();
      if (n < 20) {
         return;
      }

      double mean = 0.0;
      for (long interval : intervals) {
         mean += interval;
      }

      mean /= n;
      if (mean <= 0.0) {
         return;
      }

      double variance = 0.0;
      for (long interval : intervals) {
         double d = interval - mean;
         variance += d * d;
      }

      double cv = Math.sqrt(variance / (n - 1)) / mean;
      double maxCv = (Double)AnticheatConfig.autoclickerMaxCv.getValue();
      if (cv < maxCv) {
         this.consistencyFlags++;
         if (this.consistencyFlags >= 3) {
            this.flag(data, 1.5, String.format("cv=%.4f windows=%d", cv, this.consistencyFlags));
            this.consistencyFlags = 0;
         }
      } else {
         this.consistencyFlags = 0;
      }
   }

   private void checkEqualDelay(ACPlayer data, Deque<Long> intervals) {
      int minRun = (Integer)AnticheatConfig.autoclickerEqualDelayMinRun.getValue();
      Long previous = null;
      int run = 1;
      int longest = 1;
      for (long interval : intervals) {
         if (previous != null && previous.longValue() == interval) {
            run++;
            if (run > longest) {
               longest = run;
            }
         } else {
            run = 1;
         }

         previous = interval;
      }

      if (longest >= minRun) {
         this.equalDelayFlags++;
         if (this.equalDelayFlags >= 2) {
            this.flag(data, 1.5, String.format("equalDelay=%d flags=%d", longest, this.equalDelayFlags));
            this.equalDelayFlags = 0;
         }
      } else {
         this.equalDelayFlags = 0;
      }
   }

   private void checkIntervalBits(ACPlayer data, Deque<Long> intervals) {
      double bits = shannonBits(intervals);
      double minBits = (Double)AnticheatConfig.autoclickerMinBits.getValue();
      if (bits < minBits) {
         this.sustainedLowBitsWindows++;
         if (this.sustainedLowBitsWindows >= 3) {
            this.flag(data, 1.5, String.format("bits=%.2f windows=%d", bits, this.sustainedLowBitsWindows));
            this.sustainedLowBitsWindows = 0;
         }
      } else {
         this.sustainedLowBitsWindows = 0;
      }
   }

   public static double shannonBits(Deque<Long> intervals) {
      int n = intervals.size();
      if (n < 8) {
         return Double.MAX_VALUE;
      }

      long min = Long.MAX_VALUE;
      long max = Long.MIN_VALUE;
      for (long interval : intervals) {
         if (interval < min) {
            min = interval;
         }
         if (interval > max) {
            max = interval;
         }
      }

      if (max == min) {
         return 0.0;
      }

      int buckets = 8;
      int[] counts = new int[buckets];
      double span = max - min;
      for (long interval : intervals) {
         int bucket = (int) ((interval - min) * buckets / span);
         if (bucket >= buckets) {
            bucket = buckets - 1;
         }
         counts[bucket]++;
      }

      double entropy = 0.0;
      for (int count : counts) {
         if (count > 0) {
            double p = (double) count / n;
            entropy -= p * (Math.log(p) / Math.log(2.0));
         }
      }

      return entropy;
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
