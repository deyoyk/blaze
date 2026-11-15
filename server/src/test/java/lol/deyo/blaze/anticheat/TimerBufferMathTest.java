package lol.deyo.blaze.anticheat;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import lol.deyo.blaze.anticheat.checks.ViolationBuffer;
import org.junit.Test;

public class TimerBufferMathTest {
   private static final double MIN_WINDOW_TICKS = 20.0;
   private static final double MAX_PER_WINDOW = 2.0;
   private static final double MAX_BUFFER = 6.0;
   private static final double MAX_RATE = 1.08;
   private static final double FLAG_THRESHOLD = 3.0;

   private static double bufferedAfter(int windows, double rate, ViolationBuffer buffer) {
      double excess = Math.max(0.0, rate - MAX_RATE);
      double result = 0.0;
      for (int i = 0; i < windows; i++) {
         if (excess > 0.0) {
            result = buffer.add(Math.min(excess * MIN_WINDOW_TICKS, MAX_PER_WINDOW));
         } else {
            result = buffer.addClean();
         }
      }

      return result;
   }

   @Test
   public void vanillaTimerNeverAccumulates() {
      ViolationBuffer buffer = new ViolationBuffer(0.3, MAX_BUFFER);
      double buffered = bufferedAfter(50, 1.0, buffer);
      assertTrue(buffered <= 0.0);
   }

   @Test
   public void singleLagSpikeDoesNotFlag() {
      ViolationBuffer buffer = new ViolationBuffer(0.3, MAX_BUFFER);
      double buffered = bufferedAfter(1, 2.0, buffer);
      assertTrue(buffered < FLAG_THRESHOLD);
   }

   @Test
   public void sustainedTimerCheatAccumulatesToFlag() {
      ViolationBuffer buffer = new ViolationBuffer(0.3, MAX_BUFFER);
      double buffered = bufferedAfter(10, 1.5, buffer);
      assertTrue(buffered > FLAG_THRESHOLD);
   }

   @Test
   public void intermittentCheatAccumulatesSlowly() {
      ViolationBuffer buffer = new ViolationBuffer(0.3, MAX_BUFFER);
      double result = 0.0;
      for (int i = 0; i < 40; i++) {
         result = bufferedAfter(1, i % 2 == 0 ? 1.5 : 1.0, buffer);
      }

      assertTrue(result > FLAG_THRESHOLD);
   }

   @Test
   public void slowCheaterJustAboveCapIsCaughtEventually() {
      ViolationBuffer buffer = new ViolationBuffer(0.3, MAX_BUFFER);
      double buffered = bufferedAfter(40, 1.1, buffer);
      assertTrue(buffered > FLAG_THRESHOLD);
   }

   @Test
   public void onePointFiveTimesRateExceedsCap() {
      assertTrue(1.5 > MAX_RATE);
   }
}
