package org.bukkit.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class PlayerAchievementAwardedEvent extends PlayerEvent implements Cancellable {
   private static final HandlerList handlers = new HandlerList();
   private final org.bukkit.Achievement achievement;
   private boolean isCancelled = false;

   public PlayerAchievementAwardedEvent(Player player, org.bukkit.Achievement achievement) {
      super(player);
      this.achievement = achievement;
   }

   public org.bukkit.Achievement getAchievement() {
      return this.achievement;
   }

   @Override
   public boolean isCancelled() {
      return this.isCancelled;
   }

   @Override
   public void setCancelled(boolean cancel) {
      this.isCancelled = cancel;
   }

   @Override
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
