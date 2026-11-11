package org.bukkit.plugin.java;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.regex.Pattern;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.AuthorNagException;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.UnknownDependencyException;
import org.yaml.snakeyaml.error.YAMLException;

public final class JavaPluginLoader implements PluginLoader {
   final org.bukkit.Server server;
   private final Pattern[] fileFilters = new Pattern[]{Pattern.compile("\\.jar$")};
   private final Map<String, Class<?>> classes = new ConcurrentHashMap<>();
   private final Map<String, PluginClassLoader> loaders = new LinkedHashMap<>();

   @Deprecated
   public JavaPluginLoader(org.bukkit.Server instance) {
      Validate.notNull(instance, "Server cannot be null");
      this.server = instance;
   }

   @Override
   public Plugin loadPlugin(File file) throws InvalidPluginException {
      Validate.notNull(file, "File cannot be null");
      if (!file.exists()) {
         throw new InvalidPluginException(new FileNotFoundException(file.getPath() + " does not exist"));
      } else {
         PluginDescriptionFile description;
         try {
            description = this.getPluginDescription(file);
         } catch (InvalidDescriptionException var11) {
            throw new InvalidPluginException(var11);
         }

         File parentFile = file.getParentFile();
         File dataFolder = new File(parentFile, description.getName());
         File oldDataFolder = new File(parentFile, description.getRawName());
         if (!dataFolder.equals(oldDataFolder)) {
            if (dataFolder.isDirectory() && oldDataFolder.isDirectory()) {
               this.server
                  .getLogger()
                  .warning(
                     String.format(
                        "While loading %s (%s) found old-data folder: `%s' next to the new one `%s'",
                        description.getFullName(),
                        file,
                        oldDataFolder,
                        dataFolder
                     )
                  );
            } else if (oldDataFolder.isDirectory() && !dataFolder.exists()) {
               if (!oldDataFolder.renameTo(dataFolder)) {
                  throw new InvalidPluginException("Unable to rename old data folder: `" + oldDataFolder + "' to: `" + dataFolder + "'");
               }

               this.server
                  .getLogger()
                  .log(
                     Level.INFO,
                     String.format("While loading %s (%s) renamed data folder: `%s' to `%s'", description.getFullName(), file, oldDataFolder, dataFolder)
                  );
            }
         }

         if (dataFolder.exists() && !dataFolder.isDirectory()) {
            throw new InvalidPluginException(
               String.format("Projected datafolder: `%s' for %s (%s) exists and is not a directory", dataFolder, description.getFullName(), file)
            );
         } else {
            for (String pluginName : description.getDepend()) {
               PluginClassLoader current = this.loaders.get(pluginName);
               if (current == null) {
                  throw new UnknownDependencyException(pluginName);
               }
            }

            PluginClassLoader loader;
            try {
               loader = new PluginClassLoader(this, this.getClass().getClassLoader(), description, dataFolder, file);
            } catch (InvalidPluginException var9) {
               throw var9;
            } catch (Throwable var10) {
               throw new InvalidPluginException(var10);
            }

            this.loaders.put(description.getName(), loader);

            try {
               if (description.getCommands().containsKey("ver") || description.getCommands().containsKey("version")) {
                  org.bukkit.Bukkit.getLogger().info("Didn't load plugin " + description.getName() + " due to attempt to create the command /version.");
                  return null;
               }
            } catch (NullPointerException var12) {
            }

            return loader.plugin;
         }
      }
   }

   @Override
   public PluginDescriptionFile getPluginDescription(File file) throws InvalidDescriptionException {
      Validate.notNull(file, "File cannot be null");
      JarFile jar = null;
      InputStream stream = null;

      PluginDescriptionFile var5;
      try {
         jar = new JarFile(file);
         JarEntry entry = jar.getJarEntry("plugin.yml");
         if (entry == null) {
            throw new InvalidDescriptionException(new FileNotFoundException("Jar does not contain plugin.yml"));
         }

         stream = jar.getInputStream(entry);
         var5 = new PluginDescriptionFile(stream);
      } catch (YAMLException | IOException var17) {
         throw new InvalidDescriptionException(var17);
      } finally {
         if (jar != null) {
            try {
               jar.close();
            } catch (IOException var16) {
            }
         }

         if (stream != null) {
            try {
               stream.close();
            } catch (IOException var15) {
            }
         }
      }

      return var5;
   }

   @Override
   public Pattern[] getPluginFileFilters() {
      return (Pattern[])this.fileFilters.clone();
   }

   Class<?> getClassByName(String name) {
      Class<?> cachedClass = this.classes.get(name);
      if (cachedClass != null) {
         return cachedClass;
      } else {
         for (String current : this.loaders.keySet()) {
            PluginClassLoader loader = this.loaders.get(current);

            try {
               cachedClass = loader.findClass(name, false);
            } catch (ClassNotFoundException var7) {
            }

            if (cachedClass != null) {
               return cachedClass;
            }
         }

         return null;
      }
   }

   void setClass(String name, Class<?> clazz) {
      if (!this.classes.containsKey(name)) {
         this.classes.put(name, clazz);
         if (ConfigurationSerializable.class.isAssignableFrom(clazz)) {
            Class<? extends ConfigurationSerializable> serializable = clazz.asSubclass(ConfigurationSerializable.class);
            ConfigurationSerialization.registerClass(serializable);
         }
      }
   }

