package org.bukkit.event.painting;

import org.bukkit.entity.Painting;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

@Deprecated
@org.bukkit.Warning(
   reason = "This event has been replaced by HangingBreakEvent"
)
public class PaintingBreakEvent extends PaintingEvent implements Cancellable {
   private static final HandlerList handlers = new HandlerList();
   private boolean cancelled;
   private final PaintingBreakEvent.RemoveCause cause;

   public PaintingBreakEvent(Painting painting, PaintingBreakEvent.RemoveCause cause) {
      super(painting);
      this.cause = cause;
   }

   public PaintingBreakEvent.RemoveCause getCause() {
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
      FIRE,
      OBSTRUCTION,
      WATER,
      PHYSICS;
   }
}
