package org.bukkit.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import org.apache.commons.lang.Validate;
import org.bukkit.command.defaults.HelpCommand;
import org.bukkit.command.defaults.PluginsCommand;
import org.bukkit.command.defaults.ReloadCommand;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.entity.Player;
import org.bukkit.util.Java15Compat;
import org.bukkit.util.StringUtil;

public class SimpleCommandMap implements CommandMap {
   private static final Pattern PATTERN_ON_SPACE = Pattern.compile(" ", 16);
   protected final Map<String, Command> knownCommands = new HashMap<>();
   private final org.bukkit.Server server;

   public SimpleCommandMap(org.bukkit.Server server) {
      this.server = server;
      this.setDefaultCommands();
   }

   private void setDefaultCommands() {
      this.register("bukkit", new ReloadCommand("reload"));
      this.register("bukkit", new PluginsCommand("plugins"));
   }

   public void setFallbackCommands() {
      this.register("bukkit", new HelpCommand());
   }

   @Override
   public void registerAll(String fallbackPrefix, List<Command> commands) {
      if (commands != null) {
         for (Command c : commands) {
            this.register(fallbackPrefix, c);
         }
      }
   }

   @Override
   public boolean register(String fallbackPrefix, Command command) {
      return this.register(command.getName(), fallbackPrefix, command);
   }

   @Override
   public boolean register(String label, String fallbackPrefix, Command command) {
      label = label.toLowerCase().trim();
      fallbackPrefix = fallbackPrefix.toLowerCase().trim();
      boolean registered = this.register(label, command, false, fallbackPrefix);
      Iterator<String> iterator = command.getAliases().iterator();

      while (iterator.hasNext()) {
         if (!this.register(iterator.next(), command, true, fallbackPrefix)) {
            iterator.remove();
         }
      }

      if (!registered) {
         command.setLabel(fallbackPrefix + ":" + label);
      }

      command.register(this);
      return registered;
   }

   private synchronized boolean register(String label, Command command, boolean isAlias, String fallbackPrefix) {
      this.knownCommands.put(fallbackPrefix + ":" + label, command);
      if ((command instanceof VanillaCommand || isAlias) && this.knownCommands.containsKey(label)) {
         return false;
      } else {
         Command conflict = this.knownCommands.get(label);
         if (conflict != null && conflict.getLabel().equals(label)) {
            return false;
         } else {
            if (!isAlias) {
               command.setLabel(label);
            }

            this.knownCommands.put(label, command);
            return true;
         }
      }
   }

   @Override
   public boolean dispatch(CommandSender sender, String commandLine) throws CommandException {
      String[] args = PATTERN_ON_SPACE.split(commandLine);
      if (args.length == 0) {
         return false;
      } else {
         String sentCommandLabel = args[0].toLowerCase();
         Command target = this.getCommand(sentCommandLabel);
         if (target == null) {
            return false;
         } else {
            try {
               target.execute(sender, sentCommandLabel, Java15Compat.Arrays_copyOfRange(args, 1, args.length));
               return true;
            } catch (CommandException var7) {
               throw var7;
            } catch (Throwable var8) {
               throw new CommandException("Unhandled exception executing '" + commandLine + "' in " + target, var8);
            }
         }
      }
   }

   @Override
   public synchronized void clearCommands() {
      for (Entry<String, Command> entry : this.knownCommands.entrySet()) {
         entry.getValue().unregister(this);
      }

      this.knownCommands.clear();
      this.setDefaultCommands();
   }

   @Override
   public Command getCommand(String name) {
      return this.knownCommands.get(name.toLowerCase());
   }

   @Override
   public List<String> tabComplete(CommandSender sender, String cmdLine) {
      return this.tabComplete(sender, cmdLine, null);
   }

   public List<String> tabComplete(CommandSender sender, String cmdLine, org.bukkit.Location location) {
      Validate.notNull(sender, "Sender cannot be null");
      Validate.notNull(cmdLine, "Command line cannot null");
      int spaceIndex = cmdLine.indexOf(32);
      if (spaceIndex == -1) {
         ArrayList<String> completions = new ArrayList<>();
         Map<String, Command> knownCommands = this.knownCommands;
         String prefix = sender instanceof Player ? "/" : "";

         for (Entry<String, Command> commandEntry : knownCommands.entrySet()) {
            Command command = commandEntry.getValue();
            if (command.testPermissionSilent(sender)) {
               String name = commandEntry.getKey();
               if (StringUtil.startsWithIgnoreCase(name, cmdLine)) {
                  completions.add(prefix + name);
               }
            }
         }

         completions.sort(String.CASE_INSENSITIVE_ORDER);
         return completions;
      } else {
         String commandName = cmdLine.substring(0, spaceIndex);
         Command target = this.getCommand(commandName);
         if (target == null) {
            return null;
         } else if (!target.testPermissionSilent(sender)) {
            return null;
         } else {
            String argLine = cmdLine.substring(spaceIndex + 1);
            String[] args = PATTERN_ON_SPACE.split(argLine, -1);

            try {
               return target.tabComplete(sender, commandName, args, location);
            } catch (CommandException var12) {
               throw var12;
            } catch (Throwable var13) {
               throw new CommandException("Unhandled exception executing tab-completer for '" + cmdLine + "' in " + target, var13);
            }
         }
      }
   }

   public Collection<Command> getCommands() {
      return Collections.unmodifiableCollection(this.knownCommands.values());
   }

   public void registerServerAliases() {
      Map<String, String[]> values = this.server.getCommandAliases();

      for (String alias : values.keySet()) {
         if (!alias.contains(":") && !alias.contains(" ")) {
            String[] commandStrings = values.get(alias);
            List<String> targets = new ArrayList<>();
            StringBuilder bad = new StringBuilder();

            for (String commandString : commandStrings) {
               String[] commandArgs = commandString.split(" ");
               Command command = this.getCommand(commandArgs[0]);
               if (command == null) {
                  if (bad.length() > 0) {
                     bad.append(", ");
                  }

                  bad.append(commandString);
               } else {
                  targets.add(commandString);
               }
            }

            if (bad.length() <= 0) {
               if (!targets.isEmpty()) {
                  this.knownCommands.put(alias.toLowerCase(), new FormattedCommandAlias(alias.toLowerCase(), targets.toArray(new String[0])));
               } else {
                  this.knownCommands.remove(alias.toLowerCase());
               }
            }
         }
      }
   }
}
