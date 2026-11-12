package org.bukkit.craftbukkit.v1_8_R3;

import com.avaje.ebean.config.DataSourceConfig;
import com.avaje.ebean.config.ServerConfig;
import com.avaje.ebean.config.dbplatform.SQLitePlatform;
import com.avaje.ebeaninternal.server.lib.sql.TransactionIsolation;
import com.eatthepath.uuid.FastUUID;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import lol.deyo.blaze.configs.BlazeConfig;
import lol.deyo.blaze.utils.JavaUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import net.md_5.bungee.api.chat.BaseComponent;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.CommandAbstract;
import net.minecraft.server.v1_8_R3.CommandDispatcher;
import net.minecraft.server.v1_8_R3.Convertable;
import net.minecraft.server.v1_8_R3.CraftingManager;
import net.minecraft.server.v1_8_R3.DedicatedPlayerList;
import net.minecraft.server.v1_8_R3.DedicatedServer;
import net.minecraft.server.v1_8_R3.Enchantment;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.EntityTracker;
import net.minecraft.server.v1_8_R3.EnumDifficulty;
import net.minecraft.server.v1_8_R3.ExceptionWorldConflict;
import net.minecraft.server.v1_8_R3.ICommand;
import net.minecraft.server.v1_8_R3.ICommandListener;
import net.minecraft.server.v1_8_R3.IDataManager;
import net.minecraft.server.v1_8_R3.IProgressUpdate;
import net.minecraft.server.v1_8_R3.Items;
import net.minecraft.server.v1_8_R3.JsonListEntry;
import net.minecraft.server.v1_8_R3.LocaleI18n;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.MobEffectList;
import net.minecraft.server.v1_8_R3.PersistentCollection;
import net.minecraft.server.v1_8_R3.PlayerList;
import net.minecraft.server.v1_8_R3.PropertyManager;
import net.minecraft.server.v1_8_R3.RecipesFurnace;
import net.minecraft.server.v1_8_R3.RegionFile;
import net.minecraft.server.v1_8_R3.RegionFileCache;
import net.minecraft.server.v1_8_R3.ServerCommand;
import net.minecraft.server.v1_8_R3.ServerNBTManager;
import net.minecraft.server.v1_8_R3.WorldData;
import net.minecraft.server.v1_8_R3.WorldLoaderServer;
import net.minecraft.server.v1_8_R3.WorldManager;
import net.minecraft.server.v1_8_R3.WorldMap;
import net.minecraft.server.v1_8_R3.WorldNBTStorage;
import net.minecraft.server.v1_8_R3.WorldServer;
import net.minecraft.server.v1_8_R3.WorldSettings;
import net.minecraft.server.v1_8_R3.WorldSettings.EnumGamemode;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.conversations.Conversable;
import org.bukkit.craftbukkit.Main;
import org.bukkit.craftbukkit.v1_8_R3.command.VanillaCommandWrapper;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.generator.CraftChunkData;
import org.bukkit.craftbukkit.v1_8_R3.help.SimpleHelpMap;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftFurnaceRecipe;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryCustom;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemFactory;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftRecipe;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftShapedRecipe;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftShapelessRecipe;
import org.bukkit.craftbukkit.v1_8_R3.inventory.RecipeIterator;
import org.bukkit.craftbukkit.v1_8_R3.map.CraftMapView;
import org.bukkit.craftbukkit.v1_8_R3.metadata.EntityMetadataStore;
import org.bukkit.craftbukkit.v1_8_R3.metadata.PlayerMetadataStore;
import org.bukkit.craftbukkit.v1_8_R3.metadata.WorldMetadataStore;
import org.bukkit.craftbukkit.v1_8_R3.potion.CraftPotionBrewer;
import org.bukkit.craftbukkit.v1_8_R3.scheduler.CraftScheduler;
import org.bukkit.craftbukkit.v1_8_R3.scoreboard.CraftScoreboardManager;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftIconCache;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
import org.bukkit.craftbukkit.v1_8_R3.util.DatFileFilter;
import org.bukkit.craftbukkit.v1_8_R3.util.Versioning;
import org.bukkit.craftbukkit.v1_8_R3.util.Waitable;
import org.bukkit.craftbukkit.v1_8_R3.util.permissions.CraftDefaultPermissions;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.help.HelpMap;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLoadOrder;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.SimpleServicesManager;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.StandardMessenger;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitWorker;
import org.bukkit.util.StringUtil;
import org.bukkit.util.permissions.DefaultPermissions;
import org.github.paperspigot.PaperSpigotConfig;
import org.spigotmc.RestartCommand;
import org.spigotmc.SpigotConfig;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.error.MarkedYAMLException;

public final class CraftServer implements org.bukkit.Server {
   private static final Player[] EMPTY_PLAYER_ARRAY = new Player[0];
   final MinecraftServer console;
   private final DedicatedPlayerList playerList;
   private final String serverName = (String)BlazeConfig.serverBranding.getValue();
   private final String serverVersion;
   private final String bukkitVersion = Versioning.getBukkitVersion();
   private final Logger logger = Logger.getLogger("Minecraft");
   private final ServicesManager servicesManager = new SimpleServicesManager();
   private final CraftScheduler scheduler = new CraftScheduler();
   private final SimpleCommandMap commandMap = new SimpleCommandMap(this);
   private final SimpleHelpMap helpMap = new SimpleHelpMap(this);
   private final StandardMessenger messenger = new StandardMessenger();
   private final PluginManager pluginManager = new SimplePluginManager(this, this.commandMap);
   private final Map<String, org.bukkit.World> worlds = new LinkedHashMap<>();
   private final Yaml yaml = new Yaml(new SafeConstructor());
   private final Cache<UUID, org.bukkit.OfflinePlayer> offlinePlayers = CacheBuilder.newBuilder().weakValues().build();
   private final EntityMetadataStore entityMetadata = new EntityMetadataStore();
   private final PlayerMetadataStore playerMetadata = new PlayerMetadataStore();
   private final WorldMetadataStore worldMetadata = new WorldMetadataStore();
   private final CraftServer.BooleanWrapper online = new CraftServer.BooleanWrapper();
   private final List<CraftPlayer> playerView;
   public int chunkGCPeriod;
   public int chunkGCLoadThresh;
   public CraftScoreboardManager scoreboardManager;
   public boolean playerCommandState;
   public int reloadCount;
   private YamlConfiguration configuration;
   private final org.bukkit.Server.Spigot spigot = new org.bukkit.Server.Spigot() {
      @Override
      public double[] getTPS() {
         return new double[]{
            MinecraftServer.getServer().tps1.getAverage(), MinecraftServer.getServer().tps5.getAverage(), MinecraftServer.getServer().tps15.getAverage()
         };
      }

      @Deprecated
      @Override
      public YamlConfiguration getConfig() {
         return this.getBukkitConfig();
      }

      @Override
      public YamlConfiguration getBukkitConfig() {
         return CraftServer.this.configuration;
      }

      @Override
      public YamlConfiguration getSpigotConfig() {
         return SpigotConfig.config;
      }

      @Override
      public YamlConfiguration getPaperSpigotConfig() {
         return PaperSpigotConfig.config;
      }

      @Override
      public void restart() {
         RestartCommand.restart();
      }

      @Override
      public void broadcast(BaseComponent component) {
         for (Player player : CraftServer.this.getOnlinePlayers()) {
            player.spigot().sendMessage(component);
         }
      }

      @Override
      public void broadcast(BaseComponent... components) {
         for (Player player : CraftServer.this.getOnlinePlayers()) {
            player.spigot().sendMessage(components);
         }
      }
   };
   private YamlConfiguration commandsConfiguration;
   private int monsterSpawn;
   private int animalSpawn;
   private int waterAnimalSpawn;
   private int ambientSpawn;
   private File container;
   private org.bukkit.Warning.WarningState warningState;
   private boolean printSaveWarning;
   private CraftIconCache icon;
   private boolean overrideAllCommandBlockCommands;

