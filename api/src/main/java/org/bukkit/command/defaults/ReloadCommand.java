package org.bukkit.command.defaults;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ReloadCommand extends BukkitCommand {
   public ReloadCommand(String name) {
      super(name);
      this.description = "Reloads the server configuration and plugins";
      this.usageMessage = "/reload";
      this.setPermission("bukkit.command.reload");
      this.setAliases(Arrays.asList("rl"));
   }

   @Override
   public boolean execute(CommandSender sender, String currentAlias, String[] args) {
      if (!this.testPermission(sender)) {
         return true;
      } else {
         Command.broadcastCommandMessage(sender, org.bukkit.ChatColor.RED + "The reload command is known to cause memory leaks and break things. Be careful.");
         Command.broadcastCommandMessage(
            sender, org.bukkit.ChatColor.RED + "Please note that this command is not supported and may cause issues when using some plugins."
         );
         Command.broadcastCommandMessage(sender, org.bukkit.ChatColor.RED + "If you encounter any issues please use the /stop command to restart your server.");
         org.bukkit.Bukkit.reload();
         Command.broadcastCommandMessage(sender, org.bukkit.ChatColor.GREEN + "Reload complete.");
         return true;
      }
   }

   @Override
   public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
      return Collections.emptyList();
   }
}
