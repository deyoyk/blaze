package org.bukkit.command.defaults;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.Validate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

@Deprecated
public class DeopCommand extends VanillaCommand {
   public DeopCommand() {
      super("deop");
      this.description = "Takes the specified player's operator status";
      this.usageMessage = "/deop <player>";
      this.setPermission("bukkit.command.op.take");
   }

   @Override
   public boolean execute(CommandSender sender, String currentAlias, String[] args) {
      if (!this.testPermission(sender)) {
         return true;
      } else if (args.length == 1 && args[0].length() != 0) {
         org.bukkit.OfflinePlayer player = org.bukkit.Bukkit.getOfflinePlayer(args[0]);
         player.setOp(false);
         if (player instanceof Player) {
            ((Player)player).sendMessage(org.bukkit.ChatColor.YELLOW + "You are no longer op!");
         }

         Command.broadcastCommandMessage(sender, "De-opped " + args[0]);
         return true;
      } else {
         sender.sendMessage(org.bukkit.ChatColor.RED + "Usage: " + this.usageMessage);
         return false;
      }
   }

   @Override
   public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
      Validate.notNull(sender, "Sender cannot be null");
      Validate.notNull(args, "Arguments cannot be null");
      Validate.notNull(alias, "Alias cannot be null");
      if (args.length == 1) {
         List<String> completions = new ArrayList<>();

         for (org.bukkit.OfflinePlayer player : org.bukkit.Bukkit.getOperators()) {
            String playerName = player.getName();
            if (StringUtil.startsWithIgnoreCase(playerName, args[0])) {
               completions.add(playerName);
            }
         }

         return completions;
      } else {
         return ImmutableList.of();
      }
   }
}
