package org.bukkit;

import com.avaje.ebean.config.ServerConfig;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.help.HelpMap;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.map.MapView;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.PluginMessageRecipient;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.util.CachedServerIcon;

public interface Server extends PluginMessageRecipient {
   String BROADCAST_CHANNEL_ADMINISTRATIVE = "bukkit.broadcast.admin";
   String BROADCAST_CHANNEL_USERS = "bukkit.broadcast.user";

   String getName();

   String getVersion();

   int setMaxPlayers(int var1);

   String getBukkitVersion();

   @Deprecated
   Player[] _INVALID_getOnlinePlayers();

   Collection<? extends Player> getOnlinePlayers();

   int getMaxPlayers();

   int getPort();

   int getViewDistance();

   String getIp();

   String getServerName();

   String getServerId();

   String getWorldType();

   boolean getGenerateStructures();

   boolean getAllowEnd();

   boolean getAllowNether();

   boolean hasWhitelist();

   void setWhitelist(boolean var1);

   Set<org.bukkit.OfflinePlayer> getWhitelistedPlayers();

   void reloadWhitelist();

   int broadcastMessage(String var1);

   void broadcast(BaseComponent var1);

   void broadcast(BaseComponent... var1);

   String getUpdateFolder();

   File getUpdateFolderFile();

   long getConnectionThrottle();

   int getTicksPerAnimalSpawns();

   int getTicksPerMonsterSpawns();

   Player getPlayer(String var1);

   Player getPlayerExact(String var1);

   List<Player> matchPlayer(String var1);

   Player getPlayer(UUID var1);

   PluginManager getPluginManager();

   BukkitScheduler getScheduler();

   ServicesManager getServicesManager();

   List<org.bukkit.World> getWorlds();

   org.bukkit.World createWorld(org.bukkit.WorldCreator var1);

   boolean unloadWorld(String var1, boolean var2);

   boolean unloadWorld(org.bukkit.World var1, boolean var2);

   org.bukkit.World getWorld(String var1);

   org.bukkit.World getWorld(UUID var1);

   @Deprecated
   MapView getMap(short var1);

   MapView createMap(org.bukkit.World var1);

   void reload();

   void reloadConfigs();

   Logger getLogger();

   PluginCommand getPluginCommand(String var1);

   void savePlayers();

   boolean dispatchCommand(CommandSender var1, String var2) throws CommandException;

   void configureDbConfig(ServerConfig var1);

   boolean addRecipe(Recipe var1);

   List<Recipe> getRecipesFor(ItemStack var1);

   Iterator<Recipe> recipeIterator();

   void clearRecipes();

   void resetRecipes();

   Map<String, String[]> getCommandAliases();

   int getSpawnRadius();

   void setSpawnRadius(int var1);

   boolean getOnlineMode();

   boolean getAllowFlight();

   boolean isHardcore();

   @Deprecated
   boolean useExactLoginLocation();

   void shutdown();

   int broadcast(String var1, String var2);

   @Deprecated
   org.bukkit.OfflinePlayer getOfflinePlayer(String var1);

   org.bukkit.OfflinePlayer getOfflinePlayer(UUID var1);

   Set<String> getIPBans();

   void banIP(String var1);

   void unbanIP(String var1);

   Set<org.bukkit.OfflinePlayer> getBannedPlayers();

   org.bukkit.BanList getBanList(org.bukkit.BanList.Type var1);

   Set<org.bukkit.OfflinePlayer> getOperators();

   org.bukkit.GameMode getDefaultGameMode();

   void setDefaultGameMode(org.bukkit.GameMode var1);

   ConsoleCommandSender getConsoleSender();

   File getWorldContainer();

   org.bukkit.OfflinePlayer[] getOfflinePlayers();

   Messenger getMessenger();

   HelpMap getHelpMap();

   Inventory createInventory(InventoryHolder var1, InventoryType var2);

   Inventory createInventory(InventoryHolder var1, InventoryType var2, String var3);

   Inventory createInventory(InventoryHolder var1, int var2) throws IllegalArgumentException;

   Inventory createInventory(InventoryHolder var1, int var2, String var3) throws IllegalArgumentException;

   int getMonsterSpawnLimit();

   int getAnimalSpawnLimit();

   int getWaterAnimalSpawnLimit();

   int getAmbientSpawnLimit();

   boolean isPrimaryThread();

   String getMotd();

   String getShutdownMessage();

   org.bukkit.Warning.WarningState getWarningState();

   ItemFactory getItemFactory();

   ScoreboardManager getScoreboardManager();

   CachedServerIcon getServerIcon();

   CachedServerIcon loadServerIcon(File var1) throws IllegalArgumentException, Exception;

   CachedServerIcon loadServerIcon(BufferedImage var1) throws IllegalArgumentException, Exception;

   void setIdleTimeout(int var1);

   int getIdleTimeout();

   ChunkGenerator.ChunkData createChunkData(org.bukkit.World var1);

   @Deprecated
   org.bukkit.UnsafeValues getUnsafe();

   CommandMap getCommandMap();

   org.bukkit.Server.Spigot spigot();

   public static class Spigot {
      @Deprecated
      public YamlConfiguration getConfig() {
         throw new UnsupportedOperationException("Not supported yet.");
      }

      public YamlConfiguration getBukkitConfig() {
         throw new UnsupportedOperationException("Not supported yet.");
      }

      public YamlConfiguration getSpigotConfig() {
         throw new UnsupportedOperationException("Not supported yet.");
      }

      public YamlConfiguration getPaperSpigotConfig() {
         throw new UnsupportedOperationException("Not supported yet.");
      }

      public void broadcast(BaseComponent component) {
         throw new UnsupportedOperationException("Not supported yet.");
      }

      public void broadcast(BaseComponent... components) {
         throw new UnsupportedOperationException("Not supported yet.");
      }

      public void restart() {
         throw new UnsupportedOperationException("Not supported yet.");
      }

      public double[] getTPS() {
         throw new UnsupportedOperationException("Not supported yet.");
      }
   }
}
