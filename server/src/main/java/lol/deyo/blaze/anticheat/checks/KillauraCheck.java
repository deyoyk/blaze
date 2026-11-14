package lol.deyo.blaze.anticheat.checks;

import lol.deyo.blaze.anticheat.ACManager;
import lol.deyo.blaze.anticheat.ACPlayer;
import lol.deyo.blaze.anticheat.AnticheatConfig;
import lol.deyo.blaze.anticheat.Check;
import net.minecraft.server.v1_8_R3.EntityPlayer;

public class KillauraCheck extends Check {
   private float lastDeltaYaw;
   private boolean exempt = true;

   public KillauraCheck(ACManager manager) {
      super(manager, "Killaura");
   }

   @Override
   public boolean enabled() {
      return (Boolean)AnticheatConfig.combatEnabled.getValue()
         && (Boolean)AnticheatConfig.killauraEnabled.getValue();
   }

   @Override
   public void onFlying(ACPlayer data, long nowNanos) {
      EntityPlayer player = data.player;
      if (data.hasTeleportedSinceLastMove() || player.playerConnection.hasPendingTeleport() || player.vehicle != null) {
         this.exempt = true;
         return;
      }

      if (this.exempt) {
         this.exempt = false;
         this.lastDeltaYaw = (float) data.getYawDelta();
         return;
      }

      if (data.getYawDelta() > 320.0
         && this.lastDeltaYaw < 30.0) {
         this.flag(data, 1.0, String.format("snap=%.0f prev=%.0f", data.getYawDelta(), this.lastDeltaYaw));
      }

      this.lastDeltaYaw = (float) data.getYawDelta();
   }

   @Override
   public void onAttack(ACPlayer data, long nowNanos) {
      long flying = data.getReceiveNanosLastFlying();
      if (flying > 0L) {
         long sinceFlying = nowNanos - flying;
         int threshold = (Integer)AnticheatConfig.killauraMaxPostNanos.getValue();
         if (sinceFlying >= 0L && sinceFlying < threshold) {
            this.flag(data, 0.5, String.format("post=%.1fms", sinceFlying / 1000000.0));
         } else if (sinceFlying > 1000000000L) {
            this.flag(data, 0.5, String.format("stale=%.1fms", sinceFlying / 1000000.0));
         }
      }

      double aim = data.getYawDelta() + data.getPitchDelta();
      double maxAim = (Double)AnticheatConfig.killauraMaxAimDelta.getValue();
      if (aim > maxAim) {
         this.flag(data, 1.0, String.format("aim=%.1f", aim));
      }
   }
}
