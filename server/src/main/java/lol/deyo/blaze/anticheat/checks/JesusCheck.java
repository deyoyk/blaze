package lol.deyo.blaze.anticheat.checks;

import lol.deyo.blaze.anticheat.ACManager;
import lol.deyo.blaze.anticheat.ACPlayer;
import lol.deyo.blaze.anticheat.AnticheatConfig;
import lol.deyo.blaze.anticheat.Check;
import net.minecraft.server.v1_8_R3.Block;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.Blocks;
import net.minecraft.server.v1_8_R3.EntityPlayer;

public class JesusCheck extends Check {
   private int waterWalkTicks;

   public JesusCheck(ACManager manager) {
      super(manager, "Jesus");
   }

   @Override
   public boolean enabled() {
      return (Boolean)AnticheatConfig.movementEnabled.getValue()
         && (Boolean)AnticheatConfig.jesusEnabled.getValue();
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
         this.waterWalkTicks = 0;
         return;
      }

      if (!data.isOnGround() || player.inWater) {
         this.waterWalkTicks = 0;
         return;
      }

      double horizontal = data.getDeltaXZ();
      if (horizontal < 0.05) {
         this.waterWalkTicks = 0;
         return;
      }

      double feetY = player.getBoundingBox().b;
      BlockPosition below = new BlockPosition(player.locX, feetY - 0.6, player.locZ);
      Block belowBlock = player.world.getType(below).getBlock();
      if (belowBlock != Blocks.WATER && belowBlock != Blocks.FLOWING_WATER) {
         this.waterWalkTicks = 0;
         return;
      }

      BlockPosition at = new BlockPosition(player.locX, feetY, player.locZ);
      Block atBlock = player.world.getType(at).getBlock();
      if (atBlock == Blocks.WATER || atBlock == Blocks.FLOWING_WATER) {
         this.waterWalkTicks = 0;
         return;
      }

      this.waterWalkTicks++;
      if (this.waterWalkTicks >= 6 && horizontal > (Double)AnticheatConfig.jesusMaxSpeed.getValue()) {
         this.flag(data, 1.0, String.format("speed=%.3f ticks=%d", horizontal, this.waterWalkTicks));
         this.waterWalkTicks = 0;
      }
   }
}
