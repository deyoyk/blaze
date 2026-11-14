package lol.deyo.blaze.anticheat.checks;

import lol.deyo.blaze.anticheat.ACManager;
import lol.deyo.blaze.anticheat.ACPlayer;
import lol.deyo.blaze.anticheat.AnticheatConfig;
import lol.deyo.blaze.anticheat.Check;
import net.minecraft.server.v1_8_R3.Block;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.Blocks;
import net.minecraft.server.v1_8_R3.EntityPlayer;

public class CriticalsCheck extends Check {
   private int violations;

   public CriticalsCheck(ACManager manager) {
      super(manager, "Criticals");
   }

   @Override
   public boolean enabled() {
      return (Boolean)AnticheatConfig.combatEnabled.getValue()
         && (Boolean)AnticheatConfig.criticalsEnabled.getValue();
   }

   @Override
   public void onAttack(ACPlayer data, long nowNanos) {
      EntityPlayer player = data.player;
      if (player.vehicle != null
         || data.hasTeleportedSinceLastMove()
         || player.playerConnection.hasPendingTeleport()) {
         return;
      }

      if (player.ticksLived < 60 || data.isExpectingKnockback()) {
         return;
      }

      boolean airborne = !data.isOnGround() && !player.onGround;
      if (!airborne || Math.abs(data.getDeltaY()) > 0.001) {
         this.violations = 0;
         return;
      }

      Block below = player.world.getType(new BlockPosition(player.locX, player.locY - 0.6, player.locZ)).getBlock();
      if (below == Blocks.WATER
         || below == Blocks.FLOWING_WATER
         || below == Blocks.LADDER
         || below == Blocks.VINE) {
         return;
      }

      this.violations++;
      if (this.violations >= 3) {
         if (this.flag(data, 2.0, String.format("air-flat x%d dy=%.4f", this.violations, data.getDeltaY()))) {
            this.violations = 0;
         }
      }
   }
}
