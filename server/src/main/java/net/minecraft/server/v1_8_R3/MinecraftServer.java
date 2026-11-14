package net.minecraft.server.v1_8_R3;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import com.eatthepath.uuid.FastUUID;
import com.google.common.collect.Lists;
import com.grinderwolf.swm.clsm.ClassModifier;
import com.google.common.io.Files;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import lol.deyo.blaze.Blaze;
import lol.deyo.blaze.configs.BlazeConfig;
import lol.deyo.blaze.configs.AnticheatConfigHolder;
import lol.deyo.blaze.tickloop.BlazeTickloop;
import io.netty.util.ResourceLeakDetector;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.Proxy;
import java.security.KeyPair;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import joptsimple.OptionSet;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.World.Environment;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.chunkio.ChunkIOExecutor;
import org.bukkit.craftbukkit.v1_8_R3.scoreboard.CraftScoreboardManager;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftChatMessage;
import org.bukkit.craftbukkit.v1_8_R3.util.ServerShutdownThread;
import org.bukkit.event.server.ServerShutdownEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.PluginLoadOrder;
import org.spigotmc.SpigotConfig;
import org.spigotmc.WatchdogThread;

public abstract class MinecraftServer implements ICommandListener, IAsyncTaskHandler {
   public static boolean HAS_PROTOCOL_SUPPORT = false;
   public final MethodProfiler methodProfiler = new MethodProfiler();
   public static final Logger LOGGER = LogManager.getLogger();
   public static final File a = new File("usercache.json");
   private static MinecraftServer l;
   protected final ICommandHandler b;
   protected final Random s = new Random();
   protected final Proxy e;
   public final Queue<FutureTask<?>> j = new ConcurrentLinkedQueue<>();
   private final List<IUpdatePlayerListBox> p = Lists.newArrayList();
   private final ServerConnection q;
   public final ServerPing r = new ServerPing();
   private final YggdrasilAuthenticationService V;
   private final MinecraftSessionService W;
   private final GameProfileRepository Y;
   private final UserCache Z;
   private final Object stopLock = new Object();
   public final Queue<Runnable> processQueue = new ConcurrentLinkedQueue<Runnable>() {
      public boolean add(Runnable runnable) {
         MinecraftServer.this.postToMainThread(runnable);
         return true;
      }
   };
   public Convertable convertable;
   public File universe;
   public WorldServer[] worldServer;
   public String f;
   public int g;
   public long[][] i;
   public List<WorldServer> worlds = new ArrayList<>();
   public CraftServer server;
   public OptionSet options;
   public ConsoleCommandSender console;
   public RemoteConsoleCommandSender remoteConsole;
   public int autosavePeriod;
   public double[] recentTps = new double[3];
   private String serverIp;
   private int u = -1;
   private PlayerList v;
   private boolean isRunning = true;
   public boolean isStopped;
   private int ticks;
   private boolean spawnAnimals = true;
   private boolean spawnNPCs;
   private boolean pvpMode;
   private boolean allowFlight;
   private String motd;
   private int G = 0;
   private KeyPair H;
   private String I;
   private String J;
   private boolean demoMode;
   private boolean N;
   private String O = "";
   private String P = "";
   private boolean U;
   public Thread primaryThread;
   public Thread serverThread;
   public long ab = az();
   private boolean hasStopped = false;
   public static long currentTickLong = 0L;
   public static int currentTick = 0;
   private static final int SAMPLE_INTERVAL = 20;
   private static final BigDecimal TPS_BASE = new BigDecimal("1E9").multiply(new BigDecimal(20));
   public static final int TPS = 20;
   private static final long SEC_IN_NANO = 1000000000L;
   public static final int TICK_TIME = 50000000;
   public long lastTick = 0L;
   public final MinecraftServer.RollingAverage tps1 = new MinecraftServer.RollingAverage(1);
   public final MinecraftServer.RollingAverage tps5 = new MinecraftServer.RollingAverage(300);
   public final MinecraftServer.RollingAverage tps15 = new MinecraftServer.RollingAverage(900);
   public long tickSection;

