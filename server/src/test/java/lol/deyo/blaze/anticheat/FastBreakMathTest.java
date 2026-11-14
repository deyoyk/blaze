package lol.deyo.blaze.anticheat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import lol.deyo.blaze.anticheat.checks.FastBreakCheck;
import org.junit.Test;

public class FastBreakMathTest {
   private static final double TICK_NANOS = 50000000.0;

   @Test
   public void instantBreakBlocksAreExempt() {
      assertEquals(-1.0, FastBreakCheck.minBreakNanos(0.0F), 0.0001);
      assertEquals(-1.0, FastBreakCheck.minBreakNanos(-0.5F), 0.0001);
   }

   @Test
   public void stoneBreakRequiresFractionOfVanillaTime() {
      double damage = 1.5F;
      double expected = damage / 0.1 * TICK_NANOS * 0.7;
      assertEquals(expected, FastBreakCheck.minBreakNanos((float)damage), 1.0);
   }

   @Test
   public void longBreaksAreNotExempt() {
      double damage = 25.0F;
      double expected = damage / 0.1 * TICK_NANOS * 0.7;
      assertEquals(expected, FastBreakCheck.minBreakNanos((float)damage), 1.0);
   }

   @Test
   public void woodBreakRequiresFractionOfVanillaTime() {
      double damage = 2.0F;
      double expected = damage / 0.1 * TICK_NANOS * 0.7;
      assertEquals(expected, FastBreakCheck.minBreakNanos((float)damage), 1.0);
   }
}
