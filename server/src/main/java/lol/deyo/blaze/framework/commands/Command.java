package lol.deyo.blaze.framework.commands;

import lol.deyo.blaze.configs.LanguageConfig;
import lol.deyo.blaze.utils.CC;
import lol.deyo.blaze.utils.Utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

public abstract class Command {
   protected final String name;
   protected String permission;
   protected List<String> aliases;
   protected BukkitCommand bukkitCommand;

   public Command(String name) {
      this.name = name;
      this.permission = null;
      this.aliases = new ArrayList<>();
   }

   public abstract void execute(CommandSender var1, String[] var2);

   public List<String> tabComplete(CommandSender sender, String[] args) {
      return Collections.emptyList();
   }

   public BukkitCommand asBukkitCommand() {
      if (this.bukkitCommand != null) {
         return this.bukkitCommand;
      } else {
         BukkitCommand command = new BukkitCommand(this.name) {
            public boolean execute(CommandSender sender, String commandLabel, String[] args) {
               if (Command.this.permission != null && !sender.hasPermission(Command.this.permission)) {
                  Command.this.sendMessage(sender, CC.CREDITS, LanguageConfig.insufficientPerm.getValue());
                  return true;
               } else {
                  Command.this.execute(sender, args);
                  return true;
               }
            }

            public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
               if (Command.this.permission != null && !sender.hasPermission(Command.this.permission)) {
                  return super.tabComplete(sender, alias, args);
               } else {
                  List<String> tabComplete = Command.this.tabComplete(sender, args);
                  return tabComplete.isEmpty() ? super.tabComplete(sender, alias, args) : tabComplete;
               }
            }
         };
         if (!this.aliases.isEmpty()) {
            command.setAliases(this.aliases);
         }

         this.bukkitCommand = command;
         return command;
      }
   }

   public void unregister() {
      if (this.bukkitCommand != null) {
         Utils.unregister(this.bukkitCommand);
      }
   }

   public void sendMessage(CommandSender sender, String... strings) {
      for (String string : strings) {
         sender.sendMessage(CC.t(string));
      }
   }

   public void setPermission(String permission) {
      this.permission = permission;
   }

   public void setAliases(List<String> aliases) {
      this.aliases = aliases;
   }

   public Integer getInt(String string) {
      try {
         return Integer.parseInt(string);
      } catch (NumberFormatException var3) {
         return null;
      }
   }

   public Double getDouble(String string) {
      try {
         return Double.parseDouble(string);
      } catch (NumberFormatException var3) {
         return null;
      }
   }

   public Float getFloat(String string) {
      try {
         return Float.parseFloat(string);
      } catch (NumberFormatException var3) {
         return null;
      }
   }
}