   public MinecraftServer(OptionSet options, Proxy proxy, File file1) {
      this.q = new ServerConnection(this);
      ResourceLeakDetector.setEnabled(false);
      this.e = proxy;
      l = this;
      this.Z = new UserCache(this, file1);
      this.Z.saveUserCache(true);
      this.b = this.h();
      this.V = new YggdrasilAuthenticationService(proxy, FastUUID.toString(UUID.randomUUID()));
      this.W = this.V.createMinecraftSessionService();
      this.Y = this.V.createProfileRepository();
      this.options = options;
      Runtime.getRuntime().addShutdownHook(new ServerShutdownThread(this));
      this.primaryThread = this.serverThread = new Thread(new BlazeTickloop(this), "Server thread");
      this.primaryThread.start();
   }

   public static void main(OptionSet options) {
      DispenserRegistry.c();

      try {
         DedicatedServer dedicatedserver = new DedicatedServer(options);
         if (options.has("port")) {
            int port = (Integer)options.valueOf("port");
            if (port > 0) {
               dedicatedserver.setPort(port);
            }
         }

         if (options.has("universe")) {
            dedicatedserver.universe = (File)options.valueOf("universe");
         }

         if (options.has("world")) {
            dedicatedserver.setWorld((String)options.valueOf("world"));
         }
      } catch (Exception var3) {
         LOGGER.fatal("Failed to start the minecraft server", var3);
      }
   }

   public static MinecraftServer getServer() {
      return l;
   }

   public static long az() {
      return System.currentTimeMillis();
   }

   public abstract PropertyManager getPropertyManager();

   protected CommandDispatcher h() {
      return new CommandDispatcher();
   }

   public abstract boolean init() throws IOException;

   protected void a(String s) {
      if (this.getConvertable().isConvertable(s)) {
         LOGGER.info("Converting map!");
         this.getConvertable().convert(s, new IProgressUpdate() {
            private long b = System.currentTimeMillis();

            @Override
            public void a(String s) {
            }

            @Override
            public void a(int i) {
               if (System.currentTimeMillis() - this.b >= 1000L) {
                  this.b = System.currentTimeMillis();
                  MinecraftServer.LOGGER.info("Converting... " + i + "%");
               }
            }

            @Override
            public void c(String s) {
            }
         });
      }
   }

   protected synchronized void b(String s) {
   }

