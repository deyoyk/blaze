package lol.deyo.blaze.anticheat.checks;

import lol.deyo.blaze.anticheat.ACManager;
import lol.deyo.blaze.anticheat.ACPlayer;
import lol.deyo.blaze.anticheat.Check;
import lol.deyo.blaze.anticheat.AnticheatConfig;

public class HitboxCheck extends Check {
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
      if (aim < maxAngle) {
         this.flag(data, 1.0, String.format("dot=%.3f min=%.3f", aim, maxAngle));
      }
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
