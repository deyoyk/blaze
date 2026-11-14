package lol.deyo.blaze.anticheat;

import lol.deyo.blaze.anticheat.AnticheatConfig;
import net.minecraft.server.v1_8_R3.MinecraftServer;

public abstract class Check {
   protected final String name;
   protected final ACManager manager;

   protected Check(ACManager manager, String name) {
      this.name = name;
      this.manager = manager;
   }

   public String getName() {
      return this.name;
   }

   public abstract boolean enabled();

   public void onFlying(ACPlayer data, long nowNanos) {
   }

   public void onAttack(ACPlayer data, long nowNanos) {
   }

   public void onSwing(ACPlayer data, long nowNanos) {
   }

   public void onDig(ACPlayer data, long nowNanos) {
   }

   public void onPlace(ACPlayer data, long nowNanos) {
   }

   public void tick(ACPlayer data) {
   }

   protected boolean flag(ACPlayer data, double weight, String detail) {
      return this.manager.flag(this, data, weight, detail);
   }

   protected void debugLog(String message) {
      if ((Boolean)AnticheatConfig.debug.getValue()) {
         MinecraftServer.LOGGER.info("[AC] " + message);
      }
   }
}
