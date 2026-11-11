package org.bukkit.event.server;

import java.beans.ConstructorProperties;
import org.bukkit.event.HandlerList;

public final class ServerShutdownEvent extends ServerEvent {
   private static final HandlerList handlerList = new HandlerList();
   private final org.bukkit.Server server;

   @ConstructorProperties({"server"})
   public ServerShutdownEvent(org.bukkit.Server server) {
      this.server = server;
   }

   public static HandlerList getHandlerList() {
      return handlerList;
   }

   public org.bukkit.Server getServer() {
      return this.server;
   }

   @Override
   public HandlerList getHandlers() {
      return handlerList;
   }
}
