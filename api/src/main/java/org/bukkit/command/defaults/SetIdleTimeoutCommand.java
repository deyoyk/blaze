package org.bukkit.command.defaults;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.apache.commons.lang.Validate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@Deprecated
public class SetIdleTimeoutCommand extends VanillaCommand {
   public SetIdleTimeoutCommand() {
      super("setidletimeout");
      this.description = "Sets the server's idle timeout";
      this.usageMessage = "/setidletimeout <Minutes until kick>";
      this.setPermission("bukkit.command.setidletimeout");
   }

   @Override
   public boolean execute(CommandSender sender, String currentAlias, String[] args) {
      if (!this.testPermission(sender)) {
         return true;
      } else if (args.length == 1) {
         int minutes;
         try {
            minutes = this.getInteger(sender, args[0], 0, Integer.MAX_VALUE, true);
         } catch (NumberFormatException var6) {
            sender.sendMessage(var6.getMessage());
            return true;
         }

         org.bukkit.Bukkit.getServer().setIdleTimeout(minutes);
         Command.broadcastCommandMessage(sender, "Successfully set the idle timeout to " + minutes + " minutes.");
         return true;
      } else {
         sender.sendMessage(org.bukkit.ChatColor.RED + "Usage: " + this.usageMessage);
         return false;
      }
   }

   @Override
   public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
      Validate.notNull(sender, "Sender cannot be null");
      Validate.notNull(args, "Arguments cannot be null");
      Validate.notNull(alias, "Alias cannot be null");
      return ImmutableList.of();
   }
}
