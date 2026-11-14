package lol.deyo.blaze.anticheat.checks;

import lol.deyo.blaze.anticheat.ACManager;
import lol.deyo.blaze.anticheat.ACPlayer;
import lol.deyo.blaze.anticheat.AnticheatConfig;
import lol.deyo.blaze.anticheat.Check;
import net.minecraft.server.v1_8_R3.EntityPlayer;

public class ReachCheck extends Check {
   private static final double MAX_SURVIVABLE_REACH = 4.2;

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
      double ey = player.locY + 1.62;
      double ez = player.locZ;
      double dx = data.getVictimX() - ex;
      double dy = data.getVictimY() - ey;
      double dz = data.getVictimZ() - ez;
      double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
      double allowed = allowedReach((Double)AnticheatConfig.reachMaxDistance.getValue(), data.getPingMillis());

      if (dist > MAX_SURVIVABLE_REACH) {
         if (this.flag(data, 4.0, String.format("dist=%.2f hard", dist))) {
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
