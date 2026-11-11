package org.bukkit.conversations;

import org.bukkit.plugin.Plugin;

public class PluginNameConversationPrefix implements ConversationPrefix {
   protected String separator;
   protected org.bukkit.ChatColor prefixColor;
   protected Plugin plugin;
   private String cachedPrefix;

   public PluginNameConversationPrefix(Plugin plugin) {
      this(plugin, " > ", org.bukkit.ChatColor.LIGHT_PURPLE);
   }

   public PluginNameConversationPrefix(Plugin plugin, String separator, org.bukkit.ChatColor prefixColor) {
      this.separator = separator;
      this.prefixColor = prefixColor;
      this.plugin = plugin;
      this.cachedPrefix = prefixColor + plugin.getDescription().getName() + separator + org.bukkit.ChatColor.WHITE;
   }

   @Override
   public String getPrefix(ConversationContext context) {
      return this.cachedPrefix;
   }
}
