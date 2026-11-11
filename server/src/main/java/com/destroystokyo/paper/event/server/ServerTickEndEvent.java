package com.destroystokyo.paper.event.server;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ServerTickEndEvent extends Event {
   private static final HandlerList handlers = new HandlerList();
   private final int tickNumber;
   private final double tickDuration;
   private final long timeEnd;

   public ServerTickEndEvent(int tickNumber, double tickDuration, long timeRemaining) {
      this.tickNumber = tickNumber;
      this.tickDuration = tickDuration;
      this.timeEnd = System.nanoTime() + timeRemaining;
   }

   public int getTickNumber() {
      return this.tickNumber;
   }

   public double getTickDuration() {
      return this.tickDuration;
   }

   public long getTimeRemaining() {
      return this.timeEnd - System.nanoTime();
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
