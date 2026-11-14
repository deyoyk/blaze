package lol.deyo.blaze.anticheat.checks;

import lol.deyo.blaze.anticheat.ACManager;
import lol.deyo.blaze.anticheat.ACPlayer;
import lol.deyo.blaze.anticheat.Check;
import lol.deyo.blaze.anticheat.AnticheatConfig;

public class KillauraCheck extends Check {
   public KillauraCheck(ACManager manager) {
      super(manager, "Killaura");
   }

   @Override
   public boolean enabled() {
      return (Boolean)AnticheatConfig.combatEnabled.getValue()
         && (Boolean)AnticheatConfig.killauraEnabled.getValue();
   }

   @Override
   public void onAttack(ACPlayer data, long nowNanos) {
      long flying = data.getReceiveNanosLastFlying();
      if (flying > 0L) {
         long sinceFlying = nowNanos - flying;
         int threshold = (Integer)AnticheatConfig.killauraMaxPostNanos.getValue();
         if (sinceFlying >= 0L && sinceFlying < threshold) {
            this.flag(data, 0.5, String.format("post=%.1fms", sinceFlying / 1000000.0));
         } else if (sinceFlying > 1000000000L) {
            this.flag(data, 0.5, String.format("stale=%.1fms", sinceFlying / 1000000.0));
         }
      }

      double aim = data.getYawDelta() + data.getPitchDelta();
      double maxAim = (Double)AnticheatConfig.killauraMaxAimDelta.getValue();
      if (aim > maxAim) {
         this.flag(data, 1.0, String.format("aim=%.1f", aim));
      }
   }
}
