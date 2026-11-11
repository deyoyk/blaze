package org.bukkit.command;

import java.util.List;

public interface CommandMap {
   void registerAll(String var1, List<Command> var2);

   boolean register(String var1, String var2, Command var3);

   boolean register(String var1, Command var2);

   boolean dispatch(CommandSender var1, String var2) throws CommandException;

   void clearCommands();

   Command getCommand(String var1);

   List<String> tabComplete(CommandSender var1, String var2) throws IllegalArgumentException;
}
