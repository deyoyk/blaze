package org.bukkit.plugin.java;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import org.apache.commons.lang.Validate;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.PluginDescriptionFile;

public final class PluginClassLoader extends URLClassLoader {
   private final JavaPluginLoader loader;
   private final Map<String, Class<?>> classes = new ConcurrentHashMap<>();
   private final PluginDescriptionFile description;
   private final File dataFolder;
   private final File file;
   final JavaPlugin plugin;
   private JavaPlugin pluginInit;
   private IllegalStateException pluginState;

   public JavaPlugin getPlugin() {
      return this.plugin;
   }

   PluginClassLoader(JavaPluginLoader loader, ClassLoader parent, PluginDescriptionFile description, File dataFolder, File file) throws InvalidPluginException, MalformedURLException {
      super(new URL[]{file.toURI().toURL()}, parent);
      Validate.notNull(loader, "Loader cannot be null");
      this.loader = loader;
      this.description = description;
      this.dataFolder = dataFolder;
      this.file = file;

      try {
         Class<?> jarClass;
         try {
            jarClass = Class.forName(description.getMain(), true, this);
         } catch (ClassNotFoundException var10) {
            throw new InvalidPluginException("Cannot find main class `" + description.getMain() + "'", var10);
         }

         Class<? extends JavaPlugin> pluginClass;
         try {
            pluginClass = jarClass.asSubclass(JavaPlugin.class);
         } catch (ClassCastException var9) {
            throw new InvalidPluginException("main class `" + description.getMain() + "' does not extend JavaPlugin", var9);
         }

         this.plugin = pluginClass.newInstance();
      } catch (IllegalAccessException var11) {
         throw new InvalidPluginException("No public constructor", var11);
      } catch (InstantiationException var12) {
         throw new InvalidPluginException("Abnormal plugin type", var12);
      }
   }

   @Override
   protected Class<?> findClass(String name) throws ClassNotFoundException {
      return this.findClass(name, true);
   }

   Class<?> findClass(String name, boolean checkGlobal) throws ClassNotFoundException {
      if (!name.startsWith("org.bukkit.") && !name.startsWith("net.minecraft.")) {
         Class<?> result = this.classes.get(name);
         if (result == null) {
            if (checkGlobal) {
               result = this.loader.getClassByName(name);
            }

            if (result == null) {
               result = super.findClass(name);
               if (result != null) {
                  this.loader.setClass(name, result);
               }
            }

            this.classes.put(name, result);
         }

         return result;
      } else {
         throw new ClassNotFoundException(name);
      }
   }

   Set<String> getClasses() {
      return this.classes.keySet();
   }

   synchronized void initialize(JavaPlugin javaPlugin) {
      Validate.notNull(javaPlugin, "Initializing plugin cannot be null");
      Validate.isTrue(javaPlugin.getClass().getClassLoader() == this, "Cannot initialize plugin outside of this class loader");
      if (this.plugin == null && this.pluginInit == null) {
         this.pluginState = new IllegalStateException("Initial initialization");
         this.pluginInit = javaPlugin;
         javaPlugin.init(this.loader, this.loader.server, this.description, this.dataFolder, this.file, this);
      } else {
         throw new IllegalArgumentException("Plugin already initialized!", this.pluginState);
      }
   }

   static {
      try {
         Method method = ClassLoader.class.getDeclaredMethod("registerAsParallelCapable");
         if (method != null) {
            boolean oldAccessible = method.isAccessible();
            method.setAccessible(true);
            method.invoke(null);
            method.setAccessible(oldAccessible);
         }
      } catch (NoSuchMethodException var2) {
      } catch (Exception var3) {
         org.bukkit.Bukkit.getLogger().log(Level.WARNING, "Error setting PluginClassLoader as parallel capable", (Throwable)var3);
      }
   }
}
