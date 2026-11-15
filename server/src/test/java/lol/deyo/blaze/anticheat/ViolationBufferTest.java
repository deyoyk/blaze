package lol.deyo.blaze.anticheat;

import lol.deyo.blaze.anticheat.checks.ViolationBuffer;
import org.junit.Assert;
import org.junit.Test;

public class ViolationBufferTest {
   @Test
   public void singleSpikeBelowLimitNeverFlags() {
      ViolationBuffer buffer = new ViolationBuffer(0.5, 5.0);
      double buffered = buffer.add(3.0);
      Assert.assertEquals(3.0, buffered, 0.0001);
      Assert.assertTrue(buffered < 5.0);
   }

   @Test
   public void sustainedExcessAccumulatesPastLimit() {
      ViolationBuffer buffer = new ViolationBuffer(0.5, 5.0);
      double buffered = 0.0;
      for (int i = 0; i < 6; i++) {
         buffered = buffer.add(2.0);
      }

      Assert.assertTrue(buffered > 5.0);
   }

   @Test
   public void cleanEventsDecayBuffer() {
      ViolationBuffer buffer = new ViolationBuffer(0.5, 5.0);
      buffer.add(3.0);
      double after = 0.0;
      for (int i = 0; i < 20; i++) {
         after = buffer.addClean();
      }

      Assert.assertEquals(0.0, after, 0.0001);
   }

   @Test
   public void mixedSpikeAndCleanStaysBelowLimit() {
      ViolationBuffer buffer = new ViolationBuffer(0.5, 5.0);
      for (int i = 0; i < 20; i++) {
         buffer.add(2.0);
         for (int j = 0; j < 10; j++) {
            buffer.addClean();
         }
      }

      Assert.assertTrue(buffer.buffered() < 5.0);
   }

   @Test
   public void bufferCapsAtTwiceLimit() {
      ViolationBuffer buffer = new ViolationBuffer(0.0, 5.0);
      for (int i = 0; i < 100; i++) {
         buffer.add(1.0);
      }

      Assert.assertEquals(10.0, buffer.buffered(), 0.0001);
   }

   @Test
   public void resetClearsBuffer() {
      ViolationBuffer buffer = new ViolationBuffer(0.5, 5.0);
      buffer.add(4.0);
      buffer.reset();
      Assert.assertEquals(0.0, buffer.buffered(), 0.0001);
   }
}
