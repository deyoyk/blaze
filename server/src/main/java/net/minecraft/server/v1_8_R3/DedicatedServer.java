package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Lists;
import lol.deyo.blaze.Blaze;
import lol.deyo.blaze.console.BlazeConsole;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Proxy;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;
import joptsimple.OptionSet;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.craftbukkit.Main;
import org.bukkit.craftbukkit.v1_8_R3.LoggerOutputStream;
import org.bukkit.craftbukkit.v1_8_R3.command.CraftRemoteConsoleCommandSender;
import org.bukkit.craftbukkit.v1_8_R3.util.ForwardLogHandler;
import org.bukkit.craftbukkit.v1_8_R3.util.Waitable;
import org.bukkit.event.server.RemoteServerCommandEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLoadOrder;
import org.github.paperspigot.PaperSpigotConfig;
import org.spigotmc.SpigotConfig;

public class DedicatedServer extends MinecraftServer implements IMinecraftServer {
   public static final Logger LOGGER = LogManager.getLogger();
   public PropertyManager propertyManager;
   private final List<ServerCommand> l = Collections.synchronizedList(Lists.newArrayList());
   public RemoteStatusListener m;
   public RemoteControlListener n;
   public boolean generateStructures;
   public WorldSettings.EnumGamemode r;
   public boolean s;

   public DedicatedServer(OptionSet options) {
      super(options, Proxy.NO_PROXY, a);
      new Thread("Server Infinisleeper") {
         {
            this.setDaemon(true);
            this.start();
         }

         @Override
         public void run() {
            while (true) {
               try {
                  Thread.sleep(2147483647L);
               } catch (InterruptedException var2) {
               }
            }
         }
      };
   }

