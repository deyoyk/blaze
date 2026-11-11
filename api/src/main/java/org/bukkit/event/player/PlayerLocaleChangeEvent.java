package org.bukkit.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class PlayerLocaleChangeEvent extends PlayerEvent {
   private static final HandlerList handlers = new HandlerList();
   private final String oldLocale;
   private final String newLocale;

   public PlayerLocaleChangeEvent(Player player, String oldLocale, String newLocale) {
      super(player);
      this.oldLocale = oldLocale;
      this.newLocale = newLocale;
   }

   public String getOldLocale() {
      return this.oldLocale;
   }

   public String getNewLocale() {
      return this.newLocale;
   }

   @Override
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
