package org.bukkit.command;

import java.util.List;
import org.apache.commons.lang.Validate;
import org.bukkit.plugin.Plugin;

public final class PluginCommand extends Command implements PluginIdentifiableCommand {
   private final Plugin owningPlugin;
   private CommandExecutor executor;
   private TabCompleter completer;

   protected PluginCommand(String name, Plugin owner) {
      super(name);
      this.executor = owner;
      this.owningPlugin = owner;
      this.usageMessage = "";
   }

   @Override
   public boolean execute(CommandSender sender, String commandLabel, String[] args) {
      boolean success = false;
      if (!this.owningPlugin.isEnabled()) {
         return false;
      } else if (!this.testPermission(sender)) {
         return true;
      } else {
         try {
            success = this.executor.onCommand(sender, this, commandLabel, args);
         } catch (Throwable var9) {
            throw new CommandException(
               "Unhandled exception executing command '" + commandLabel + "' in plugin " + this.owningPlugin.getDescription().getFullName(), var9
            );
         }

         if (!success && this.usageMessage.length() > 0) {
            for (String line : this.usageMessage.replace("<command>", commandLabel).split("\n")) {
               sender.sendMessage(line);
            }
         }

         return success;
      }
   }

   public void setExecutor(CommandExecutor executor) {
      this.executor = (CommandExecutor)(executor == null ? this.owningPlugin : executor);
   }

   public CommandExecutor getExecutor() {
      return this.executor;
   }

   public void setTabCompleter(TabCompleter completer) {
      this.completer = completer;
   }

   public TabCompleter getTabCompleter() {
      return this.completer;
   }

   @Override
   public Plugin getPlugin() {
      return this.owningPlugin;
   }

   @Override
   public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws CommandException, IllegalArgumentException {
      return this.tabComplete(sender, alias, args, null);
   }

   @Override
   public List<String> tabComplete(CommandSender sender, String alias, String[] args, org.bukkit.Location location) throws CommandException, IllegalArgumentException {
      Validate.notNull(sender, "Sender cannot be null");
      Validate.notNull(args, "Arguments cannot be null");
      Validate.notNull(alias, "Alias cannot be null");
      List<String> completions = null;

      try {
         if (this.completer != null) {
            completions = this.completer.onTabComplete(sender, this, alias, args, location);
         }

         if (completions == null && this.executor instanceof TabCompleter) {
            completions = ((TabCompleter)this.executor).onTabComplete(sender, this, alias, args, location);
         }
      } catch (Throwable var12) {
         StringBuilder message = new StringBuilder();
         message.append("Unhandled exception during tab completion for command '/").append(alias).append(' ');

         for (String arg : args) {
            message.append(arg).append(' ');
         }

         message.deleteCharAt(message.length() - 1).append("' in plugin ").append(this.owningPlugin.getDescription().getFullName());
         throw new CommandException(message.toString(), var12);
      }

      return completions == null ? super.tabComplete(sender, alias, args) : completions;
   }

   @Override
   public String toString() {
      StringBuilder stringBuilder = new StringBuilder(super.toString());
      stringBuilder.deleteCharAt(stringBuilder.length() - 1);
      stringBuilder.append(", ").append(this.owningPlugin.getDescription().getFullName()).append(')');
      return stringBuilder.toString();
   }
}
