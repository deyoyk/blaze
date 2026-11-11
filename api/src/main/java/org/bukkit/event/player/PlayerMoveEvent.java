package org.bukkit.event.player;

import com.google.common.base.Preconditions;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class PlayerMoveEvent extends PlayerEvent implements Cancellable {
   private static final HandlerList handlers = new HandlerList();
   private boolean cancel = false;
   private org.bukkit.Location from;
   private org.bukkit.Location to;

   public PlayerMoveEvent(Player player, org.bukkit.Location from, org.bukkit.Location to) {
      super(player);
      this.from = from;
      this.to = to;
   }

   @Override
   public boolean isCancelled() {
      return this.cancel;
   }

   @Override
   public void setCancelled(boolean cancel) {
      this.cancel = cancel;
   }

   public org.bukkit.Location getFrom() {
      return this.from;
   }

   public void setFrom(org.bukkit.Location from) {
      this.validateLocation(from);
      this.from = from;
   }

   public org.bukkit.Location getTo() {
      return this.to;
   }

   public void setTo(org.bukkit.Location to) {
      this.validateLocation(to);
      this.to = to;
   }

   private void validateLocation(org.bukkit.Location loc) {
      Preconditions.checkArgument(loc != null, "Cannot use null location!");
      Preconditions.checkArgument(loc.getWorld() != null, "Cannot use null location with null world!");
   }

   @Override
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
