package org.spigotmc;

import com.google.common.base.Throwables;
import gnu.trove.map.hash.TObjectIntHashMap;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import net.minecraft.server.v1_8_R3.AttributeRanged;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class SpigotConfig {
   private static File CONFIG_FILE;
   private static final String HEADER = "This is the main configuration file for Spigot.\nAs you can see, there's tons to configure. Some options may impact gameplay, so use\nwith caution, and make sure you know what each option does before configuring.\nFor a reference for any variable inside this file, check out the Spigot wiki at\nhttp://www.spigotmc.org/wiki/spigot-configuration/\n\nIf you need help with the configuration or have any questions related to Spigot,\njoin us at the IRC or drop by our forums and leave a post.\n\nIRC: #spigot @ irc.spi.gt ( http://www.spigotmc.org/pages/irc/ )\nForums: http://www.spigotmc.org/\n";
   public static YamlConfiguration config;
   static int version;
   static Map<String, Command> commands;
   public static boolean logCommands;
   public static int tabComplete;
   public static String whitelistMessage;
   public static String unknownCommandMessage;
   public static String serverFullMessage;
   public static String outdatedClientMessage = "Outdated client! Please use {0}";
   public static String outdatedServerMessage = "Outdated server! I'm still on {0}";
   public static int timeoutTime = 60;
   public static boolean restartOnCrash = true;
   public static String restartScript = "./start.sh";
   public static String restartMessage;
   public static boolean bungee;
   public static boolean lateBind;
   public static boolean disableStatSaving;
   public static TObjectIntHashMap<String> forcedStats = new TObjectIntHashMap();
   public static int playerSample;
   public static int playerShuffle;
   public static List<String> spamExclusions;
   public static boolean silentCommandBlocks;
   public static boolean filterCreativeItems;
   public static Set<String> replaceCommands;
   public static int userCacheCap;
   public static boolean saveUserCacheOnStopOnly;
   public static int intCacheLimit;
   public static double movedTooQuicklyThreshold;
   public static double maxHealth = 2048.0;
   public static double movementSpeed = 2048.0;
   public static double attackDamage = 2048.0;

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
            "This is the main configuration file for Spigot.\nAs you can see, there's tons to configure. Some options may impact gameplay, so use\nwith caution, and make sure you know what each option does before configuring.\nFor a reference for any variable inside this file, check out the Spigot wiki at\nhttp://www.spigotmc.org/wiki/spigot-configuration/\n\nIf you need help with the configuration or have any questions related to Spigot,\njoin us at the IRC or drop by our forums and leave a post.\n\nIRC: #spigot @ irc.spi.gt ( http://www.spigotmc.org/pages/irc/ )\nForums: http://www.spigotmc.org/\n"
         );
      config.options().copyDefaults(true);
      commands = new HashMap<>();
      version = getInt("config-version", 8);
      set("config-version", 8);
      readConfig(org.spigotmc.SpigotConfig.class, null);
   }

   public static void registerCommands() {
      for (Entry<String, Command> entry : commands.entrySet()) {
         MinecraftServer.getServer().server.getCommandMap().register(entry.getKey(), "Spigot", entry.getValue());
      }
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

   private static double getDouble(String path, double def) {
      config.addDefault(path, def);
      return config.getDouble(path, config.getDouble(path));
   }

   private static void logCommands() {
      logCommands = getBoolean("commands.log", true);
   }

   private static void tabComplete() {
      if (version < 6) {
         boolean oldValue = getBoolean("commands.tab-complete", true);
         if (oldValue) {
            set("commands.tab-complete", 0);
         } else {
            set("commands.tab-complete", -1);
         }
      }

      tabComplete = getInt("commands.tab-complete", 0);
   }

   private static String transform(String s) {
      return ChatColor.translateAlternateColorCodes('&', s).replaceAll("\\n", "\n");
   }

   private static void messages() {
      if (version < 8) {
         set("messages.outdated-client", outdatedClientMessage);
         set("messages.outdated-server", outdatedServerMessage);
      }

      whitelistMessage = transform(getString("messages.whitelist", "You are not whitelisted on this server!"));
      unknownCommandMessage = transform(getString("messages.unknown-command", "Unknown command. Type \"/help\" for help."));
      serverFullMessage = transform(getString("messages.server-full", "The server is full!"));
      outdatedClientMessage = transform(getString("messages.outdated-client", outdatedClientMessage));
      outdatedServerMessage = transform(getString("messages.outdated-server", outdatedServerMessage));
   }

   private static void watchdog() {
      timeoutTime = getInt("settings.timeout-time", timeoutTime);
      restartOnCrash = getBoolean("settings.restart-on-crash", restartOnCrash);
      restartScript = getString("settings.restart-script", restartScript);
      restartMessage = transform(getString("messages.restart", "Server is restarting"));
      commands.put("restart", new org.spigotmc.RestartCommand("restart"));
      org.spigotmc.WatchdogThread.doStart(timeoutTime, restartOnCrash);
   }

   private static void bungee() {
      if (version < 4) {
         set("settings.bungeecord", false);
         System.out.println("Oudated config, disabling BungeeCord support!");
      }

      bungee = getBoolean("settings.bungeecord", false);
   }

   protected static String timeSummary(int seconds) {
      String time = "";
      if (seconds > 3600) {
         time = time + TimeUnit.SECONDS.toHours(seconds) + "h";
         seconds /= 60;
      }

      if (seconds > 0) {
         time = time + TimeUnit.SECONDS.toMinutes(seconds) + "m";
      }

      return time;
   }

   private static void lateBind() {
      lateBind = getBoolean("settings.late-bind", false);
   }

   private static void stats() {
      disableStatSaving = getBoolean("stats.disable-saving", false);
      if (!config.contains("stats.forced-stats")) {
         config.createSection("stats.forced-stats");
      }

      ConfigurationSection section = config.getConfigurationSection("stats.forced-stats");

      for (String name : section.getKeys(true)) {
         if (section.isInt(name)) {
            forcedStats.put(name, section.getInt(name));
         }
      }

      if (disableStatSaving && section.getInt("achievement.openInventory", 0) < 1) {
         Bukkit.getLogger()
            .warning(
               "*** WARNING *** stats.disable-saving is true but stats.forced-stats.achievement.openInventory isn't set to 1. Disabling stat saving without forcing the achievement may cause it to get stuck on the player's screen."
            );
      }
   }

   private static void playerSample() {
      playerSample = getInt("settings.sample-count", 12);
   }

   private static void playerShuffle() {
      playerShuffle = getInt("settings.player-shuffle", 0);
   }

   private static void spamExclusions() {
      spamExclusions = getList("commands.spam-exclusions", Arrays.asList("/skill"));
   }

   private static void silentCommandBlocks() {
      silentCommandBlocks = getBoolean("commands.silent-commandblock-console", false);
   }

   private static void filterCreativeItems() {
      filterCreativeItems = getBoolean("settings.filter-creative-items", true);
   }

   private static void replaceCommands() {
      if (config.contains("replace-commands")) {
         set("commands.replace-commands", config.getStringList("replace-commands"));
         config.set("replace-commands", null);
      }

      replaceCommands = new HashSet<>(getList("commands.replace-commands", Arrays.asList("setblock", "summon", "testforblock", "tellraw")));
   }

   private static void userCacheCap() {
      userCacheCap = getInt("settings.user-cache-size", 1000);
   }

   private static void saveUserCacheOnStopOnly() {
      saveUserCacheOnStopOnly = getBoolean("settings.save-user-cache-on-stop-only", true);
   }

   private static void intCacheLimit() {
      intCacheLimit = getInt("settings.int-cache-limit", 1024);
   }

   private static void movedTooQuicklyThreshold() {
      movedTooQuicklyThreshold = getDouble("settings.moved-too-quickly-threshold", 100.0);
   }

   private static void attributeMaxes() {
      maxHealth = getDouble("settings.attribute.maxHealth.max", maxHealth);
      ((AttributeRanged)GenericAttributes.maxHealth).b = maxHealth;
      movementSpeed = getDouble("settings.attribute.movementSpeed.max", movementSpeed);
      ((AttributeRanged)GenericAttributes.MOVEMENT_SPEED).b = movementSpeed;
      attackDamage = getDouble("settings.attribute.attackDamage.max", attackDamage);
      ((AttributeRanged)GenericAttributes.ATTACK_DAMAGE).b = attackDamage;
   }
}
