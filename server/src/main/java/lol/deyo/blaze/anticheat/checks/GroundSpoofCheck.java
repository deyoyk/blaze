package lol.deyo.blaze.anticheat.checks;

import lol.deyo.blaze.anticheat.ACManager;
import lol.deyo.blaze.anticheat.ACPlayer;
import lol.deyo.blaze.anticheat.Check;
import lol.deyo.blaze.anticheat.AnticheatConfig;
import net.minecraft.server.v1_8_R3.EntityPlayer;

public class GroundSpoofCheck extends Check {
   public GroundSpoofCheck(ACManager manager) {
      super(manager, "GroundSpoof");
   }

   @Override
   public boolean enabled() {
      return (Boolean)AnticheatConfig.movementEnabled.getValue()
         && (Boolean)AnticheatConfig.groundSpoofEnabled.getValue();
   }

   @Override
   public void onFlying(ACPlayer data, long nowNanos) {
      EntityPlayer player = data.player;
      if (player.vehicle != null || data.hasTeleportedSinceLastMove() || player.playerConnection.hasPendingTeleport()) {
         return;
      }

      boolean clientGround = data.isOnGround();
      double deltaY = data.getDeltaY();
      if (clientGround && deltaY > 0.001) {
         this.flag(data, 1.0, String.format("ground-up dy=%.4f", deltaY));
      } else if (!clientGround && data.wasOnGround() && deltaY < -3.0) {
         this.flag(data, 1.0, String.format("void-drop dy=%.4f", deltaY));
      }
   }
}
