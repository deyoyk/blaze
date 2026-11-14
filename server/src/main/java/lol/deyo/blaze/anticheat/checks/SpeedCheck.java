package lol.deyo.blaze.anticheat.checks;

import lol.deyo.blaze.anticheat.ACManager;
import lol.deyo.blaze.anticheat.ACPlayer;
import lol.deyo.blaze.anticheat.Check;
import lol.deyo.blaze.anticheat.AnticheatConfig;
import net.minecraft.server.v1_8_R3.Block;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.Blocks;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.MobEffectList;

public class SpeedCheck extends Check {
   private static final double WALK_SPEED = 0.221;
   private static final double SPRINT_MULTIPLIER = 1.3;
   private static final double SNEAK_MULTIPLIER = 0.3;
   private static final double ICE_MULTIPLIER = 1.6;
   private static final double FRICTION_GROUND = 1.42;
   private static final double FRICTION_AIR = 1.02;
   private static final double FRICTION_WATER = 0.45;

   public SpeedCheck(ACManager manager) {
      super(manager, "Speed");
   }

   @Override
   public boolean enabled() {
      return (Boolean)AnticheatConfig.movementEnabled.getValue()
         && (Boolean)AnticheatConfig.speedEnabled.getValue();
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
         return;
      }

      double deltaXZ = data.getDeltaXZ();
      if (deltaXZ < 0.001) {
         return;
      }

      double maxSpeed = this.maxSpeed(data);
      double allowed = maxSpeed * (Double)AnticheatConfig.speedMaxMultiplier.getValue() + 0.03;
      if (!data.isOnGround()) {
         if (data.isClientSprinting() || player.isSprinting()) {
            allowed += 0.25;
         }

         if (data.isOnGround() != data.wasOnGround() && data.wasOnGround()) {
            allowed += 0.15;
         }
      } else if (!data.wasOnGround()) {
         allowed += 0.15;
      }

      if (data.isExpectingKnockback()) {
         allowed += 1.0;
      }

      if (deltaXZ > allowed) {
         if (this.flag(data, Math.min(2.0, 0.5 + (deltaXZ - allowed) * 5.0), String.format("d=%.3f max=%.3f", deltaXZ, allowed))) {
            this.setback(data);
         }
      }
   }

   private double maxSpeed(ACPlayer data) {
      EntityPlayer player = data.player;
      double speed = WALK_SPEED;
      if (data.isClientSprinting() || player.isSprinting()) {
         speed *= SPRINT_MULTIPLIER;
      }

      if (data.isClientSneaking() || player.isSneaking()) {
         speed *= SNEAK_MULTIPLIER;
      }

      int speedEffect = this.effectAmplifier(player, MobEffectList.FASTER_MOVEMENT);
      if (speedEffect >= 0) {
         speed *= 1.0 + 0.2 * (speedEffect + 1);
      }

      int slowEffect = this.effectAmplifier(player, MobEffectList.SLOWER_MOVEMENT);
      if (slowEffect >= 0) {
         speed *= 1.0 - 0.15 * (slowEffect + 1);
      }

      if (player.inWater) {
         return speed * FRICTION_WATER;
      }

      if (this.onIce(player)) {
         return speed * ICE_MULTIPLIER;
      }

      return data.isOnGround() ? speed * FRICTION_GROUND : speed * FRICTION_AIR;
   }

   private int effectAmplifier(EntityPlayer player, MobEffectList effect) {
      return player.hasEffect(effect) ? player.getEffect(effect).getAmplifier() : -1;
   }

   private boolean onIce(EntityPlayer player) {
      BlockPosition below = new BlockPosition(player.locX, player.getBoundingBox().b - 0.5000001, player.locZ);
      Block block = player.world.getType(below).getBlock();
      return block == Blocks.ICE || block == Blocks.PACKED_ICE;
   }

   private void setback(ACPlayer data) {
      if ((Boolean)AnticheatConfig.setbackOnViolation.getValue() && data.hasSafePosition()) {
         data.player.playerConnection.a(data.getSafeX(), data.getSafeY(), data.getSafeZ(), data.player.yaw, data.player.pitch);
      }
   }
}
