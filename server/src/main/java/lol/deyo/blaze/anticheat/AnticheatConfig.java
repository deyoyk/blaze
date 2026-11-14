package lol.deyo.blaze.anticheat;

import lol.deyo.blaze.Blaze;
import lol.deyo.blaze.configs.extra.Config;
import lol.deyo.blaze.configs.extra.ConfigSetting;
import lol.deyo.blaze.utils.CC;

public class AnticheatConfig extends Config {
   public static final ConfigSetting<Boolean> enabled = new ConfigSetting<>(
      true, "general", "Master switch for the built-in anticheat."
   );
   public static final ConfigSetting<Boolean> debug = new ConfigSetting<>(
      false, "general", "Log every check failure for tuning."
   );
   public static final ConfigSetting<Boolean> alertOnConsole = new ConfigSetting<>(
      true, "general", "Print violations to console."
   );
   public static final ConfigSetting<String> alertPermission = new ConfigSetting<>(
      "blaze.ac.alerts", "general", "Permission required to receive and toggle alerts."
   );
   public static final ConfigSetting<Boolean> alertJoinMessage = new ConfigSetting<>(
      true, "general", "Tell players with alert permission that alerts are available on join."
   );
   public static final ConfigSetting<Integer> alertCooldownMs = new ConfigSetting<>(
      400, "general", "Minimum millis between alerts for the same player and check."
   );
   public static final ConfigSetting<Integer> maxViolationLevel = new ConfigSetting<>(
      20, "general", "Violation level that triggers the configured punishment."
   );
   public static final ConfigSetting<String> punishmentCommand = new ConfigSetting<>(
      "ban %player% Blaze Anticheat: Unfair advantage", "general", "Console command run when a player exceeds maxViolationLevel."
   );
   public static final ConfigSetting<Boolean> setbackOnViolation = new ConfigSetting<>(
      true, "general", "Rubber-band movement back to the last safe position on movement violations."
   );

   public static final ConfigSetting<String> storageType = new ConfigSetting<>(
      "SQLITE", "storage", "Storage backend: SQLITE or MYSQL."
   );
   public static final ConfigSetting<Boolean> storageLogViolations = new ConfigSetting<>(
      true, "storage", "Store violations in the database."
   );
   public static final ConfigSetting<Integer> storageFlushSeconds = new ConfigSetting<>(
      30, "storage", "How often queued violations are flushed to the database."
   );
   public static final ConfigSetting<String> mysqlHost = new ConfigSetting<>("localhost", "storage.mysql", false);
   public static final ConfigSetting<Integer> mysqlPort = new ConfigSetting<>(3306, "storage.mysql", false);
   public static final ConfigSetting<String> mysqlDatabase = new ConfigSetting<>("blaze", "storage.mysql", false);
   public static final ConfigSetting<String> mysqlUsername = new ConfigSetting<>("root", "storage.mysql", false);
   public static final ConfigSetting<String> mysqlPassword = new ConfigSetting<>("", "storage.mysql", false);
   public static final ConfigSetting<Boolean> mysqlUseSsl = new ConfigSetting<>(false, "storage.mysql", false);

   public static final ConfigSetting<Boolean> combatEnabled = new ConfigSetting<>(true, "checks.combat");
   public static final ConfigSetting<Boolean> killauraEnabled = new ConfigSetting<>(
      true, "checks.combat.killaura", "Post-attack swing ordering and impossible aim delta checks."
   );
   public static final ConfigSetting<Integer> killauraMaxPostNanos = new ConfigSetting<>(
      10000000, "checks.combat.killaura", "Max nanos after a flying packet in which an attack looks like packet-order manipulation."
   );
   public static final ConfigSetting<Double> killauraMaxAimDelta = new ConfigSetting<>(
      40.0, "checks.combat.killaura", "Max combined yaw+pitch delta while attacking."
   );
   public static final ConfigSetting<Boolean> autoclickerEnabled = new ConfigSetting<>(
      true, "checks.combat.autoclicker", "CPS cap and click-interval entropy checks."
   );
   public static final ConfigSetting<Integer> autoclickerMaxCps = new ConfigSetting<>(
      20, "checks.combat.autoclicker", "Maximum sustained clicks per second."
   );
   public static final ConfigSetting<Double> autoclickerMinEntropy = new ConfigSetting<>(
      0.006, "checks.combat.autoclicker", "Minimum click interval standard deviation in millis over a 20 click window."
   );
   public static final ConfigSetting<Boolean> velocityEnabled = new ConfigSetting<>(
      true, "checks.combat.velocity", "Detect knockback resistance by comparing applied motion to expected knockback."
   );
   public static final ConfigSetting<Double> velocityMinHorizontalRatio = new ConfigSetting<>(
      0.6, "checks.combat.velocity", "Fraction of expected horizontal knockback the client must take."
   );
   public static final ConfigSetting<Double> velocityMinVerticalRatio = new ConfigSetting<>(
      0.7, "checks.combat.velocity", "Fraction of expected vertical knockback the client must take."
   );
   public static final ConfigSetting<Boolean> hitboxEnabled = new ConfigSetting<>(
      true, "checks.combat.hitbox", "Attack look direction must stay within an angle of the victim."
   );
   public static final ConfigSetting<Double> hitboxMaxAngle = new ConfigSetting<>(
      0.35, "checks.combat.hitbox", "Minimum look-direction dot product toward the victim."
   );

