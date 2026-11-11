package lol.deyo.blaze.console;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.craftbukkit.v1_8_R3.command.CraftConsoleCommandSender;

public class TerminalConsoleCommandSender extends CraftConsoleCommandSender {
   private static final Logger LOGGER = LogManager.getRootLogger();

   public void sendRawMessage(String message) {
      LOGGER.info(message);
   }
}
