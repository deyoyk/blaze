package org.github.paperspigot.exception;

import com.google.common.base.Preconditions;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class ServerEventException extends ServerPluginException {
   private final Listener listener;
   private final Event event;

   public ServerEventException(String message, Throwable cause, Plugin responsiblePlugin, Listener listener, Event event) {
      super(message, cause, responsiblePlugin);
      this.listener = (Listener)Preconditions.checkNotNull(listener, "listener");
      this.event = (Event)Preconditions.checkNotNull(event, "event");
   }

   public ServerEventException(Throwable cause, Plugin responsiblePlugin, Listener listener, Event event) {
      super(cause, responsiblePlugin);
      this.listener = (Listener)Preconditions.checkNotNull(listener, "listener");
      this.event = (Event)Preconditions.checkNotNull(event, "event");
   }

   protected ServerEventException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Plugin responsiblePlugin, Listener listener, Event event
   ) {
      super(message, cause, enableSuppression, writableStackTrace, responsiblePlugin);
      this.listener = (Listener)Preconditions.checkNotNull(listener, "listener");
      this.event = (Event)Preconditions.checkNotNull(event, "event");
   }

   public Listener getListener() {
      return this.listener;
   }

   public Event getEvent() {
      return this.event;
   }
}
