package lol.deyo.blaze.anticheat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import lol.deyo.blaze.anticheat.checks.AutoClickerCheck;
import java.util.ArrayDeque;
import java.util.Deque;
import org.junit.Test;

public class ClickEntropyTest {
   @Test
   public void identicalIntervalsHaveNearZeroEntropy() {
      Deque<Long> intervals = new ArrayDeque<>();
      for (int i = 0; i < 20; i++) {
         intervals.add(50_000_000L);
      }

      double stddev = AutoClickerCheck.stddev(intervals);
      assertEquals(0.0, stddev, 0.0001);
   }

   @Test
   public void humanLikeIntervalsHaveHighEntropy() {
      Deque<Long> intervals = new ArrayDeque<>();
      long[] samples = new long[]{42, 61, 55, 73, 38, 66, 49, 88, 52, 60, 45, 71, 57, 39, 64, 53, 78, 47, 59, 69};
      for (long ms : samples) {
         intervals.add(ms * 1000000L);
      }

      double stddev = AutoClickerCheck.stddev(intervals);
      assertTrue("expected human entropy above 0.006ms-scale, got " + stddev, stddev > 1.0);
   }

   @Test
   public void machineLikeConstantJitterFlagsLow() {
      Deque<Long> intervals = new ArrayDeque<>();
      for (int i = 0; i < 20; i++) {
         intervals.add(50_000_000L + (i % 2) * 10_000L);
      }

      double stddev = AutoClickerCheck.stddev(intervals);
      assertTrue("expected machine-like entropy below 1ms, got " + stddev, stddev < 1.0);
   }

   @Test
   public void fewerThanTwoSamplesIsMaxEntropy() {
      Deque<Long> intervals = new ArrayDeque<>();
      intervals.add(50_000_000L);
      assertEquals(Double.MAX_VALUE, AutoClickerCheck.stddev(intervals), 0.0001);
   }
}
