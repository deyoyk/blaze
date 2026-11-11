package org.bukkit.command;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.permissions.Permissible;

public interface CommandSender extends Permissible {
   void sendMessage(String var1);

   void sendMessage(String[] var1);

   org.bukkit.Server getServer();

   String getName();

   default void sendMessage(BaseComponent component) {
      this.sendMessage(component.toLegacyText());
   }

   default void sendMessage(BaseComponent... components) {
      this.sendMessage(new TextComponent(components).toLegacyText());
   }
}
