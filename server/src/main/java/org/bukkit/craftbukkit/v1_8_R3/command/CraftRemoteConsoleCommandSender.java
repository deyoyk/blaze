package org.bukkit.craftbukkit.v1_8_R3.command;

import java.text.MessageFormat;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.RemoteControlCommandListener;
import org.bukkit.command.RemoteConsoleCommandSender;

public class CraftRemoteConsoleCommandSender extends ServerCommandSender implements RemoteConsoleCommandSender {
   @Override
   public void sendMessage(String message) {
      RemoteControlCommandListener.getInstance().sendMessage(new ChatComponentText(message + "\n"));
   }

   @Override
   public void sendMessage(String[] messages) {
      for (String message : messages) {
         this.sendMessage(message);
      }
   }

   public void sendFormattedMessage(String message, Object... args) {
      this.sendMessage(MessageFormat.format(message, args));
   }

   @Override
   public String getName() {
      return "Rcon";
   }

   @Override
   public boolean isOp() {
      return true;
   }

   @Override
   public void setOp(boolean value) {
      throw new UnsupportedOperationException("Cannot change operator status of remote controller.");
   }
}
