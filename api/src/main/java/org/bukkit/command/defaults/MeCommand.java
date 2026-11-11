package org.bukkit.command.defaults;

import org.bukkit.command.CommandSender;

@Deprecated
public class MeCommand extends VanillaCommand {
   public MeCommand() {
      super("me");
      this.description = "Performs the specified action in chat";
      this.usageMessage = "/me <action>";
      this.setPermission("bukkit.command.me");
   }

   @Override
   public boolean execute(CommandSender sender, String currentAlias, String[] args) {
      if (!this.testPermission(sender)) {
         return true;
      } else if (args.length < 1) {
         sender.sendMessage(org.bukkit.ChatColor.RED + "Usage: " + this.usageMessage);
         return false;
      } else {
         StringBuilder message = new StringBuilder();
         message.append(sender.getName());

         for (String arg : args) {
            message.append(" ");
            message.append(arg);
         }

         org.bukkit.Bukkit.broadcastMessage("* " + message);
         return true;
      }
   }
}
