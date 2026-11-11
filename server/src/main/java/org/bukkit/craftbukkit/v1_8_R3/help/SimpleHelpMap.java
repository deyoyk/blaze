package org.bukkit.craftbukkit.v1_8_R3.help;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.MultipleCommandAlias;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.command.VanillaCommandWrapper;
import org.bukkit.help.GenericCommandHelpTopic;
import org.bukkit.help.HelpMap;
import org.bukkit.help.HelpTopic;
import org.bukkit.help.HelpTopicComparator;
import org.bukkit.help.HelpTopicFactory;
import org.bukkit.help.IndexHelpTopic;

public class SimpleHelpMap implements HelpMap {
   private HelpTopic defaultTopic;
   private final Map<String, HelpTopic> helpTopics = new TreeMap<>(HelpTopicComparator.topicNameComparatorInstance());
   private final Map<Class, HelpTopicFactory<Command>> topicFactoryMap = new HashMap<>();
   private final CraftServer server;
   private HelpYamlReader yaml;

   public SimpleHelpMap(CraftServer server) {
      this.server = server;
      this.yaml = new HelpYamlReader(server);
      Predicate indexFilter = Predicates.not(Predicates.instanceOf(CommandAliasHelpTopic.class));
      if (!this.yaml.commandTopicsInMasterIndex()) {
         indexFilter = Predicates.and(indexFilter, Predicates.not(new SimpleHelpMap.IsCommandTopicPredicate()));
      }

      this.defaultTopic = new IndexHelpTopic(
         "Index", null, null, Collections2.filter(this.helpTopics.values(), indexFilter), "Use /help [n] to get page n of help."
      );
      this.registerHelpTopicFactory(MultipleCommandAlias.class, new MultipleCommandAliasHelpTopicFactory());
   }

   @Override
   public synchronized HelpTopic getHelpTopic(String topicName) {
      if (topicName.equals("")) {
         return this.defaultTopic;
      } else {
         return this.helpTopics.containsKey(topicName) ? this.helpTopics.get(topicName) : null;
      }
   }

   @Override
   public Collection<HelpTopic> getHelpTopics() {
      return this.helpTopics.values();
   }

   @Override
   public synchronized void addTopic(HelpTopic topic) {
      if (!this.helpTopics.containsKey(topic.getName())) {
         this.helpTopics.put(topic.getName(), topic);
      }
   }

   @Override
   public synchronized void clear() {
      this.helpTopics.clear();
   }

   @Override
   public List<String> getIgnoredPlugins() {
      return this.yaml.getIgnoredPlugins();
   }

   public synchronized void initializeGeneralTopics() {
      this.yaml = new HelpYamlReader(this.server);

      for (HelpTopic topic : this.yaml.getGeneralTopics()) {
         this.addTopic(topic);
      }

      for (HelpTopic topic : this.yaml.getIndexTopics()) {
         if (topic.getName().equals("Default")) {
            this.defaultTopic = topic;
         } else {
            this.addTopic(topic);
         }
      }
   }

