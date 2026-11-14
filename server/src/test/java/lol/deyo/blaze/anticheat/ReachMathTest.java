package lol.deyo.blaze.anticheat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import lol.deyo.blaze.anticheat.checks.ReachMath;
import org.junit.Test;

public class ReachMathTest {
   @Test
   public void zeroPingUsesConfigBase() {
      assertEquals(3.5, lol.deyo.blaze.anticheat.checks.ReachCheck.allowedReach(3.5, 0), 0.0001);
   }

   @Test
   public void pingAddsScaledAllowance() {
      assertEquals(3.58, lol.deyo.blaze.anticheat.checks.ReachCheck.allowedReach(3.5, 200), 0.0001);
      assertEquals(3.62, lol.deyo.blaze.anticheat.checks.ReachCheck.allowedReach(3.5, 300), 0.0001);
   }

   @Test
   public void highPingAllowanceStaysUnderHardBound() {
      assertTrue(lol.deyo.blaze.anticheat.checks.ReachCheck.allowedReach(3.5, 500) < 4.2);
   }

   @Test
   public void rayThroughBoxCenterHits() {
      boolean hit = ReachMath.rayIntersectsBox(
         0.0, 1.62, 0.0, 1.0, 0.0, 0.0,
         4.1, 1.0, -0.4, 4.9, 2.0, 0.4
      );
      assertTrue(hit);
   }

   @Test
   public void rayPastBoxMisses() {
      boolean hit = ReachMath.rayIntersectsBox(
         0.0, 1.62, 0.0, 1.0, 0.0, 0.0,
         4.1, 1.0, 3.0, 4.9, 2.0, 4.0
      );
      assertFalse(hit);
   }

   @Test
   public void minDistanceAtBoxFace() {
      double dist = ReachMath.minAttackDistance(
         0.0, 1.62, 0.0, 1.0, 0.0, 0.0,
         4.1, 1.0, -0.4, 4.9, 2.0, 0.4,
         99.0
      );
      assertEquals(4.1, dist, 0.0001);
   }

   @Test
   public void minDistanceInsideBoxIsZero() {
      double dist = ReachMath.minAttackDistance(
         4.5, 1.5, 0.0, 1.0, 0.0, 0.0,
         4.1, 1.0, -0.4, 4.9, 2.0, 0.4,
         99.0
      );
      assertEquals(0.0, dist, 0.0001);
   }

   @Test
   public void missedRayFallsBackToCenterDistance() {
      double fallback = 7.5;
      double dist = ReachMath.minAttackDistance(
         0.0, 1.62, 0.0, 0.0, -1.0, 0.0,
         4.1, 1.0, -0.4, 4.9, 2.0, 0.4,
         fallback
      );
      assertEquals(fallback, dist, 0.0001);
   }

   @Test
   public void grazingCornerRayStillHits() {
      boolean hit = ReachMath.rayIntersectsBox(
         0.0, 1.62, 0.0, 1.0, 0.02, 0.08,
         4.1, 1.0, -0.4, 4.9, 2.0, 0.4
      );
      assertTrue(hit);
   }

   @Test
   public void pointBoxDistanceZeroInsideBox() {
      double dist = ReachMath.pointBoxDistance(
         4.5, 1.5, 0.0,
         4.1, 1.0, -0.4, 4.9, 2.0, 0.4
      );
      assertEquals(0.0, dist, 0.0001);
   }

   @Test
   public void pointBoxDistanceToNearestFace() {
      double dist = ReachMath.pointBoxDistance(
         0.0, 1.62, 0.0,
         3.0, 1.0, -0.4, 3.8, 2.0, 0.4
      );
      assertEquals(3.0, dist, 0.0001);
   }

   @Test
   public void pointBoxDistanceAboveBoxUsesCorner() {
      double dist = ReachMath.pointBoxDistance(
         4.1, 3.0, 0.0,
         4.1, 1.0, -0.4, 4.9, 2.0, 0.4
      );
      assertEquals(1.0, dist, 0.0001);
   }
}
