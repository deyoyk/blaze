package lol.deyo.blaze.anticheat.checks;

import lol.deyo.blaze.anticheat.ACManager;
import lol.deyo.blaze.anticheat.ACPlayer;
import lol.deyo.blaze.anticheat.AnticheatConfig;
import lol.deyo.blaze.anticheat.Check;
import net.minecraft.server.v1_8_R3.Block;
import net.minecraft.server.v1_8_R3.BlockPosition;

public class FastBreakCheck extends Check {
   private static final double TICK_NANOS = 50000000.0;
   private long lastEvaluatedStopNanos;

   public FastBreakCheck(ACManager manager) {
      super(manager, "FastBreak");
   }

   @Override
   public boolean enabled() {
      return (Boolean)AnticheatConfig.worldEnabled.getValue()
         && (Boolean)AnticheatConfig.fastBreakEnabled.getValue();
   }

   @Override
   public void onDig(ACPlayer data, long nowNanos) {
      long startNanos = data.getLastDigStartNanos();
      long stopNanos = data.getLastDigStopNanos();
      if (startNanos <= 0L || stopNanos < startNanos || stopNanos == this.lastEvaluatedStopNanos) {
         return;
      }
      this.lastEvaluatedStopNanos = stopNanos;

      if (data.player.playerInteractManager.getGameMode() == net.minecraft.server.v1_8_R3.WorldSettings.EnumGamemode.CREATIVE) {
         return;
      }

      int x = data.getLastDigX();
      int y = data.getLastDigY();
      int z = data.getLastDigZ();
      if (x != data.getLastDigStartX() || y != data.getLastDigStartY() || z != data.getLastDigStartZ()) {
         return;
      }

      double minNanos = minBreakNanos(data.player, data.player.world, new BlockPosition(x, y, z));
      if (minNanos < 0.0) {
         return;
      }

      long elapsed = stopNanos - startNanos;
      if ((double)elapsed < minNanos) {
         this.flag(data, 1.0, String.format("elapsed=%.0fms min=%.0fms", elapsed / 1000000.0, minNanos / 1000000.0));
      }
   }

   static double minBreakNanos(net.minecraft.server.v1_8_R3.EntityPlayer player,
                               net.minecraft.server.v1_8_R3.World world,
                               BlockPosition pos) {
      Block block = world.getType(pos).getBlock();
      float damage = block.getDamage(player, world, pos);
      return minBreakNanos(damage);
   }

   public static double minBreakNanos(float damage) {
      if (damage <= 0.0F) {
         return -1.0;
      }

      double ticks = damage / 0.1;
      double minFraction = (Double)AnticheatConfig.fastBreakMinFraction.getValue();

      return ticks * TICK_NANOS * minFraction;
   }
}
