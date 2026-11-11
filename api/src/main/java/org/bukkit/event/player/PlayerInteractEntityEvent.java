package org.bukkit.event.player;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class PlayerInteractEntityEvent extends PlayerEvent implements Cancellable {
   private static final HandlerList handlers = new HandlerList();
   protected Entity clickedEntity;
   boolean cancelled = false;

   public PlayerInteractEntityEvent(Player who, Entity clickedEntity) {
      super(who);
      this.clickedEntity = clickedEntity;
   }

   @Override
   public boolean isCancelled() {
      return this.cancelled;
   }

   @Override
   public void setCancelled(boolean cancel) {
      this.cancelled = cancel;
   }

   public Entity getRightClicked() {
      return this.clickedEntity;
   }

   @Override
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
