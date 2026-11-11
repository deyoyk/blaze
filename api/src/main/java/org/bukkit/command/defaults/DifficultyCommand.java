package org.bukkit.command.defaults;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.Validate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

@Deprecated
public class DifficultyCommand extends VanillaCommand {
   private static final List<String> DIFFICULTY_NAMES = ImmutableList.of("peaceful", "easy", "normal", "hard");

   public DifficultyCommand() {
      super("difficulty");
      this.description = "Sets the game difficulty";
      this.usageMessage = "/difficulty <new difficulty> ";
      this.setPermission("bukkit.command.difficulty");
   }

   @Override
   public boolean execute(CommandSender sender, String currentAlias, String[] args) {
      if (!this.testPermission(sender)) {
         return true;
      } else if (args.length == 1 && args[0].length() != 0) {
         org.bukkit.Difficulty difficulty = org.bukkit.Difficulty.getByValue(this.getDifficultyForString(sender, args[0]));
         if (org.bukkit.Bukkit.isHardcore()) {
            difficulty = org.bukkit.Difficulty.HARD;
         }

         org.bukkit.Bukkit.getWorlds().get(0).setDifficulty(difficulty);
         int levelCount = 1;
         if (org.bukkit.Bukkit.getAllowNether()) {
            org.bukkit.Bukkit.getWorlds().get(levelCount).setDifficulty(difficulty);
            levelCount++;
         }

         if (org.bukkit.Bukkit.getAllowEnd()) {
            org.bukkit.Bukkit.getWorlds().get(levelCount).setDifficulty(difficulty);
         }

         Command.broadcastCommandMessage(sender, "Set difficulty to " + difficulty.toString());
         return true;
      } else {
         sender.sendMessage(org.bukkit.ChatColor.RED + "Usage: " + this.usageMessage);
         return false;
      }
   }

   protected int getDifficultyForString(CommandSender sender, String name) {
      if (name.equalsIgnoreCase("peaceful") || name.equalsIgnoreCase("p")) {
         return 0;
      } else if (name.equalsIgnoreCase("easy") || name.equalsIgnoreCase("e")) {
         return 1;
      } else if (name.equalsIgnoreCase("normal") || name.equalsIgnoreCase("n")) {
         return 2;
      } else {
         return !name.equalsIgnoreCase("hard") && !name.equalsIgnoreCase("h") ? this.getInteger(sender, name, 0, 3) : 3;
      }
   }

   @Override
   public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
      Validate.notNull(sender, "Sender cannot be null");
      Validate.notNull(args, "Arguments cannot be null");
      Validate.notNull(alias, "Alias cannot be null");
      return (List<String>)(args.length == 1
         ? StringUtil.copyPartialMatches(args[0], DIFFICULTY_NAMES, new ArrayList<>(DIFFICULTY_NAMES.size()))
         : ImmutableList.of());
   }
}