   protected void a(String s, String s1, long i, WorldType worldtype, String s2) {
      this.a(s);
      Object[] slimeWorlds = ClassModifier.getDefaultWorlds();
      this.worldServer = new WorldServer[3];
      int worldCount = 3;

      for (int j = 0; j < worldCount; j++) {
         byte dimension = 0;
         if (j == 1) {
            if (!this.getAllowNether()) {
               continue;
            }

            dimension = -1;
         }

         if (j == 2) {
            if (!this.server.getAllowEnd()) {
               continue;
            }

            dimension = 1;
         }

         String worldType = Environment.getEnvironment(dimension).toString().toLowerCase();
         String name = dimension == 0 ? s : s + "_" + worldType;
         ChunkGenerator gen = this.server.getGenerator(name);
         WorldSettings worldsettings = new WorldSettings(i, this.getGamemode(), this.getGenerateStructures(), this.isHardcore(), worldtype);
         worldsettings.setGeneratorSettings(s2);
         WorldServer slimeWorld = slimeWorlds == null ? null : (WorldServer)slimeWorlds[j];
         WorldServer world;
         if (slimeWorld != null) {
            world = slimeWorld;
            if (j == 0) {
               SpigotConfig.disableStatSaving = true;
               this.server.scoreboardManager = new CraftScoreboardManager(this, world.getScoreboard());
            }
         } else if (j == 0) {
            IDataManager idatamanager = new ServerNBTManager(this.server.getWorldContainer(), s1, true);
            WorldData worlddata = idatamanager.getWorldData();
            if (worlddata == null) {
               worlddata = new WorldData(worldsettings, s1);
            }

            worlddata.checkName(s1);
            world = (WorldServer)new WorldServer(
                  this, idatamanager, worlddata, dimension, getServer().methodProfiler, Environment.getEnvironment(dimension), gen
               )
               .b();
            world.a(worldsettings);
            this.server.scoreboardManager = new CraftScoreboardManager(this, world.getScoreboard());
         } else {
            String dim = "DIM" + dimension;
            File newWorld = new File(new File(name), dim);
            File oldWorld = new File(new File(s), dim);
            if (!newWorld.isDirectory() && oldWorld.isDirectory()) {
               LOGGER.info("---- Migration of old " + worldType + " folder required ----");
               LOGGER.info(
                  "Unfortunately due to the way that Minecraft implemented multiworld support in 1.6, Bukkit requires that you move your "
                     + worldType
                     + " folder to a new location in order to operate correctly."
               );
               LOGGER.info(
                  "We will move this folder for you, but it will mean that you need to move it back should you wish to stop using Bukkit in the future."
               );
               LOGGER.info("Attempting to move " + oldWorld + " to " + newWorld + "...");
               if (newWorld.exists()) {
                  LOGGER.warn("A file or folder already exists at " + newWorld + "!");
                  LOGGER.info("---- Migration of old " + worldType + " folder failed ----");
               } else if (newWorld.getParentFile().mkdirs()) {
                  if (oldWorld.renameTo(newWorld)) {
                     LOGGER.info("Success! To restore " + worldType + " in the future, simply move " + newWorld + " to " + oldWorld);

                     try {
                        Files.copy(new File(new File(s), "level.dat"), new File(new File(name), "level.dat"));
                     } catch (IOException var20) {
                        LOGGER.warn("Unable to migrate world data.");
                     }

                     LOGGER.info("---- Migration of old " + worldType + " folder complete ----");
                  } else {
                     LOGGER.warn("Could not move folder " + oldWorld + " to " + newWorld + "!");
                     LOGGER.info("---- Migration of old " + worldType + " folder failed ----");
                  }
               } else {
                  LOGGER.warn("Could not create path for " + newWorld + "!");
                  LOGGER.info("---- Migration of old " + worldType + " folder failed ----");
               }
            }

            IDataManager idatamanager = new ServerNBTManager(this.server.getWorldContainer(), name, true);
            WorldData worlddata = idatamanager.getWorldData();
            if (worlddata == null) {
               worlddata = new WorldData(worldsettings, name);
            }

            worlddata.checkName(name);
            world = (WorldServer)new SecondaryWorldServer(
                  this, idatamanager, dimension, this.worlds.get(0), null, worlddata, Environment.getEnvironment(dimension), gen
               )
               .b();
         }

         this.server.getPluginManager().callEvent(new WorldInitEvent(world.getWorld()));
         if (slimeWorld == null) {
            world.addIWorldAccess(new WorldManager(this, world));
         }

         world.getWorldData().setGameType(this.getGamemode());
         this.worlds.add(world);
         this.getPlayerList().setPlayerFileData(this.worlds.toArray(new WorldServer[0]));
      }

      this.a(this.getDifficulty());
      this.k();
   }

   protected void k() {
      for (WorldServer worldserver : this.worlds) {
         if (worldserver.getWorld().getKeepSpawnInMemory()) {
            BlockPosition blockposition = worldserver.getSpawn();
            long j = az();
            int i = 0;

            for (int k = -192; k <= 192 && this.isRunning(); k += 16) {
               for (int l = -192; l <= 192 && this.isRunning(); l += 16) {
                  long i1 = az();
                  if (i1 - j > 1000L) {
                     this.a_("Preparing spawn area", i * 100 / 625);
                     j = i1;
                  }

                  i++;
                  worldserver.chunkProviderServer.getChunkAt(blockposition.getX() + k >> 4, blockposition.getZ() + l >> 4, () -> {});
               }
            }
         }
      }

      for (WorldServer world : this.worlds) {
         this.server.getPluginManager().callEvent(new WorldLoadEvent(world.getWorld()));
      }

      this.s();
   }

