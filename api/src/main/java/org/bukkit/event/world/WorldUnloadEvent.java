package org.bukkit.event.world;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class WorldUnloadEvent extends WorldEvent implements Cancellable {
   private static final HandlerList handlers = new HandlerList();
   private boolean isCancelled;

   public WorldUnloadEvent(org.bukkit.World world) {
      super(world);
   }

   @Override
   public boolean isCancelled() {
      return this.isCancelled;
   }

   @Override
   public void setCancelled(boolean cancel) {
      this.isCancelled = cancel;
   }

   @Override
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
