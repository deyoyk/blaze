package lol.deyo.blaze.command;

import lol.deyo.blaze.configs.LanguageConfig;
import lol.deyo.blaze.framework.commands.Command;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.util.StringUtil;

public class VersionCommand extends Command {
   public VersionCommand() {
      super("version");
      this.setDescription("Show the Blaze server version");
      this.setAliases(Arrays.asList("ver", "about", "icanhasbukkit"));
   }

   @Override
   public void execute(CommandSender sender, String[] args) {
      if (args.length > 0 && sender.hasPermission("bukkit.command.version")) {
         StringBuilder name = new StringBuilder();

         for (String arg : args) {
            if (name.length() > 0) {
               name.append(' ');
            }

            name.append(arg);
         }

         String pluginName = name.toString();
         Plugin exactPlugin = Bukkit.getPluginManager().getPlugin(pluginName);
         if (exactPlugin != null) {
            this.describeToSender(exactPlugin, sender);
         } else {
            boolean found = false;
            pluginName = pluginName.toLowerCase();

            Plugin[] var10;
            for (Plugin plugin : var10 = Bukkit.getPluginManager().getPlugins()) {
               if (plugin.getName().toLowerCase().contains(pluginName)) {
                  this.describeToSender(plugin, sender);
                  found = true;
               }
            }

            if (!found) {
               sender.sendMessage("This server is not running any plugin by that name.");
               sender.sendMessage("Use /plugins to get a list of plugins.");
            }
         }
      } else {
         for (String string : LanguageConfig.versionMessage.getValue()) {
            this.sendMessage(sender, new String[]{string});
         }
      }
   }

   private void describeToSender(Plugin plugin, CommandSender sender) {
      PluginDescriptionFile desc = plugin.getDescription();
      sender.sendMessage(ChatColor.GREEN + desc.getName() + ChatColor.WHITE + " version " + ChatColor.GREEN + desc.getVersion());
      if (desc.getDescription() != null) {
         sender.sendMessage(desc.getDescription());
      }

      if (desc.getWebsite() != null) {
         sender.sendMessage("Website: " + ChatColor.GREEN + desc.getWebsite());
      }

      if (!desc.getAuthors().isEmpty()) {
         if (desc.getAuthors().size() == 1) {
            sender.sendMessage("Author: " + this.getAuthors(desc));
         } else {
            sender.sendMessage("Authors: " + this.getAuthors(desc));
         }
      }
   }

   private String getAuthors(PluginDescriptionFile desc) {
      StringBuilder result = new StringBuilder();
      List<String> authors = desc.getAuthors();

      for (int i = 0; i < authors.size(); i++) {
         if (result.length() > 0) {
            result.append(ChatColor.WHITE);
            if (i < authors.size() - 1) {
               result.append(", ");
            } else {
               result.append(" and ");
            }
         }

         result.append(ChatColor.GREEN);
         result.append(authors.get(i));
      }

      return result.toString();
   }

   @Override
   public List<String> tabComplete(CommandSender sender, String[] args) {
      if (args.length == 1 && sender.hasPermission("bukkit.command.version")) {
         List<String> completions = new ArrayList<>();
         String toComplete = args[0].toLowerCase();

         Plugin[] var8;
         for (Plugin plugin : var8 = Bukkit.getPluginManager().getPlugins()) {
            if (StringUtil.startsWithIgnoreCase(plugin.getName(), toComplete)) {
               completions.add(plugin.getName());
            }
         }

         return completions;
      } else {
         return super.tabComplete(sender, args);
      }
   }
}
