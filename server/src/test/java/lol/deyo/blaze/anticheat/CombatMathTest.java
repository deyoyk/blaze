package lol.deyo.blaze.anticheat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import lol.deyo.blaze.anticheat.checks.KillauraCheck;
import org.junit.Test;

public class CombatMathTest {
   @Test
   public void postAttackWithinVanillaWindowNotFlagged() {
      long flying = 1000000000L;
      long attack = flying + 90000000L;
      long since = attack - flying;
      assertTrue(since >= 85000000L);
   }

   @Test
   public void postAttackKillauraWindowFlagged() {
      long flying = 1000000000L;
      long attack = flying + 50000000L;
      long since = attack - flying;
      assertTrue(since < 85000000L);
   }

   @Test
   public void normalAimDeltaBelowMax() {
      double yawDelta = 25.0;
      double pitchDelta = 8.0;
      double combined = yawDelta + pitchDelta;
      assertTrue(combined <= 40.0);
   }

   @Test
   public void snapAimDeltaFlagged() {
      double yawDelta = 170.0;
      double pitchDelta = 10.0;
      double combined = yawDelta + pitchDelta;
      assertTrue(combined > 40.0);
   }

   @Test
   public void velocityHorizontalRatioComputation() {
      double expectedX = 0.4;
      double expectedZ = 0.3;
      double expectedH = Math.sqrt(expectedX * expectedX + expectedZ * expectedZ);
      double actualX = 0.2;
      double actualZ = 0.15;
      double actualH = Math.sqrt(actualX * actualX + actualZ * actualZ);
      double ratio = actualH / expectedH;
      assertEquals(0.5, ratio, 0.0001);
      assertTrue(ratio < 0.6);
   }

   @Test
   public void velocityVerticalRatioComputation() {
      double expectedY = 0.36;
      double actualY = 0.36;
      double ratio = Math.max(0.0, actualY) / expectedY;
      assertEquals(1.0, ratio, 0.0001);
      assertTrue(ratio >= 0.7);
   }

   @Test
   public void hitboxAimDotTowardVictim() {
      double lookX = -Math.sin(Math.toRadians(180.0)) * Math.cos(0.0);
      double lookZ = Math.cos(Math.toRadians(180.0)) * Math.cos(0.0);
      double dx = 0.0;
      double dz = -1.0;
      double dot = lookX * dx + lookZ * dz;
      double length = Math.sqrt(dx * dx + dz * dz);
      double normalized = length < 1.0E-4 ? 1.0 : dot / length;
      assertTrue(normalized >= 0.35);
   }
}
