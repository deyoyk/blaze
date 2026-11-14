package lol.deyo.blaze.anticheat.checks;

import lol.deyo.blaze.anticheat.ACManager;
import lol.deyo.blaze.anticheat.ACPlayer;
import lol.deyo.blaze.anticheat.AnticheatConfig;
import lol.deyo.blaze.anticheat.Check;
import net.minecraft.server.v1_8_R3.EntityPlayer;

public class VelocityCheck extends Check {
   public VelocityCheck(ACManager manager) {
      super(manager, "Velocity");
   }

   @Override
   public boolean enabled() {
      return (Boolean)AnticheatConfig.combatEnabled.getValue()
         && (Boolean)AnticheatConfig.velocityEnabled.getValue();
   }

   @Override
   public void onFlying(ACPlayer data, long nowNanos) {
      if (data.getKbSamples() < 3) {
         return;
      }

      EntityPlayer player = data.player;
      if (player.positionChanged
         || player.inWater
         || player.k_()
         || player.vehicle != null
         || player.ticksLived < 60) {
         data.resetKbSamples();
         return;
      }

      double horizontal = data.getKbHorizontalRatioSum() / data.getKbSamples();
      double vertical = data.getKbVerticalRatioSum() / data.getKbSamples();
      double minHorizontal = (Double)AnticheatConfig.velocityMinHorizontalRatio.getValue();
      double minVertical = (Double)AnticheatConfig.velocityMinVerticalRatio.getValue();
      if (horizontal < minHorizontal || vertical < minVertical) {
         this.flag(
            data,
            1.0,
            String.format("h=%.2f v=%.2f min=%.2f/%.2f", horizontal, vertical, minHorizontal, minVertical)
         );
      }

      data.resetKbSamples();
   }
}
