package org.github.paperspigot.exception;

import org.bukkit.plugin.Plugin;

public class ServerPluginEnableDisableException extends ServerPluginException {
   public ServerPluginEnableDisableException(String message, Throwable cause, Plugin responsiblePlugin) {
      super(message, cause, responsiblePlugin);
   }

   public ServerPluginEnableDisableException(Throwable cause, Plugin responsiblePlugin) {
      super(cause, responsiblePlugin);
   }

   protected ServerPluginEnableDisableException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Plugin responsiblePlugin
   ) {
      super(message, cause, enableSuppression, writableStackTrace, responsiblePlugin);
   }
}
