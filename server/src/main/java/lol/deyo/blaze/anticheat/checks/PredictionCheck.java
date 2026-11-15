package lol.deyo.blaze.anticheat.checks;

import lol.deyo.blaze.anticheat.ACManager;
import lol.deyo.blaze.anticheat.ACPlayer;
import lol.deyo.blaze.anticheat.AnticheatConfig;
import lol.deyo.blaze.anticheat.Check;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.MobEffectList;

public class PredictionCheck extends Check {
   private static final double GRAVITY = 0.08;
   private static final double DRAG = 0.98;
   private static final double UNCERTAINTY = 0.03;
   private static final double JUMP_HEIGHT = 0.42;
   private int airborneTicks;
   private double expectedY;
   private boolean tracking;

   public PredictionCheck(ACManager manager) {
      super(manager, "Prediction");
   }

   @Override
   public boolean enabled() {
      return (Boolean)AnticheatConfig.movementEnabled.getValue()
         && (Boolean)AnticheatConfig.predictionEnabled.getValue();
   }

   @Override
   public void onFlying(ACPlayer data, long nowNanos) {
      EntityPlayer player = data.player;
      if (player.abilities.canFly
         || player.isSpectator()
         || player.vehicle != null
         || player.inWater
         || player.k_()
         || data.hasTeleportedSinceLastMove()
         || player.playerConnection.hasPendingTeleport()
         || player.ticksLived < 60) {
         this.tracking = false;
         this.airborneTicks = 0;
         return;
      }

      double deltaY = data.getDeltaY();
      if (!this.tracking) {
         this.tracking = true;
         this.expectedY = deltaY;
         this.airborneTicks = data.isOnGround() ? 0 : 1;
         return;
      }

      boolean clientGround = data.isOnGround();
      if (clientGround || Math.abs(deltaY) > 1.0) {
         this.tracking = false;
         this.airborneTicks = 0;
         return;
      }

      this.airborneTicks++;
      double expected = this.expectedY;
      if (this.airborneTicks == 1 && expected <= 0.0 && player.getEffect(MobEffectList.JUMP) != null) {
         this.tracking = false;
         return;
      }

      if (this.airborneTicks == 1 && data.wasOnGround() && deltaY > 0.35 && deltaY < 0.5) {
         expected = JUMP_HEIGHT;
      } else {
         expected = (expected - GRAVITY) * DRAG;
      }

      double error = Math.abs(deltaY - expected);
      this.expectedY = expected;

      if (error > UNCERTAINTY && Math.abs(deltaY) < 0.9) {
         if (error > (Double)AnticheatConfig.predictionMaxError.getValue()) {
            this.flag(data, 2.0, String.format("dy=%.3f exp=%.3f err=%.3f", deltaY, expected, error));
         } else {
            this.flag(data, 1.0, String.format("dy=%.3f exp=%.3f err=%.3f", deltaY, expected, error));
         }

         this.tracking = false;
      }
   }
}
