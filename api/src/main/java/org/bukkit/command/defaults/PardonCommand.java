package org.bukkit.command.defaults;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.Validate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

@Deprecated
public class PardonCommand extends VanillaCommand {
   public PardonCommand() {
      super("pardon");
      this.description = "Allows the specified player to use this server";
      this.usageMessage = "/pardon <player>";
      this.setPermission("bukkit.command.unban.player");
   }

   @Override
   public boolean execute(CommandSender sender, String currentAlias, String[] args) {
      if (!this.testPermission(sender)) {
         return true;
      } else if (args.length != 1) {
         sender.sendMessage(org.bukkit.ChatColor.RED + "Usage: " + this.usageMessage);
         return false;
      } else {
         org.bukkit.Bukkit.getBanList(org.bukkit.BanList.Type.NAME).pardon(args[0]);
         Command.broadcastCommandMessage(sender, "Pardoned " + args[0]);
         return true;
      }
   }

   @Override
   public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
      Validate.notNull(sender, "Sender cannot be null");
      Validate.notNull(args, "Arguments cannot be null");
      Validate.notNull(alias, "Alias cannot be null");
      if (args.length == 1) {
         List<String> completions = new ArrayList<>();

         for (org.bukkit.OfflinePlayer player : org.bukkit.Bukkit.getBannedPlayers()) {
            String name = player.getName();
            if (StringUtil.startsWithIgnoreCase(name, args[0])) {
               completions.add(name);
            }
         }

         return completions;
      } else {
         return ImmutableList.of();
      }
   }
}
