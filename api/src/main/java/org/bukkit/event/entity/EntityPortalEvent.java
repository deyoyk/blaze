package org.bukkit.event.entity;

import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;

public class EntityPortalEvent extends EntityTeleportEvent {
   private static final HandlerList handlers = new HandlerList();
   protected boolean useTravelAgent = true;
   protected org.bukkit.TravelAgent travelAgent;

   public EntityPortalEvent(Entity entity, org.bukkit.Location from, org.bukkit.Location to, org.bukkit.TravelAgent pta) {
      super(entity, from, to);
      this.travelAgent = pta;
   }

   public void useTravelAgent(boolean useTravelAgent) {
      this.useTravelAgent = useTravelAgent;
   }

   public boolean useTravelAgent() {
      return this.useTravelAgent;
   }

   public org.bukkit.TravelAgent getPortalTravelAgent() {
      return this.travelAgent;
   }

   public void setPortalTravelAgent(org.bukkit.TravelAgent travelAgent) {
      this.travelAgent = travelAgent;
   }

   @Override
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
