package org.bukkit.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class PlayerResourcePackStatusEvent extends PlayerEvent {
   private static final HandlerList handlers = new HandlerList();
   private final String hash;
   private final PlayerResourcePackStatusEvent.Status status;

   @Deprecated
   public PlayerResourcePackStatusEvent(Player who, PlayerResourcePackStatusEvent.Status resourcePackStatus) {
      super(who);
      this.hash = null;
      this.status = resourcePackStatus;
   }

   public PlayerResourcePackStatusEvent(Player who, PlayerResourcePackStatusEvent.Status resourcePackStatus, String hash) {
      super(who);
      this.hash = hash;
      this.status = resourcePackStatus;
   }

   public String getHash() {
      return this.hash;
   }

   public PlayerResourcePackStatusEvent.Status getStatus() {
      return this.status;
   }

   @Override
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }

   public static enum Status {
      SUCCESSFULLY_LOADED,
      DECLINED,
      FAILED_DOWNLOAD,
      ACCEPTED;
   }
}
