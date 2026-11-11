package org.bukkit.event.entity;

import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class EntitySpawnEvent extends EntityEvent implements Cancellable {
   private static final HandlerList handlers = new HandlerList();
   private boolean canceled;

   public EntitySpawnEvent(Entity spawnee) {
      super(spawnee);
   }

   @Override
   public boolean isCancelled() {
      return this.canceled;
   }

   @Override
   public void setCancelled(boolean cancel) {
      this.canceled = cancel;
   }

   public org.bukkit.Location getLocation() {
      return this.getEntity().getLocation();
   }

   @Override
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