   protected void a(String s, IDataManager idatamanager) {
      File file = new File(idatamanager.getDirectory(), "resources.zip");
      if (file.isFile()) {
         this.setResourcePack("level://" + s + "/" + file.getName(), "");
      }
   }

   public abstract boolean getGenerateStructures();

   public abstract WorldSettings.EnumGamemode getGamemode();

   public void setGamemode(WorldSettings.EnumGamemode worldsettings_enumgamemode) {
      for (int i = 0; i < this.worlds.size(); i++) {
         getServer().worlds.get(i).getWorldData().setGameType(worldsettings_enumgamemode);
      }
   }

   public abstract EnumDifficulty getDifficulty();

   public abstract boolean isHardcore();

   public abstract int p();

   public abstract boolean q();

   public abstract boolean r();

   protected void a_(String s, int i) {
      this.f = s;
      this.g = i;
      LOGGER.info("{}: {}%", new Object[]{s, i});
   }

   protected void s() {
      this.f = null;
      this.g = 0;
      this.server.enablePlugins(PluginLoadOrder.POSTWORLD);
   }

   protected void saveChunks(boolean flag) throws ExceptionWorldConflict {
      if (!this.N) {
         for (WorldServer worldserver : this.worlds) {
            if (worldserver != null) {
               if (!flag) {
                  LOGGER.info("Saving chunks for level '{}'/{}", new Object[]{worldserver.getWorldData().getName(), worldserver.worldProvider.getName()});
               }

               try {
                  worldserver.save(true, null);
                  worldserver.saveLevel();
               } catch (ExceptionWorldConflict var5) {
                  LOGGER.warn(var5.getMessage());
               }
            }
         }
      }
   }

   public void stop() throws ExceptionWorldConflict {
      synchronized (this.stopLock) {
         if (this.hasStopped) {
            return;
         }

         this.hasStopped = true;
      }

      if (!this.N) {
         this.server.getPluginManager().callEvent(new ServerShutdownEvent(this.server));
         LOGGER.info("Stopping server");
         if (this.server != null) {
            this.server.disablePlugins();
         }

         if (this.aq() != null) {
            this.aq().b();
         }

         Blaze.getInstance().shutdown();
         if (this.v != null) {
            LOGGER.info("Saving players");
            this.v.savePlayers();
            this.v.u();

            try {
               Thread.sleep(100L);
            } catch (InterruptedException var2) {
            }
         }

         if (this.worldServer != null) {
            LOGGER.info("Saving worlds");
            this.saveChunks(false);
         }

         if (SpigotConfig.saveUserCacheOnStopOnly) {
            LOGGER.info("Saving usercache.json");
            this.Z.c();
         }
      }
   }

   public String getServerIp() {
      return this.serverIp;
   }

   public void c(String s) {
      this.serverIp = s;
   }

   public boolean isRunning() {
      return this.isRunning;
   }

   public void safeShutdown() {
      this.isRunning = false;
   }

   public File y() {
      return new File(".");
   }

   public void processPackets() {
      int maximum = (Integer)BlazeConfig.maxPacketsPerDrain.getValue();
      if (maximum < 1) {
         maximum = 4096;
      }

      long deadline = System.nanoTime() + 2000000L;
      int processed = 0;
      FutureTask<?> entry;
      while ((entry = this.j.poll()) != null) {
         SystemUtils.a(entry, LOGGER);
         processed++;
         if (processed >= maximum || System.nanoTime() >= deadline) {
            break;
         }
      }
   }

   public void startTick() {
      new ServerTickStartEvent(this.ticks + 1).callEvent();
      this.ticks++;
   }

