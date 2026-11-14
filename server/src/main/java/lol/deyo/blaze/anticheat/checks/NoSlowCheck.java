package lol.deyo.blaze.anticheat.checks;

import lol.deyo.blaze.anticheat.ACManager;
import lol.deyo.blaze.anticheat.ACPlayer;
import lol.deyo.blaze.anticheat.AnticheatConfig;
import lol.deyo.blaze.anticheat.Check;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.MobEffectList;

public class NoSlowCheck extends Check {
   private static final double SPRINT_MULTIPLIER = 1.3;

   private int graceTicks;

   public NoSlowCheck(ACManager manager) {
      super(manager, "NoSlow");
   }

   @Override
   public boolean enabled() {
      return (Boolean)AnticheatConfig.movementEnabled.getValue()
         && (Boolean)AnticheatConfig.noSlowEnabled.getValue();
   }

   @Override
   public void onFlying(ACPlayer data, long nowNanos) {
      EntityPlayer player = data.player;
      if (player.abilities.canFly
         || player.isSpectator()
         || player.vehicle != null
         || data.hasTeleportedSinceLastMove()
         || player.ticksLived < 60
         || player.playerConnection.hasPendingTeleport()) {
         this.graceTicks = 0;
         return;
      }

      boolean usingItem = player.isEating() || player.isBlocking();

      if (!usingItem) {
         this.graceTicks = 0;
         return;
      }

      double horizontal = data.getDeltaXZ();
      if (horizontal < 0.02) {
         return;
      }

      double maxSpeed = (Double)AnticheatConfig.noSlowMaxSpeed.getValue();
      if (data.isClientSprinting() || player.isSprinting()) {
         maxSpeed *= SPRINT_MULTIPLIER;
      }

      int speedEffect = player.hasEffect(MobEffectList.FASTER_MOVEMENT)
         ? player.getEffect(MobEffectList.FASTER_MOVEMENT).getAmplifier() : -1;
      if (speedEffect >= 0) {
         maxSpeed *= 1.0 + 0.2 * (speedEffect + 1);
      }

      if (horizontal > maxSpeed) {
         this.graceTicks++;
         if (this.graceTicks >= 3) {
            this.flag(data, 1.0, String.format("speed=%.3f max=%.3f", horizontal, maxSpeed));
            this.graceTicks = 0;
         }
      } else {
         this.graceTicks = 0;
      }
   }
}
