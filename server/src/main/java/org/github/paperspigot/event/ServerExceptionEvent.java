package org.github.paperspigot.event;

import com.google.common.base.Preconditions;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.github.paperspigot.exception.ServerException;

public class ServerExceptionEvent extends Event {
   private static final HandlerList handlers = new HandlerList();
   private ServerException exception;

   public ServerExceptionEvent(ServerException exception) {
      this.exception = (ServerException)Preconditions.checkNotNull(exception, "exception");
   }

   public ServerException getException() {
      return this.exception;
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
