package org.bukkit.command;

import java.util.List;

public interface TabCompleter {
   List<String> onTabComplete(CommandSender var1, Command var2, String var3, String[] var4);

   default List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args, org.bukkit.Location location) {
      return this.onTabComplete(sender, command, alias, args);
   }
}
