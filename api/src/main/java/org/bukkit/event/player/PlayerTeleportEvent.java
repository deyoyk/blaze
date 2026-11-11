package org.bukkit.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class PlayerTeleportEvent extends PlayerMoveEvent {
   private static final HandlerList handlers = new HandlerList();
   private PlayerTeleportEvent.TeleportCause cause = PlayerTeleportEvent.TeleportCause.UNKNOWN;

   public PlayerTeleportEvent(Player player, org.bukkit.Location from, org.bukkit.Location to) {
      super(player, from, to);
   }

   public PlayerTeleportEvent(Player player, org.bukkit.Location from, org.bukkit.Location to, PlayerTeleportEvent.TeleportCause cause) {
      this(player, from, to);
      this.cause = cause;
   }

   public PlayerTeleportEvent.TeleportCause getCause() {
      return this.cause;
   }

   @Override
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }

   public static enum TeleportCause {
      ENDER_PEARL,
      COMMAND,
      PLUGIN,
      NETHER_PORTAL,
      END_PORTAL,
      SPECTATE,
      UNKNOWN,
      ANTI_CHEAT;
   }
}
