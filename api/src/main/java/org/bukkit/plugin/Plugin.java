package org.bukkit.plugin;

import com.avaje.ebean.EbeanServer;
import java.io.File;
import java.io.InputStream;
import java.util.logging.Logger;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.generator.ChunkGenerator;

public interface Plugin extends TabExecutor {
   File getDataFolder();

   PluginDescriptionFile getDescription();

   FileConfiguration getConfig();

   InputStream getResource(String var1);

   void saveConfig();

   void saveDefaultConfig();

   void saveResource(String var1, boolean var2);

   void reloadConfig();

   PluginLoader getPluginLoader();

   org.bukkit.Server getServer();

   boolean isEnabled();

   void onDisable();

   void onLoad();

   void onEnable();

   boolean isNaggable();

   void setNaggable(boolean var1);

   EbeanServer getDatabase();

   ChunkGenerator getDefaultWorldGenerator(String var1, String var2);

   Logger getLogger();

   String getName();
}
