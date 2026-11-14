package lol.deyo.blaze.anticheat.checks;

import lol.deyo.blaze.anticheat.ACManager;
import lol.deyo.blaze.anticheat.ACPlayer;
import lol.deyo.blaze.anticheat.AnticheatConfig;
import lol.deyo.blaze.anticheat.Check;
import net.minecraft.server.v1_8_R3.AxisAlignedBB;
import net.minecraft.server.v1_8_R3.Block;
import net.minecraft.server.v1_8_R3.BlockAnvil;
import net.minecraft.server.v1_8_R3.BlockBed;
import net.minecraft.server.v1_8_R3.BlockCauldron;
import net.minecraft.server.v1_8_R3.BlockChest;
import net.minecraft.server.v1_8_R3.BlockDaylightDetector;
import net.minecraft.server.v1_8_R3.BlockDoor;
import net.minecraft.server.v1_8_R3.BlockFence;
import net.minecraft.server.v1_8_R3.BlockFenceGate;
import net.minecraft.server.v1_8_R3.BlockHopper;
import net.minecraft.server.v1_8_R3.BlockPiston;
import net.minecraft.server.v1_8_R3.BlockPistonExtension;
import net.minecraft.server.v1_8_R3.BlockPistonMoving;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.BlockSkull;
import net.minecraft.server.v1_8_R3.BlockSnow;
import net.minecraft.server.v1_8_R3.BlockStairs;
import net.minecraft.server.v1_8_R3.BlockStepAbstract;
import net.minecraft.server.v1_8_R3.BlockTrapdoor;
import net.minecraft.server.v1_8_R3.EntityPlayer;

public class PhaseCheck extends Check {
   private static final long BLOCK_CHANGE_GRACE_NANOS = 500_000_000L;
   private static final double OVERLAP_EPSILON = 0.001;
   private int pendingOverlap;

   public PhaseCheck(ACManager manager) {
      super(manager, "Phase");
   }

   @Override
   public boolean enabled() {
      return (Boolean)AnticheatConfig.movementEnabled.getValue()
         && (Boolean)AnticheatConfig.phaseEnabled.getValue();
   }

   @Override
   public void onFlying(ACPlayer data, long nowNanos) {
      EntityPlayer player = data.player;
      if (player.vehicle != null
         || data.hasTeleportedSinceLastMove()
         || player.playerConnection.hasPendingTeleport()) {
         return;
      }

      if (player.ticksLived < 60 || data.isExpectingKnockback()) {
         return;
      }

      if (nowNanos - data.getReceiveNanosLastPlace() < BLOCK_CHANGE_GRACE_NANOS
         || nowNanos - data.getReceiveNanosLastDig() < BLOCK_CHANGE_GRACE_NANOS) {
         return;
      }

      double lastX = data.getLastX();
      double lastY = data.getLastY();
      double lastZ = data.getLastZ();
      double dx = data.getX() - lastX;
      double dy = data.getY() - lastY;
      double dz = data.getZ() - lastZ;
      if (dx * dx + dy * dy + dz * dz > 64.0) {
         return;
      }

      Block solid = this.findSolidOverlap(player, data.getX(), data.getY(), data.getZ());
      if (solid == null) {
         double midX = lastX + dx * 0.5;
         double midY = lastY + dy * 0.5;
         double midZ = lastZ + dz * 0.5;
         solid = this.findSolidOverlap(player, midX, midY, midZ);
      }

      if (solid != null && this.findSolidOverlap(player, lastX, lastY, lastZ) == null) {
         // Persistence buffer: require the overlap on two consecutive flying packets so
         // single-tick transients (piston pushes, chunk-load lag) never flag.
         this.pendingOverlap++;
         if (this.pendingOverlap >= 2) {
            this.pendingOverlap = 0;
            if (this.flag(data, 2.0, String.format("block=%s", solid.getName()))) {
               this.setback(data);
            }
         }
      } else {
         this.pendingOverlap = 0;
      }
   }

   private void setback(ACPlayer data) {
      if ((Boolean)AnticheatConfig.setbackOnViolation.getValue() && data.hasSafePosition()) {
         data.player.playerConnection.a(data.getSafeX(), data.getSafeY(), data.getSafeZ(), data.player.yaw, data.player.pitch);
      }
   }

   private Block findSolidOverlap(EntityPlayer player, double x, double y, double z) {
      AxisAlignedBB bb = player.boundingBox;
      double halfWidth = (bb.d - bb.a) / 2.0;
      double height = bb.e - bb.b;
      double minX = x - halfWidth;
      double maxX = x + halfWidth;
      double minY = y;
      double maxY = y + height;
      double minZ = z - halfWidth;
      double maxZ = z + halfWidth;

      int bx0 = floor(minX);
      int bx1 = floor(maxX);
      int by0 = floor(minY);
      int by1 = floor(maxY);
      int bz0 = floor(minZ);
      int bz1 = floor(maxZ);

      for (int bx = bx0; bx <= bx1; bx++) {
         for (int by = by0; by <= by1; by++) {
            for (int bz = bz0; bz <= bz1; bz++) {
               Block block = player.world.getType(new BlockPosition(bx, by, bz)).getBlock();
               if (!isFullSolid(block)) {
                  continue;
               }

               if (aabbOverlapsBlock(minX, minY, minZ, maxX, maxY, maxZ, bx, by, bz, OVERLAP_EPSILON)) {
                  return block;
               }
            }
         }
      }

      return null;
   }

   static boolean isFullSolid(Block block) {
      if (!block.u()) {
         return false;
      }

      return !(block instanceof BlockStepAbstract
         || block instanceof BlockStairs
         || block instanceof BlockDoor
         || block instanceof BlockFence
         || block instanceof BlockFenceGate
         || block instanceof BlockTrapdoor
         || block instanceof BlockSnow
         || block instanceof BlockSkull
         || block instanceof BlockChest
         || block instanceof BlockBed
         || block instanceof BlockCauldron
         || block instanceof BlockHopper
         || block instanceof BlockDaylightDetector
         || block instanceof BlockAnvil
         || block instanceof BlockPiston
         || block instanceof BlockPistonExtension
         || block instanceof BlockPistonMoving);
   }

   public static boolean aabbOverlapsBlock(
      double minX, double minY, double minZ,
      double maxX, double maxY, double maxZ,
      int bx, int by, int bz,
      double epsilon
   ) {
      return Math.min(maxX, bx + 1.0) - Math.max(minX, bx) > epsilon
         && Math.min(maxY, by + 1.0) - Math.max(minY, by) > epsilon
         && Math.min(maxZ, bz + 1.0) - Math.max(minZ, bz) > epsilon;
   }

   private static int floor(double v) {
      int i = (int)v;
      return v < i ? i - 1 : i;
   }
}
