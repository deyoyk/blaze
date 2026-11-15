package lol.deyo.blaze.anticheat;

import org.junit.Assert;
import org.junit.Test;

public class PunishmentManagerTest {
   @Test
   public void testDurationParsing() {
      Assert.assertEquals(0L, PunishmentManager.parseDuration("0"));
      Assert.assertEquals(0L, PunishmentManager.parseDuration("perm"));
      Assert.assertEquals(0L, PunishmentManager.parseDuration("forever"));
      Assert.assertEquals(0L, PunishmentManager.parseDuration(null));
      Assert.assertEquals(86400000L, PunishmentManager.parseDuration("1d"));
      Assert.assertEquals(2L * 86400000L, PunishmentManager.parseDuration("2d"));
      Assert.assertEquals(7L * 86400000L, PunishmentManager.parseDuration("7d"));
      Assert.assertEquals(3600000L, PunishmentManager.parseDuration("1h"));
      Assert.assertEquals(1800000L, PunishmentManager.parseDuration("30m"));
      Assert.assertEquals(45000L, PunishmentManager.parseDuration("45s"));
      Assert.assertEquals(5L * 86400000L, PunishmentManager.parseDuration("5"));
   }

   @Test
   public void testParseEntry() {
      PunishmentManager manager = new PunishmentManager();
      PunishmentManager.ScheduledPunishment kick = manager.parseEntry("global", "6:KICK");
      Assert.assertNotNull(kick);
      Assert.assertEquals(6, kick.violationNumber);
      Assert.assertEquals(PunishmentManager.PunishmentType.KICK, kick.type);
      Assert.assertEquals(0L, kick.durationMillis);

      PunishmentManager.ScheduledPunishment ban = manager.parseEntry("Speed", "5:BAN:2d");
      Assert.assertNotNull(ban);
      Assert.assertEquals(5, ban.violationNumber);
      Assert.assertEquals(PunishmentManager.PunishmentType.BAN, ban.type);
      Assert.assertEquals(2L * 86400000L, ban.durationMillis);
      Assert.assertEquals("Speed", ban.source);

      Assert.assertNull(manager.parseEntry("Speed", "5"));
      Assert.assertNull(manager.parseEntry("Speed", "5:FLY"));
      Assert.assertNull(manager.parseEntry("Speed", "abc:BAN"));
      Assert.assertNull(manager.parseEntry("Speed", ""));
      Assert.assertNull(manager.parseEntry("Speed", null));
   }

   @Test
   public void testResolveFallsBackToGlobal() {
      PunishmentManager manager = new PunishmentManager();
      manager.schedule.put("global", java.util.Collections.singletonList(
         new PunishmentManager.ScheduledPunishment(10, PunishmentManager.PunishmentType.BAN, 86400000L, "global")));
      manager.schedule.put("speed", java.util.Arrays.asList(
         new PunishmentManager.ScheduledPunishment(3, PunishmentManager.PunishmentType.WARN, 0L, "Speed"),
         new PunishmentManager.ScheduledPunishment(6, PunishmentManager.PunishmentType.KICK, 0L, "Speed"),
         new PunishmentManager.ScheduledPunishment(9, PunishmentManager.PunishmentType.BAN, 7L * 86400000L, "Speed")));

      PunishmentManager.ScheduledPunishment speed1 = manager.resolve("Speed", 1);
      Assert.assertNull(speed1);

      PunishmentManager.ScheduledPunishment speed3 = manager.resolve("Speed", 3);
      Assert.assertNotNull(speed3);
      Assert.assertEquals(PunishmentManager.PunishmentType.WARN, speed3.type);

      PunishmentManager.ScheduledPunishment speed5 = manager.resolve("Speed", 5);
      Assert.assertEquals(PunishmentManager.PunishmentType.WARN, speed5.type);

      PunishmentManager.ScheduledPunishment speed6 = manager.resolve("Speed", 6);
      Assert.assertEquals(PunishmentManager.PunishmentType.KICK, speed6.type);

      PunishmentManager.ScheduledPunishment speed20 = manager.resolve("Speed", 20);
      Assert.assertEquals(PunishmentManager.PunishmentType.BAN, speed20.type);
      Assert.assertEquals(7L * 86400000L, speed20.durationMillis);

      PunishmentManager.ScheduledPunishment other = manager.resolve("Killaura", 12);
      Assert.assertNotNull(other);
      Assert.assertEquals(PunishmentManager.PunishmentType.BAN, other.type);
      Assert.assertEquals(86400000L, other.durationMillis);
      Assert.assertEquals("global", other.source);
   }

   @Test
   public void testResolveEmptySchedule() {
      PunishmentManager manager = new PunishmentManager();
      Assert.assertNull(manager.resolve("Speed", 10));
   }

   @Test
   public void testResolveExactMatchBoundaries() {
      PunishmentManager manager = new PunishmentManager();
      manager.schedule.put("speed", java.util.Arrays.asList(
         new PunishmentManager.ScheduledPunishment(5, PunishmentManager.PunishmentType.WARN, 0L, "Speed"),
         new PunishmentManager.ScheduledPunishment(10, PunishmentManager.PunishmentType.BAN, 0L, "Speed")));

      Assert.assertNull(manager.resolve("speed", 4));
      Assert.assertEquals(5, manager.resolve("speed", 5).violationNumber);
      Assert.assertEquals(5, manager.resolve("speed", 9).violationNumber);
      Assert.assertEquals(10, manager.resolve("speed", 10).violationNumber);
      Assert.assertEquals(10, manager.resolve("speed", 100).violationNumber);
   }

   @Test
   public void testRecordFields() {
      PunishmentManager.Record record = new PunishmentManager.Record(
         123456L, java.util.UUID.randomUUID(), "Notch", "Speed",
         PunishmentManager.PunishmentType.BAN, "Blaze Anticheat: Unfair advantage",
         1000L, 2000L, true);
      Assert.assertEquals(123456L, record.id);
      Assert.assertEquals("Notch", record.name);
      Assert.assertEquals("Speed", record.check);
      Assert.assertEquals(PunishmentManager.PunishmentType.BAN, record.type);
      Assert.assertEquals(1000L, record.createdAt);
      Assert.assertEquals(2000L, record.expires);
      Assert.assertTrue(record.active);
      record.active = false;
      Assert.assertFalse(record.active);
   }
}
