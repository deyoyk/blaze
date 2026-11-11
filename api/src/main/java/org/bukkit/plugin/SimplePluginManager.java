package org.bukkit.plugin;

import com.google.common.collect.ImmutableSet;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.Validate;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommandYamlParser;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.util.FileUtil;

public final class SimplePluginManager implements PluginManager {
   private final org.bukkit.Server server;
   private final Map<Pattern, PluginLoader> fileAssociations = new HashMap<>();
   private final List<Plugin> plugins = new ArrayList<>();
   private final Map<String, Plugin> lookupNames = new HashMap<>();
   private static File updateDirectory = null;
   private final SimpleCommandMap commandMap;
   private final Map<String, Permission> permissions = new HashMap<>();
   private final Map<Boolean, Set<Permission>> defaultPerms = new LinkedHashMap<>();
   private final Map<String, Map<Permissible, Boolean>> permSubs = new HashMap<>();
   private final Map<Boolean, Map<Permissible, Boolean>> defSubs = new HashMap<>();

   public SimplePluginManager(org.bukkit.Server instance, SimpleCommandMap commandMap) {
      this.server = instance;
      this.commandMap = commandMap;
      this.defaultPerms.put(true, new HashSet<>());
      this.defaultPerms.put(false, new HashSet<>());
   }

   @Override
   public void registerInterface(Class<? extends PluginLoader> loader) throws IllegalArgumentException {
      if (PluginLoader.class.isAssignableFrom(loader)) {
         PluginLoader instance;
         try {
            Constructor<? extends PluginLoader> constructor = loader.getConstructor(org.bukkit.Server.class);
            instance = constructor.newInstance(this.server);
         } catch (NoSuchMethodException var10) {
            String className = loader.getName();
            throw new IllegalArgumentException(String.format("Class %s does not have a public %s(Server) constructor", className, className), var10);
         } catch (Exception var11) {
            throw new IllegalArgumentException(
               String.format("Unexpected exception %s while attempting to construct a new instance of %s", var11.getClass().getName(), loader.getName()), var11
            );
         }

         Pattern[] patterns = instance.getPluginFileFilters();
         synchronized (this) {
            for (Pattern pattern : patterns) {
               this.fileAssociations.put(pattern, instance);
            }
         }
      } else {
         throw new IllegalArgumentException(String.format("Class %s does not implement interface PluginLoader", loader.getName()));
      }
   }

