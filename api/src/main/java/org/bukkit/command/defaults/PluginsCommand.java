package org.bukkit.command.defaults;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class PluginsCommand extends BukkitCommand {
   public PluginsCommand(String name) {
      super(name);
      this.description = "Gets a list of plugins running on the server";
      this.usageMessage = "/plugins";
      this.setPermission("bukkit.command.plugins");
      this.setAliases(Arrays.asList("pl"));
   }

   @Override
   public boolean execute(CommandSender sender, String currentAlias, String[] args) {
      if (!this.testPermission(sender)) {
         return true;
      } else {
         sender.sendMessage("Plugins " + this.getPluginList());
         return true;
      }
   }

   private String getPluginList() {
      StringBuilder pluginList = new StringBuilder();
      Plugin[] plugins = org.bukkit.Bukkit.getPluginManager().getPlugins();

      for (Plugin plugin : plugins) {
         if (pluginList.length() > 0) {
            pluginList.append(org.bukkit.ChatColor.WHITE);
            pluginList.append(", ");
         }

         pluginList.append(plugin.isEnabled() ? org.bukkit.ChatColor.GREEN : org.bukkit.ChatColor.RED);
         pluginList.append(plugin.getDescription().getName());
      }

      return "(" + plugins.length + "): " + pluginList.toString();
   }

   @Override
   public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
      return Collections.emptyList();
   }
}
