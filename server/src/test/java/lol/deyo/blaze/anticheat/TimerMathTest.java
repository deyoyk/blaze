package lol.deyo.blaze.anticheat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import lol.deyo.blaze.anticheat.checks.TimerCheck;
import org.junit.Test;

public class TimerMathTest {
   private static final double NANOS_PER_TICK = 50000000.0;

   @Test
   public void normalPlayIsBelowMaxRate() {
      double packets = 20;
      double elapsedNanos = 20 * NANOS_PER_TICK;
      double rate = packets / (elapsedNanos / NANOS_PER_TICK);
      assertEquals(1.0, rate, 0.0001);
      assertTrue(rate <= 1.08);
   }

   @Test
   public void timerCheatExceedsMaxRate() {
      double packets = 30;
      double elapsedNanos = 20 * NANOS_PER_TICK;
      double rate = packets / (elapsedNanos / NANOS_PER_TICK);
      assertEquals(1.5, rate, 0.0001);
      assertTrue(rate > 1.08);
   }

   @Test
   public void smallPacketCountsAreIgnored() {
      TimerCheck check = new TimerCheck(null);
      assertEquals("Timer", check.getName());
   }

   @Test
   public void onePointEightTicksPerSecondIsSuspicious() {
      double packets = 36;
      double elapsedNanos = 20 * NANOS_PER_TICK;
      double rate = packets / (elapsedNanos / NANOS_PER_TICK);
      assertEquals(1.8, rate, 0.0001);
      assertFalse(rate <= 1.08);
   }
}
