package lol.deyo.blaze.anticheat.checks;

import lol.deyo.blaze.anticheat.ACManager;
import lol.deyo.blaze.anticheat.ACPlayer;
import lol.deyo.blaze.anticheat.Check;
import lol.deyo.blaze.anticheat.AnticheatConfig;
import net.minecraft.server.v1_8_R3.EntityPlayer;

public class FlightCheck extends Check {
   private int airborneUpTicks;

   public FlightCheck(ACManager manager) {
      super(manager, "Flight");
   }

   @Override
   public boolean enabled() {
      return (Boolean)AnticheatConfig.movementEnabled.getValue()
         && (Boolean)AnticheatConfig.flightEnabled.getValue();
   }

   @Override
   public void onFlying(ACPlayer data, long nowNanos) {
      EntityPlayer player = data.player;
      if (player.abilities.canFly
         || player.isSpectator()
         || player.vehicle != null
         || player.inWater
         || data.hasTeleportedSinceLastMove()
         || player.playerConnection.hasPendingTeleport()
         || player.ticksLived < 60) {
         this.airborneUpTicks = 0;
         return;
      }

      double deltaY = data.getDeltaY();
      if (!data.isOnGround() && deltaY > -0.075 && deltaY >= data.getLastDeltaY() - 0.001) {
         this.airborneUpTicks++;
         if (this.airborneUpTicks > (Integer)AnticheatConfig.flightMaxAirTicks.getValue()) {
            if (this.flag(data, 1.5, String.format("air=%d dy=%.4f", this.airborneUpTicks, deltaY))) {
               this.airborneUpTicks = 0;
               this.setback(data);
            }
         }
      } else {
         this.airborneUpTicks = 0;
      }
   }

   private void setback(ACPlayer data) {
      if ((Boolean)AnticheatConfig.setbackOnViolation.getValue() && data.hasSafePosition()) {
         data.player.playerConnection.a(data.getSafeX(), data.getSafeY(), data.getSafeZ(), data.player.yaw, data.player.pitch);
      }
   }
}
