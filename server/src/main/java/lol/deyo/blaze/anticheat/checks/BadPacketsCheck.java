package lol.deyo.blaze.anticheat.checks;

import lol.deyo.blaze.anticheat.ACManager;
import lol.deyo.blaze.anticheat.ACPlayer;
import lol.deyo.blaze.anticheat.Check;
import lol.deyo.blaze.anticheat.AnticheatConfig;

public class BadPacketsCheck extends Check {
   public BadPacketsCheck(ACManager manager) {
      super(manager, "BadPackets");
   }

   @Override
   public boolean enabled() {
      return (Boolean)AnticheatConfig.packetsEnabled.getValue()
         && (Boolean)AnticheatConfig.badPacketsEnabled.getValue();
   }

   @Override
   public void onFlying(ACPlayer data, long nowNanos) {
      float pitch = data.getPitch();
      double maxPitch = (Double)AnticheatConfig.badPacketsMaxPitch.getValue();
      if (Math.abs(pitch) > maxPitch) {
         this.flag(data, 1.0, String.format("pitch=%.2f", pitch));
         return;
      }

      if (!Float.isFinite(pitch) || !Float.isFinite(data.getYaw())) {
         this.flag(data, 2.0, "non-finite rotation");
      }
   }
}
