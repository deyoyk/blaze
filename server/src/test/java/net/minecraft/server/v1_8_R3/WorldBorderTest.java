package net.minecraft.server.v1_8_R3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class WorldBorderTest {
   @Test
   public void followsAnotherBorderWithoutTicking() {
      WorldBorder primary = new WorldBorder();
      primary.setSize(400.0);
      primary.setCenter(100.0, 200.0);
      primary.setDamageAmount(3.0);
      primary.setDamageBuffer(7.0);
      primary.setWarningDistance(9);
      primary.setWarningTime(11);

      WorldBorder secondary = new WorldBorder();
      secondary.follow(primary);

      assertEquals(400.0, secondary.getSize(), 0.0);
      assertEquals(100.0, secondary.getCenterX(), 0.0);
      assertEquals(200.0, secondary.getCenterZ(), 0.0);
      assertEquals(3.0, secondary.getDamageAmount(), 0.0);
      assertEquals(7.0, secondary.getDamageBuffer(), 0.0);
      assertEquals(9, secondary.getWarningDistance());
      assertEquals(11, secondary.getWarningTime());

      primary.setSize(500.0);
      assertEquals(500.0, secondary.getSize(), 0.0);

      primary.setCenter(300.0, 400.0);
      assertEquals(300.0, secondary.getCenterX(), 0.0);
      assertEquals(400.0, secondary.getCenterZ(), 0.0);

      primary.transitionSizeBetween(500.0, 1000.0, 10000L);
      assertEquals(1000.0, secondary.j(), 0.0);
      assertTrue(secondary.i() > 0L);
   }
}
