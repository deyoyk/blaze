package lol.deyo.blaze.combat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CombatMathTest {
   @Test
   public void pingTrackerFollowsRttAndJitter() {
      PingTracker tracker = new PingTracker();
      tracker.onKeepAliveSent(1, 0L);
      tracker.onKeepAliveResponse(1, 100000000L);
      assertEquals(100, tracker.getPingMillis());
      assertEquals(0, tracker.getJitterMillis());

      tracker.onKeepAliveSent(2, 500000000L);
      tracker.onKeepAliveResponse(2, 700000000L);
      assertEquals(125, tracker.getPingMillis());
      assertEquals(25, tracker.getJitterMillis());
   }

   @Test
   public void snapshotInterpolationMovesHitboxAndPosition() {
      CombatSnapshot before = new CombatSnapshot();
      before.valid = true;
      before.timeNanos = 0L;
      before.minX = 0.0;
      before.minY = 0.0;
      before.minZ = 0.0;
      before.maxX = 1.0;
      before.maxY = 2.0;
      before.maxZ = 1.0;
      before.x = 0.5;
      before.y = 0.0;
      before.z = 0.5;

      CombatSnapshot after = new CombatSnapshot();
      after.valid = true;
      after.timeNanos = 100000000L;
      after.minX = 2.0;
      after.minY = 0.0;
      after.minZ = 0.0;
      after.maxX = 3.0;
      after.maxY = 2.0;
      after.maxZ = 1.0;
      after.x = 2.5;
      after.y = 0.0;
      after.z = 0.5;

      CombatSnapshot result = new CombatSnapshot();
      result.interpolate(before, after, 0.5);
      assertTrue(result.valid);
      assertEquals(1.0, result.minX, 0.0001);
      assertEquals(2.0, result.maxX, 0.0001);
      assertEquals(1.5, result.x, 0.0001);
   }
}
