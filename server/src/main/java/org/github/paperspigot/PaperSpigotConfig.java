package org.github.paperspigot;

import com.google.common.base.Throwables;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import net.minecraft.server.v1_8_R3.Items;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class PaperSpigotConfig {
   private static File CONFIG_FILE;
   private static final String HEADER = "This is the main configuration file for PaperSpigot.\nAs you can see, there's tons to configure. Some options may impact gameplay, so use\nwith caution, and make sure you know what each option does before configuring.\n\nIf you need help with the configuration or have any questions related to PaperSpigot,\njoin us at the IRC.\n\nIRC: #paperspigot @ irc.spi.gt ( http://irc.spi.gt/iris/?channels=PaperSpigot )\n";
   public static YamlConfiguration config;
   static int version;
   public static double babyZombieMovementSpeed;
   public static boolean interactLimitEnabled;
   public static double strengthEffectModifier;
   public static double weaknessEffectModifier;
   public static Set<Integer> dataValueAllowedItems;
   public static boolean stackableLavaBuckets;
   public static boolean stackableWaterBuckets;
   public static boolean stackableMilkBuckets;
   public static boolean warnForExcessiveVelocity;

   public static void init(File configFile) {
      CONFIG_FILE = configFile;
      config = new YamlConfiguration();

      try {
         config.load(CONFIG_FILE);
      } catch (IOException var2) {
      } catch (InvalidConfigurationException var3) {
         Bukkit.getLogger().log(Level.SEVERE, "Could not load spigot.yml, please correct your syntax errors", (Throwable)var3);
         throw Throwables.propagate(var3);
      }

      config.options()
         .header(
            "This is the main configuration file for PaperSpigot.\nAs you can see, there's tons to configure. Some options may impact gameplay, so use\nwith caution, and make sure you know what each option does before configuring.\n\nIf you need help with the configuration or have any questions related to PaperSpigot,\njoin us at the IRC.\n\nIRC: #paperspigot @ irc.spi.gt ( http://irc.spi.gt/iris/?channels=PaperSpigot )\n"
         );
      config.options().copyDefaults(true);
      version = getInt("config-version", 9);
      set("config-version", 9);
      readConfig(PaperSpigotConfig.class, null);
   }

   static void readConfig(Class<?> clazz, Object instance) {
      Method[] var5;
      for (Method method : var5 = clazz.getDeclaredMethods()) {
         if (Modifier.isPrivate(method.getModifiers()) && method.getParameterTypes().length == 0 && method.getReturnType() == void.class) {
            try {
               method.setAccessible(true);
               method.invoke(instance);
            } catch (InvocationTargetException var8) {
               throw Throwables.propagate(var8.getCause());
            } catch (Exception var9) {
               Bukkit.getLogger().log(Level.SEVERE, "Error invoking " + method, (Throwable)var9);
            }
         }
      }

      try {
         config.save(CONFIG_FILE);
      } catch (IOException var7) {
         Bukkit.getLogger().log(Level.SEVERE, "Could not save " + CONFIG_FILE, (Throwable)var7);
      }
   }

   private static void set(String path, Object val) {
      config.set(path, val);
   }

   private static boolean getBoolean(String path, boolean def) {
      config.addDefault(path, def);
      return config.getBoolean(path, config.getBoolean(path));
   }

   private static double getDouble(String path, double def) {
      config.addDefault(path, def);
      return config.getDouble(path, config.getDouble(path));
   }

   private static float getFloat(String path, float def) {
      return (float)getDouble(path, def);
   }

   private static int getInt(String path, int def) {
      config.addDefault(path, def);
      return config.getInt(path, config.getInt(path));
   }

   private static <T> List getList(String path, T def) {
      config.addDefault(path, def);
      return config.getList(path, config.getList(path));
   }

   private static String getString(String path, String def) {
      config.addDefault(path, def);
      return config.getString(path, config.getString(path));
   }

   private static void babyZombieMovementSpeed() {
      babyZombieMovementSpeed = getDouble("settings.baby-zombie-movement-speed", 0.5);
   }

   private static void interactLimitEnabled() {
      interactLimitEnabled = getBoolean("settings.limit-player-interactions", true);
      if (!interactLimitEnabled) {
         Bukkit.getLogger().log(Level.INFO, "Disabling player interaction limiter, your server may be more vulnerable to malicious users");
      }
   }

   private static void effectModifiers() {
      strengthEffectModifier = getDouble("effect-modifiers.strength", 1.3);
      weaknessEffectModifier = getDouble("effect-modifiers.weakness", -0.5);
   }

   private static void dataValueAllowedItems() {
      dataValueAllowedItems = new HashSet<>(getList("data-value-allowed-items", Collections.emptyList()));
   }

   private static void stackableBuckets() {
      stackableLavaBuckets = getBoolean("stackable-buckets.lava", false);
      stackableWaterBuckets = getBoolean("stackable-buckets.water", false);
      stackableMilkBuckets = getBoolean("stackable-buckets.milk", false);

      try {
         if (stackableLavaBuckets) {
            Material.LAVA_BUCKET.maxStack = Material.BUCKET.getMaxStackSize();
            Items.LAVA_BUCKET.c(Material.BUCKET.getMaxStackSize());
         }

         if (stackableWaterBuckets) {
            Material.WATER_BUCKET.maxStack = Material.BUCKET.getMaxStackSize();
            Items.WATER_BUCKET.c(Material.BUCKET.getMaxStackSize());
         }

         if (stackableMilkBuckets) {
            Material.MILK_BUCKET.maxStack = Material.BUCKET.getMaxStackSize();
            Items.MILK_BUCKET.c(Material.BUCKET.getMaxStackSize());
         }
      } catch (Exception var1) {
         var1.printStackTrace();
      }
   }

   private static void excessiveVelocityWarning() {
      warnForExcessiveVelocity = getBoolean("warnWhenSettingExcessiveVelocity", true);
   }
}
