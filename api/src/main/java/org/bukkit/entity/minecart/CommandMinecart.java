package org.bukkit.entity.minecart;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Minecart;

public interface CommandMinecart extends Minecart, CommandSender {
   String getCommand();

   void setCommand(String var1);

   void setName(String var1);
}
