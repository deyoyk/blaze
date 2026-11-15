package lol.deyo.blaze.anticheat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ViolationTrackerTest {
   @Test
   public void penalizeAccumulatesWeightsWithDecay() {
      ViolationTracker tracker = new ViolationTracker();
      StubCheck check = new StubCheck("Speed");
      tracker.register(check, 0.5);
      double level = tracker.penalize("Speed", 1.0, 1000000000L);
      assertEquals(1.0, level, 0.0001);

      double second = tracker.penalize("Speed", 1.5, 2000000000L);
      assertEquals(2.0, second, 0.0001);
   }

   @Test
   public void decayReducesLevelOverTime() {
      ViolationTracker tracker = new ViolationTracker();
      tracker.register(new StubCheck("Speed"), 1.0);
      tracker.penalize("Speed", 2.0, 0L);
      double decayed = tracker.penalize("Speed", 0.0, 1000000000L);
      assertEquals(1.0, decayed, 0.0001);
   }

   @Test
   public void decayNeverBelowZero() {
      ViolationTracker tracker = new ViolationTracker();
      tracker.register(new StubCheck("Speed"), 10.0);
      tracker.penalize("Speed", 1.0, 0L);
      double decayed = tracker.penalize("Speed", 0.0, 10000000000L);
      assertEquals(0.0, decayed, 0.0001);
   }

   @Test
   public void unknownCheckReturnsWeightOnly() {
      ViolationTracker tracker = new ViolationTracker();
      double level = tracker.penalize("Missing", 3.0, 0L);
      assertEquals(3.0, level, 0.0001);
   }

   @Test
   public void resetClearsAllLevels() {
      ViolationTracker tracker = new ViolationTracker();
      tracker.register(new StubCheck("Speed"), 0.0);
      tracker.register(new StubCheck("Flight"), 0.0);
      tracker.penalize("Speed", 5.0, 0L);
      tracker.penalize("Flight", 5.0, 0L);
      tracker.reset();
      assertEquals(0.0, tracker.levelOf("Speed"), 0.0001);
      assertEquals(0.0, tracker.levelOf("Flight"), 0.0001);
   }

   @Test
   public void registerGrowsBeyondInitialCapacity() {
      ViolationTracker tracker = new ViolationTracker();
      for (int i = 0; i < 20; i++) {
         tracker.register(new StubCheck("Check" + i), 0.1);
      }

      assertEquals(20, tracker.size());
      tracker.penalize("Check19", 2.0, 0L);
      assertEquals(2.0, tracker.levelOf("Check19"), 0.0001);
      assertTrue(tracker.levelOf("Check0") == 0.0);
   }

   @Test
   public void violationCountIncrementsPerPenalize() {
      ViolationTracker tracker = new ViolationTracker();
      tracker.register(new StubCheck("Speed"), 0.0);
      assertEquals(0, tracker.violationCount("Speed"));
      tracker.penalize("Speed", 1.0, 0L);
      tracker.penalize("Speed", 1.0, 1000000000L);
      tracker.penalize("Speed", 1.0, 2000000000L);
      assertEquals(3, tracker.violationCount("Speed"));
      assertEquals(0, tracker.violationCount("Flight"));
   }

   @Test
   public void resetSingleCheckClearsLevelOnly() {
      ViolationTracker tracker = new ViolationTracker();
      tracker.register(new StubCheck("Speed"), 0.0);
      tracker.penalize("Speed", 5.0, 0L);
      tracker.penalize("Speed", 5.0, 1000L);
      tracker.reset("Speed");
      assertEquals(0.0, tracker.levelOf("Speed"), 0.0001);
      assertEquals(2, tracker.violationCount("Speed"));
   }

   @Test
   public void resetViolationsClearsLevelAndCount() {
      ViolationTracker tracker = new ViolationTracker();
      tracker.register(new StubCheck("Speed"), 0.0);
      tracker.penalize("Speed", 5.0, 0L);
      tracker.penalize("Speed", 5.0, 1000L);
      tracker.resetViolations("Speed");
      assertEquals(0.0, tracker.levelOf("Speed"), 0.0001);
      assertEquals(0, tracker.violationCount("Speed"));
   }

   @Test
   public void resetClearsViolationCountsToo() {
      ViolationTracker tracker = new ViolationTracker();
      tracker.register(new StubCheck("Speed"), 0.0);
      tracker.register(new StubCheck("Flight"), 0.0);
      tracker.penalize("Speed", 5.0, 0L);
      tracker.penalize("Flight", 5.0, 0L);
      tracker.reset();
      assertEquals(0, tracker.violationCount("Speed"));
      assertEquals(0, tracker.violationCount("Flight"));
   }

   @Test
   public void violationCountGrowsBeyondInitialCapacity() {
      ViolationTracker tracker = new ViolationTracker();
      for (int i = 0; i < 20; i++) {
         tracker.register(new StubCheck("Check" + i), 0.0);
      }

      tracker.penalize("Check19", 2.0, 0L);
      tracker.penalize("Check19", 2.0, 1000L);
      assertEquals(2, tracker.violationCount("Check19"));
      assertEquals(2, tracker.violationCountAt(19));
   }

   private static final class StubCheck extends Check {
      StubCheck(String name) {
         super(null, name);
      }

      @Override
      public boolean enabled() {
         return true;
      }
   }
}
