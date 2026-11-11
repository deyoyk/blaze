package org.bukkit.event.world;

import org.bukkit.event.HandlerList;

public class SpawnChangeEvent extends WorldEvent {
   private static final HandlerList handlers = new HandlerList();
   private final org.bukkit.Location previousLocation;

   public SpawnChangeEvent(org.bukkit.World world, org.bukkit.Location previousLocation) {
      super(world);
      this.previousLocation = previousLocation;
   }

   public org.bukkit.Location getPreviousLocation() {
      return this.previousLocation;
   }

   @Override
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