   public void endTick() {
      long endTime = System.nanoTime();
      long remaining = 50000000L - (endTime - this.lastTick);
      new ServerTickEndEvent(this.ticks, (endTime - this.lastTick) / 1000000.0, remaining).callEvent();
   }

   public void crash(Throwable throwable) {
      LOGGER.error("Encountered an unexpected exception", throwable);
      if (throwable.getCause() != null) {
         LOGGER.error("\tCause of unexpected exception was", throwable.getCause());
      }

      CrashReport crashreport;
      if (throwable instanceof ReportedException) {
         crashreport = this.b(((ReportedException)throwable).a());
      } else {
         crashreport = this.b(new CrashReport("Exception in server tick loop", throwable));
      }

      File file = new File(new File(this.y(), "crash-reports"), "crash-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + "-server.txt");
      if (crashreport.a(file)) {
         LOGGER.error("This crash report has been saved to: {}", new Object[]{file.getAbsolutePath()});
      } else {
         LOGGER.error("We were unable to save this crash report to disk.");
      }

      this.a(crashreport);
   }

   public void a(CrashReport crashreport) {
   }

   public void z() {
   }

   public void A() throws ExceptionWorldConflict {
      long curTime = System.nanoTime();
      this.lastTick = curTime;
      currentTickLong++;
      if (++currentTick % 20 == 0) {
         long diff = curTime - this.tickSection;
         BigDecimal currentTps = TPS_BASE.divide(new BigDecimal(diff), 30, RoundingMode.HALF_UP);
         this.tps1.add(currentTps, diff);
         this.tps5.add(currentTps, diff);
         this.tps15.add(currentTps, diff);
         this.recentTps[0] = this.tps1.getAverage();
         this.recentTps[1] = this.tps5.getAverage();
         this.recentTps[2] = this.tps15.getAverage();
         this.tickSection = curTime;
      }

      this.B();
      if (this.autosavePeriod > 0 && this.ticks % this.autosavePeriod == 0) {
         this.v.savePlayers();
         this.server.playerCommandState = true;

         for (World world : this.worlds) {
            world.getWorld().save(false);
         }

         this.server.playerCommandState = false;
      }

      WatchdogThread.tick();
   }

   public void B() {
      this.aq().c();
      this.server.getScheduler().mainThreadHeartbeat(this.ticks);
      ChunkIOExecutor.tick();
      Blaze.getInstance().getMenuManager().tick();
      if (AnticheatConfigHolder.ENABLED) {
         lol.deyo.blaze.anticheat.ACManager.INSTANCE.tick();
      }

      for (WorldServer world : this.worlds) {
         boolean doDaylight = world.getGameRules().getBoolean("doDaylightCycle");
         long dayTime = world.getDayTime();
         long worldTime = world.getTime();
         PacketPlayOutUpdateTime worldPacket = new PacketPlayOutUpdateTime(worldTime, dayTime, doDaylight);

         for (EntityHuman entityhuman : world.players) {
            if (entityhuman instanceof EntityPlayer && (this.ticks + entityhuman.getId()) % 20 == 0) {
               EntityPlayer entityplayer = (EntityPlayer)entityhuman;
               long playerTime = entityplayer.getPlayerTime();
               PacketPlayOutUpdateTime packet = playerTime == dayTime ? worldPacket : new PacketPlayOutUpdateTime(worldTime, playerTime, doDaylight);
               entityplayer.playerConnection.sendPacket(packet);
            }
         }
      }

      for (WorldServer worldserver : this.worlds) {
         if (currentTick % 100 == 0) {
            Blaze.getInstance().getThreadingManager().getAsyncExecutor().getFilesWorker().queueTask(() -> {
               try {
                  worldserver.checkSession();
               } catch (Exception var2) {
                  throw new RuntimeException(var2);
               }
            });
         }

         boolean old = worldserver.ticking;
         if (worldserver.checkTicking() || old) {
            try {
               worldserver.doTick();
            } catch (Exception var18) {
               Exception e = var18;
               var18.printStackTrace();

               CrashReport crashreport;
               try {
                  crashreport = CrashReport.a(e, "Exception ticking world");
               } catch (Throwable var16) {
                  throw new RuntimeException("Error generating crash report", var16);
               }

               worldserver.a(crashreport);
               throw new ReportedException(crashreport);
            }

            try {
               worldserver.tickEntities();
            } catch (Throwable var17) {
               Throwable throwable1 = var17;

               CrashReport crashreport;
               try {
                  crashreport = CrashReport.a(
                     throwable1,
                     "Exception ticking world entities [World Name: "
                        + worldserver.getWorld().getName()
                        + ", IWorldAccess Size: "
                        + worldserver.u.size()
                        + ", Entities: "
                        + worldserver.entityList.size()
                        + "]"
                  );
               } catch (Throwable var15) {
                  throw new RuntimeException("Error generating crash report", var15);
               }

               worldserver.a(crashreport);
               throw new ReportedException(crashreport);
            }

            if (this.getPlayerList().getPlayerCount() != 0) {
               worldserver.tracker.updatePlayers();

               for (EntityHuman human : worldserver.players) {
                  if (human instanceof EntityPlayer) {
                     EntityPlayer player = (EntityPlayer)human;
                     player.playerConnection.networkManager.flushAndWriteCurrentQueue();
                  }
               }
            }

            worldserver.explosionDensityCache.clear();
            worldserver.movementCache.clear();
         }
      }

      if ((Boolean)BlazeConfig.tickPlayerLatency.getValue()) {
         this.v.tick();
      }

      for (IUpdatePlayerListBox iUpdatePlayerListBox : this.p) {
         iUpdatePlayerListBox.c();
      }
   }

   public boolean getAllowNether() {
      return true;
   }

   public void a(IUpdatePlayerListBox iupdateplayerlistbox) {
      this.p.add(iupdateplayerlistbox);
   }

   public void C() {
   }

   public File d(String s) {
      return new File(this.y(), s);
   }

   public void info(String s) {
      LOGGER.info(s);
   }

   public void warning(String s) {
      LOGGER.warn(s);
   }

   public WorldServer getWorldServer(int i) {
      for (WorldServer world : this.worlds) {
         if (world.dimension == i) {
            return world;
         }
      }

      return this.worlds.get(0);
   }

   public String E() {
      return this.serverIp;
   }

   public int F() {
      return this.u;
   }

   public String G() {
      return this.motd;
   }

   public String getVersion() {
      return "1.8.8";
   }

   public int I() {
      return this.v.getPlayerCount();
   }

   public int J() {
      return this.v.getMaxPlayers();
   }

   public String[] getPlayers() {
      return this.v.f();
   }

   public GameProfile[] L() {
      return this.v.g();
   }

   public boolean isDebugging() {
      return this.getPropertyManager().getBoolean("debug", false);
   }

   public void g(String s) {
      LOGGER.error(s);
   }

   public void h(String s) {
      if (this.isDebugging()) {
         LOGGER.info(s);
      }
   }

   public String getServerModName() {
      return (String)BlazeConfig.serverBranding.getValue();
   }

   public CrashReport b(CrashReport crashreport) {
      if (this.v != null) {
         crashreport.g().a("Player Count", new Callable<Object>() {
            public String a() {
               return MinecraftServer.this.v.getPlayerCount() + " / " + MinecraftServer.this.v.getMaxPlayers() + "; " + MinecraftServer.this.v.v();
            }

            @Override
            public Object call() {
               return this.a();
            }
         });
      }

      return crashreport;
   }

   public List<String> tabCompleteCommand(ICommandListener icommandlistener, String s, BlockPosition blockposition) {
      return this.server.tabComplete(icommandlistener, s, blockposition);
   }

   public boolean O() {
      return true;
   }

   @Override
   public String getName() {
      return "Server";
   }

   @Override
   public void sendMessage(IChatBaseComponent ichatbasecomponent) {
      LOGGER.info(CraftChatMessage.fromComponent(ichatbasecomponent, EnumChatFormat.WHITE));
   }

   @Override
   public boolean a(int i, String s) {
      return true;
   }

   public ICommandHandler getCommandHandler() {
      return this.b;
   }

   public KeyPair Q() {
      return this.H;
   }

   public void au() {
   }

   public int R() {
      return this.u;
   }

   public void setPort(int i) {
      this.u = i;
   }

   public String S() {
      return this.I;
   }

   public void i(String s) {
      this.I = s;
   }

   public boolean T() {
      return this.I != null;
   }

   public String U() {
      return this.J;
   }

   public void a(KeyPair keypair) {
      this.H = keypair;
   }

   public void a(EnumDifficulty enumdifficulty) {
      for (WorldServer worldserver : this.worlds) {
         if (worldserver != null) {
            if (worldserver.getWorldData().isHardcore()) {
               worldserver.getWorldData().setDifficulty(EnumDifficulty.HARD);
               worldserver.setSpawnFlags(true, true);
            } else {
               worldserver.getWorldData().setDifficulty(enumdifficulty);
               worldserver.setSpawnFlags(this.getSpawnMonsters(), this.spawnAnimals);
            }
         }
      }
   }

   protected boolean getSpawnMonsters() {
      return true;
   }

   public boolean X() {
      return this.demoMode;
   }

   public void b(boolean flag) {
      this.demoMode = flag;
   }

   public void c(boolean flag) {
   }

   public Convertable getConvertable() {
      return this.convertable;
   }

   public void aa() {
      this.N = true;
      this.getConvertable().d();

      for (WorldServer worldserver : this.worlds) {
         if (worldserver != null) {
            worldserver.saveLevel();
         }
      }

      this.getConvertable().e(this.worlds.get(0).getDataManager().g());
      this.safeShutdown();
   }

   public String getResourcePack() {
      return this.O;
   }

   public String getResourcePackHash() {
      return this.P;
   }

   public void setResourcePack(String s, String s1) {
      this.O = s;
      this.P = s1;
   }

   public void a(MojangStatisticsGenerator mojangstatisticsgenerator) {
   }

   public void b(MojangStatisticsGenerator mojangstatisticsgenerator) {
   }

   public boolean getSnooperEnabled() {
      return false;
   }

   public abstract boolean ae();

   public boolean getOnlineMode() {
      return this.server.getOnlineMode();
   }

   public void setOnlineMode(boolean flag) {
   }

   public boolean getSpawnAnimals() {
      return this.spawnAnimals;
   }

   public void setSpawnAnimals(boolean flag) {
      this.spawnAnimals = flag;
   }

   public boolean getSpawnNPCs() {
      return this.spawnNPCs;
   }

   public void setSpawnNPCs(boolean flag) {
      this.spawnNPCs = flag;
   }

   public abstract boolean ai();

   public boolean getPVP() {
      return this.pvpMode;
   }

   public void setPVP(boolean flag) {
      this.pvpMode = flag;
   }

   public boolean getAllowFlight() {
      return this.allowFlight;
   }

   public void setAllowFlight(boolean flag) {
      this.allowFlight = flag;
   }

   public abstract boolean getEnableCommandBlock();

   public String getMotd() {
      return this.motd;
   }

   public void setMotd(String s) {
      this.motd = s;
   }

   public int getMaxBuildHeight() {
      return 16;
   }

   public void c(int i) {
   }

   public boolean isStopped() {
      return this.isStopped;
   }

   public PlayerList getPlayerList() {
      return this.v;
   }

   public void a(PlayerList playerlist) {
      this.v = playerlist;
   }

   public ServerConnection getServerConnection() {
      return this.q;
   }

   public ServerConnection aq() {
      return this.q;
   }

   public boolean as() {
      return false;
   }

   public abstract String a(WorldSettings.EnumGamemode var1, boolean var2);

   public int at() {
      return this.ticks;
   }

   @Override
   public BlockPosition getChunkCoordinates() {
      return BlockPosition.ZERO;
   }

   @Override
   public Vec3D d() {
      return new Vec3D(0.0, 0.0, 0.0);
   }

   @Override
   public World getWorld() {
      return this.worlds.get(0);
   }

   public void setWorld(String s) {
      this.J = s;
   }

   @Override
   public Entity f() {
      return null;
   }

   public int getSpawnProtection() {
      return 16;
   }

   public boolean a(World world, BlockPosition blockposition, EntityHuman entityhuman) {
      return false;
   }

   public boolean getForceGamemode() {
      return this.U;
   }

   public void setForceGamemode(boolean flag) {
      this.U = flag;
   }

   public Proxy ay() {
      return this.e;
   }

   public int getIdleTimeout() {
      return this.G;
   }

   public void setIdleTimeout(int i) {
      this.G = i;
   }

   @Override
   public IChatBaseComponent getScoreboardDisplayName() {
      return new ChatComponentText(this.getName());
   }

   public boolean aB() {
      return true;
   }

   public MinecraftSessionService aD() {
      return this.W;
   }

   public GameProfileRepository getGameProfileRepository() {
      return this.Y;
   }

   public UserCache getUserCache() {
      return this.Z;
   }

   public ServerPing aG() {
      return this.r;
   }

   public Entity a(UUID uuid) {
      for (WorldServer worldserver : this.worlds) {
         if (worldserver != null) {
            Entity entity = worldserver.getEntity(uuid);
            if (entity != null) {
               return entity;
            }
         }
      }

      return null;
   }

   @Override
   public boolean getSendCommandFeedback() {
      return getServer().worlds.get(0).getGameRules().getBoolean("sendCommandFeedback");
   }

   @Override
   public void a(CommandObjectiveExecutor.EnumCommandResult commandobjectiveexecutor_enumcommandresult, int i) {
   }

   public int aI() {
      return 29999984;
   }

   public <V> ListenableFuture<V> a(Callable<V> callable) {
      Validate.notNull(callable);
      if (!this.isMainThread()) {
         ListenableFutureTask<V> listenablefuturetask = ListenableFutureTask.create(callable);
         this.j.add(listenablefuturetask);
         return listenablefuturetask;
      } else {
         try {
            return Futures.immediateFuture(callable.call());
         } catch (Exception var3) {
            return Futures.immediateFailedFuture(var3);
         }
      }
   }

   @Override
   public ListenableFuture<Object> postToMainThread(Runnable runnable) {
      Validate.notNull(runnable);
      return this.a(Executors.callable(runnable));
   }

   @Override
   public boolean isMainThread() {
      return Thread.currentThread() == this.serverThread;
   }

   public int aK() {
      return 256;
   }

   public long aL() {
      return this.ab;
   }

   public Thread aM() {
      return this.serverThread;
   }

   public static class RollingAverage {
      private final int size;
      private long time;
      private BigDecimal total;
      private int index = 0;
      private final BigDecimal[] samples;
      private final long[] times;

      RollingAverage(int size) {
         this.size = size;
         this.time = size * 1000000000L;
         this.total = dec(20L).multiply(dec(1000000000L)).multiply(dec(size));
         this.samples = new BigDecimal[size];
         this.times = new long[size];

         for (int i = 0; i < size; i++) {
            this.samples[i] = dec(20L);
            this.times[i] = 1000000000L;
         }
      }

      private static BigDecimal dec(long t) {
         return new BigDecimal(t);
      }

      public void add(BigDecimal x, long t) {
         this.time = this.time - this.times[this.index];
         this.total = this.total.subtract(this.samples[this.index].multiply(dec(this.times[this.index])));
         this.samples[this.index] = x;
         this.times[this.index] = t;
         this.time += t;
         this.total = this.total.add(x.multiply(dec(t)));
         if (++this.index == this.size) {
            this.index = 0;
         }
      }

      public double getAverage() {
         return this.total.divide(dec(this.time), 30, RoundingMode.HALF_UP).doubleValue();
      }
   }
}
