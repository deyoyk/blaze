package org.bukkit.event.hanging;

import org.bukkit.entity.Hanging;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class HangingBreakEvent extends HangingEvent implements Cancellable {
   private static final HandlerList handlers = new HandlerList();
   private boolean cancelled;
   private final HangingBreakEvent.RemoveCause cause;

   public HangingBreakEvent(Hanging hanging, HangingBreakEvent.RemoveCause cause) {
      super(hanging);
      this.cause = cause;
   }

   public HangingBreakEvent.RemoveCause getCause() {
      return this.cause;
   }

   @Override
   public boolean isCancelled() {
      return this.cancelled;
   }

   @Override
   public void setCancelled(boolean cancel) {
      this.cancelled = cancel;
   }

   @Override
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }

   public static enum RemoveCause {
      ENTITY,
      EXPLOSION,
      OBSTRUCTION,
      PHYSICS,
      DEFAULT;
   }
}