   @Override
   public boolean init() throws IOException {
      Thread thread = new Thread("Server console handler") {
         @Override
         public void run() {
            if (Main.useConsole) {
               new BlazeConsole(DedicatedServer.this).start();
            }
         }
      };
      java.util.logging.Logger global = java.util.logging.Logger.getLogger("");
      global.setUseParentHandlers(false);

      Handler[] j;
      for (Handler handler : j = global.getHandlers()) {
         global.removeHandler(handler);
      }

      global.addHandler(new ForwardLogHandler());
      Logger logger = LogManager.getRootLogger();
      System.setOut(new PrintStream(new LoggerOutputStream(logger, Level.INFO), true));
      System.setErr(new PrintStream(new LoggerOutputStream(logger, Level.WARN), true));
      thread.setDaemon(true);
      thread.start();
      LOGGER.info("Starting minecraft server version 1.8.8");
      if (Runtime.getRuntime().maxMemory() / 1024L / 1024L < 128L) {
         LOGGER.warn("To start the server with more ram, launch it as 'java -Xmx1024M -Xms1024M -jar minecraft_server.jar'");
      }

      this.propertyManager = new PropertyManager(this.options);
      this.generateStructures = this.propertyManager.getBoolean("generate-structures", true);
      this.c(this.propertyManager.getString("server-ip", ""));
      this.setSpawnAnimals(this.propertyManager.getBoolean("spawn-animals", true));
      this.setSpawnNPCs(this.propertyManager.getBoolean("spawn-npcs", true));
      this.setPVP(this.propertyManager.getBoolean("pvp", true));
      this.setAllowFlight(this.propertyManager.getBoolean("allow-flight", false));
      this.setResourcePack(this.propertyManager.getString("resource-pack", ""), this.propertyManager.getString("resource-pack-hash", ""));
      this.setMotd(this.propertyManager.getString("motd", "A Minecraft Server"));
      this.setForceGamemode(this.propertyManager.getBoolean("force-gamemode", false));
      this.setIdleTimeout(this.propertyManager.getInt("player-idle-timeout", 0));
      if (this.propertyManager.getInt("difficulty", 1) < 0) {
         this.propertyManager.setProperty("difficulty", 0);
      } else if (this.propertyManager.getInt("difficulty", 1) > 3) {
         this.propertyManager.setProperty("difficulty", 3);
      }

      int i = this.propertyManager.getInt("gamemode", WorldSettings.EnumGamemode.SURVIVAL.getId());
      this.r = WorldSettings.a(i);
      InetAddress inetaddress = null;
      if (!this.getServerIp().isEmpty()) {
         inetaddress = InetAddress.getByName(this.getServerIp());
      }

      if (this.R() < 0) {
         this.setPort(this.propertyManager.getInt("server-port", 25565));
      }

      this.a(new DedicatedPlayerList(this));
      SpigotConfig.init((File)this.options.valueOf("spigot-settings"));
      SpigotConfig.registerCommands();
      PaperSpigotConfig.init((File)this.options.valueOf("paper-settings"));
      Blaze.getInstance().registerCommands(false);
      if (this.getOnlineMode()) {
         this.a(MinecraftEncryption.b());
      }

      LOGGER.info("Starting Minecraft server on {}:{}", new Object[]{this.getServerIp().isEmpty() ? '*' : this.getServerIp(), this.R()});
      if (!SpigotConfig.lateBind) {
         try {
            this.aq().a(inetaddress, this.R());
         } catch (IOException var17) {
            LOGGER.warn("**** FAILED TO BIND TO PORT!");
            LOGGER.warn("The exception was: {}", new Object[]{var17.toString()});
            LOGGER.warn("Perhaps a server is already running on that port?");
            this.z();
         }
      }

      if (!this.server.getOnlineMode()) {
         LOGGER.warn("**** SERVER IS RUNNING IN OFFLINE/INSECURE MODE!");
         LOGGER.warn("The server will make no attempt to authenticate usernames. Beware.");
         if (SpigotConfig.bungee) {
            LOGGER.warn(
               "Whilst this makes it possible to use BungeeCord, unless access to your server is properly restricted, it also opens up the ability for hackers to connect with any username they choose."
            );
            LOGGER.warn("Please see http://www.spigotmc.org/wiki/firewall-guide/ for further information.");
         } else {
            LOGGER.warn(
               "While this makes the game possible to play without internet access, it also opens up the ability for hackers to connect with any username they choose."
            );
         }

         LOGGER.warn("To change this, set 'online-mode' to 'true' in the server.properties file.");
      }

      if (this.aR()) {
         this.getUserCache().c();
      }

      this.server.loadPlugins();
      this.server.enablePlugins(PluginLoadOrder.STARTUP);
      HAS_PROTOCOL_SUPPORT = this.server.getPluginManager().isPluginEnabled("ProtocolSupport");
      if (NameReferencingFileConverter.a(this.propertyManager)) {
         this.convertable = new WorldLoaderServer(this.server.getWorldContainer());
         long jx = System.nanoTime();
         if (this.U() == null) {
            this.setWorld(this.propertyManager.getString("level-name", "world"));
         }

         String s = this.propertyManager.getString("level-seed", "");
         String s1 = this.propertyManager.getString("level-type", "DEFAULT");
         String s2 = this.propertyManager.getString("generator-settings", "");
         long k = super.s.nextLong();
         if (!s.isEmpty()) {
            try {
               long l = Long.parseLong(s);
               if (l != 0L) {
                  k = l;
               }
            } catch (NumberFormatException var16) {
               k = s.hashCode();
            }
         }

         WorldType worldtype = WorldType.getType(s1);
         if (worldtype == null) {
            worldtype = WorldType.NORMAL;
         }

         this.aB();
         this.getEnableCommandBlock();
         this.p();
         this.aK();
         LOGGER.info("Preparing level \"{}\"", new Object[]{this.U()});
         this.a(this.U(), this.U(), k, worldtype, s2);
         LOGGER.info("Done ({})! For help, type \"help\" or \"?\"", new Object[]{String.format("%.3fs", (System.nanoTime() - jx) / 1.0E9)});
         if (this.propertyManager.getBoolean("enable-query", false)) {
            this.m = new RemoteStatusListener(this);
            this.m.a();
         }

         if (this.propertyManager.getBoolean("enable-rcon", false)) {
            this.n = new RemoteControlListener(this);
            this.n.a();
            this.remoteConsole = new CraftRemoteConsoleCommandSender();
         }

         if (this.server.getBukkitSpawnRadius() > -1) {
            this.propertyManager.properties.remove("spawn-protection");
            this.propertyManager.getInt("spawn-protection", this.server.getBukkitSpawnRadius());
            this.server.removeBukkitSpawnRadius();
            this.propertyManager.savePropertiesFile();
         }

         if (SpigotConfig.lateBind) {
            try {
               this.aq().a(inetaddress, this.R());
            } catch (IOException var15) {
               LOGGER.warn("**** FAILED TO BIND TO PORT! ");
               LOGGER.warn(" The exception was: {}", new Object[]{var15.toString()});
               LOGGER.warn(" Perhaps a server is already running on that port?");
               return false;
            }
         }

         System.gc();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public PropertyManager getPropertyManager() {
      return this.propertyManager;
   }

   @Override
   public boolean getGenerateStructures() {
      return this.generateStructures;
   }

   @Override
   public WorldSettings.EnumGamemode getGamemode() {
      return this.r;
   }

   @Override
   public void setGamemode(WorldSettings.EnumGamemode worldsettings_enumgamemode) {
      super.setGamemode(worldsettings_enumgamemode);
      this.r = worldsettings_enumgamemode;
   }

   @Override
   public EnumDifficulty getDifficulty() {
      return EnumDifficulty.getById(this.propertyManager.getInt("difficulty", EnumDifficulty.NORMAL.a()));
   }

   @Override
   public boolean isHardcore() {
      return this.propertyManager.getBoolean("hardcore", false);
   }

   @Override
   public CrashReport b(CrashReport crashreport) {
      crashreport = super.b(crashreport);
      crashreport.g().a("Is Modded", () -> {
         String s = this.getServerModName();
         return !s.equals("vanilla") ? "Definitely; Server brand changed to '" + s + "'" : "Unknown (can't tell)";
      });
      crashreport.g().a("Type", () -> "Dedicated Server (map_server.txt)");
      return crashreport;
   }

   @Override
   public void z() {
      System.exit(0);
   }

   @Override
   public void B() {
      super.B();
      this.aO();
   }

   @Override
   public boolean getAllowNether() {
      return this.propertyManager.getBoolean("allow-nether", true);
   }

   @Override
   public boolean getSpawnMonsters() {
      return this.propertyManager.getBoolean("spawn-monsters", true);
   }

   @Override
   public boolean getSnooperEnabled() {
      return this.propertyManager.getBoolean("snooper-enabled", true);
   }

   public void issueCommand(String s, ICommandListener icommandlistener) {
      this.l.add(new ServerCommand(s, icommandlistener));
   }

   public void aO() {
      while (!this.l.isEmpty()) {
         ServerCommand servercommand = this.l.remove(0);
         ServerCommandEvent event = new ServerCommandEvent(this.console, servercommand.command);
         this.server.getPluginManager().callEvent(event);
         if (!event.isCancelled()) {
            servercommand = new ServerCommand(event.getCommand(), servercommand.source);
            this.server.dispatchServerCommand(this.console, servercommand);
         }
      }
   }

   @Override
   public boolean ae() {
      return true;
   }

   @Override
   public boolean ai() {
      return this.propertyManager.getBoolean("use-native-transport", true);
   }

   public DedicatedPlayerList aP() {
      return (DedicatedPlayerList)super.getPlayerList();
   }

   @Override
   public int a(String s, int i) {
      return this.propertyManager.getInt(s, i);
   }

   @Override
   public String a(String s, String s1) {
      return this.propertyManager.getString(s, s1);
   }

   public boolean a(String s, boolean flag) {
      return this.propertyManager.getBoolean(s, flag);
   }

   @Override
   public void a(String s, Object object) {
      this.propertyManager.setProperty(s, object);
   }

   @Override
   public void a() {
      this.propertyManager.savePropertiesFile();
   }

   @Override
   public String b() {
      File file = this.propertyManager.c();
      return file != null ? file.getAbsolutePath() : "No settings file";
   }

   public void aQ() {
      ServerGUI.a(this);
      this.s = true;
   }

   @Override
   public boolean as() {
      return this.s;
   }

   @Override
   public String a(WorldSettings.EnumGamemode worldsettings_enumgamemode, boolean flag) {
      return "";
   }

   @Override
   public boolean getEnableCommandBlock() {
      return this.propertyManager.getBoolean("enable-command-block", false);
   }

   @Override
   public int getSpawnProtection() {
      return this.propertyManager.getInt("spawn-protection", super.getSpawnProtection());
   }

   @Override
   public boolean a(World world, BlockPosition blockposition, EntityHuman entityhuman) {
      if (world.worldProvider.getDimension() != 0) {
         return false;
      } else if (this.aP().getOPs().isEmpty()) {
         return false;
      } else if (this.aP().isOp(entityhuman.getProfile())) {
         return false;
      } else if (this.getSpawnProtection() <= 0) {
         return false;
      } else {
         BlockPosition blockposition1 = world.getSpawn();
         int i = MathHelper.a(blockposition.getX() - blockposition1.getX());
         int j = MathHelper.a(blockposition.getZ() - blockposition1.getZ());
         int k = Math.max(i, j);
         return k <= this.getSpawnProtection();
      }
   }

   public long aS() {
      return TimeUnit.MINUTES.toMillis(1L);
   }

   @Override
   public int p() {
      return this.propertyManager.getInt("op-permission-level", 4);
   }

   @Override
   public void setIdleTimeout(int i) {
      super.setIdleTimeout(i);
      this.propertyManager.setProperty("player-idle-timeout", i);
      this.a();
   }

   @Override
   public boolean q() {
      return this.propertyManager.getBoolean("broadcast-rcon-to-ops", true);
   }

   @Override
   public boolean r() {
      return this.propertyManager.getBoolean("broadcast-console-to-ops", true);
   }

   @Override
   public boolean aB() {
      return this.propertyManager.getBoolean("announce-player-achievements", true);
   }

   @Override
   public int aI() {
      int i = this.propertyManager.getInt("max-world-size", super.aI());
      if (i < 1) {
         i = 1;
      } else if (i > super.aI()) {
         i = super.aI();
      }

      return i;
   }

   @Override
   public int aK() {
      return this.propertyManager.getInt("network-compression-threshold", super.aK());
   }

   public boolean aR() {
      boolean flag = false;

      for (int i = 0; !flag && i <= 2; i++) {
         if (i > 0) {
            LOGGER.warn("Encountered a problem while converting the user banlist, retrying in a few seconds");
            this.aU();
         }

         flag = NameReferencingFileConverter.a(this);
      }

      boolean flag1 = false;

      for (int var7 = 0; !flag1 && var7 <= 2; var7++) {
         if (var7 > 0) {
            LOGGER.warn("Encountered a problem while converting the ip banlist, retrying in a few seconds");
            this.aU();
         }

         flag1 = NameReferencingFileConverter.b(this);
      }

      boolean flag2 = false;

      for (int var8 = 0; !flag2 && var8 <= 2; var8++) {
         if (var8 > 0) {
            LOGGER.warn("Encountered a problem while converting the op list, retrying in a few seconds");
            this.aU();
         }

         flag2 = NameReferencingFileConverter.c(this);
      }

      boolean flag3 = false;

      for (int var9 = 0; !flag3 && var9 <= 2; var9++) {
         if (var9 > 0) {
            LOGGER.warn("Encountered a problem while converting the whitelist, retrying in a few seconds");
            this.aU();
         }

         flag3 = NameReferencingFileConverter.d(this);
      }

      boolean flag4 = false;

      for (int var10 = 0; !flag4 && var10 <= 2; var10++) {
         if (var10 > 0) {
            LOGGER.warn("Encountered a problem while converting the player save files, retrying in a few seconds");
            this.aU();
         }

         flag4 = NameReferencingFileConverter.a(this, this.propertyManager);
      }

      return flag || flag1 || flag2 || flag3 || flag4;
   }

   private void aU() {
      try {
         Thread.sleep(5000L);
      } catch (InterruptedException var2) {
      }
   }

   @Override
   public String getPlugins() {
      StringBuilder result = new StringBuilder();
      Plugin[] plugins = this.server.getPluginManager().getPlugins();
      result.append(this.server.getName());
      result.append(" on Bukkit ");
      result.append(this.server.getBukkitVersion());
      if (plugins.length > 0 && this.server.getQueryPlugins()) {
         result.append(": ");

         for (int i = 0; i < plugins.length; i++) {
            if (i > 0) {
               result.append("; ");
            }

            result.append(plugins[i].getDescription().getName());
            result.append(" ");
            result.append(plugins[i].getDescription().getVersion().replaceAll(";", ","));
         }
      }

      return result.toString();
   }

   @Override
   public String executeRemoteCommand(final String s) {
      Waitable<String> waitable = new Waitable<String>() {
         protected String evaluate() {
            RemoteControlCommandListener.getInstance().i();
            RemoteServerCommandEvent event = new RemoteServerCommandEvent(DedicatedServer.this.remoteConsole, s);
            DedicatedServer.this.server.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
               return "";
            } else {
               ServerCommand serverCommand = new ServerCommand(event.getCommand(), RemoteControlCommandListener.getInstance());
               DedicatedServer.this.server.dispatchServerCommand(DedicatedServer.this.remoteConsole, serverCommand);
               return RemoteControlCommandListener.getInstance().j();
            }
         }
      };
      this.postToMainThread(waitable);

      try {
         return (String)waitable.get();
      } catch (ExecutionException var4) {
         throw new RuntimeException("Exception processing rcon command " + s, var4.getCause());
      } catch (InterruptedException var5) {
         Thread.currentThread().interrupt();
         throw new RuntimeException("Interrupted processing rcon command " + s, var5);
      }
   }

   @Override
   public PlayerList getPlayerList() {
      return this.aP();
   }
}
