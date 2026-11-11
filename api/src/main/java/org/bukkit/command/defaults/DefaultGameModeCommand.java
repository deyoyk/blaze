package org.bukkit.command.defaults;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.Validate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

@Deprecated
public class DefaultGameModeCommand extends VanillaCommand {
   private static final List<String> GAMEMODE_NAMES = ImmutableList.of("adventure", "creative", "survival");

   public DefaultGameModeCommand() {
      super("defaultgamemode");
      this.description = "Set the default gamemode";
      this.usageMessage = "/defaultgamemode <mode>";
      this.setPermission("bukkit.command.defaultgamemode");
   }

   @Override
   public boolean execute(CommandSender sender, String commandLabel, String[] args) {
      if (!this.testPermission(sender)) {
         return true;
      } else if (args.length == 0) {
         sender.sendMessage("Usage: " + this.usageMessage);
         return false;
      } else {
         String modeArg = args[0];
         int value = -1;

         try {
            value = Integer.parseInt(modeArg);
         } catch (NumberFormatException var7) {
         }

         org.bukkit.GameMode mode = org.bukkit.GameMode.getByValue(value);
         if (mode == null) {
            if (modeArg.equalsIgnoreCase("creative") || modeArg.equalsIgnoreCase("c")) {
               mode = org.bukkit.GameMode.CREATIVE;
            } else if (!modeArg.equalsIgnoreCase("adventure") && !modeArg.equalsIgnoreCase("a")) {
               mode = org.bukkit.GameMode.SURVIVAL;
            } else {
               mode = org.bukkit.GameMode.ADVENTURE;
            }
         }

         org.bukkit.Bukkit.getServer().setDefaultGameMode(mode);
         Command.broadcastCommandMessage(sender, "Default game mode set to " + mode.toString().toLowerCase());
         return true;
      }
   }

   @Override
   public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
      Validate.notNull(sender, "Sender cannot be null");
      Validate.notNull(args, "Arguments cannot be null");
      Validate.notNull(alias, "Alias cannot be null");
      return (List<String>)(args.length == 1
         ? StringUtil.copyPartialMatches(args[0], GAMEMODE_NAMES, new ArrayList<>(GAMEMODE_NAMES.size()))
         : ImmutableList.of());
   }
}
