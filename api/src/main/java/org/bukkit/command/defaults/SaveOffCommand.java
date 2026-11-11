package org.bukkit.command.defaults;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.apache.commons.lang.Validate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@Deprecated
public class SaveOffCommand extends VanillaCommand {
   public SaveOffCommand() {
      super("save-off");
      this.description = "Disables server autosaving";
      this.usageMessage = "/save-off";
      this.setPermission("bukkit.command.save.disable");
   }

   @Override
   public boolean execute(CommandSender sender, String currentAlias, String[] args) {
      if (!this.testPermission(sender)) {
         return true;
      } else {
         for (org.bukkit.World world : org.bukkit.Bukkit.getWorlds()) {
            world.setAutoSave(false);
         }

         Command.broadcastCommandMessage(sender, "Disabled level saving..");
         return true;
      }
   }

   @Override
   public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
      Validate.notNull(sender, "Sender cannot be null");
      Validate.notNull(args, "Arguments cannot be null");
      Validate.notNull(alias, "Alias cannot be null");
      return ImmutableList.of();
   }
}