   private void removeClass(String name) {
      Class<?> clazz = this.classes.remove(name);

      try {
         if (clazz != null && ConfigurationSerializable.class.isAssignableFrom(clazz)) {
            Class<? extends ConfigurationSerializable> serializable = clazz.asSubclass(ConfigurationSerializable.class);
            ConfigurationSerialization.unregisterClass(serializable);
         }
      } catch (NullPointerException var4) {
      }
   }

   @Override
   public Map<Class<? extends Event>, Set<RegisteredListener>> createRegisteredListeners(Listener listener, Plugin plugin) {
      Validate.notNull(plugin, "Plugin can not be null");
      Validate.notNull(listener, "Listener can not be null");
      Map<Class<? extends Event>, Set<RegisteredListener>> ret = new HashMap<>();

      Set<Method> methods;
      try {
         Method[] publicMethods = listener.getClass().getMethods();
         Method[] privateMethods = listener.getClass().getDeclaredMethods();
         methods = new HashSet<>(publicMethods.length + privateMethods.length, 1.0F);
         methods.addAll(Arrays.asList(publicMethods));
         methods.addAll(Arrays.asList(privateMethods));
      } catch (NoClassDefFoundError var14) {
         plugin.getLogger()
            .severe(
               "Plugin "
                  + plugin.getDescription().getFullName()
                  + " has failed to register events for "
                  + listener.getClass()
                  + " because "
                  + var14.getMessage()
                  + " does not exist."
            );
         return ret;
      }

      for (Method method : methods) {
         EventHandler eh = method.getAnnotation(EventHandler.class);
         if (eh != null && !method.isBridge() && !method.isSynthetic()) {
            Class<?> checkClass;
            if (method.getParameterTypes().length == 1 && Event.class.isAssignableFrom(checkClass = method.getParameterTypes()[0])) {
               Class<? extends Event> eventClass = checkClass.asSubclass(Event.class);
               method.setAccessible(true);
               Set<RegisteredListener> eventSet = ret.computeIfAbsent(eventClass, k -> new HashSet<>());

               for (Class<?> clazz = eventClass; Event.class.isAssignableFrom(clazz); clazz = clazz.getSuperclass()) {
                  if (clazz.getAnnotation(Deprecated.class) != null) {
                     org.bukkit.Warning warning = clazz.getAnnotation(org.bukkit.Warning.class);
                     org.bukkit.Warning.WarningState warningState = this.server.getWarningState();
                     if (warningState.printFor(warning)) {
                        plugin.getLogger()
                           .log(
                              Level.WARNING,
                              String.format(
                                 "\"%s\" has registered a listener for %s on method \"%s\", but the event is Deprecated. \"%s\"; please notify the authors %s.",
                                 plugin.getDescription().getFullName(),
                                 clazz.getName(),
                                 method.toGenericString(),
                                 warning != null && warning.reason().length() != 0 ? warning.reason() : "Server performance will be affected",
                                 Arrays.toString(plugin.getDescription().getAuthors().toArray())
                              ),
                              (Throwable)(warningState == org.bukkit.Warning.WarningState.ON ? new AuthorNagException(null) : null)
                           );
                     }
                     break;
                  }
               }

               eventSet.add(new RegisteredListener(listener, EventExecutor.create(method, eventClass), eh.priority(), plugin, eh.ignoreCancelled()));
            } else {
               plugin.getLogger()
                  .severe(
                     plugin.getDescription().getFullName()
                        + " attempted to register an invalid EventHandler method signature \""
                        + method.toGenericString()
                        + "\" in "
                        + listener.getClass()
                  );
            }
         }
      }

      return ret;
   }

   @Override
   public void enablePlugin(Plugin plugin) {
      Validate.isTrue(plugin instanceof JavaPlugin, "Plugin is not associated with this PluginLoader");
      if (!plugin.isEnabled()) {
         plugin.getLogger().info("Enabling " + plugin.getDescription().getFullName());
         JavaPlugin jPlugin = (JavaPlugin)plugin;
         String pluginName = jPlugin.getDescription().getName();
         if (!this.loaders.containsKey(pluginName)) {
            this.loaders.put(pluginName, (PluginClassLoader)jPlugin.getClassLoader());
         }

         try {
            jPlugin.setEnabled(true);
         } catch (Throwable var5) {
            this.server.getLogger().log(Level.SEVERE, "Error occurred while enabling " + plugin.getDescription().getFullName() + " (Is it up to date?)", var5);
            this.disablePlugin(jPlugin);
            return;
         }

         this.server.getPluginManager().callEvent(new PluginEnableEvent(plugin));
      }
   }

   @Override
   public void disablePlugin(Plugin plugin) {
      Validate.isTrue(plugin instanceof JavaPlugin, "Plugin is not associated with this PluginLoader");
      if (plugin.isEnabled()) {
         String message = String.format("Disabling %s", plugin.getDescription().getFullName());
         plugin.getLogger().info(message);
         this.server.getPluginManager().callEvent(new PluginDisableEvent(plugin));
         JavaPlugin jPlugin = (JavaPlugin)plugin;
         ClassLoader cloader = jPlugin.getClassLoader();

         try {
            jPlugin.setEnabled(false);
         } catch (Throwable var9) {
            this.server.getLogger().log(Level.SEVERE, "Error occurred while disabling " + plugin.getDescription().getFullName() + " (Is it up to date?)", var9);
         }

         this.loaders.remove(jPlugin.getDescription().getName());
         if (cloader instanceof PluginClassLoader) {
            PluginClassLoader loader = (PluginClassLoader)cloader;

            for (String name : loader.getClasses()) {
               this.removeClass(name);
            }
         }
      }
   }
}
