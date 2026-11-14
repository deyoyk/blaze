package lol.deyo.blaze.anticheat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import lol.deyo.blaze.anticheat.checks.ReachCheck;
import org.junit.Test;

public class ReachMathTest {
   @Test
   public void zeroPingUsesConfigBase() {
      assertEquals(3.5, ReachCheck.allowedReach(3.5, 0), 0.0001);
   }

   @Test
   public void pingAddsScaledAllowance() {
      assertEquals(3.58, ReachCheck.allowedReach(3.5, 200), 0.0001);
      assertEquals(3.62, ReachCheck.allowedReach(3.5, 300), 0.0001);
   }

   @Test
   public void highPingAllowanceStaysUnderHardBound() {
      assertTrue(ReachCheck.allowedReach(3.5, 500) < 4.2);
   }
}
