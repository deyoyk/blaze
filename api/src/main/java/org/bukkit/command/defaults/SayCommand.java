package org.bukkit.command.defaults;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.apache.commons.lang.Validate;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

@Deprecated
public class SayCommand extends VanillaCommand {
   public SayCommand() {
      super("say");
      this.description = "Broadcasts the given message as the sender";
      this.usageMessage = "/say <message ...>";
      this.setPermission("bukkit.command.say");
   }

   @Override
   public boolean execute(CommandSender sender, String currentAlias, String[] args) {
      if (!this.testPermission(sender)) {
         return true;
      } else if (args.length == 0) {
         sender.sendMessage(org.bukkit.ChatColor.RED + "Usage: " + this.usageMessage);
         return false;
      } else {
         StringBuilder message = new StringBuilder();
         message.append(org.bukkit.ChatColor.LIGHT_PURPLE).append("[");
         if (sender instanceof ConsoleCommandSender) {
            message.append("Server");
         } else if (sender instanceof Player) {
            message.append(((Player)sender).getDisplayName());
         } else {
            message.append(sender.getName());
         }

         message.append(org.bukkit.ChatColor.LIGHT_PURPLE).append("] ");
         if (args.length > 0) {
            message.append(args[0]);

            for (int i = 1; i < args.length; i++) {
               message.append(" ").append(args[i]);
            }
         }

         org.bukkit.Bukkit.broadcastMessage(message.toString());
         return true;
      }
   }

   @Override
   public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
      Validate.notNull(sender, "Sender cannot be null");
      Validate.notNull(args, "Arguments cannot be null");
      return (List<String>)(args.length >= 1 ? super.tabComplete(sender, alias, args) : ImmutableList.of());
   }
}
