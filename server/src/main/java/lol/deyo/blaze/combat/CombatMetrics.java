package lol.deyo.blaze.combat;

import lol.deyo.blaze.configs.BlazeConfig;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.command.CommandSender;

public final class CombatMetrics {
   private static final Logger LOGGER = LogManager.getLogger("BlazeCombat");
   private static long attempts;
   private static long accepted;
   private static long rejectedReach;
   private static long rejectedLineOfSight;
   private static long rejectedAim;
   private static long rejectedRate;
   private static long rejectedInvalid;
   private static long missingHistory;
   private static long hitDelayRejected;
   private static long lastRewindNanos;
   private static long lastPingNanos;

   private CombatMetrics() {
   }

   public static void record(EntityPlayer attacker, EntityPlayer victim, CombatResult result) {
      attempts++;
      lastRewindNanos = result.rewindNanos;
      lastPingNanos = (long)result.attackerPing * 1000000L;

      if (result.accepted) {
         accepted++;
      } else if ("reach".equals(result.reason)) {
         rejectedReach++;
      } else if ("line-of-sight".equals(result.reason)) {
         rejectedLineOfSight++;
      } else if ("aim".equals(result.reason)) {
         rejectedAim++;
      } else if ("attack-rate".equals(result.reason)) {
         rejectedRate++;
      } else {
         rejectedInvalid++;
      }

      if (!result.historical) {
         missingHistory++;
      }

      if ((Boolean)BlazeConfig.combatDebug.getValue()) {
         LOGGER.info(
            "combat {} -> {} accepted={} reason={} ping={}ms jitter={}ms rewind={}ms distance={} max={} historical={}",
            new Object[]{
               attacker.getName(),
               victim == null ? "entity" : victim.getName(),
               result.accepted,
               result.reason,
               result.attackerPing,
               result.attackerJitter,
               (int)(result.rewindNanos / 1000000L),
               String.format("%.3f", result.distance),
               String.format("%.3f", result.maxReach),
               result.historical
            }
         );
      }
   }

   public static void recordHitDelayReject() {
      hitDelayRejected++;
      if ((Boolean)BlazeConfig.combatDebug.getValue()) {
         LOGGER.info("combat hit-delay rejected");
      }
   }

   public static void print(CommandSender sender) {
      sender.sendMessage("[Blaze Combat] attempts=" + attempts + " accepted=" + accepted);
      sender.sendMessage(
         "[Blaze Combat] rejected reach=" + rejectedReach
            + " los=" + rejectedLineOfSight
            + " aim=" + rejectedAim
            + " rate=" + rejectedRate
            + " invalid=" + rejectedInvalid
      );
      sender.sendMessage(
         "[Blaze Combat] missing-history=" + missingHistory
            + " hit-delay=" + hitDelayRejected
            + " last-ping=" + (int)(lastPingNanos / 1000000L) + "ms"
            + " last-rewind=" + (int)(lastRewindNanos / 1000000L) + "ms"
      );
   }
}
