package org.bukkit.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class PlayerChangedWorldEvent extends PlayerEvent {
   private static final HandlerList handlers = new HandlerList();
   private final org.bukkit.World from;

   public PlayerChangedWorldEvent(Player player, org.bukkit.World from) {
      super(player);
      this.from = from;
   }

   public org.bukkit.World getFrom() {
      return this.from;
   }

   @Override
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
