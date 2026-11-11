package org.bukkit.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class PlayerCrossPearlEvent extends PlayerEvent implements Cancellable {
   private static final HandlerList handlers = new HandlerList();
   private final org.bukkit.Location from;
   private boolean cancelled;
   private org.bukkit.Location to;

   public PlayerCrossPearlEvent(Player player, org.bukkit.Location from, org.bukkit.Location to) {
      super(player);
      this.from = from;
      this.to = to;
   }

   @Override
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }

   public org.bukkit.Location getFrom() {
      return this.from;
   }

   public void setFrom(org.bukkit.Location to) {
      this.to = to;
   }

   public org.bukkit.Location getTo() {
      return this.to;
   }

   public void setTo(org.bukkit.Location to) {
      this.to = to;
   }

   @Override
   public boolean isCancelled() {
      return this.cancelled;
   }

   @Override
   public void setCancelled(boolean cancelled) {
      this.cancelled = cancelled;
   }
}
