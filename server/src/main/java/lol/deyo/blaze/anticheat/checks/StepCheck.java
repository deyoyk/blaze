package lol.deyo.blaze.anticheat.checks;

import lol.deyo.blaze.anticheat.ACManager;
import lol.deyo.blaze.anticheat.ACPlayer;
import lol.deyo.blaze.anticheat.Check;
import lol.deyo.blaze.anticheat.AnticheatConfig;
import net.minecraft.server.v1_8_R3.EntityPlayer;

public class StepCheck extends Check {
   public StepCheck(ACManager manager) {
      super(manager, "Step");
   }

   @Override
   public boolean enabled() {
      return (Boolean)AnticheatConfig.movementEnabled.getValue()
         && (Boolean)AnticheatConfig.stepEnabled.getValue();
   }

   @Override
   public void onFlying(ACPlayer data, long nowNanos) {
      EntityPlayer player = data.player;
      if (player.vehicle != null || data.hasTeleportedSinceLastMove() || player.playerConnection.hasPendingTeleport()) {
         return;
      }

      double deltaY = data.getDeltaY();
      if (deltaY > (Double)AnticheatConfig.stepMaxHeight.getValue() && data.wasOnGround() && !player.inWater) {
         this.flag(data, Math.min(2.0, deltaY), String.format("dy=%.3f", deltaY));
      }
   }
}
