package org.github.paperspigot.exception;

import com.google.common.base.Preconditions;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class ServerPluginMessageException extends ServerPluginException {
   private final Player player;
   private final String channel;
   private final byte[] data;

   public ServerPluginMessageException(String message, Throwable cause, Plugin responsiblePlugin, Player player, String channel, byte[] data) {
      super(message, cause, responsiblePlugin);
      this.player = (Player)Preconditions.checkNotNull(player, "player");
      this.channel = (String)Preconditions.checkNotNull(channel, "channel");
      this.data = (byte[])Preconditions.checkNotNull(data, "data");
   }

   public ServerPluginMessageException(Throwable cause, Plugin responsiblePlugin, Player player, String channel, byte[] data) {
      super(cause, responsiblePlugin);
      this.player = (Player)Preconditions.checkNotNull(player, "player");
      this.channel = (String)Preconditions.checkNotNull(channel, "channel");
      this.data = (byte[])Preconditions.checkNotNull(data, "data");
   }

   protected ServerPluginMessageException(
      String message,
      Throwable cause,
      boolean enableSuppression,
      boolean writableStackTrace,
      Plugin responsiblePlugin,
      Player player,
      String channel,
      byte[] data
   ) {
      super(message, cause, enableSuppression, writableStackTrace, responsiblePlugin);
      this.player = (Player)Preconditions.checkNotNull(player, "player");
      this.channel = (String)Preconditions.checkNotNull(channel, "channel");
      this.data = (byte[])Preconditions.checkNotNull(data, "data");
   }

   public String getChannel() {
      return this.channel;
   }

   public byte[] getData() {
      return this.data;
   }

   public Player getPlayer() {
      return this.player;
   }
}
