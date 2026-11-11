package org.bukkit.event.vehicle;

import org.bukkit.entity.Vehicle;
import org.bukkit.event.HandlerList;

public class VehicleMoveEvent extends VehicleEvent {
   private static final HandlerList handlers = new HandlerList();
   private final org.bukkit.Location from;
   private final org.bukkit.Location to;

   public VehicleMoveEvent(Vehicle vehicle, org.bukkit.Location from, org.bukkit.Location to) {
      super(vehicle);
      this.from = from;
      this.to = to;
   }

   public org.bukkit.Location getFrom() {
      return this.from;
   }

   public org.bukkit.Location getTo() {
      return this.to;
   }

   @Override
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
