package org.bukkit.event.entity;

import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;

public class EntityPortalEnterEvent extends EntityEvent {
   private static final HandlerList handlers = new HandlerList();
   private final org.bukkit.Location location;

   public EntityPortalEnterEvent(Entity entity, org.bukkit.Location location) {
      super(entity);
      this.location = location;
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
