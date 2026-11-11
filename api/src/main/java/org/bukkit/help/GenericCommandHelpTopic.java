package org.bukkit.help;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.defaults.VanillaCommand;

public class GenericCommandHelpTopic extends HelpTopic {
   protected Command command;

   public GenericCommandHelpTopic(Command command) {
      this.command = command;
      if (command.getLabel().startsWith("/")) {
         this.name = command.getLabel();
      } else {
         this.name = "/" + command.getLabel();
      }

      int i = command.getDescription().indexOf("\n");
      if (i > 1) {
         this.shortText = command.getDescription().substring(0, i - 1);
      } else {
         this.shortText = command.getDescription();
      }

      StringBuffer sb = new StringBuffer();
      sb.append(org.bukkit.ChatColor.GOLD);
      sb.append("Description: ");
      sb.append(org.bukkit.ChatColor.WHITE);
      sb.append(command.getDescription());
      sb.append("\n");
      sb.append(org.bukkit.ChatColor.GOLD);
      sb.append("Usage: ");
      sb.append(org.bukkit.ChatColor.WHITE);
      sb.append(command.getUsage().replace("<command>", this.name.substring(1)));
      if (command.getAliases().size() > 0) {
         sb.append("\n");
         sb.append(org.bukkit.ChatColor.GOLD);
         sb.append("Aliases: ");
         sb.append(org.bukkit.ChatColor.WHITE);
         sb.append(org.bukkit.ChatColor.WHITE + StringUtils.join(command.getAliases(), ", "));
      }

      this.fullText = sb.toString();
   }

   @Override
   public boolean canSee(CommandSender sender) {
      if (!this.command.isRegistered() && !(this.command instanceof VanillaCommand)) {
         return false;
      } else if (sender instanceof ConsoleCommandSender) {
         return true;
      } else {
         return this.amendedPermission != null ? sender.hasPermission(this.amendedPermission) : this.command.testPermissionSilent(sender);
      }
   }
}
