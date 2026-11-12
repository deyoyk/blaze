package lol.deyo.blaze.combat;

import lol.deyo.blaze.configs.BlazeConfig;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.MovingObjectPosition;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import net.minecraft.server.v1_8_R3.Vec3D;
import net.minecraft.server.v1_8_R3.WorldSettings;

/**
 * Lag-compensated melee validation. The victim's hitbox is rewound to the
 * position the attacker saw when the attack packet was created.
 */
public final class CombatValidator {
   private CombatValidator() {
   }

   public static CombatResult validate(EntityPlayer attacker, Entity target, PacketPlayInUseEntity packet) {
      CombatResult result = new CombatResult();
      long receive = packet.receiveNanos == 0L ? System.nanoTime() : packet.receiveNanos;
      PingTracker ping = attacker.playerConnection.pingTracker;
      long pingNanos = ping.getPingNanos();
      result.attackerPing = ping.getPingMillis();
      result.attackerJitter = ping.getJitterMillis();
      result.clickNanos = Math.max(0L, receive - pingNanos / 2L);

      if (target == null || target == attacker || target.dead || !target.aD()) {
         result.reason = "invalid";
         return result;
      }
      if (attacker.isSpectator() || target instanceof EntityHuman && ((EntityHuman)target).isSpectator()) {
         result.reason = "invalid";
         return result;
      }
      if (target.world != attacker.world) {
         result.reason = "invalid";
         return result;
      }

      long rewindNanos = (long)(pingNanos * (Double)BlazeConfig.combatRewindMultiplier.getValue());
      long maxRewind = (long)(Integer)BlazeConfig.combatMaxRewindMillis.getValue() * 1000000L;
      if (rewindNanos < 0L) {
         rewindNanos = 0L;
      }
      if (rewindNanos > maxRewind) {
         rewindNanos = maxRewind;
      }
      result.rewindNanos = rewindNanos;

      if (target instanceof EntityPlayer) {
         EntityPlayer victim = (EntityPlayer)target;
         if (victim.playerConnection != null
            && victim.playerConnection.positionHistory.snapshotAt(receive - rewindNanos, result.victim)) {
            result.historical = true;
         }
      }
      if (!result.victim.valid) {
         result.victim.set(target, receive);
      }

      double eyeX = attacker.locX;
      double eyeY = attacker.locY + attacker.getHeadHeight();
      double eyeZ = attacker.locZ;
      result.eyeX = eyeX;
      result.eyeY = eyeY;
      result.eyeZ = eyeZ;

      double closestX = clamp(eyeX, result.victim.minX, result.victim.maxX);
      double closestY = clamp(eyeY, result.victim.minY, result.victim.maxY);
      double closestZ = clamp(eyeZ, result.victim.minZ, result.victim.maxZ);
      double dx = eyeX - closestX;
      double dy = eyeY - closestY;
      double dz = eyeZ - closestZ;
      result.distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

      boolean creative = attacker.playerInteractManager.getGameMode() == WorldSettings.EnumGamemode.CREATIVE;
      double maxReach = (Double)(creative ? BlazeConfig.combatCreativeReach.getValue() : BlazeConfig.combatSurvivalReach.getValue());
      maxReach += (Double)BlazeConfig.combatReachTolerance.getValue();
      result.maxReach = maxReach;

      if (result.distance > maxReach) {
         result.reason = "reach";
         return result;
      }

      if ((Boolean)BlazeConfig.combatLineOfSight.getValue()
         && result.distance > 1.5
         && !hasLineOfSight(attacker, result.victim)) {
         result.reason = "line-of-sight";
         return result;
      }

      double minAimDot = (Double)BlazeConfig.combatMinAimDot.getValue();
      if (minAimDot > 0.0 && !isAimingAt(attacker, result.victim, minAimDot)) {
         result.reason = "aim";
         return result;
      }

      long rateLimit = (long)(Integer)BlazeConfig.combatAttackRateLimitMs.getValue() * 1000000L;
      if (rateLimit > 0L && receive - attacker.playerConnection.lastAttackPacketNanos < rateLimit) {
         result.reason = "attack-rate";
         return result;
      }
      attacker.playerConnection.lastAttackPacketNanos = receive;
      result.accepted = true;
      return result;
   }

   private static boolean hasLineOfSight(EntityPlayer attacker, CombatSnapshot victim) {
      Vec3D eye = new Vec3D(attacker.locX, attacker.locY + attacker.getHeadHeight(), attacker.locZ);
      double tolerance = (Double)BlazeConfig.combatLineOfSightTolerance.getValue();
      double centerY = (victim.minY + victim.maxY) * 0.5;
      double[] offsets = new double[]{victim.maxY - 0.1, centerY, victim.minY + 0.4};

      for (double y : offsets) {
         Vec3D target = new Vec3D(victim.x, y, victim.z);
         double dx = target.a - eye.a;
         double dy = target.b - eye.b;
         double dz = target.c - eye.c;
         double targetDistance = Math.sqrt(dx * dx + dy * dy + dz * dz);
         if (targetDistance <= 0.1) {
            return true;
         }

         MovingObjectPosition hit = attacker.world.rayTrace(eye, target, false);
         if (hit == null || hit.type != MovingObjectPosition.EnumMovingObjectType.BLOCK) {
            return true;
         }

         double bx = hit.pos.a - eye.a;
         double by = hit.pos.b - eye.b;
         double bz = hit.pos.c - eye.c;
         double blockDistance = Math.sqrt(bx * bx + by * by + bz * bz);
         if (blockDistance + tolerance >= targetDistance) {
            return true;
         }
      }

      return false;
   }

   private static boolean isAimingAt(EntityPlayer attacker, CombatSnapshot victim, double minimumDot) {
      double yaw = Math.toRadians(attacker.yaw);
      double pitch = Math.toRadians(attacker.pitch);
      double lookX = -Math.sin(yaw) * Math.cos(pitch);
      double lookY = -Math.sin(pitch);
      double lookZ = Math.cos(yaw) * Math.cos(pitch);
      double dx = victim.x - attacker.locX;
      double dy = (victim.minY + victim.maxY) * 0.5 - (attacker.locY + attacker.getHeadHeight());
      double dz = victim.z - attacker.locZ;
      double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
      if (length < 1.0E-4) {
         return true;
      }
      double dot = (lookX * dx + lookY * dy + lookZ * dz) / length;
      return dot >= minimumDot;
   }

   private static double clamp(double value, double minimum, double maximum) {
      return value < minimum ? minimum : (value > maximum ? maximum : value);
   }
}
