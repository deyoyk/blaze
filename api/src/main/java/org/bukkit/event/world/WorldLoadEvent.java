package org.bukkit.event.world;

import org.bukkit.event.HandlerList;

public class WorldLoadEvent extends WorldEvent {
   private static final HandlerList handlers = new HandlerList();

   public WorldLoadEvent(org.bukkit.World world) {
      super(world);
   }

   @Override
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
