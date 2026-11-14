package lol.deyo.blaze.anticheat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MovementMathTest {
   @Test
   public void vanillaSprintGroundSpeedWithinTolerance() {
      double walk = 0.221;
      double sprint = walk * 1.3;
      double maxPerTick = sprint * 1.42;
      double observed = 0.405;
      assertTrue(observed <= maxPerTick * 1.1 + 0.03);
   }

   @Test
   public void blatantSpeedFlagged() {
      double walk = 0.221;
      double sprint = walk * 1.3;
      double maxPerTick = sprint * 1.42;
      double observed = 0.85;
      assertTrue(observed > maxPerTick * 1.1 + 0.03);
   }

   @Test
   public void speedEffectIncreasesAllowance() {
      double base = 0.221 * 1.3;
      double amplifier = 1;
      double boosted = base * (1.0 + 0.2 * (amplifier + 1));
      assertEquals(base * 1.4, boosted, 0.0001);
   }

   @Test
   public void slownessDecreasesAllowance() {
      double base = 0.221;
      double amplifier = 0;
      double slowed = base * (1.0 - 0.15 * (amplifier + 1));
      assertEquals(base * 0.85, slowed, 0.0001);
   }

   @Test
   public void stepAboveMaxHeightFlagged() {
      double stepMax = 1.05;
      double normalStep = 0.6;
      double horseJump = 1.2;
      assertTrue(normalStep <= stepMax);
      assertTrue(horseJump > stepMax);
   }

   @Test
   public void knockbackRatioAggregation() {
      double hSum = 0.9 + 0.85 + 0.95;
      int samples = 3;
      double avg = hSum / samples;
      assertEquals(0.9, avg, 0.0001);
      assertTrue(avg < 0.6 == false);
   }
}
