package org.bukkit.command;

import java.util.List;

@Deprecated
public interface TabCommandExecutor extends CommandExecutor {
   List<String> onTabComplete();
}