   @Override
   public Plugin[] loadPlugins(File directory) {
      Validate.notNull(directory, "Directory cannot be null");
      Validate.isTrue(directory.isDirectory(), "Directory must be a directory");
      List<Plugin> result = new ArrayList<>();
      Set<Pattern> filters = this.fileAssociations.keySet();
      if (!this.server.getUpdateFolder().equals("")) {
         updateDirectory = new File(directory, this.server.getUpdateFolder());
      }

      Map<String, File> plugins = new HashMap<>();
      Set<String> loadedPlugins = new HashSet<>();
      Map<String, Collection<String>> dependencies = new HashMap<>();
      Map<String, Collection<String>> softDependencies = new HashMap<>();

      for (File file : Objects.requireNonNull(directory.listFiles())) {
         PluginLoader loader = null;

         for (Pattern filter : filters) {
            Matcher match = filter.matcher(file.getName());
            if (match.find()) {
               loader = this.fileAssociations.get(filter);
            }
         }

         if (loader != null) {
            PluginDescriptionFile description;
            try {
               description = loader.getPluginDescription(file);
               String name = description.getName();
               if (name.equalsIgnoreCase("bukkit") || name.equalsIgnoreCase("minecraft") || name.equalsIgnoreCase("mojang")) {
                  this.server.getLogger().log(Level.SEVERE, "Could not load '" + file.getPath() + "' in folder '" + directory.getPath() + "': Restricted Name");
                  continue;
               }

               if (description.rawName.indexOf(32) != -1) {
                  this.server
                     .getLogger()
                     .warning(
                        String.format(
                           "Plugin `%s' uses the space-character (0x20) in its name `%s' - this is discouraged", description.getFullName(), description.rawName
                        )
                     );
               }
            } catch (InvalidDescriptionException var23) {
               this.server.getLogger().log(Level.SEVERE, "Could not load '" + file.getPath() + "' in folder '" + directory.getPath() + "'", (Throwable)var23);
               continue;
            }

            File replacedFile = plugins.put(description.getName(), file);
            if (replacedFile != null) {
               this.server
                  .getLogger()
                  .severe(
                     String.format(
                        "Ambiguous plugin name `%s' for files `%s' and `%s' in `%s'",
                        description.getName(),
                        file.getPath(),
                        replacedFile.getPath(),
                        directory.getPath()
                     )
                  );
            }

            Collection<String> softDependencySet = description.getSoftDepend();
            if (softDependencySet != null && !softDependencySet.isEmpty()) {
               if (softDependencies.containsKey(description.getName())) {
                  softDependencies.get(description.getName()).addAll(softDependencySet);
               } else {
                  softDependencies.put(description.getName(), new LinkedList<>(softDependencySet));
               }
            }

            Collection<String> dependencySet = description.getDepend();
            if (dependencySet != null && !dependencySet.isEmpty()) {
               dependencies.put(description.getName(), new LinkedList<>(dependencySet));
            }

            Collection<String> loadBeforeSet = description.getLoadBefore();
            if (loadBeforeSet != null && !loadBeforeSet.isEmpty()) {
               for (String loadBeforeTarget : loadBeforeSet) {
                  if (softDependencies.containsKey(loadBeforeTarget)) {
                     softDependencies.get(loadBeforeTarget).add(description.getName());
                  } else {
                     Collection<String> shortSoftDependency = new LinkedList<>();
                     shortSoftDependency.add(description.getName());
                     softDependencies.put(loadBeforeTarget, shortSoftDependency);
                  }
               }
            }
         }
      }

      while (!plugins.isEmpty()) {
         boolean missingDependency = true;
         Iterator<String> pluginIterator = plugins.keySet().iterator();

         while (pluginIterator.hasNext()) {
            String plugin = pluginIterator.next();
            if (dependencies.containsKey(plugin)) {
               Iterator<String> dependencyIterator = dependencies.get(plugin).iterator();

               while (dependencyIterator.hasNext()) {
                  String dependency = dependencyIterator.next();
                  if (loadedPlugins.contains(dependency)) {
                     dependencyIterator.remove();
                  } else if (!plugins.containsKey(dependency)) {
                     missingDependency = false;
                     File file = plugins.get(plugin);
                     pluginIterator.remove();
                     softDependencies.remove(plugin);
                     dependencies.remove(plugin);
                     this.server
                        .getLogger()
                        .log(
                           Level.SEVERE,
                           "Could not load '" + file.getPath() + "' in folder '" + directory.getPath() + "'",
                           (Throwable)(new UnknownDependencyException(dependency))
                        );
                     break;
                  }
               }

               if (dependencies.containsKey(plugin) && dependencies.get(plugin).isEmpty()) {
                  dependencies.remove(plugin);
               }
            }

            if (softDependencies.containsKey(plugin)) {
               softDependencies.get(plugin).removeIf(softDependency -> !plugins.containsKey(softDependency));
               if (softDependencies.get(plugin).isEmpty()) {
                  softDependencies.remove(plugin);
               }
            }

            if (!dependencies.containsKey(plugin) && !softDependencies.containsKey(plugin) && plugins.containsKey(plugin)) {
               File file = plugins.get(plugin);
               pluginIterator.remove();
               missingDependency = false;

               try {
                  Plugin pl = this.loadPlugin(file);
                  if (pl != null) {
                     result.add(pl);
                     loadedPlugins.add(plugin);
                  }
               } catch (InvalidPluginException var21) {
                  this.server
                     .getLogger()
                     .log(Level.SEVERE, "Could not load '" + file.getPath() + "' in folder '" + directory.getPath() + "'", (Throwable)var21);
               }
            }
         }

         if (missingDependency) {
            pluginIterator = plugins.keySet().iterator();

            while (pluginIterator.hasNext()) {
               String pluginx = pluginIterator.next();
               if (!dependencies.containsKey(pluginx)) {
                  softDependencies.remove(pluginx);
                  missingDependency = false;
                  File file = plugins.get(pluginx);
                  pluginIterator.remove();

                  try {
                     Plugin pl = this.loadPlugin(file);
                     if (pl != null) {
                        result.add(pl);
                        loadedPlugins.add(pluginx);
                     }
                     break;
                  } catch (InvalidPluginException var22) {
                     this.server
                        .getLogger()
                        .log(Level.SEVERE, "Could not load '" + file.getPath() + "' in folder '" + directory.getPath() + "'", (Throwable)var22);
                  }
               }
            }

            if (missingDependency) {
               softDependencies.clear();
               dependencies.clear();
               Iterator<File> failedPluginIterator = plugins.values().iterator();

               while (failedPluginIterator.hasNext()) {
                  File file = failedPluginIterator.next();
                  failedPluginIterator.remove();
                  this.server
                     .getLogger()
                     .log(Level.SEVERE, "Could not load '" + file.getPath() + "' in folder '" + directory.getPath() + "': circular dependency detected");
               }
            }
         }
      }

      return result.toArray(new Plugin[0]);
   }

