package org.bukkit.event.player;

import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class PlayerCommandPreprocessEvent extends PlayerEvent implements Cancellable {
   private static final HandlerList handlers = new HandlerList();
   private boolean cancel = false;
   private String message;
   private String format = "<%1$s> %2$s";
   private final Set<Player> recipients;

   public PlayerCommandPreprocessEvent(Player player, String message) {
      super(player);
      this.recipients = new HashSet<>(player.getServer().getOnlinePlayers());
      this.message = message;
   }

   public PlayerCommandPreprocessEvent(Player player, String message, Set<Player> recipients) {
      super(player);
      this.recipients = recipients;
      this.message = message;
   }

   @Override
   public boolean isCancelled() {
      return this.cancel;
   }

   @Override
   public void setCancelled(boolean cancel) {
      this.cancel = cancel;
   }

   public String getMessage() {
      return this.message;
   }

   public void setMessage(String command) throws IllegalArgumentException {
      Validate.notNull(command, "Command cannot be null");
      Validate.notEmpty(command, "Command cannot be empty");
      this.message = command;
   }

   public void setPlayer(Player player) throws IllegalArgumentException {
      Validate.notNull(player, "Player cannot be null");
      this.player = player;
   }

   @Deprecated
   public String getFormat() {
      return this.format;
   }

   @Deprecated
   public void setFormat(String format) {
      try {
         String.format(format, this.player, this.message);
      } catch (RuntimeException var3) {
         var3.fillInStackTrace();
         throw var3;
      }

      this.format = format;
   }

   @Deprecated
   public Set<Player> getRecipients() {
      return this.recipients;
   }

   @Override
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
