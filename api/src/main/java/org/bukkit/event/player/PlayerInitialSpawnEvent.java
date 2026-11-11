package org.bukkit.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class PlayerInitialSpawnEvent extends PlayerEvent {
   private static final HandlerList handlers = new HandlerList();
   private org.bukkit.Location spawnLocation;

   public PlayerInitialSpawnEvent(Player player, org.bukkit.Location spawnLocation) {
      super(player);
      this.spawnLocation = spawnLocation;
   }

   public org.bukkit.Location getSpawnLocation() {
      return this.spawnLocation;
   }

   public void setSpawnLocation(org.bukkit.Location spawnLocation) {
      this.spawnLocation = spawnLocation;
   }

   @Override
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
