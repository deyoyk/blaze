package org.bukkit.command.defaults;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.Validate;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.util.StringUtil;

@Deprecated
public class GameRuleCommand extends VanillaCommand {
   private static final List<String> GAMERULE_STATES = ImmutableList.of("true", "false");

   public GameRuleCommand() {
      super("gamerule");
      this.description = "Sets a server's game rules";
      this.usageMessage = "/gamerule <rule name> <value> OR /gamerule <rule name>";
      this.setPermission("bukkit.command.gamerule");
   }

   @Override
   public boolean execute(CommandSender sender, String currentAlias, String[] args) {
      if (!this.testPermission(sender)) {
         return true;
      } else if (args.length > 0) {
         String rule = args[0];
         org.bukkit.World world = this.getGameWorld(sender);
         if (world.isGameRule(rule)) {
            if (args.length > 1) {
               String value = args[1];
               world.setGameRuleValue(rule, value);
               Command.broadcastCommandMessage(sender, "Game rule " + rule + " has been set to: " + value);
            } else {
               String value = world.getGameRuleValue(rule);
               sender.sendMessage(rule + " = " + value);
            }
         } else {
            sender.sendMessage(org.bukkit.ChatColor.RED + "No game rule called " + rule + " is available");
         }

         return true;
      } else {
         sender.sendMessage(org.bukkit.ChatColor.RED + "Usage: " + this.usageMessage);
         sender.sendMessage("Rules: " + this.createString(this.getGameWorld(sender).getGameRules(), 0, ", "));
         return true;
      }
   }

   private org.bukkit.World getGameWorld(CommandSender sender) {
      if (sender instanceof HumanEntity) {
         org.bukkit.World world = ((HumanEntity)sender).getWorld();
         if (world != null) {
            return world;
         }
      } else if (sender instanceof BlockCommandSender) {
         return ((BlockCommandSender)sender).getBlock().getWorld();
      }

      return org.bukkit.Bukkit.getWorlds().get(0);
   }

   @Override
   public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
      Validate.notNull(sender, "Sender cannot be null");
      Validate.notNull(args, "Arguments cannot be null");
      Validate.notNull(alias, "Alias cannot be null");
      if (args.length == 1) {
         return StringUtil.copyPartialMatches(args[0], Arrays.asList(this.getGameWorld(sender).getGameRules()), new ArrayList<>());
      } else {
         return (List<String>)(args.length == 2
            ? StringUtil.copyPartialMatches(args[1], GAMERULE_STATES, new ArrayList<>(GAMERULE_STATES.size()))
            : ImmutableList.of());
      }
   }
}
