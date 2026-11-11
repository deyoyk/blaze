package org.bukkit.event.entity;

import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class EntityTeleportEvent extends EntityEvent implements Cancellable {
   private static final HandlerList handlers = new HandlerList();
   private boolean cancel;
   private org.bukkit.Location from;
   private org.bukkit.Location to;

   public EntityTeleportEvent(Entity what, org.bukkit.Location from, org.bukkit.Location to) {
      super(what);
      this.from = from;
      this.to = to;
      this.cancel = false;
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
      this.from = from;
   }

   public org.bukkit.Location getTo() {
      return this.to;
   }

   public void setTo(org.bukkit.Location to) {
      this.to = to;
   }

   @Override
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
