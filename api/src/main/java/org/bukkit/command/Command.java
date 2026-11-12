package org.bukkit.command;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.CommandMinecart;
import org.bukkit.permissions.Permissible;
import org.bukkit.util.StringUtil;

public abstract class Command {
   private String name;
   private String nextLabel;
   private String label;
   private List<String> aliases;
   private List<String> activeAliases;
   private CommandMap commandMap = null;
   protected String description = "";
   protected String usageMessage;
   private String permission;
   private String permissionMessage;

   public String getTimingName() {
      return this.getName();
   }

   protected Command(String name) {
      this(name, "", "/" + name, new ArrayList<>());
   }

   protected Command(String name, String description, String usageMessage, List<String> aliases) {
      this.name = name;
      this.nextLabel = name;
      this.label = name;
      this.description = description;
      this.usageMessage = usageMessage;
      this.aliases = aliases;
      this.activeAliases = new ArrayList<>(aliases);
   }

   public abstract boolean execute(CommandSender var1, String var2, String[] var3);

   @Deprecated
   public List<String> tabComplete(CommandSender sender, String[] args) {
      return null;
   }

   public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
      Validate.notNull(sender, "Sender cannot be null");
      Validate.notNull(args, "Arguments cannot be null");
      Validate.notNull(alias, "Alias cannot be null");
      if (args.length == 0) {
         return ImmutableList.of();
      } else {
         String lastWord = args[args.length - 1];
         Player senderPlayer = sender instanceof Player ? (Player)sender : null;
         ArrayList<String> matchedPlayers = new ArrayList<>();

         for (Player player : sender.getServer().getOnlinePlayers()) {
            String name = player.getName();
            if ((senderPlayer == null || senderPlayer.canSee(player)) && StringUtil.startsWithIgnoreCase(name, lastWord)) {
               matchedPlayers.add(name);
            }
         }

         matchedPlayers.sort(String.CASE_INSENSITIVE_ORDER);
         return matchedPlayers;
      }
   }

   public List<String> tabComplete(CommandSender sender, String alias, String[] args, org.bukkit.Location location) throws IllegalArgumentException {
      return this.tabComplete(sender, alias, args);
   }

   public String getName() {
      return this.name;
   }

   public boolean setName(String name) {
      if (!this.isRegistered()) {
         this.name = name;
         return true;
      } else {
         return false;
      }
   }

   public String getPermission() {
      return this.permission;
   }

   public void setPermission(String permission) {
      this.permission = permission;
   }

   public boolean testPermission(CommandSender target) {
      if (this.testPermissionSilent(target)) {
         return true;
      } else {
         target.sendMessage(org.bukkit.ChatColor.GOLD + "Blaze Spigot " + org.bukkit.ChatColor.YELLOW + "by deyo");
         if (this.permissionMessage == null) {
            target.sendMessage(
               org.bukkit.ChatColor.RED
                  + "I'm sorry, but you do not have permission to perform this command. Please contact the server administrators if you believe that this is in error."
            );
         } else if (this.permissionMessage.length() != 0) {
            for (String line : this.permissionMessage.replace("<permission>", this.permission).split("\n")) {
               target.sendMessage(line);
            }
         }

         return false;
      }
   }

   public boolean testPermissionSilent(CommandSender target) {
      if (this.permission != null && this.permission.length() != 0) {
         for (String p : this.permission.split(";")) {
            if (target.hasPermission(p)) {
               return true;
            }
         }

         return false;
      } else {
         return true;
      }
   }

   public String getLabel() {
      return this.label;
   }

   public boolean setLabel(String name) {
      this.nextLabel = name;
      if (!this.isRegistered()) {
         this.label = name;
         return true;
      } else {
         return false;
      }
   }

   public boolean register(CommandMap commandMap) {
      if (this.allowChangesFrom(commandMap)) {
         this.commandMap = commandMap;
         return true;
      } else {
         return false;
      }
   }

   public boolean unregister(CommandMap commandMap) {
      if (this.allowChangesFrom(commandMap)) {
         this.commandMap = null;
         this.activeAliases = new ArrayList<>(this.aliases);
         this.label = this.nextLabel;
         return true;
      } else {
         return false;
      }
   }

   private boolean allowChangesFrom(CommandMap commandMap) {
      return null == this.commandMap || this.commandMap == commandMap;
   }

   public boolean isRegistered() {
      return null != this.commandMap;
   }

   public List<String> getAliases() {
      return this.activeAliases;
   }

   public String getPermissionMessage() {
      return this.permissionMessage;
   }

   public String getDescription() {
      return this.description;
   }

   public String getUsage() {
      return this.usageMessage;
   }

   public Command setAliases(List<String> aliases) {
      this.aliases = aliases;
      if (!this.isRegistered()) {
         this.activeAliases = new ArrayList<>(aliases);
      }

      return this;
   }

   public Command setDescription(String description) {
      this.description = description;
      return this;
   }

   public Command setPermissionMessage(String permissionMessage) {
      this.permissionMessage = permissionMessage;
      return this;
   }

   public Command setUsage(String usage) {
      this.usageMessage = usage;
      return this;
   }

   public static void broadcastCommandMessage(CommandSender source, String message) {
      broadcastCommandMessage(source, message, true);
   }

   public static void broadcastCommandMessage(CommandSender source, String message, boolean sendToSource) {
      String result = source.getName() + ": " + message;
      if (source instanceof BlockCommandSender) {
         BlockCommandSender blockCommandSender = (BlockCommandSender)source;
         if (blockCommandSender.getBlock().getWorld().getGameRuleValue("commandBlockOutput").equalsIgnoreCase("false")) {
            org.bukkit.Bukkit.getConsoleSender().sendMessage(result);
            return;
         }
      } else if (source instanceof CommandMinecart) {
         CommandMinecart commandMinecart = (CommandMinecart)source;
         if (commandMinecart.getWorld().getGameRuleValue("commandBlockOutput").equalsIgnoreCase("false")) {
            org.bukkit.Bukkit.getConsoleSender().sendMessage(result);
            return;
         }
      }

      Set<Permissible> users = org.bukkit.Bukkit.getPluginManager().getPermissionSubscriptions("bukkit.broadcast.admin");
      String colored = org.bukkit.ChatColor.GRAY
         + ""
         + org.bukkit.ChatColor.ITALIC
         + "["
         + result
         + org.bukkit.ChatColor.GRAY
         + org.bukkit.ChatColor.ITALIC
         + "]";
      if (sendToSource && !(source instanceof ConsoleCommandSender)) {
         source.sendMessage(message);
      }

      for (Permissible user : users) {
         if (user instanceof CommandSender) {
            CommandSender target = (CommandSender)user;
            if (target instanceof ConsoleCommandSender) {
               target.sendMessage(result);
            } else if (target != source) {
               target.sendMessage(colored);
            }
         }
      }
   }

   @Override
   public String toString() {
      return this.getClass().getName() + '(' + this.name + ')';
   }
}
