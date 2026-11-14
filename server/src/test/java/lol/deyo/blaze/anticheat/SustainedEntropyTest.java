package lol.deyo.blaze.anticheat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import lol.deyo.blaze.anticheat.checks.AutoClickerCheck;
import java.util.ArrayDeque;
import java.util.Deque;
import org.junit.Test;

public class SustainedEntropyTest {
   private Deque<Long> fixedIntervals(int count) {
      Deque<Long> intervals = new ArrayDeque<>();
      for (int i = 0; i < count; i++) {
         intervals.add(50_000_000L);
      }
      return intervals;
   }

   @Test
   public void fortyIdenticalIntervalsHaveZeroEntropy() {
      assertEquals(0.0, AutoClickerCheck.stddev(fixedIntervals(40)), 0.0001);
   }

   @Test
   public void fortyHumanIntervalsHaveHighEntropy() {
      long[] samples = new long[]{
         42, 61, 55, 73, 38, 66, 49, 88, 52, 60, 45, 71, 57, 39, 64, 53, 78, 47, 59, 69,
         44, 82, 51, 67, 40, 74, 56, 85, 48, 62, 46, 77, 54, 41, 63, 58, 80, 50, 65, 43
      };
      Deque<Long> intervals = new ArrayDeque<>();
      for (long ms : samples) {
         intervals.add(ms * 1000000L);
      }

      assertTrue("expected human entropy above 0.006, got " + AutoClickerCheck.stddev(intervals),
         AutoClickerCheck.stddev(intervals) > 0.006);
   }

   @Test
   public void microsecondJitterCheatStaysBelowThreshold() {
      Deque<Long> intervals = new ArrayDeque<>();
      long[] jitter = new long[]{-800, 600, -400, 900, -700, 300, -500, 800, -900, 500, -300, 700, -600, 400, -800, 900, -400, 600, -500, 300};
      for (long j : jitter) {
         intervals.add(50_000_000L + j);
      }

      assertTrue("expected microsecond-jitter cheat below 0.006, got " + AutoClickerCheck.stddev(intervals),
         AutoClickerCheck.stddev(intervals) < 0.006);
   }
}
