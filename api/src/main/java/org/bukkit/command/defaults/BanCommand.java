package org.bukkit.command.defaults;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Deprecated
public class BanCommand extends VanillaCommand {
   public BanCommand() {
      super("ban");
      this.description = "Prevents the specified player from using this server";
      this.usageMessage = "/ban <player> [reason ...]";
      this.setPermission("bukkit.command.ban.player");
   }

   @Override
   public boolean execute(CommandSender sender, String currentAlias, String[] args) {
      if (!this.testPermission(sender)) {
         return true;
      } else if (args.length == 0) {
         sender.sendMessage(org.bukkit.ChatColor.RED + "Usage: " + this.usageMessage);
         return false;
      } else {
         String reason = args.length > 0 ? StringUtils.join(args, ' ', 1, args.length) : null;
         org.bukkit.Bukkit.getBanList(org.bukkit.BanList.Type.NAME).addBan(args[0], reason, null, sender.getName());
         Player player = org.bukkit.Bukkit.getPlayer(args[0]);
         if (player != null) {
            player.kickPlayer("Banned by admin.");
         }

         Command.broadcastCommandMessage(sender, "Banned player " + args[0]);
         return true;
      }
   }

   @Override
   public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
      Validate.notNull(sender, "Sender cannot be null");
      Validate.notNull(args, "Arguments cannot be null");
      Validate.notNull(alias, "Alias cannot be null");
      return (List<String>)(args.length >= 1 ? super.tabComplete(sender, alias, args) : ImmutableList.of());
   }
}
