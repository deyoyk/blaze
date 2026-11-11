package org.bukkit.event;

public abstract class Event {
   private String name;
   private final boolean async;

   public Event() {
      this(false);
   }

   public Event(boolean isAsync) {
      this.async = isAsync;
   }

   public boolean callEvent() {
      org.bukkit.Bukkit.getPluginManager().callEvent(this);
      return this instanceof Cancellable ? !((Cancellable)this).isCancelled() : true;
   }

   public String getEventName() {
      if (this.name == null) {
         this.name = this.getClass().getSimpleName();
      }

      return this.name;
   }

   public abstract HandlerList getHandlers();

   public final boolean isAsynchronous() {
      return this.async;
   }

   public static enum Result {
      DENY,
      DEFAULT,
      ALLOW;
   }
}