   static {
      ConfigurationSerialization.registerClass(CraftOfflinePlayer.class);
      CraftItemFactory.instance();
   }

   public CraftServer(MinecraftServer console, PlayerList playerList) {
      this.console = console;
      this.playerList = (DedicatedPlayerList)playerList;
      this.playerView = Collections.unmodifiableList(Lists.transform(playerList.players, EntityPlayer::getBukkitEntity));
      this.serverVersion = CraftServer.class.getPackage().getImplementationVersion();
      this.online.value = console.getPropertyManager().getBoolean("online-mode", true);
      org.bukkit.Bukkit.setServer(this);
      Enchantment.DAMAGE_ALL.getClass();
      org.bukkit.enchantments.Enchantment.stopAcceptingRegistrations();
      Potion.setPotionBrewer(new CraftPotionBrewer());
      MobEffectList.BLINDNESS.getClass();
      PotionEffectType.stopAcceptingRegistrations();
      if (!Main.useConsole) {
         this.getLogger().info("Console input is disabled due to --noconsole command argument");
      }

      this.configuration = YamlConfiguration.loadConfiguration(this.getConfigFile());
      this.configuration.options().copyDefaults(true);
      this.configuration
         .setDefaults(
            YamlConfiguration.loadConfiguration(
               new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("configurations/bukkit.yml"), Charsets.UTF_8)
            )
         );
      ConfigurationSection legacyAlias = null;
      if (!this.configuration.isString("aliases")) {
         legacyAlias = this.configuration.getConfigurationSection("aliases");
         this.configuration.set("aliases", "now-in-commands.yml");
      }

      this.saveConfig();
      if (this.getCommandsConfigFile().isFile()) {
         legacyAlias = null;
      }

