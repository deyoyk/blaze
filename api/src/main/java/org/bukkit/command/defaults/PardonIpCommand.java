package org.bukkit.command.defaults;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.Validate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

@Deprecated
public class PardonIpCommand extends VanillaCommand {
   public PardonIpCommand() {
      super("pardon-ip");
      this.description = "Allows the specified IP address to use this server";
      this.usageMessage = "/pardon-ip <address>";
      this.setPermission("bukkit.command.unban.ip");
   }

   @Override
   public boolean execute(CommandSender sender, String currentAlias, String[] args) {
      if (!this.testPermission(sender)) {
         return true;
      } else if (args.length != 1) {
         sender.sendMessage(org.bukkit.ChatColor.RED + "Usage: " + this.usageMessage);
         return false;
      } else {
         if (BanIpCommand.ipValidity.matcher(args[0]).matches()) {
            org.bukkit.Bukkit.unbanIP(args[0]);
            Command.broadcastCommandMessage(sender, "Pardoned ip " + args[0]);
         } else {
            sender.sendMessage("Invalid ip");
         }

         return true;
      }
   }

   @Override
   public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
      Validate.notNull(sender, "Sender cannot be null");
      Validate.notNull(args, "Arguments cannot be null");
      Validate.notNull(alias, "Alias cannot be null");
      return (List<String>)(args.length == 1 ? StringUtil.copyPartialMatches(args[0], org.bukkit.Bukkit.getIPBans(), new ArrayList<>()) : ImmutableList.of());
   }
}
