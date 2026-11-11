package org.github.paperspigot.exception;

import com.google.common.base.Preconditions;
import org.bukkit.plugin.Plugin;

public class ServerPluginException extends ServerException {
   private final Plugin responsiblePlugin;

   public ServerPluginException(String message, Throwable cause, Plugin responsiblePlugin) {
      super(message, cause);
      this.responsiblePlugin = (Plugin)Preconditions.checkNotNull(responsiblePlugin, "responsiblePlugin");
   }

   public ServerPluginException(Throwable cause, Plugin responsiblePlugin) {
      super(cause);
      this.responsiblePlugin = (Plugin)Preconditions.checkNotNull(responsiblePlugin, "responsiblePlugin");
   }

   protected ServerPluginException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Plugin responsiblePlugin) {
      super(message, cause, enableSuppression, writableStackTrace);
      this.responsiblePlugin = (Plugin)Preconditions.checkNotNull(responsiblePlugin, "responsiblePlugin");
   }

   public Plugin getResponsiblePlugin() {
      return this.responsiblePlugin;
   }
}