   @Override
   public synchronized Plugin loadPlugin(File file) throws InvalidPluginException, UnknownDependencyException {
      Validate.notNull(file, "File cannot be null");
      this.checkUpdate(file);
      Set<Pattern> filters = this.fileAssociations.keySet();
      Plugin result = null;

      for (Pattern filter : filters) {
         String name = file.getName();
         Matcher match = filter.matcher(name);
         if (match.find()) {
            PluginLoader loader = this.fileAssociations.get(filter);
            result = loader.loadPlugin(file);
         }
      }

      if (result != null) {
         this.plugins.add(result);
         this.lookupNames.put(result.getDescription().getName().toLowerCase(), result);
      }

      return result;
   }

   private void checkUpdate(File file) {
      if (updateDirectory != null && updateDirectory.isDirectory()) {
         File updateFile = new File(updateDirectory, file.getName());
         if (updateFile.isFile() && FileUtil.copy(updateFile, file)) {
            updateFile.delete();
         }
      }
   }

   @Override
   public synchronized Plugin getPlugin(String name) {
      return this.lookupNames.get(name.replace(' ', '_').toLowerCase());
   }

   @Override
   public synchronized Plugin[] getPlugins() {
      return this.plugins.toArray(new Plugin[0]);
   }

   @Override
   public boolean isPluginEnabled(String name) {
      Plugin plugin = this.getPlugin(name);
      return this.isPluginEnabled(plugin);
   }

   @Override
   public boolean isPluginEnabled(Plugin plugin) {
      return plugin != null && this.plugins.contains(plugin) && plugin.isEnabled();
   }

   @Override
   public boolean useTimings() {
      return false;
   }

   @Override
   public void enablePlugin(Plugin plugin) {
      if (!plugin.isEnabled()) {
         List<Command> pluginCommands = PluginCommandYamlParser.parse(plugin);
         if (!pluginCommands.isEmpty()) {
            this.commandMap.registerAll(plugin.getDescription().getName(), pluginCommands);
         }

         try {
            plugin.getPluginLoader().enablePlugin(plugin);
         } catch (Throwable var4) {
            this.server
               .getLogger()
               .log(
                  Level.SEVERE, "Error occurred (in the plugin loader) while enabling " + plugin.getDescription().getFullName() + " (Is it up to date?)", var4
               );
         }

         HandlerList.bakeAll();
      }
   }

   @Override
   public void disablePlugins() {
      Plugin[] plugins = this.getPlugins();

      for (int i = plugins.length - 1; i >= 0; i--) {
         this.disablePlugin(plugins[i]);
      }
   }

   @Override
   public void disablePlugin(Plugin plugin) {
      if (plugin.isEnabled()) {
         try {
            plugin.getPluginLoader().disablePlugin(plugin);
         } catch (Throwable var7) {
            this.server
               .getLogger()
               .log(
                  Level.SEVERE, "Error occurred (in the plugin loader) while disabling " + plugin.getDescription().getFullName() + " (Is it up to date?)", var7
               );
         }

         try {
            this.server.getScheduler().cancelTasks(plugin);
         } catch (Throwable var6) {
            this.server
               .getLogger()
               .log(
                  Level.SEVERE,
                  "Error occurred (in the plugin loader) while cancelling tasks for " + plugin.getDescription().getFullName() + " (Is it up to date?)",
                  var6
               );
         }

         try {
            this.server.getServicesManager().unregisterAll(plugin);
         } catch (Throwable var5) {
            this.server
               .getLogger()
               .log(
                  Level.SEVERE,
                  "Error occurred (in the plugin loader) while unregistering services for " + plugin.getDescription().getFullName() + " (Is it up to date?)",
                  var5
               );
         }

         try {
            HandlerList.unregisterAll(plugin);
         } catch (Throwable var4) {
            this.server
               .getLogger()
               .log(
                  Level.SEVERE,
                  "Error occurred (in the plugin loader) while unregistering events for " + plugin.getDescription().getFullName() + " (Is it up to date?)",
                  var4
               );
         }

         try {
            this.server.getMessenger().unregisterIncomingPluginChannel(plugin);
            this.server.getMessenger().unregisterOutgoingPluginChannel(plugin);
         } catch (Throwable var3) {
            this.server
               .getLogger()
               .log(
                  Level.SEVERE,
                  "Error occurred (in the plugin loader) while unregistering plugin channels for "
                     + plugin.getDescription().getFullName()
                     + " (Is it up to date?)",
                  var3
               );
         }
      }
   }

