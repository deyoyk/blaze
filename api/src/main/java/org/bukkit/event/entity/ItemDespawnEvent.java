package org.bukkit.event.entity;

import org.bukkit.entity.Item;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class ItemDespawnEvent extends EntityEvent implements Cancellable {
   private static final HandlerList handlers = new HandlerList();
   private boolean canceled;
   private final org.bukkit.Location location;

   public ItemDespawnEvent(Item despawnee, org.bukkit.Location loc) {
      super(despawnee);
      this.location = loc;
   }

   @Override
   public boolean isCancelled() {
      return this.canceled;
   }

   @Override
   public void setCancelled(boolean cancel) {
      this.canceled = cancel;
   }

   public Item getEntity() {
      return (Item)this.entity;
   }

   public org.bukkit.Location getLocation() {
      return this.location;
   }

   @Override
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
