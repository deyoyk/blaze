package lol.deyo.blaze.anticheat.checks;

import lol.deyo.blaze.anticheat.ACManager;
import lol.deyo.blaze.anticheat.ACPlayer;
import lol.deyo.blaze.anticheat.AnticheatConfig;
import lol.deyo.blaze.anticheat.Check;
import net.minecraft.server.v1_8_R3.EntityPlayer;

public class ReachCheck extends Check {
   private static final double MAX_SURVIVABLE_REACH = 4.2;
   private static final double HITBOX_MARGIN = 0.1;

   public ReachCheck(ACManager manager) {
      super(manager, "Reach");
   }

   @Override
   public boolean enabled() {
      return (Boolean)AnticheatConfig.combatEnabled.getValue()
         && (Boolean)AnticheatConfig.reachEnabled.getValue();
   }

   @Override
   public void onAttack(ACPlayer data, long nowNanos) {
      EntityPlayer player = data.player;
      if (!data.hasVictim() || player.vehicle != null) {
         return;
      }

      if (player.ticksLived < 60) {
         return;
      }

      double ex = player.locX;
      double ey = player.locY + player.getHeadHeight();
      double ez = player.locZ;
      double vx = data.getVictimX();
      double vy = data.getVictimY();
      double vz = data.getVictimZ();
      double dx = vx - ex;
      double dy = vy - ey;
      double dz = vz - ez;
      double centerDist = Math.sqrt(dx * dx + dy * dy + dz * dz);
      double lookX = -Math.sin(Math.toRadians(player.yaw)) * Math.cos(Math.toRadians(player.pitch));
      double lookY = -Math.sin(Math.toRadians(player.pitch));
      double lookZ = Math.cos(Math.toRadians(player.yaw)) * Math.cos(Math.toRadians(player.pitch));
      double half = 0.4 + HITBOX_MARGIN;
      double rayDist = ReachMath.minAttackDistance(
         ex, ey, ez, lookX, lookY, lookZ,
         vx - half, vy - half, vz - half, vx + half, vy + half, vz + half,
         centerDist
      );
      double boxDist = ReachMath.pointBoxDistance(
         ex, ey, ez,
         vx - half, vy - half, vz - half, vx + half, vy + half, vz + half
      );
      double dist = Math.min(rayDist, boxDist);
      double allowed = allowedReach((Double)AnticheatConfig.reachMaxDistance.getValue(), data.getPingMillis());

      if (centerDist > MAX_SURVIVABLE_REACH) {
         if (this.flag(data, 4.0, String.format("dist=%.2f hard", centerDist))) {
            this.setback(data);
         }
      } else if (dist > allowed) {
         this.flag(data, 1.0, String.format("dist=%.2f max=%.2f", dist, allowed));
      }
   }

   public static double allowedReach(double configMax, int pingMillis) {
      return configMax + pingMillis / 1000.0 * 0.4;
   }

   private void setback(ACPlayer data) {
      if ((Boolean)AnticheatConfig.setbackOnViolation.getValue() && data.hasSafePosition()) {
         data.player.playerConnection.a(data.getSafeX(), data.getSafeY(), data.getSafeZ(), data.player.yaw, data.player.pitch);
      }
   }
}