   @Override
   public void clearPlugins() {
      synchronized (this) {
         this.disablePlugins();
         this.plugins.clear();
         this.lookupNames.clear();
         HandlerList.unregisterAll();
         this.fileAssociations.clear();
         this.permissions.clear();
         this.defaultPerms.get(true).clear();
         this.defaultPerms.get(false).clear();
      }
   }

   @Override
   public void callEvent(Event event) {
      if (event.isAsynchronous()) {
         if (Thread.holdsLock(this)) {
            throw new IllegalStateException(event.getEventName() + " cannot be triggered asynchronously from inside synchronized code.");
         }

         if (this.server.isPrimaryThread()) {
            throw new IllegalStateException(event.getEventName() + " cannot be triggered asynchronously from primary server thread.");
         }

         this.fireEvent(event);
      } else {
         synchronized (this) {
            this.fireEvent(event);
         }
      }
   }

   private void fireEvent(Event event) {
      HandlerList handlers = event.getHandlers();
      RegisteredListener[] listeners = handlers.getRegisteredListeners();

      for (RegisteredListener registration : listeners) {
         if (registration.getPlugin().isEnabled()) {
            try {
               registration.callEvent(event);
            } catch (AuthorNagException var10) {
               Plugin plugin = registration.getPlugin();
               if (plugin.isNaggable()) {
                  plugin.setNaggable(false);
                  this.server
                     .getLogger()
                     .log(
                        Level.SEVERE,
                        String.format(
                           "Nag author(s): '%s' of '%s' about the following: %s",
                           plugin.getDescription().getAuthors(),
                           plugin.getDescription().getFullName(),
                           var10.getMessage()
                        )
                     );
               }
            } catch (Throwable var11) {
               this.server
                  .getLogger()
                  .log(Level.SEVERE, "Could not pass event " + event.getEventName() + " to " + registration.getPlugin().getDescription().getFullName(), var11);
            }
         }
      }
   }

   @Override
   public void registerEvents(Listener listener, Plugin plugin) {
      if (!plugin.isEnabled()) {
         throw new IllegalPluginAccessException("Plugin attempted to register " + listener + " while not enabled");
      } else {
         for (Entry<Class<? extends Event>, Set<RegisteredListener>> entry : plugin.getPluginLoader().createRegisteredListeners(listener, plugin).entrySet()) {
            this.getEventListeners(this.getRegistrationClass(entry.getKey())).registerAll(entry.getValue());
         }
      }
   }

   @Override
   public void registerEvent(Class<? extends Event> event, Listener listener, EventPriority priority, EventExecutor executor, Plugin plugin) {
      this.registerEvent(event, listener, priority, executor, plugin, false);
   }

   @Override
   public void registerEvent(
      Class<? extends Event> event, Listener listener, EventPriority priority, EventExecutor executor, Plugin plugin, boolean ignoreCancelled
   ) {
      Validate.notNull(listener, "Listener cannot be null");
      Validate.notNull(priority, "Priority cannot be null");
      Validate.notNull(executor, "Executor cannot be null");
      Validate.notNull(plugin, "Plugin cannot be null");
      if (!plugin.isEnabled()) {
         throw new IllegalPluginAccessException("Plugin attempted to register " + event + " while not enabled");
      } else {
         this.getEventListeners(event).register(new RegisteredListener(listener, executor, priority, plugin, ignoreCancelled));
      }
   }

   private HandlerList getEventListeners(Class<? extends Event> type) {
      try {
         Method method = this.getRegistrationClass(type).getDeclaredMethod("getHandlerList");
         method.setAccessible(true);
         return (HandlerList)method.invoke(null);
      } catch (Exception var3) {
         throw new IllegalPluginAccessException(var3.toString());
      }
   }

