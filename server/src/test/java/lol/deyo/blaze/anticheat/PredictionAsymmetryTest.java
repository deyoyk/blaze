package lol.deyo.blaze.anticheat;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PredictionAsymmetryTest {
   private static final double GRAVITY = 0.08;
   private static final double DRAG = 0.98;
   private static final double UNCERTAINTY = 0.03;
   private static final double MAX_ERROR = 0.12;

   private static double nextExpected(double current) {
      return (current - GRAVITY) * DRAG;
   }

   private static boolean flags(double deltaY, double expected) {
      double error = Math.abs(deltaY - expected);
      boolean advantage = deltaY > expected + UNCERTAINTY;
      return advantage && error > MAX_ERROR;
   }

   @Test
   public void vanillaGravityCurveNeverFlags() {
      double y = 0.42;
      for (int tick = 0; tick < 40; tick++) {
         double expected = nextExpected(y);
         double actual = nextExpected(y);
         assertFalse(flags(actual, expected));
         y = actual;
      }
   }

   @Test
   public void fallingFasterThanGravityNeverFlags() {
      double y = 0.42;
      for (int tick = 0; tick < 40; tick++) {
         double expected = nextExpected(y);
         double terminal = -3.92;
         assertFalse(flags(terminal, expected));
         y = nextExpected(y);
      }
   }

   @Test
   public void jumpBoostHeightFlags() {
      double y = 0.0;
      double expected = nextExpected(y);
      assertTrue(flags(0.9, expected));
   }

   @Test
   public void hoverReSyncsInsteadOfFlagging() {
      double y = 0.42;
      double expected = nextExpected(y);
      assertFalse(flags(0.0, expected));
   }

   @Test
   public void smallDesyncWithinUncertaintyDoesNotFlag() {
      double y = 0.42;
      double expected = nextExpected(y);
      assertFalse(flags(expected + 0.02, expected));
   }
}