      this.commandsConfiguration = YamlConfiguration.loadConfiguration(this.getCommandsConfigFile());
      this.commandsConfiguration.options().copyDefaults(true);
      this.commandsConfiguration
         .setDefaults(
            YamlConfiguration.loadConfiguration(
               new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("configurations/commands.yml"), Charsets.UTF_8)
            )
         );
      this.saveCommandsConfig();
      if (legacyAlias != null) {
         ConfigurationSection aliases = this.commandsConfiguration.createSection("aliases");

         for (String key : legacyAlias.getKeys(false)) {
            ArrayList<String> commands = new ArrayList<>();
            if (legacyAlias.isList(key)) {
               for (String command : legacyAlias.getStringList(key)) {
                  commands.add(command + " $1-");
               }
            } else {
               commands.add(legacyAlias.getString(key) + " $1-");
            }

            aliases.set(key, commands);
         }
      }

      this.saveCommandsConfig();
      this.overrideAllCommandBlockCommands = this.commandsConfiguration.getStringList("command-block-overrides").contains("*");
      this.monsterSpawn = this.configuration.getInt("spawn-limits.monsters");
      this.animalSpawn = this.configuration.getInt("spawn-limits.animals");
      this.waterAnimalSpawn = this.configuration.getInt("spawn-limits.water-animals");
      this.ambientSpawn = this.configuration.getInt("spawn-limits.ambient");
      console.autosavePeriod = this.configuration.getInt("ticks-per.autosave");
      this.warningState = org.bukkit.Warning.WarningState.value(this.configuration.getString("settings.deprecated-verbose"));
      this.chunkGCPeriod = this.configuration.getInt("chunk-gc.period-in-ticks");
      this.chunkGCLoadThresh = this.configuration.getInt("chunk-gc.load-threshold");
      this.loadIcon();
   }

   static CraftIconCache loadServerIcon0(File file) throws Exception {
      return loadServerIcon0(ImageIO.read(file));
   }

   static CraftIconCache loadServerIcon0(BufferedImage image) throws Exception {
      ByteBuf bytebuf = Unpooled.buffer();
      Validate.isTrue(image.getWidth() == 64, "Must be 64 pixels wide");
      Validate.isTrue(image.getHeight() == 64, "Must be 64 pixels high");
      ImageIO.write(image, "PNG", new ByteBufOutputStream(bytebuf));
      ByteBuf bytebuf1 = Base64.encode(bytebuf);
      String str = bytebuf1.toString(Charsets.UTF_8);
      bytebuf1.release();
      return new CraftIconCache("data:image/png;base64," + str);
   }

   public boolean getCommandBlockOverride(String command) {
      return this.overrideAllCommandBlockCommands || this.commandsConfiguration.getStringList("command-block-overrides").contains(command);
   }

   private File getConfigFile() {
      return (File)this.console.options.valueOf("bukkit-settings");
   }

   private File getCommandsConfigFile() {
      return (File)this.console.options.valueOf("commands-settings");
   }

   @Override
   public int setMaxPlayers(int maxPlayers) {
      this.playerList.maxPlayers = maxPlayers;
      return maxPlayers;
   }

   private void saveConfig() {
      try {
         this.configuration.save(this.getConfigFile());
      } catch (IOException var2) {
         Logger.getLogger(CraftServer.class.getName()).log(Level.SEVERE, "Could not save " + this.getConfigFile(), (Throwable)var2);
      }
   }

   private void saveCommandsConfig() {
      try {
         this.commandsConfiguration.save(this.getCommandsConfigFile());
      } catch (IOException var2) {
         Logger.getLogger(CraftServer.class.getName()).log(Level.SEVERE, "Could not save " + this.getCommandsConfigFile(), (Throwable)var2);
      }
   }

   public void loadPlugins() {
      this.pluginManager.registerInterface(JavaPluginLoader.class);
      File pluginFolder = (File)this.console.options.valueOf("plugins");
      if (!pluginFolder.exists()) {
         pluginFolder.mkdir();
      }

      this.installBundledSpark(pluginFolder);
      Plugin[] plugins = this.pluginManager.loadPlugins(pluginFolder);

      for (Plugin plugin : plugins) {
         JavaUtils.checkMalware(plugin);

         try {
            String message = String.format("Loading %s", plugin.getDescription().getFullName());
            plugin.getLogger().info(message);
            plugin.onLoad();
         } catch (Throwable var8) {
            Logger.getLogger(CraftServer.class.getName())
               .log(Level.SEVERE, var8.getMessage() + " initializing " + plugin.getDescription().getFullName() + " (Is it up to date?)", var8);
         }
      }
   }

   private void installBundledSpark(File pluginFolder) {
      File[] existing = pluginFolder.listFiles();
      if (existing != null) {
         for (File file : existing) {
            String name = file.getName().toLowerCase();
            if (file.isFile() && name.endsWith(".jar") && name.contains("spark")) {
               return;
            }
         }
      }

      File target = new File(pluginFolder, "Spark.jar");
      try (InputStream input = CraftServer.class.getResourceAsStream("/builtin/spark.jar")) {
         if (input != null) {
            java.nio.file.Files.copy(input, target.toPath());
         }
      } catch (IOException ex) {
         this.logger.log(Level.WARNING, "Could not install bundled Spark profiler", ex);
      }
   }

   public void enablePlugins(PluginLoadOrder type) {
      if (type == PluginLoadOrder.STARTUP) {
         this.helpMap.clear();
         this.helpMap.initializeGeneralTopics();
      }

      Plugin[] plugins = this.pluginManager.getPlugins();

      for (Plugin plugin : plugins) {
         if (!plugin.isEnabled() && plugin.getDescription().getLoad() == type) {
            this.loadPlugin(plugin);
         }
      }

      if (type == PluginLoadOrder.POSTWORLD) {
         this.setVanillaCommands(true);
         this.commandMap.setFallbackCommands();
         this.setVanillaCommands(false);
         this.commandMap.registerServerAliases();
         this.loadCustomPermissions();
         DefaultPermissions.registerCorePermissions();
         CraftDefaultPermissions.registerCorePermissions();
         this.helpMap.initializeCommands();
      }
   }

   public void disablePlugins() {
      this.pluginManager.disablePlugins();
   }

   private void setVanillaCommands(boolean first) {
      Map<String, ICommand> commands = new CommandDispatcher().getCommands();

      for (ICommand cmd : commands.values()) {
         VanillaCommandWrapper wrapper = new VanillaCommandWrapper((CommandAbstract)cmd, LocaleI18n.get(cmd.getUsage(null)));
         if (SpigotConfig.replaceCommands.contains(wrapper.getName())) {
            if (first) {
               this.commandMap.register("minecraft", wrapper);
            }
         } else if (!first) {
            this.commandMap.register("minecraft", wrapper);
         }
      }
   }

   private void loadPlugin(Plugin plugin) {
      try {
         this.pluginManager.enablePlugin(plugin);

         for (Permission perm : plugin.getDescription().getPermissions()) {
            try {
               this.pluginManager.addPermission(perm);
            } catch (IllegalArgumentException var6) {
               this.getLogger()
                  .log(
                     Level.WARNING,
                     "Plugin " + plugin.getDescription().getFullName() + " tried to register permission '" + perm.getName() + "' but it's already registered",
                     (Throwable)var6
                  );
            }
         }
      } catch (Throwable var7) {
         Logger.getLogger(CraftServer.class.getName())
            .log(Level.SEVERE, var7.getMessage() + " loading " + plugin.getDescription().getFullName() + " (Is it up to date?)", var7);
      }
   }

   @Override
   public String getName() {
      return this.serverName;
   }

   @Override
   public String getVersion() {
      return this.serverVersion + " (MC: " + this.console.getVersion() + ")";
   }

   @Override
   public String getBukkitVersion() {
      return this.bukkitVersion;
   }

   @Deprecated
   @Override
   public Player[] _INVALID_getOnlinePlayers() {
      return this.getOnlinePlayers().toArray(EMPTY_PLAYER_ARRAY);
   }

   public List<CraftPlayer> getOnlinePlayers() {
      return this.playerView;
   }

   @Deprecated
   @Override
   public Player getPlayer(String name) {
      Validate.notNull(name, "Name cannot be null");
      Player found = this.getPlayerExact(name);
      if (found != null) {
         return found;
      } else {
         String lowerName = name.toLowerCase();
         int delta = Integer.MAX_VALUE;

         for (Player player : this.getOnlinePlayers()) {
            if (player.getName().toLowerCase().startsWith(lowerName)) {
               int curDelta = Math.abs(player.getName().length() - lowerName.length());
               if (curDelta < delta) {
                  found = player;
                  delta = curDelta;
               }

               if (curDelta == 0) {
                  break;
               }
            }
         }

         return found;
      }
   }

   @Deprecated
   @Override
   public Player getPlayerExact(String name) {
      Validate.notNull(name, "Name cannot be null");
      EntityPlayer player = this.playerList.getPlayer(name);
      return player != null ? player.getBukkitEntity() : null;
   }

   @Override
   public Player getPlayer(UUID id) {
      EntityPlayer player = this.playerList.a(id);
      return player != null ? player.getBukkitEntity() : null;
   }

   @Override
   public int broadcastMessage(String message) {
      return this.broadcast(message, "bukkit.broadcast.user");
   }

   public Player getPlayer(EntityPlayer entity) {
      return entity.getBukkitEntity();
   }

   @Deprecated
   @Override
   public List<Player> matchPlayer(String partialName) {
      Validate.notNull(partialName, "PartialName cannot be null");
      List<Player> matchedPlayers = new ArrayList<>();

      for (Player iterPlayer : this.getOnlinePlayers()) {
         String iterPlayerName = iterPlayer.getName();
         if (partialName.equalsIgnoreCase(iterPlayerName)) {
            matchedPlayers.clear();
            matchedPlayers.add(iterPlayer);
            break;
         }

         if (iterPlayerName.toLowerCase().contains(partialName.toLowerCase())) {
            matchedPlayers.add(iterPlayer);
         }
      }

      return matchedPlayers;
   }

   @Override
   public int getMaxPlayers() {
      return this.playerList.getMaxPlayers();
   }

   @Override
   public int getPort() {
      return this.getConfigInt("server-port", 25565);
   }

   @Override
   public int getViewDistance() {
      return this.getConfigInt("view-distance", 5);
   }

   @Override
   public String getIp() {
      return this.getConfigString("server-ip", "");
   }

   @Override
   public String getServerName() {
      return this.getConfigString("server-name", "Unknown Server");
   }

   @Override
   public String getServerId() {
      return this.getConfigString("server-id", "unnamed");
   }

   @Override
   public String getWorldType() {
      return this.getConfigString("level-type", "DEFAULT");
   }

   @Override
   public boolean getGenerateStructures() {
      return this.getConfigBoolean("generate-structures", true);
   }

   @Override
   public boolean getAllowEnd() {
      return this.configuration.getBoolean("settings.allow-end");
   }

   @Override
   public boolean getAllowNether() {
      return this.getConfigBoolean("allow-nether", true);
   }

   public boolean getWarnOnOverload() {
      return this.configuration.getBoolean("settings.warn-on-overload");
   }

   public boolean getQueryPlugins() {
      return this.configuration.getBoolean("settings.query-plugins");
   }

   @Override
   public boolean hasWhitelist() {
      return this.getConfigBoolean("white-list", false);
   }

   private String getConfigString(String variable, String defaultValue) {
      return this.console.getPropertyManager().getString(variable, defaultValue);
   }

   private int getConfigInt(String variable, int defaultValue) {
      return this.console.getPropertyManager().getInt(variable, defaultValue);
   }

   private boolean getConfigBoolean(String variable, boolean defaultValue) {
      return this.console.getPropertyManager().getBoolean(variable, defaultValue);
   }

   @Override
   public String getUpdateFolder() {
      return this.configuration.getString("settings.update-folder", "update");
   }

   @Override
   public File getUpdateFolderFile() {
      return new File((File)this.console.options.valueOf("plugins"), this.configuration.getString("settings.update-folder", "update"));
   }

   @Override
   public long getConnectionThrottle() {
      return SpigotConfig.bungee ? -1L : this.configuration.getInt("settings.connection-throttle");
   }

   @Override
   public int getTicksPerAnimalSpawns() {
      return this.configuration.getInt("ticks-per.animal-spawns");
   }

   @Override
   public int getTicksPerMonsterSpawns() {
      return this.configuration.getInt("ticks-per.monster-spawns");
   }

   @Override
   public PluginManager getPluginManager() {
      return this.pluginManager;
   }

   public CraftScheduler getScheduler() {
      return this.scheduler;
   }

   @Override
   public ServicesManager getServicesManager() {
      return this.servicesManager;
   }

   @Override
   public List<org.bukkit.World> getWorlds() {
      return new ArrayList<>(this.worlds.values());
   }

   public DedicatedPlayerList getHandle() {
      return this.playerList;
   }

   public boolean dispatchServerCommand(CommandSender sender, ServerCommand serverCommand) {
      if (sender instanceof Conversable) {
         Conversable conversable = (Conversable)sender;
         if (conversable.isConversing()) {
            conversable.acceptConversationInput(serverCommand.command);
            return true;
         }
      }

      try {
         this.playerCommandState = true;
         return this.dispatchCommand(sender, serverCommand.command);
      } catch (Exception var8) {
         this.getLogger().log(Level.WARNING, "Unexpected exception while parsing console command \"" + serverCommand.command + '"', (Throwable)var8);
      } finally {
         this.playerCommandState = false;
      }

      return false;
   }

   @Override
   public boolean dispatchCommand(final CommandSender sender, final String commandLine) {
      Validate.notNull(sender, "Sender cannot be null");
      Validate.notNull(commandLine, "CommandLine cannot be null");
      if (!org.bukkit.Bukkit.isPrimaryThread()) {
         org.bukkit.Bukkit.getLogger().log(Level.SEVERE, "Command Dispatched Async: " + commandLine);
         org.bukkit.Bukkit.getLogger()
            .log(Level.SEVERE, "Please notify author of plugin causing this execution to fix this bug! see: http://bit.ly/1oSiM6C", new Throwable());
         Waitable<Boolean> wait = new Waitable<Boolean>() {
            protected Boolean evaluate() {
               return CraftServer.this.dispatchCommand(sender, commandLine);
            }
         };
         MinecraftServer.getServer().postToMainThread(wait);

         try {
            return wait.get();
         } catch (InterruptedException var7) {
            Thread.currentThread().interrupt();
         } catch (Exception var8) {
            throw new RuntimeException("Exception processing dispatch command", var8.getCause());
         }
      }

      if (this.commandMap.dispatch(sender, commandLine)) {
         return true;
      } else {
         sender.sendMessage(SpigotConfig.unknownCommandMessage);
         return false;
      }
   }

   @Override
   public void reload() {
      this.reloadCount++;
      this.configuration = YamlConfiguration.loadConfiguration(this.getConfigFile());
      this.commandsConfiguration = YamlConfiguration.loadConfiguration(this.getCommandsConfigFile());
      PropertyManager config = new PropertyManager(this.console.options);
      ((DedicatedServer)this.console).propertyManager = config;
      boolean animals = config.getBoolean("spawn-animals", this.console.getSpawnAnimals());
      boolean monsters = config.getBoolean("spawn-monsters", ((WorldServer)this.console.worlds.get(0)).getDifficulty() != EnumDifficulty.PEACEFUL);
      EnumDifficulty difficulty = EnumDifficulty.getById(config.getInt("difficulty", ((WorldServer)this.console.worlds.get(0)).getDifficulty().ordinal()));
      this.online.value = config.getBoolean("online-mode", this.console.getOnlineMode());
      this.console.setSpawnAnimals(config.getBoolean("spawn-animals", this.console.getSpawnAnimals()));
      this.console.setPVP(config.getBoolean("pvp", this.console.getPVP()));
      this.console.setAllowFlight(config.getBoolean("allow-flight", this.console.getAllowFlight()));
      this.console.setMotd(config.getString("motd", this.console.getMotd()));
      this.monsterSpawn = this.configuration.getInt("spawn-limits.monsters");
      this.animalSpawn = this.configuration.getInt("spawn-limits.animals");
      this.waterAnimalSpawn = this.configuration.getInt("spawn-limits.water-animals");
      this.ambientSpawn = this.configuration.getInt("spawn-limits.ambient");
      this.warningState = org.bukkit.Warning.WarningState.value(this.configuration.getString("settings.deprecated-verbose"));
      this.printSaveWarning = false;
      this.console.autosavePeriod = this.configuration.getInt("ticks-per.autosave");
      this.chunkGCPeriod = this.configuration.getInt("chunk-gc.period-in-ticks");
      this.chunkGCLoadThresh = this.configuration.getInt("chunk-gc.load-threshold");
      this.loadIcon();

      try {
         this.playerList.getIPBans().load();
      } catch (IOException var13) {
         this.logger.log(Level.WARNING, "Failed to load banned-ips.json, " + var13.getMessage());
      }

      try {
         this.playerList.getProfileBans().load();
      } catch (IOException var12) {
         this.logger.log(Level.WARNING, "Failed to load banned-players.json, " + var12.getMessage());
      }

      SpigotConfig.init((File)this.console.options.valueOf("spigot-settings"));
      PaperSpigotConfig.init((File)this.console.options.valueOf("spigot-settings"));

      for (WorldServer world : this.console.worlds) {
         world.worldData.setDifficulty(difficulty);
         world.setSpawnFlags(monsters, animals);
         if (this.getTicksPerAnimalSpawns() < 0) {
            world.ticksPerAnimalSpawns = 400L;
         } else {
            world.ticksPerAnimalSpawns = this.getTicksPerAnimalSpawns();
         }

         if (this.getTicksPerMonsterSpawns() < 0) {
            world.ticksPerMonsterSpawns = 200L;
         } else {
            world.ticksPerMonsterSpawns = this.getTicksPerMonsterSpawns();
         }

         world.spigotConfig.init();
         world.paperSpigotConfig.init();
      }

      this.pluginManager.clearPlugins();
      this.commandMap.clearCommands();
      this.resetRecipes();
      SpigotConfig.registerCommands();
      this.overrideAllCommandBlockCommands = this.commandsConfiguration.getStringList("command-block-overrides").contains("*");

      for (int pollCount = 0; pollCount < 50 && !this.getScheduler().getActiveWorkers().isEmpty(); pollCount++) {
         try {
            Thread.sleep(50L);
         } catch (InterruptedException var11) {
         }
      }

      for (BukkitWorker worker : this.getScheduler().getActiveWorkers()) {
         Plugin plugin = worker.getOwner();
         String author = "<NoAuthorGiven>";
         if (plugin.getDescription().getAuthors().size() > 0) {
            author = plugin.getDescription().getAuthors().get(0);
         }

         this.getLogger()
            .log(
               Level.SEVERE,
               String.format(
                  "Nag author: '%s' of '%s' about the following: %s",
                  author,
                  plugin.getDescription().getName(),
                  "This plugin is not properly shutting down its async tasks when it is being reloaded.  This may cause conflicts with the newly loaded version of the plugin"
               )
            );
      }

      this.loadPlugins();
      this.enablePlugins(PluginLoadOrder.STARTUP);
      this.enablePlugins(PluginLoadOrder.POSTWORLD);
   }

   @Override
   public void reloadConfigs() {
      this.configuration = YamlConfiguration.loadConfiguration(this.getConfigFile());
      this.commandsConfiguration = YamlConfiguration.loadConfiguration(this.getCommandsConfigFile());
      PropertyManager config = new PropertyManager(this.console.options);
      ((DedicatedServer)this.console).propertyManager = config;
      boolean animals = config.getBoolean("spawn-animals", this.console.getSpawnAnimals());
      boolean monsters = config.getBoolean("spawn-monsters", ((WorldServer)this.console.worlds.get(0)).getDifficulty() != EnumDifficulty.PEACEFUL);
      EnumDifficulty difficulty = EnumDifficulty.getById(config.getInt("difficulty", ((WorldServer)this.console.worlds.get(0)).getDifficulty().ordinal()));
      this.online.value = config.getBoolean("online-mode", this.console.getOnlineMode());
      this.console.setSpawnAnimals(config.getBoolean("spawn-animals", this.console.getSpawnAnimals()));
      this.console.setPVP(config.getBoolean("pvp", this.console.getPVP()));
      this.console.setAllowFlight(config.getBoolean("allow-flight", this.console.getAllowFlight()));
      this.console.setMotd(config.getString("motd", this.console.getMotd()));
      this.monsterSpawn = this.configuration.getInt("spawn-limits.monsters");
      this.animalSpawn = this.configuration.getInt("spawn-limits.animals");
      this.waterAnimalSpawn = this.configuration.getInt("spawn-limits.water-animals");
      this.ambientSpawn = this.configuration.getInt("spawn-limits.ambient");
      this.warningState = org.bukkit.Warning.WarningState.value(this.configuration.getString("settings.deprecated-verbose"));
      this.printSaveWarning = false;
      this.console.autosavePeriod = this.configuration.getInt("ticks-per.autosave");
      this.chunkGCPeriod = this.configuration.getInt("chunk-gc.period-in-ticks");
      this.chunkGCLoadThresh = this.configuration.getInt("chunk-gc.load-threshold");
      this.loadIcon();

      try {
         this.playerList.getIPBans().load();
      } catch (IOException var8) {
         this.logger.log(Level.WARNING, "Failed to load banned-ips.json, " + var8.getMessage());
      }

      try {
         this.playerList.getProfileBans().load();
      } catch (IOException var7) {
         this.logger.log(Level.WARNING, "Failed to load banned-players.json, " + var7.getMessage());
      }

      SpigotConfig.init((File)this.console.options.valueOf("spigot-settings"));
      PaperSpigotConfig.init((File)this.console.options.valueOf("paper-settings"));

      for (WorldServer world : this.console.worlds) {
         world.worldData.setDifficulty(difficulty);
         world.setSpawnFlags(monsters, animals);
         if (this.getTicksPerAnimalSpawns() < 0) {
            world.ticksPerAnimalSpawns = 400L;
         } else {
            world.ticksPerAnimalSpawns = this.getTicksPerAnimalSpawns();
         }

         if (this.getTicksPerMonsterSpawns() < 0) {
            world.ticksPerMonsterSpawns = 200L;
         } else {
            world.ticksPerMonsterSpawns = this.getTicksPerMonsterSpawns();
         }

         world.spigotConfig.init();
         world.paperSpigotConfig.init();
      }
   }

   private void loadIcon() {
      this.icon = new CraftIconCache(null);

      try {
         File file = new File(new File("."), "server-icon.png");
         if (file.isFile()) {
            this.icon = loadServerIcon0(file);
         }
      } catch (Exception var2) {
         this.getLogger().log(Level.WARNING, "Couldn't load server icon", (Throwable)var2);
      }
   }

   private void loadCustomPermissions() {
      File file = new File(this.configuration.getString("settings.permissions-file"));

      FileInputStream stream;
      try {
         stream = new FileInputStream(file);
      } catch (FileNotFoundException var28) {
         try { file.createNewFile(); } catch (IOException ignored) {}

         return;
      }

      Map<String, Map<String, Object>> perms;
      label153: {
         try {
            perms = (Map<String, Map<String, Object>>)this.yaml.load(stream);
            break label153;
         } catch (MarkedYAMLException var29) {
            this.getLogger().log(Level.WARNING, "Server permissions file " + file + " is not valid YAML: " + var29);
            return;
         } catch (Throwable var30) {
            this.getLogger().log(Level.WARNING, "Server permissions file " + file + " is not valid YAML.", var30);
         } finally {
            try {
               stream.close();
            } catch (IOException var25) {
            }
         }

         return;
      }

      if (perms != null) {
         for (Permission perm : Permission.loadPermissions(perms, "Permission node '%s' in " + file + " is invalid", Permission.DEFAULT_PERMISSION)) {
            try {
               this.pluginManager.addPermission(perm);
            } catch (IllegalArgumentException var27) {
               this.getLogger().log(Level.SEVERE, "Permission in " + file + " was already defined", (Throwable)var27);
            }
         }
      }
   }

   @Override
   public String toString() {
      return "CraftServer{serverName=" + this.serverName + ",serverVersion=" + this.serverVersion + ",minecraftVersion=" + this.console.getVersion() + '}';
   }

   public org.bukkit.World createWorld(String name, org.bukkit.World.Environment environment) {
      return org.bukkit.WorldCreator.name(name).environment(environment).createWorld();
   }

   public org.bukkit.World createWorld(String name, org.bukkit.World.Environment environment, long seed) {
      return org.bukkit.WorldCreator.name(name).environment(environment).seed(seed).createWorld();
   }

   public org.bukkit.World createWorld(String name, org.bukkit.World.Environment environment, ChunkGenerator generator) {
      return org.bukkit.WorldCreator.name(name).environment(environment).generator(generator).createWorld();
   }

   public org.bukkit.World createWorld(String name, org.bukkit.World.Environment environment, long seed, ChunkGenerator generator) {
      return org.bukkit.WorldCreator.name(name).environment(environment).seed(seed).generator(generator).createWorld();
   }

   @Override
   public org.bukkit.World createWorld(org.bukkit.WorldCreator creator) {
      Validate.notNull(creator, "Creator may not be null");
      String name = creator.name();
      ChunkGenerator generator = creator.generator();
      File folder = new File(this.getWorldContainer(), name);
      org.bukkit.World world = this.getWorld(name);
      net.minecraft.server.v1_8_R3.WorldType type = net.minecraft.server.v1_8_R3.WorldType.getType(creator.type().getName());
      boolean generateStructures = creator.generateStructures();
      if (world != null) {
         return world;
      } else if (folder.exists() && !folder.isDirectory()) {
         throw new IllegalArgumentException("File exists with the name '" + name + "' and isn't a folder");
      } else {
         if (generator == null) {
            generator = this.getGenerator(name);
         }

         Convertable converter = new WorldLoaderServer(this.getWorldContainer());
         if (converter.isConvertable(name)) {
            this.getLogger().info("Converting world '" + name + "'");
            converter.convert(name, new IProgressUpdate() {
               private long b = System.currentTimeMillis();

               public void a(String s) {
               }

               public void a(int i) {
                  if (System.currentTimeMillis() - this.b >= 1000L) {
                     this.b = System.currentTimeMillis();
                     MinecraftServer.LOGGER.info("Converting... " + i + "%");
                  }
               }

               public void c(String s) {
               }
            });
         }

         int dimension = 10 + this.console.worlds.size();
         boolean used = false;

         do {
            for (WorldServer server : this.console.worlds) {
               used = server.dimension == dimension;
               if (used) {
                  dimension++;
                  break;
               }
            }
         } while (used);

         IDataManager sdm = new ServerNBTManager(this.getWorldContainer(), name, true);
         WorldData worlddata = sdm.getWorldData();
         if (worlddata == null) {
            WorldSettings worldSettings = new WorldSettings(
               creator.seed(), EnumGamemode.getById(this.getDefaultGameMode().getValue()), generateStructures, false, type
            );
            worldSettings.setGeneratorSettings(creator.generatorSettings());
            worlddata = new WorldData(worldSettings, name);
         }

         worlddata.checkName(name);
         WorldServer internal = (WorldServer)new WorldServer(
               this.console, sdm, worlddata, dimension, this.getServer().methodProfiler, creator.environment(), generator
            )
            .b();
         if (!this.worlds.containsKey(name.toLowerCase())) {
            return null;
         } else {
            internal.scoreboard = this.getScoreboardManager().getMainScoreboard().getHandle();
            internal.tracker = new EntityTracker(internal);
            internal.addIWorldAccess(new WorldManager(this.console, internal));
            internal.worldData.setDifficulty(EnumDifficulty.EASY);
            internal.setSpawnFlags(true, true);
            this.console.worlds.add(internal);
            if (generator != null) {
               internal.getWorld().getPopulators().addAll(generator.getDefaultPopulators(internal.getWorld()));
            }

            this.pluginManager.callEvent(new WorldInitEvent(internal.getWorld()));
            if (internal.getWorld().getKeepSpawnInMemory()) {
               short short1 = 196;
               long i = System.currentTimeMillis();

               for (int j = -short1; j <= short1; j += 16) {
                  for (int k = -short1; k <= short1; k += 16) {
                     long l = System.currentTimeMillis();
                     if (l < i) {
                        i = l;
                     }

                     if (l > i + 1000L) {
                        int i1 = (short1 * 2 + 1) * (short1 * 2 + 1);
                        int j1 = (j + short1) * (short1 * 2 + 1) + k + 1;
                        System.out.println("Preparing spawn area for " + name + ", " + j1 * 100 / i1 + "%");
                        i = l;
                     }

                     BlockPosition chunkcoordinates = internal.getSpawn();
                     internal.chunkProviderServer.getChunkAt(chunkcoordinates.getX() + j >> 4, chunkcoordinates.getZ() + k >> 4, () -> {});
                  }
               }
            }

            this.pluginManager.callEvent(new WorldLoadEvent(internal.getWorld()));
            return internal.getWorld();
         }
      }
   }

   @Override
   public boolean unloadWorld(String name, boolean save) {
      return this.unloadWorld(this.getWorld(name), save);
   }

   @Override
   public boolean unloadWorld(org.bukkit.World world, boolean save) {
      if (world == null) {
         return false;
      } else {
         WorldServer handle = ((CraftWorld)world).getHandle();
         if (!this.console.worlds.contains(handle)) {
            return false;
         } else if (handle.dimension <= 1) {
            return false;
         } else if (!handle.players.isEmpty()) {
            return false;
         } else {
            WorldUnloadEvent e = new WorldUnloadEvent(handle.getWorld());
            this.pluginManager.callEvent(e);
            if (e.isCancelled()) {
               return false;
            } else {
               if (save) {
                  try {
                     handle.save(true, null);
                     handle.saveLevel();
                  } catch (ExceptionWorldConflict var12) {
                     this.getLogger().log(Level.SEVERE, null, var12);
                  }
               }

               this.worlds.remove(world.getName().toLowerCase());
               this.console.worlds.remove(handle);
               File parentFolder = world.getWorldFolder().getAbsoluteFile();
               synchronized (RegionFileCache.class) {
                  Iterator<Entry<File, RegionFile>> i = RegionFileCache.a.entrySet().iterator();

                  while (i.hasNext()) {
                     Entry<File, RegionFile> entry = i.next();

                     for (File child = entry.getKey().getAbsoluteFile(); child != null; child = child.getParentFile()) {
                        if (child.equals(parentFolder)) {
                           i.remove();

                           try {
                              entry.getValue().c();
                           } catch (IOException var11) {
                              this.getLogger().log(Level.SEVERE, null, var11);
                           }
                           break;
                        }
                     }
                  }

                  return true;
               }
            }
         }
      }
   }

   public MinecraftServer getServer() {
      return this.console;
   }

   @Override
   public org.bukkit.World getWorld(String name) {
      Validate.notNull(name, "Name cannot be null");
      return this.worlds.get(name.toLowerCase());
   }

   @Override
   public org.bukkit.World getWorld(UUID uid) {
      for (org.bukkit.World world : this.worlds.values()) {
         if (world.getUID().equals(uid)) {
            return world;
         }
      }

      return null;
   }

   public void addWorld(org.bukkit.World world) {
      if (com.grinderwolf.swm.clsm.ClassModifier.skipWorldAdd(((org.bukkit.craftbukkit.v1_8_R3.CraftWorld)world).getHandle())) {
         return;
      }

      if (this.getWorld(world.getUID()) != null) {
         System.out
            .println(
               "World "
                  + world.getName()
                  + " is a duplicate of another world and has been prevented from loading. Please delete the uid.dat file from "
                  + world.getName()
                  + "'s world directory if you want to be able to load the duplicate world."
            );
      } else {
         this.worlds.put(world.getName().toLowerCase(), world);
      }
   }

   @Override
   public Logger getLogger() {
      return this.logger;
   }

   @Override
   public PluginCommand getPluginCommand(String name) {
      Command command = this.commandMap.getCommand(name);
      return command instanceof PluginCommand ? (PluginCommand)command : null;
   }

   @Override
   public void savePlayers() {
      this.checkSaveState();
      this.playerList.savePlayers();
   }

   @Override
   public void configureDbConfig(ServerConfig config) {
      Validate.notNull(config, "Config cannot be null");
      DataSourceConfig ds = new DataSourceConfig();
      ds.setDriver(this.configuration.getString("database.driver"));
      ds.setUrl(this.configuration.getString("database.url"));
      ds.setUsername(this.configuration.getString("database.username"));
      ds.setPassword(this.configuration.getString("database.password"));
      ds.setIsolationLevel(TransactionIsolation.getLevel(this.configuration.getString("database.isolation")));
      if (ds.getDriver().contains("sqlite")) {
         config.setDatabasePlatform(new SQLitePlatform());
         config.getDatabasePlatform().getDbDdlSyntax().setIdentity("");
      }

      config.setDataSourceConfig(ds);
   }

   @Override
   public boolean addRecipe(Recipe recipe) {
      CraftRecipe toAdd;
      if (recipe instanceof CraftRecipe) {
         toAdd = (CraftRecipe)recipe;
      } else if (recipe instanceof ShapedRecipe) {
         toAdd = CraftShapedRecipe.fromBukkitRecipe((ShapedRecipe)recipe);
      } else if (recipe instanceof ShapelessRecipe) {
         toAdd = CraftShapelessRecipe.fromBukkitRecipe((ShapelessRecipe)recipe);
      } else {
         if (!(recipe instanceof FurnaceRecipe)) {
            return false;
         }

         toAdd = CraftFurnaceRecipe.fromBukkitRecipe((FurnaceRecipe)recipe);
      }

      toAdd.addToCraftingManager();
      CraftingManager.getInstance().sort();
      return true;
   }

   @Override
   public List<Recipe> getRecipesFor(ItemStack result) {
      Validate.notNull(result, "Result cannot be null");
      List<Recipe> results = new ArrayList<>();
      Iterator<Recipe> iter = this.recipeIterator();

      while (iter.hasNext()) {
         Recipe recipe = iter.next();
         ItemStack stack = recipe.getResult();
         if (stack.getType() == result.getType() && (result.getDurability() == -1 || result.getDurability() == stack.getDurability())) {
            results.add(recipe);
         }
      }

      return results;
   }

   @Override
   public Iterator<Recipe> recipeIterator() {
      return new RecipeIterator();
   }

   @Override
   public void clearRecipes() {
      CraftingManager.getInstance().recipes.clear();
      RecipesFurnace.getInstance().recipes.clear();
      RecipesFurnace.getInstance().customRecipes.clear();
   }

   @Override
   public void resetRecipes() {
      CraftingManager.getInstance().recipes = (new CraftingManager()).recipes;
      RecipesFurnace.getInstance().recipes = (new RecipesFurnace()).recipes;
      RecipesFurnace.getInstance().customRecipes.clear();
   }

   @Override
   public Map<String, String[]> getCommandAliases() {
      ConfigurationSection section = this.commandsConfiguration.getConfigurationSection("aliases");
      Map<String, String[]> result = new LinkedHashMap<>();
      if (section != null) {
         for (String key : section.getKeys(false)) {
            List<String> commands;
            if (section.isList(key)) {
               commands = section.getStringList(key);
            } else {
               commands = ImmutableList.of(section.getString(key));
            }

            result.put(key, commands.toArray(new String[0]));
         }
      }

      if (!result.containsKey("gmc") && this.commandMap.getCommand("gmc") == null) {
         result.put("gmc", new String[]{"gamemode creative"});
      }
      if (!result.containsKey("gms") && this.commandMap.getCommand("gms") == null) {
         result.put("gms", new String[]{"gamemode survival"});
      }

      return result;
   }

   public void removeBukkitSpawnRadius() {
      this.configuration.set("settings.spawn-radius", null);
      this.saveConfig();
   }

   public int getBukkitSpawnRadius() {
      return this.configuration.getInt("settings.spawn-radius", -1);
   }

   @Override
   public String getShutdownMessage() {
      return this.configuration.getString("settings.shutdown-message");
   }

   @Override
   public int getSpawnRadius() {
      return ((DedicatedServer)this.console).propertyManager.getInt("spawn-protection", 16);
   }

   @Override
   public void setSpawnRadius(int value) {
      this.configuration.set("settings.spawn-radius", value);
      this.saveConfig();
   }

   @Override
   public boolean getOnlineMode() {
      return this.online.value;
   }

   @Override
   public boolean getAllowFlight() {
      return this.console.getAllowFlight();
   }

   @Override
   public boolean isHardcore() {
      return this.console.isHardcore();
   }

   @Override
   public boolean useExactLoginLocation() {
      return this.configuration.getBoolean("settings.use-exact-login-location");
   }

   public ChunkGenerator getGenerator(String world) {
      ConfigurationSection section = this.configuration.getConfigurationSection("worlds");
      ChunkGenerator result = null;
      if (section != null) {
         section = section.getConfigurationSection(world);
         if (section != null) {
            String name = section.getString("generator");
            if (name != null && !name.equals("")) {
               String[] split = name.split(":", 2);
               String id = split.length > 1 ? split[1] : null;
               Plugin plugin = this.pluginManager.getPlugin(split[0]);
               if (plugin == null) {
                  this.getLogger().severe("Could not set generator for default world '" + world + "': Plugin '" + split[0] + "' does not exist");
               } else if (!plugin.isEnabled()) {
                  this.getLogger()
                     .severe(
                        "Could not set generator for default world '"
                           + world
                           + "': Plugin '"
                           + plugin.getDescription().getFullName()
                           + "' is not enabled yet (is it load:STARTUP?)"
                     );
               } else {
                  try {
                     result = plugin.getDefaultWorldGenerator(world, id);
                     if (result == null) {
                        this.getLogger()
                           .severe(
                              "Could not set generator for default world '"
                                 + world
                                 + "': Plugin '"
                                 + plugin.getDescription().getFullName()
                                 + "' lacks a default world generator"
                           );
                     }
                  } catch (Throwable var9) {
                     plugin.getLogger()
                        .log(Level.SEVERE, "Could not set generator for default world '" + world + "': Plugin '" + plugin.getDescription().getFullName(), var9);
                  }
               }
            }
         }
      }

      return result;
   }

   @Deprecated
   public CraftMapView getMap(short id) {
      PersistentCollection collection = ((WorldServer)this.console.worlds.get(0)).worldMaps;
      WorldMap worldmap = (WorldMap)collection.get(WorldMap.class, "map_" + id);
      return worldmap == null ? null : worldmap.mapView;
   }

   public CraftMapView createMap(org.bukkit.World world) {
      Validate.notNull(world, "World cannot be null");
      net.minecraft.server.v1_8_R3.ItemStack stack = new net.minecraft.server.v1_8_R3.ItemStack(Items.MAP, 1, -1);
      WorldMap worldmap = Items.FILLED_MAP.getSavedMap(stack, ((CraftWorld)world).getHandle());
      return worldmap.mapView;
   }

   @Override
   public void shutdown() {
      this.console.safeShutdown();
   }

   @Override
   public int broadcast(String message, String permission) {
      int count = 0;

      for (Permissible permissible : this.getPluginManager().getPermissionSubscriptions(permission)) {
         if (permissible instanceof CommandSender && permissible.hasPermission(permission)) {
            CommandSender user = (CommandSender)permissible;
            user.sendMessage(message);
            count++;
         }
      }

      return count;
   }

   @Override
   public void broadcast(BaseComponent component) {
      for (Player player : this.getOnlinePlayers()) {
         player.sendMessage(component);
      }
   }

   @Override
   public void broadcast(BaseComponent... components) {
      for (Player player : this.getOnlinePlayers()) {
         player.sendMessage(components);
      }
   }

   @Deprecated
   @Override
   public org.bukkit.OfflinePlayer getOfflinePlayer(String name) {
      Validate.notNull(name, "Name cannot be null");
      Preconditions.checkArgument(!StringUtils.isBlank(name), "Name cannot be blank");
      org.bukkit.OfflinePlayer result = this.getPlayerExact(name);
      if (result == null) {
         GameProfile profile = null;
         if (MinecraftServer.getServer().getOnlineMode() || SpigotConfig.bungee) {
            profile = MinecraftServer.getServer().getUserCache().getProfile(name);
         }

         if (profile == null) {
            result = this.getOfflinePlayer(new GameProfile(UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(Charsets.UTF_8)), name));
         } else {
            result = this.getOfflinePlayer(profile);
         }
      } else {
         this.offlinePlayers.invalidate(result.getUniqueId());
      }

      return result;
   }

   @Override
   public org.bukkit.OfflinePlayer getOfflinePlayer(UUID id) {
      Validate.notNull(id, "UUID cannot be null");
      org.bukkit.OfflinePlayer result = this.getPlayer(id);
      if (result == null) {
         result = (org.bukkit.OfflinePlayer)this.offlinePlayers.getIfPresent(id);
         if (result == null) {
            result = new CraftOfflinePlayer(this, new GameProfile(id, null));
            this.offlinePlayers.put(id, result);
         }
      } else {
         this.offlinePlayers.invalidate(id);
      }

      return result;
   }

   public org.bukkit.OfflinePlayer getOfflinePlayer(GameProfile profile) {
      org.bukkit.OfflinePlayer player = new CraftOfflinePlayer(this, profile);
      this.offlinePlayers.put(profile.getId(), player);
      return player;
   }

   @Override
   public Set<String> getIPBans() {
      return new HashSet<>(Arrays.asList(this.playerList.getIPBans().getEntries()));
   }

   @Override
   public void banIP(String address) {
      Validate.notNull(address, "Address cannot be null.");
      this.getBanList(org.bukkit.BanList.Type.IP).addBan(address, null, null, null);
   }

   @Override
   public void unbanIP(String address) {
      Validate.notNull(address, "Address cannot be null.");
      this.getBanList(org.bukkit.BanList.Type.IP).pardon(address);
   }

   @Override
   public Set<org.bukkit.OfflinePlayer> getBannedPlayers() {
      Set<org.bukkit.OfflinePlayer> result = new HashSet<>();

      for (JsonListEntry<GameProfile> entry : this.playerList.getProfileBans().getValues()) {
         result.add(this.getOfflinePlayer((GameProfile)entry.getKey()));
      }

      return result;
   }

   @Override
   public org.bukkit.BanList getBanList(org.bukkit.BanList.Type type) {
      Validate.notNull(type, "Type cannot be null");
      switch (type) {
         case NAME:
         default:
            return new CraftProfileBanList(this.playerList.getProfileBans());
         case IP:
            return new CraftIpBanList(this.playerList.getIPBans());
      }
   }

   @Override
   public void setWhitelist(boolean value) {
      this.playerList.setHasWhitelist(value);
      this.console.getPropertyManager().setProperty("white-list", value);
   }

   @Override
   public Set<org.bukkit.OfflinePlayer> getWhitelistedPlayers() {
      Set<org.bukkit.OfflinePlayer> result = new LinkedHashSet<>();

      for (JsonListEntry<GameProfile> entry : this.playerList.getWhitelist().getValues()) {
         result.add(this.getOfflinePlayer((GameProfile)entry.getKey()));
      }

      return result;
   }

   @Override
   public Set<org.bukkit.OfflinePlayer> getOperators() {
      Set<org.bukkit.OfflinePlayer> result = new HashSet<>();

      for (JsonListEntry<GameProfile> entry : this.playerList.getOPs().getValues()) {
         result.add(this.getOfflinePlayer((GameProfile)entry.getKey()));
      }

      return result;
   }

   @Override
   public void reloadWhitelist() {
      this.playerList.reloadWhitelist();
   }

   @Override
   public org.bukkit.GameMode getDefaultGameMode() {
      return org.bukkit.GameMode.getByValue(((WorldServer)this.console.worlds.get(0)).getWorldData().getGameType().getId());
   }

   @Override
   public void setDefaultGameMode(org.bukkit.GameMode mode) {
      Validate.notNull(mode, "Mode cannot be null");

      for (org.bukkit.World world : this.getWorlds()) {
         ((CraftWorld)world).getHandle().worldData.setGameType(EnumGamemode.getById(mode.getValue()));
      }
   }

   @Override
   public ConsoleCommandSender getConsoleSender() {
      return this.console.console;
   }

   public EntityMetadataStore getEntityMetadata() {
      return this.entityMetadata;
   }

   public PlayerMetadataStore getPlayerMetadata() {
      return this.playerMetadata;
   }

   public WorldMetadataStore getWorldMetadata() {
      return this.worldMetadata;
   }

   @Override
   public File getWorldContainer() {
      if (this.getServer().universe != null) {
         return this.getServer().universe;
      } else {
         if (this.container == null) {
            this.container = new File(this.configuration.getString("settings.world-container", "."));
         }

         return this.container;
      }
   }

   @Override
   public org.bukkit.OfflinePlayer[] getOfflinePlayers() {
      WorldNBTStorage storage = (WorldNBTStorage)((WorldServer)this.console.worlds.get(0)).getDataManager();
      String[] files = storage.getPlayerDir().list(new DatFileFilter());
      Set<org.bukkit.OfflinePlayer> players = new HashSet<>();

      for (String file : files) {
         try {
            players.add(this.getOfflinePlayer(FastUUID.parseUUID(file.substring(0, file.length() - 4))));
         } catch (IllegalArgumentException var9) {
         }
      }

      players.addAll(this.getOnlinePlayers());
      return players.toArray(new org.bukkit.OfflinePlayer[0]);
   }

   @Override
   public Messenger getMessenger() {
      return this.messenger;
   }

   @Override
   public void sendPluginMessage(Plugin source, String channel, byte[] message) {
      StandardMessenger.validatePluginMessage(this.getMessenger(), source, channel, message);

      for (Player player : this.getOnlinePlayers()) {
         player.sendPluginMessage(source, channel, message);
      }
   }

   @Override
   public Set<String> getListeningPluginChannels() {
      Set<String> result = new HashSet<>();

      for (Player player : this.getOnlinePlayers()) {
         result.addAll(player.getListeningPluginChannels());
      }

      return result;
   }

   @Override
   public Inventory createInventory(InventoryHolder owner, InventoryType type) {
      return new CraftInventoryCustom(owner, type);
   }

   @Override
   public Inventory createInventory(InventoryHolder owner, InventoryType type, String title) {
      return new CraftInventoryCustom(owner, type, title);
   }

   @Override
   public Inventory createInventory(InventoryHolder owner, int size) throws IllegalArgumentException {
      Validate.isTrue(size % 9 == 0, "Chests must have a size that is a multiple of 9!");
      return new CraftInventoryCustom(owner, size);
   }

   @Override
   public Inventory createInventory(InventoryHolder owner, int size, String title) throws IllegalArgumentException {
      Validate.isTrue(size % 9 == 0, "Chests must have a size that is a multiple of 9!");
      return new CraftInventoryCustom(owner, size, title);
   }

   @Override
   public HelpMap getHelpMap() {
      return this.helpMap;
   }

   public SimpleCommandMap getCommandMap() {
      return this.commandMap;
   }

   @Override
   public int getMonsterSpawnLimit() {
      return this.monsterSpawn;
   }

   @Override
   public int getAnimalSpawnLimit() {
      return this.animalSpawn;
   }

   @Override
   public int getWaterAnimalSpawnLimit() {
      return this.waterAnimalSpawn;
   }

   @Override
   public int getAmbientSpawnLimit() {
      return this.ambientSpawn;
   }

   @Override
   public boolean isPrimaryThread() {
      return this.console.primaryThread == null || Thread.currentThread().equals(this.console.primaryThread);
   }

   @Override
   public String getMotd() {
      return this.console.getMotd();
   }

   @Override
   public org.bukkit.Warning.WarningState getWarningState() {
      return this.warningState;
   }

   public List<String> tabComplete(ICommandListener sender, String message) {
      return this.tabComplete(sender, message, null);
   }

   public List<String> tabComplete(ICommandListener sender, String message, BlockPosition blockPosition) {
      if (!(sender instanceof EntityPlayer)) {
         return ImmutableList.of();
      } else {
         Player player = ((EntityPlayer)sender).getBukkitEntity();
         return message.startsWith("/") ? this.tabCompleteCommand(player, message, blockPosition) : this.tabCompleteChat(player, message);
      }
   }

   public List<String> tabCompleteCommand(Player player, String message) {
      return this.tabCompleteCommand(player, message, null);
   }

   public List<String> tabCompleteCommand(Player player, String message, BlockPosition blockPosition) {
      if ((SpigotConfig.tabComplete < 0 || message.length() <= SpigotConfig.tabComplete) && !message.contains(" ")) {
         return ImmutableList.of();
      } else {
         List<String> completions = null;

         try {
            if (blockPosition != null && ((CraftWorld)player.getWorld()).getHandle().paperSpigotConfig.allowBlockLocationTabCompletion) {
               completions = this.getCommandMap()
                  .tabComplete(
                     player, message.substring(1), new org.bukkit.Location(player.getWorld(), blockPosition.getX(), blockPosition.getY(), blockPosition.getZ())
                  );
            } else {
               completions = this.getCommandMap().tabComplete(player, message.substring(1));
            }
         } catch (CommandException var6) {
            player.sendMessage(org.bukkit.ChatColor.RED + "An internal error occurred while attempting to tab-complete this command");
            this.getLogger().log(Level.SEVERE, "Exception when " + player.getName() + " attempted to tab complete " + message, (Throwable)var6);
         }

         return (List<String>)(completions == null ? ImmutableList.of() : completions);
      }
   }

   public List<String> tabCompleteChat(Player player, String message) {
      List<String> completions = new ArrayList<>();
      PlayerChatTabCompleteEvent event = new PlayerChatTabCompleteEvent(player, message, completions);
      String token = event.getLastToken();

      for (Player p : this.getOnlinePlayers()) {
         if (player.canSee(p) && StringUtil.startsWithIgnoreCase(p.getName(), token)) {
            completions.add(p.getName());
         }
      }

      this.pluginManager.callEvent(event);
      completions.removeIf(Objects::isNull);
      completions.sort(String.CASE_INSENSITIVE_ORDER);
      return completions;
   }

   public CraftItemFactory getItemFactory() {
      return CraftItemFactory.instance();
   }

   public CraftScoreboardManager getScoreboardManager() {
      return this.scoreboardManager;
   }

   public void checkSaveState() {
      if (!this.playerCommandState && !this.printSaveWarning && this.console.autosavePeriod > 0) {
         this.printSaveWarning = true;
         this.getLogger()
            .log(
               Level.WARNING,
               "A manual (plugin-induced) save has been detected while server is configured to auto-save. This may affect performance.",
               this.warningState == org.bukkit.Warning.WarningState.ON ? new Throwable() : null
            );
      }
   }

   public CraftIconCache getServerIcon() {
      return this.icon;
   }

   public CraftIconCache loadServerIcon(File file) throws Exception {
      Validate.notNull(file, "File cannot be null");
      if (!file.isFile()) {
         throw new IllegalArgumentException(file + " is not a file");
      } else {
         return loadServerIcon0(file);
      }
   }

   public CraftIconCache loadServerIcon(BufferedImage image) throws Exception {
      Validate.notNull(image, "Image cannot be null");
      return loadServerIcon0(image);
   }

   @Override
   public int getIdleTimeout() {
      return this.console.getIdleTimeout();
   }

   @Override
   public void setIdleTimeout(int threshold) {
      this.console.setIdleTimeout(threshold);
   }

   @Override
   public ChunkGenerator.ChunkData createChunkData(org.bukkit.World world) {
      return new CraftChunkData(world);
   }

   @Deprecated
   @Override
   public org.bukkit.UnsafeValues getUnsafe() {
      return CraftMagicNumbers.INSTANCE;
   }

   @Override
   public org.bukkit.Server.Spigot spigot() {
      return this.spigot;
   }

   private final class BooleanWrapper {
      private boolean value = true;

      private BooleanWrapper() {
      }
   }
}