   private Class<? extends Event> getRegistrationClass(Class<? extends Event> clazz) {
      try {
         clazz.getDeclaredMethod("getHandlerList");
         return clazz;
      } catch (NoSuchMethodException var3) {
         if (clazz.getSuperclass() != null && !clazz.getSuperclass().equals(Event.class) && Event.class.isAssignableFrom(clazz.getSuperclass())) {
            return this.getRegistrationClass(clazz.getSuperclass().asSubclass(Event.class));
         } else {
            throw new IllegalPluginAccessException("Unable to find handler list for event " + clazz.getName() + ". Static getHandlerList method required!");
         }
      }
   }

   @Override
   public Permission getPermission(String name) {
      return this.permissions.get(name.toLowerCase());
   }

   @Override
   public void addPermission(Permission perm) {
      String name = perm.getName().toLowerCase();
      if (this.permissions.containsKey(name)) {
         throw new IllegalArgumentException("The permission " + name + " is already defined!");
      } else {
         this.permissions.put(name, perm);
         this.calculatePermissionDefault(perm);
      }
   }

   @Override
   public Set<Permission> getDefaultPermissions(boolean op) {
      return ImmutableSet.copyOf(this.defaultPerms.get(op));
   }

   @Override
   public void removePermission(Permission perm) {
      this.removePermission(perm.getName());
   }

   @Override
   public void removePermission(String name) {
      this.permissions.remove(name.toLowerCase());
   }

   @Override
   public void recalculatePermissionDefaults(Permission perm) {
      if (perm != null && this.permissions.containsKey(perm.getName().toLowerCase())) {
         this.defaultPerms.get(true).remove(perm);
         this.defaultPerms.get(false).remove(perm);
         this.calculatePermissionDefault(perm);
      }
   }

   private void calculatePermissionDefault(Permission perm) {
      if (perm.getDefault() == PermissionDefault.OP || perm.getDefault() == PermissionDefault.TRUE) {
         this.defaultPerms.get(true).add(perm);
         this.dirtyPermissibles(true);
      }

      if (perm.getDefault() == PermissionDefault.NOT_OP || perm.getDefault() == PermissionDefault.TRUE) {
         this.defaultPerms.get(false).add(perm);
         this.dirtyPermissibles(false);
      }
   }

   private void dirtyPermissibles(boolean op) {
      for (Permissible p : this.getDefaultPermSubscriptions(op)) {
         p.recalculatePermissions();
      }
   }

   @Override
   public void subscribeToPermission(String permission, Permissible permissible) {
      String name = permission.toLowerCase();
      Map<Permissible, Boolean> map = this.permSubs.computeIfAbsent(name, k -> new WeakHashMap<>());
      map.put(permissible, true);
   }

   @Override
   public void unsubscribeFromPermission(String permission, Permissible permissible) {
      String name = permission.toLowerCase();
      Map<Permissible, Boolean> map = this.permSubs.get(name);
      if (map != null) {
         map.remove(permissible);
         if (map.isEmpty()) {
            this.permSubs.remove(name);
         }
      }
   }

   @Override
   public Set<Permissible> getPermissionSubscriptions(String permission) {
      String name = permission.toLowerCase();
      Map<Permissible, Boolean> map = this.permSubs.get(name);
      return map == null ? ImmutableSet.of() : ImmutableSet.copyOf(map.keySet());
   }

   @Override
   public void subscribeToDefaultPerms(boolean op, Permissible permissible) {
      Map<Permissible, Boolean> map = this.defSubs.computeIfAbsent(op, k -> new WeakHashMap<>());
      map.put(permissible, true);
   }

   @Override
   public void unsubscribeFromDefaultPerms(boolean op, Permissible permissible) {
      Map<Permissible, Boolean> map = this.defSubs.get(op);
      if (map != null) {
         map.remove(permissible);
         if (map.isEmpty()) {
            this.defSubs.remove(op);
         }
      }
   }

   @Override
   public Set<Permissible> getDefaultPermSubscriptions(boolean op) {
      Map<Permissible, Boolean> map = this.defSubs.get(op);
      return map == null ? ImmutableSet.of() : ImmutableSet.copyOf(map.keySet());
   }

   @Override
   public Set<Permission> getPermissions() {
      return new HashSet<>(this.permissions.values());
   }
}
