package com.destroystokyo.paper.event.server;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ServerTickStartEvent extends Event {
   private static final HandlerList handlers = new HandlerList();
   private final int tickNumber;

   public ServerTickStartEvent(int tickNumber) {
      this.tickNumber = tickNumber;
   }

   public int getTickNumber() {
      return this.tickNumber;
   }

   @NotNull
   public HandlerList getHandlers() {
      return handlers;
   }

   @NotNull
   public static HandlerList getHandlerList() {
      return handlers;
   }
}
