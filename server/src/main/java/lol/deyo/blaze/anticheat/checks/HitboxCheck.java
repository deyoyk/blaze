package lol.deyo.blaze.anticheat.checks;

import lol.deyo.blaze.anticheat.ACManager;
import lol.deyo.blaze.anticheat.ACPlayer;
import lol.deyo.blaze.anticheat.AnticheatConfig;
import lol.deyo.blaze.anticheat.Check;
import net.minecraft.server.v1_8_R3.EntityPlayer;

public class HitboxCheck extends Check {
   private static final double RAY_MARGIN = 0.25;

   public HitboxCheck(ACManager manager) {
      super(manager, "Hitbox");
   }

   @Override
   public boolean enabled() {
      return (Boolean)AnticheatConfig.combatEnabled.getValue()
         && (Boolean)AnticheatConfig.hitboxEnabled.getValue();
   }

   @Override
   public void onAttack(ACPlayer data, long nowNanos) {
      double aim = this.aimDot(data);
      double maxAngle = (Double)AnticheatConfig.hitboxMaxAngle.getValue();
      if (aim >= maxAngle || this.rayPass(data)) {
         return;
      }

      this.flag(data, 1.0, String.format("dot=%.3f min=%.3f", aim, maxAngle));
   }

   private boolean rayPass(ACPlayer data) {
      EntityPlayer player = data.player;
      double ex = player.locX;
      double ey = player.locY + player.getHeadHeight();
      double ez = player.locZ;
      double yawRad = Math.toRadians(player.yaw);
      double pitchRad = Math.toRadians(player.pitch);
      double lookX = -Math.sin(yawRad) * Math.cos(pitchRad);
      double lookY = -Math.sin(pitchRad);
      double lookZ = Math.cos(yawRad) * Math.cos(pitchRad);
      double vx = data.getVictimX();
      double vy = data.getVictimY();
      double vz = data.getVictimZ();
      double half = 0.4 + RAY_MARGIN;
      return ReachMath.rayIntersectsBox(
         ex, ey, ez, lookX, lookY, lookZ,
         vx - half, vy - half, vz - half, vx + half, vy + half, vz + half
      );
   }

   private double aimDot(ACPlayer data) {
      double yawRad = Math.toRadians(data.getYaw());
      double pitchRad = Math.toRadians(data.getPitch());
      double lookX = -Math.sin(yawRad) * Math.cos(pitchRad);
      double lookY = -Math.sin(pitchRad);
      double lookZ = Math.cos(yawRad) * Math.cos(pitchRad);
      double dx = data.getVictimX() - data.player.locX;
      double dy = data.getVictimY() - (data.player.locY + data.player.getHeadHeight());
      double dz = data.getVictimZ() - data.player.locZ;
      double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
      if (length < 1.0E-4) {
         return 1.0;
      }

      return (lookX * dx + lookY * dy + lookZ * dz) / length;
   }
}
