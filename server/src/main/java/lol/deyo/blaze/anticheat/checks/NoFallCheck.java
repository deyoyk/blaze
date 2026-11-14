package lol.deyo.blaze.anticheat.checks;

import lol.deyo.blaze.anticheat.ACManager;
import lol.deyo.blaze.anticheat.ACPlayer;
import lol.deyo.blaze.anticheat.Check;
import lol.deyo.blaze.anticheat.AnticheatConfig;
import net.minecraft.server.v1_8_R3.EntityPlayer;

public class NoFallCheck extends Check {
   private double fallDistance;

   public NoFallCheck(ACManager manager) {
      super(manager, "NoFall");
   }

   @Override
   public boolean enabled() {
      return (Boolean)AnticheatConfig.movementEnabled.getValue()
         && (Boolean)AnticheatConfig.nofallEnabled.getValue();
   }

   @Override
   public void onFlying(ACPlayer data, long nowNanos) {
      EntityPlayer player = data.player;
      if (player.vehicle != null || player.inWater || data.hasTeleportedSinceLastMove() || player.playerConnection.hasPendingTeleport()) {
         this.fallDistance = 0.0;
         return;
      }

      double deltaY = data.getDeltaY();
      if (deltaY < 0.0) {
         this.fallDistance -= deltaY;
      } else if (deltaY > 0.0) {
         this.fallDistance = 0.0;
      }

      if (data.isOnGround() && this.fallDistance > (Double)AnticheatConfig.nofallMinFallDistance.getValue()) {
         if (player.noDamageTicks <= 0) {
            this.flag(data, 1.0, String.format("fall=%.2f no-damage", this.fallDistance));
         }

         this.fallDistance = 0.0;
      }
   }
}