   public static final ConfigSetting<Boolean> movementEnabled = new ConfigSetting<>(true, "checks.movement");
   public static final ConfigSetting<Boolean> speedEnabled = new ConfigSetting<>(
      true, "checks.movement.speed", "Horizontal speed limit derived from vanilla movement attributes."
   );
   public static final ConfigSetting<Double> speedMaxMultiplier = new ConfigSetting<>(
      1.1, "checks.movement.speed", "Allowed multiplier over the computed vanilla maximum speed."
   );
   public static final ConfigSetting<Boolean> flightEnabled = new ConfigSetting<>(
      true, "checks.movement.flight", "Vertical acceleration and hover checks for airborne players."
   );
   public static final ConfigSetting<Integer> flightMaxAirTicks = new ConfigSetting<>(
      12, "checks.movement.flight", "Consecutive non-falling airborne ticks allowed before flying is flagged."
   );
   public static final ConfigSetting<Boolean> stepEnabled = new ConfigSetting<>(
      true, "checks.movement.step", "Vertical movement per tick while on ground."
   );
   public static final ConfigSetting<Double> stepMaxHeight = new ConfigSetting<>(
      1.05, "checks.movement.step", "Maximum vertical rise per tick while stepping."
   );
   public static final ConfigSetting<Boolean> phaseEnabled = new ConfigSetting<>(
      true, "checks.movement.phase", "Detects movement through solid blocks via position sweep."
   );
   public static final ConfigSetting<Boolean> nofallEnabled = new ConfigSetting<>(
      true, "checks.movement.nofall", "Flags long falls that land without damage."
   );
   public static final ConfigSetting<Double> nofallMinFallDistance = new ConfigSetting<>(
      3.5, "checks.movement.nofall", "Fall distance in blocks that must have produced damage."
   );
   public static final ConfigSetting<Boolean> groundSpoofEnabled = new ConfigSetting<>(
      true, "checks.movement.groundspoof", "Client onGround state must be possible for the reported motion."
   );
   public static final ConfigSetting<Boolean> timerEnabled = new ConfigSetting<>(
      true, "checks.movement.timer", "Balance of client movement packets against real elapsed time."
   );
   public static final ConfigSetting<Double> timerMaxRate = new ConfigSetting<>(
      1.08, "checks.movement.timer", "Maximum ratio of movement packets to elapsed ticks."
   );
   public static final ConfigSetting<Boolean> safeWalkEnabled = new ConfigSetting<>(
      true, "checks.movement.safewalk", "Detects walking along block edges without sneaking."
   );
   public static final ConfigSetting<Integer> safeWalkMinEdgeTicks = new ConfigSetting<>(
      5, "checks.movement.safewalk", "Consecutive edge-walk ticks required before flagging."
   );
   public static final ConfigSetting<Boolean> autoBridgeEnabled = new ConfigSetting<>(
      true, "checks.movement.autobridge", "Detects block placement below feet while moving backward over air."
   );
   public static final ConfigSetting<Double> autoBridgeMaxPitch = new ConfigSetting<>(
      60.0, "checks.movement.autobridge", "Minimum downward pitch for a place to count as bridging."
   );
   public static final ConfigSetting<Boolean> jesusEnabled = new ConfigSetting<>(
      true, "checks.movement.jesus", "Detects walking on top of water at ground speed."
   );
   public static final ConfigSetting<Double> jesusMaxSpeed = new ConfigSetting<>(
      0.18, "checks.movement.jesus", "Maximum horizontal speed per tick while on ground over water."
   );
   public static final ConfigSetting<Boolean> noSlowEnabled = new ConfigSetting<>(
      true, "checks.movement.noslow", "Detects full-speed movement while eating or blocking."
   );
   public static final ConfigSetting<Double> noSlowMaxSpeed = new ConfigSetting<>(
      0.16, "checks.movement.noslow", "Maximum horizontal speed per tick while using an item."
   );
   public static final ConfigSetting<Integer> noSlowReleaseGraceTicks = new ConfigSetting<>(
      10, "checks.movement.noslow", "Ticks after releasing use item where speed is exempted."
   );

   public static final ConfigSetting<Boolean> packetsEnabled = new ConfigSetting<>(true, "checks.packets");
   public static final ConfigSetting<Boolean> badPacketsEnabled = new ConfigSetting<>(
      true, "checks.packets.badpackets", "Impossible rotations and client state."
   );
   public static final ConfigSetting<Double> badPacketsMaxPitch = new ConfigSetting<>(
      90.01, "checks.packets.badpackets", "Absolute pitch beyond this is flagged."
   );
   public static final ConfigSetting<Boolean> packetOrderEnabled = new ConfigSetting<>(
      true, "checks.packets.packetorder", "Swing, use and dig ordering that vanilla never produces."
   );
   public static final ConfigSetting<Integer> packetRateLimit = new ConfigSetting<>(
      500, "checks.packets", "Packets allowed per second before flagging; -1 disables."
   );

   public static final ConfigSetting<Boolean> worldEnabled = new ConfigSetting<>(true, "checks.world");
   public static final ConfigSetting<Boolean> fastPlaceEnabled = new ConfigSetting<>(
      true, "checks.world.fastplace", "Minimum interval between block placements."
   );
   public static final ConfigSetting<Integer> fastPlaceMinIntervalMs = new ConfigSetting<>(
      45, "checks.world.fastplace", "Minimum millis between block placements (vanilla allows ~4 per second)."
   );
   public static final ConfigSetting<Boolean> fastBreakEnabled = new ConfigSetting<>(
      true, "checks.world.fastbreak", "Minimum dig time relative to vanilla block break duration."
   );
   public static final ConfigSetting<Double> fastBreakMinFraction = new ConfigSetting<>(
      0.7, "checks.world.fastbreak", "Fraction of vanilla break duration the client must spend."
   );

   public AnticheatConfig(Blaze blaze) {
      super(
         blaze,
         "anticheat/config",
         CC.getConfigHeader("Blaze Anticheat", "All checks are lag compensated", "and never cancel attacks.")
      );
   }
}
