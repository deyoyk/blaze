package lol.deyo.blaze.anticheat.checks;

import lol.deyo.blaze.anticheat.ACManager;
import lol.deyo.blaze.anticheat.ACPlayer;
import lol.deyo.blaze.anticheat.AnticheatConfig;
import lol.deyo.blaze.anticheat.Check;

public class FastPlaceCheck extends Check {
   private static final int FACE_ITEM_USE = 255;

   public FastPlaceCheck(ACManager manager) {
      super(manager, "FastPlace");
   }

   @Override
   public boolean enabled() {
      return (Boolean)AnticheatConfig.worldEnabled.getValue()
         && (Boolean)AnticheatConfig.fastPlaceEnabled.getValue();
   }

   @Override
   public void onPlace(ACPlayer data, long nowNanos) {
      if (data.hasTeleportedSinceLastMove() || data.player.playerConnection.hasPendingTeleport()) {
         return;
      }

      if (data.getLastPlaceFace() == FACE_ITEM_USE) {
         return;
      }

      long previous = data.getPlaceNanosAt(2);
      if (previous <= 0L) {
         return;
      }

      long intervalMs = (nowNanos - previous) / 1000000L;
      int minIntervalMs = (Integer)AnticheatConfig.fastPlaceMinIntervalMs.getValue();
      if (intervalMs < (long)minIntervalMs) {
         this.flag(data, 1.0, "interval=" + intervalMs + "ms min=" + minIntervalMs);
      }
   }
}
