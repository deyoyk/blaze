package lol.deyo.blaze.anticheat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import lol.deyo.blaze.anticheat.checks.AutoClickerCheck;
import org.junit.Test;

public class AutoClickerStatsTest {
   @Test
   public void shannonBitsConstantIntervalsIsZero() {
      Deque<Long> intervals = new ArrayDeque<>(Arrays.asList(100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L));
      assertEquals(0.0, AutoClickerCheck.shannonBits(intervals), 0.0001);
   }

   @Test
   public void shannonBitsUniformIntervalsIsMax() {
      Deque<Long> intervals = new ArrayDeque<>(Arrays.asList(90L, 110L, 130L, 150L, 170L, 190L, 210L, 230L, 250L, 270L));
      double bits = AutoClickerCheck.shannonBits(intervals);
      assertTrue("expected high entropy, got " + bits, bits > 2.0);
   }

   @Test
   public void shannonBitsHumanLikeIntervalsStaysHigh() {
      long[] base = {95L, 105L, 98L, 112L, 90L, 118L, 101L, 94L, 108L, 99L, 111L, 96L, 104L, 93L, 115L, 102L, 97L, 109L, 100L, 106L};
      Deque<Long> intervals = new ArrayDeque<>();
      for (long v : base) {
         intervals.add(v);
      }

      double bits = AutoClickerCheck.shannonBits(intervals);
      assertTrue("expected human-like entropy above threshold, got " + bits, bits > 0.35);
   }

   @Test
   public void stddevConstantIntervalsIsZero() {
      Deque<Long> intervals = new ArrayDeque<>(Arrays.asList(50L, 50L, 50L, 50L, 50L));
      assertEquals(0.0, AutoClickerCheck.stddev(intervals), 0.0001);
   }

   @Test
   public void stddevVariedIntervalsAboveFloor() {
      Deque<Long> intervals = new ArrayDeque<>(Arrays.asList(
         80_000_000L, 120_000_000L, 90_000_000L, 130_000_000L, 85_000_000L, 125_000_000L
      ));
      assertTrue(AutoClickerCheck.stddev(intervals) > 0.006);
   }

   @Test
   public void shannonBitsSmallWindowReturnsMax() {
      Deque<Long> intervals = new ArrayDeque<>(Arrays.asList(100L, 100L, 100L));
      assertEquals(Double.MAX_VALUE, AutoClickerCheck.shannonBits(intervals), 0.0001);
   }
}
