package lol.deyo.blaze.anticheat.checks;

import lol.deyo.blaze.anticheat.ACManager;
import lol.deyo.blaze.anticheat.ACPlayer;
import lol.deyo.blaze.anticheat.AnticheatConfig;
import lol.deyo.blaze.anticheat.Check;
import net.minecraft.server.v1_8_R3.AxisAlignedBB;
import net.minecraft.server.v1_8_R3.Block;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.EntityPlayer;

public class SafeWalkCheck extends Check {
   private int edgeTicks;
   private int streakFlags;

   public SafeWalkCheck(ACManager manager) {
      super(manager, "SafeWalk");
   }

   @Override
   public boolean enabled() {
      return (Boolean)AnticheatConfig.movementEnabled.getValue()
         && (Boolean)AnticheatConfig.safeWalkEnabled.getValue();
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
         this.edgeTicks = 0;
         this.streakFlags = 0;
         return;
      }

      if (!data.isOnGround()) {
         this.edgeTicks = 0;
         this.streakFlags = 0;
         return;
      }

      double horizontal = data.getDeltaXZ();
      if (horizontal < 0.1) {
         this.edgeTicks = 0;
         this.streakFlags = 0;
         return;
      }

      if (this.hasGroundUnderFootprint(player)) {
         this.edgeTicks = 0;
         this.streakFlags = 0;
         return;
      }

      this.edgeTicks++;
      int required = (Integer)AnticheatConfig.safeWalkMinEdgeTicks.getValue();
      if (this.edgeTicks >= required) {
         this.streakFlags++;
         double weight = this.streakFlags >= 4 ? 2.5 : 1.0;
         this.flag(data, weight, "edge=" + this.edgeTicks + " h=" + String.format("%.3f", horizontal));
         this.edgeTicks = 0;
      }
   }

   private boolean hasGroundUnderFootprint(EntityPlayer player) {
      AxisAlignedBB bbox = player.getBoundingBox();
      double y = bbox.b - 0.1;
      return this.isSolidAt(player, bbox.a, y, bbox.c)
         || this.isSolidAt(player, bbox.d, y, bbox.c)
         || this.isSolidAt(player, bbox.a, y, bbox.f)
         || this.isSolidAt(player, bbox.d, y, bbox.f)
         || this.isSolidAt(player, (bbox.a + bbox.d) / 2.0, y, (bbox.c + bbox.f) / 2.0);
   }

   private boolean isSolidAt(EntityPlayer player, double x, double y, double z) {
      Block block = player.world.getType(new BlockPosition(x, y, z)).getBlock();
      return block.getMaterial() != null && block.getMaterial().isSolid();
   }
}
