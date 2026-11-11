package org.bukkit.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class PlayerPortalEvent extends PlayerTeleportEvent {
   private static final HandlerList handlers = new HandlerList();
   protected boolean useTravelAgent = true;
   protected org.bukkit.TravelAgent travelAgent;

   public PlayerPortalEvent(Player player, org.bukkit.Location from, org.bukkit.Location to, org.bukkit.TravelAgent pta) {
      super(player, from, to);
      this.travelAgent = pta;
   }

   public PlayerPortalEvent(
      Player player, org.bukkit.Location from, org.bukkit.Location to, org.bukkit.TravelAgent pta, PlayerTeleportEvent.TeleportCause cause
   ) {
      super(player, from, to, cause);
      this.travelAgent = pta;
   }

   public void useTravelAgent(boolean useTravelAgent) {
      this.useTravelAgent = useTravelAgent;
   }

   public boolean useTravelAgent() {
      return this.useTravelAgent && this.travelAgent != null;
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
