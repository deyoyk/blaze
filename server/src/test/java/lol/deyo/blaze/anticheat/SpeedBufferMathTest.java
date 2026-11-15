package lol.deyo.blaze.anticheat;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import lol.deyo.blaze.anticheat.checks.ViolationBuffer;
import org.junit.Test;

public class SpeedBufferMathTest {
   private static final double SPEED_BUFFER_DECAY = 0.08;
   private static final double SPEED_BUFFER_LIMIT = 0.5;

   @Test
   public void singleLagSpikeBelowLimit() {
      ViolationBuffer buffer = new ViolationBuffer(SPEED_BUFFER_DECAY, SPEED_BUFFER_LIMIT);
      double buffered = buffer.add(0.4);
      assertTrue(buffered < SPEED_BUFFER_LIMIT);
   }

   @Test
   public void sustainedSpeedHackAccumulates() {
      ViolationBuffer buffer = new ViolationBuffer(SPEED_BUFFER_DECAY, SPEED_BUFFER_LIMIT);
      double buffered = 0.0;
      for (int i = 0; i < 20; i++) {
         buffered = buffer.add(0.4);
      }

      assertTrue(buffered > SPEED_BUFFER_LIMIT);
   }

   @Test
   public void cleanTicksDecayFully() {
      ViolationBuffer buffer = new ViolationBuffer(SPEED_BUFFER_DECAY, SPEED_BUFFER_LIMIT);
      buffer.add(0.4);
      double after = 0.0;
      for (int i = 0; i < 50; i++) {
         after = buffer.addClean();
      }

      assertTrue(after < SPEED_BUFFER_LIMIT);
   }

   @Test
   public void alternatingBurstAndCleanStaysClean() {
      ViolationBuffer buffer = new ViolationBuffer(SPEED_BUFFER_DECAY, SPEED_BUFFER_LIMIT);
      double result = 0.0;
      for (int i = 0; i < 30; i++) {
         result = buffer.add(0.4);
         for (int j = 0; j < 8; j++) {
            result = buffer.addClean();
         }
      }

      assertFalse(result > SPEED_BUFFER_LIMIT);
   }

   @Test
   public void constantSlowDrainFlags() {
      ViolationBuffer buffer = new ViolationBuffer(SPEED_BUFFER_DECAY, SPEED_BUFFER_LIMIT);
      double buffered = 0.0;
      for (int i = 0; i < 40; i++) {
         buffered = buffer.add(0.1);
      }

      assertTrue(buffered > SPEED_BUFFER_LIMIT);
   }
}
