package lol.deyo.blaze.anticheat.checks;

import lol.deyo.blaze.anticheat.ACManager;
import lol.deyo.blaze.anticheat.ACPlayer;
import lol.deyo.blaze.anticheat.Check;
import lol.deyo.blaze.anticheat.AnticheatConfig;

public class PacketOrderCheck extends Check {
   public PacketOrderCheck(ACManager manager) {
      super(manager, "PacketOrder");
   }

   @Override
   public boolean enabled() {
      return (Boolean)AnticheatConfig.packetsEnabled.getValue()
         && (Boolean)AnticheatConfig.packetOrderEnabled.getValue();
   }

   @Override
   public void onSwing(ACPlayer data, long nowNanos) {
      long attack = data.getReceiveNanosLastAttack();
      if (attack > 0L && nowNanos - attack < 0L) {
         this.flag(data, 1.0, "swing-before-attack");
      }
   }

   @Override
   public void onDig(ACPlayer data, long nowNanos) {
      long swing = data.getReceiveNanosLastSwing();
      if (swing > 0L && nowNanos + 500000000L < swing) {
         this.flag(data, 1.0, "dig-far-past-swing");
      }
   }
}
