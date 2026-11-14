package lol.deyo.blaze.anticheat;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import lol.deyo.blaze.anticheat.checks.PhaseCheck;
import org.junit.Test;

public class PhaseSweepTest {
   private static final double EPSILON = 0.001;

   @Test
   public void playerAabbInsideSingleBlockOverlaps() {
      assertTrue(PhaseCheck.aabbOverlapsBlock(0.5, 70.0, 0.5, 0.7, 71.8, 0.7, 0, 70, 0, EPSILON));
   }

   @Test
   public void adjacentBlockDoesNotOverlap() {
      assertFalse(PhaseCheck.aabbOverlapsBlock(0.0, 70.0, 0.0, 1.0, 71.8, 1.0, 1, 70, 0, EPSILON));
   }

   @Test
   public void touchingFaceWithinEpsilonDoesNotOverlap() {
      assertFalse(PhaseCheck.aabbOverlapsBlock(0.0, 70.0, 0.0, 1.0, 71.8, 1.001, 0, 70, 1, EPSILON));
   }

   @Test
   public void yOnlyProximityDoesNotCount() {
      assertFalse(PhaseCheck.aabbOverlapsBlock(0.5, 70.0, 0.5, 0.7, 71.8, 0.7, 0, 69, 0, EPSILON));
   }

   @Test
   public void walkingThroughWallMidpointOverlaps() {
      assertTrue(PhaseCheck.aabbOverlapsBlock(2.2, 70.0, 0.5, 2.8, 71.8, 0.7, 2, 70, 0, EPSILON));
   }

   @Test
   public void standingInAirAdjacentToWallDoesNotOverlap() {
      assertFalse(PhaseCheck.aabbOverlapsBlock(3.0005, 70.0, 0.5, 3.05, 71.8, 0.7, 2, 70, 0, EPSILON));
   }

   @Test
   public void crossingBlockBoundaryByMoreThanEpsilonOverlaps() {
      assertTrue(PhaseCheck.aabbOverlapsBlock(2.9, 70.0, 0.5, 3.1, 71.8, 0.7, 3, 70, 0, EPSILON));
   }
}
