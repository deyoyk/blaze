package lol.deyo.blaze.anticheat.checks;

import lol.deyo.blaze.anticheat.ACManager;
import lol.deyo.blaze.anticheat.ACPlayer;
import lol.deyo.blaze.anticheat.AnticheatConfig;
import lol.deyo.blaze.anticheat.Check;
import net.minecraft.server.v1_8_R3.Block;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.EntityPlayer;

public class AutoBridgeCheck extends Check {
   private static final int FACE_ITEM_USE = 255;
   private static final long QUALIFYING_WINDOW_NANOS = 2000000000L;

   private int bridgingPlaces;
   private long lastQualifyingNanos;

   public AutoBridgeCheck(ACManager manager) {
      super(manager, "AutoBridge");
   }

   @Override
   public boolean enabled() {
      return (Boolean)AnticheatConfig.movementEnabled.getValue()
         && (Boolean)AnticheatConfig.autoBridgeEnabled.getValue();
   }

   @Override
   public void onPlace(ACPlayer data, long nowNanos) {
      if (data.hasTeleportedSinceLastMove() || data.player.playerConnection.hasPendingTeleport()) {
         this.bridgingPlaces = 0;
         return;
      }

      int face = data.getLastPlaceFace();
      if (face == FACE_ITEM_USE) {
         return;
      }

      EntityPlayer player = data.player;
      float pitch = data.getPitch();
      if (pitch < (float)(double)(Double)AnticheatConfig.autoBridgeMaxPitch.getValue()) {
         return;
      }

      int py = data.getLastPlaceY();
      int feetBlockY = (int)Math.floor(player.locY - 0.5);
      if (py > feetBlockY) {
         return;
      }

      double dxz = data.getDeltaXZ();
      if (dxz < 0.08) {
         return;
      }

      double dx = player.locX - (double)data.getLastPlaceX() - 0.5;
      double dz = player.locZ - (double)data.getLastPlaceZ() - 0.5;
      double placeDist = Math.sqrt(dx * dx + dz * dz);
      if (placeDist > 2.5) {
         return;
      }

      double behindX = player.locX - data.getDeltaX() * 1.5;
      double behindZ = player.locZ - data.getDeltaZ() * 1.5;
      double behindDist = Math.sqrt(
         (behindX - (double)data.getLastPlaceX() - 0.5) * (behindX - (double)data.getLastPlaceX() - 0.5)
            + (behindZ - (double)data.getLastPlaceZ() - 0.5) * (behindZ - (double)data.getLastPlaceZ() - 0.5)
      );
      if (behindDist > 1.8) {
         return;
      }

      if (data.isClientSneaking() || player.isSneaking()) {
         return;
      }

      if (nowNanos - this.lastQualifyingNanos > QUALIFYING_WINDOW_NANOS) {
         this.bridgingPlaces = 0;
      }

      this.lastQualifyingNanos = nowNanos;
      this.bridgingPlaces++;
      if (this.bridgingPlaces >= 6) {
         this.flag(data, 1.0, "places=" + this.bridgingPlaces + " pitch=" + String.format("%.0f", pitch));
         this.bridgingPlaces = 0;
      }
   }

   @Override
   public void onFlying(ACPlayer data, long nowNanos) {
      if (data.getDeltaXZ() < 0.02) {
         this.bridgingPlaces = 0;
      } else if (this.lastQualifyingNanos > 0L && nowNanos - this.lastQualifyingNanos > QUALIFYING_WINDOW_NANOS) {
         this.bridgingPlaces = 0;
      }
   }
}