   public synchronized void initializeCommands() {
      Set<String> ignoredPlugins = new HashSet<>(this.yaml.getIgnoredPlugins());
      if (!ignoredPlugins.contains("All")) {
         label88:
         for (Command command : this.server.getCommandMap().getCommands()) {
            if (!this.commandInIgnoredPlugin(command, ignoredPlugins)) {
               for (Class c : this.topicFactoryMap.keySet()) {
                  if (c.isAssignableFrom(command.getClass())) {
                     HelpTopic t = this.topicFactoryMap.get(c).createTopic(command);
                     if (t != null) {
                        this.addTopic(t);
                     }
                     continue label88;
                  }

                  if (command instanceof PluginCommand && c.isAssignableFrom(((PluginCommand)command).getExecutor().getClass())) {
                     HelpTopic t = this.topicFactoryMap.get(c).createTopic(command);
                     if (t != null) {
                        this.addTopic(t);
                     }
                     continue label88;
                  }
               }

               this.addTopic(new GenericCommandHelpTopic(command));
            }
         }

         for (Command commandx : this.server.getCommandMap().getCommands()) {
            if (!this.commandInIgnoredPlugin(commandx, ignoredPlugins)) {
               for (String alias : commandx.getAliases()) {
                  if (this.server.getCommandMap().getCommand(alias) == commandx) {
                     this.addTopic(new CommandAliasHelpTopic("/" + alias, "/" + commandx.getLabel(), this));
                  }
               }
            }
         }

         Collection<HelpTopic> filteredTopics = Collections2.filter(this.helpTopics.values(), Predicates.instanceOf(CommandAliasHelpTopic.class));
         if (!filteredTopics.isEmpty()) {
            this.addTopic(new IndexHelpTopic("Aliases", "Lists command aliases", null, filteredTopics));
         }

         Map<String, Set<HelpTopic>> pluginIndexes = new HashMap<>();
         this.fillPluginIndexes(pluginIndexes, this.server.getCommandMap().getCommands());

         for (Entry<String, Set<HelpTopic>> entry : pluginIndexes.entrySet()) {
            this.addTopic(
               new IndexHelpTopic(
                  entry.getKey(), "All commands for " + entry.getKey(), null, entry.getValue(), "Below is a list of all " + entry.getKey() + " commands:"
               )
            );
         }

         for (HelpTopicAmendment amendment : this.yaml.getTopicAmendments()) {
            if (this.helpTopics.containsKey(amendment.getTopicName())) {
               this.helpTopics.get(amendment.getTopicName()).amendTopic(amendment.getShortText(), amendment.getFullText());
               if (amendment.getPermission() != null) {
                  this.helpTopics.get(amendment.getTopicName()).amendCanSee(amendment.getPermission());
               }
            }
         }
      }
   }

   private void fillPluginIndexes(Map<String, Set<HelpTopic>> pluginIndexes, Collection<? extends Command> commands) {
      for (Command command : commands) {
         String pluginName = this.getCommandPluginName(command);
         if (pluginName != null) {
            HelpTopic topic = this.getHelpTopic("/" + command.getLabel());
            if (topic != null) {
               if (!pluginIndexes.containsKey(pluginName)) {
                  pluginIndexes.put(pluginName, new TreeSet<>(HelpTopicComparator.helpTopicComparatorInstance()));
               }

               pluginIndexes.get(pluginName).add(topic);
            }
         }
      }
   }

   private String getCommandPluginName(Command command) {
      if (command instanceof VanillaCommandWrapper) {
         return "Minecraft";
      } else if (command instanceof BukkitCommand || command instanceof VanillaCommand) {
         return "Bukkit";
      } else {
         return command instanceof PluginIdentifiableCommand ? ((PluginIdentifiableCommand)command).getPlugin().getName() : null;
      }
   }

   private boolean commandInIgnoredPlugin(Command command, Set<String> ignoredPlugins) {
      return (command instanceof BukkitCommand || command instanceof VanillaCommand) && ignoredPlugins.contains("Bukkit")
         ? true
         : command instanceof PluginIdentifiableCommand && ignoredPlugins.contains(((PluginIdentifiableCommand)command).getPlugin().getName());
   }

   @Override
   public void registerHelpTopicFactory(Class commandClass, HelpTopicFactory factory) {
      if (!Command.class.isAssignableFrom(commandClass) && !CommandExecutor.class.isAssignableFrom(commandClass)) {
         throw new IllegalArgumentException("commandClass must implement either Command or CommandExecutor!");
      } else {
         this.topicFactoryMap.put(commandClass, factory);
      }
   }

   private class IsCommandTopicPredicate implements Predicate<HelpTopic> {
      @Override
      public boolean apply(HelpTopic topic) {
         return topic.getName().charAt(0) == '/';
      }
   }
}
