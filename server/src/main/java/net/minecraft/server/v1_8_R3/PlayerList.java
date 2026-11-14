package net.minecraft.server.v1_8_R3;

import com.eatthepath.uuid.FastUUID;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import lol.deyo.blaze.Blaze;
import lol.deyo.blaze.configs.BlazeConfig;
import lol.deyo.blaze.configs.LanguageConfig;
import lol.deyo.blaze.console.TerminalConsoleCommandSender;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.TravelAgent;
import org.bukkit.WeatherType;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftTravelAgent;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.chunkio.ChunkIOExecutor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryView;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInitialSpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.util.Vector;
import org.spigotmc.CaseInsensitiveMap;
import org.spigotmc.SpigotConfig;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

public abstract class PlayerList {
   public static final File a = new File("banned-players.json");
   public static final File b = new File("banned-ips.json");
   public static final File c = new File("ops.json");
   public static final File d = new File("whitelist.json");
   private static final Logger f = LogManager.getLogger();
   private static final SimpleDateFormat g = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
   public final List<EntityPlayer> players = new CopyOnWriteArrayList<>();
   private final MinecraftServer server;
   private final Map<UUID, EntityPlayer> j = Maps.newHashMap();
   private final GameProfileBanList k;
   private final IpBanList l;
   private final OpList operators;
   private final WhiteList whitelist;
   private final Map<UUID, ServerStatisticManager> o;
   public final Map<String, EntityPlayer> playersByName = new CaseInsensitiveMap();
   private final CraftServer cserver;
   public IPlayerFileData playerFileData;
   public int maxPlayers;
   private boolean hasWhitelist;
   private int r;
   private int u;

   public PlayerList(MinecraftServer minecraftserver) {
      this.cserver = minecraftserver.server = new CraftServer(minecraftserver, this);
      minecraftserver.console = new TerminalConsoleCommandSender();
      this.k = new GameProfileBanList(a);
      this.l = new IpBanList(b);
      this.operators = new OpList(c);
      this.whitelist = new WhiteList(d);
      this.o = Maps.newHashMap();
      this.server = minecraftserver;
      this.k.a(false);
      this.l.a(false);
      this.maxPlayers = 8;
   }

   public void a(NetworkManager networkmanager, EntityPlayer entityplayer) {
      GameProfile gameprofile = entityplayer.getProfile();
      UserCache usercache = this.server.getUserCache();
      GameProfile gameprofile1 = usercache.a(gameprofile.getId());
      String s = gameprofile1 == null ? gameprofile.getName() : gameprofile1.getName();
      usercache.a(gameprofile);
      NBTTagCompound nbttagcompound = this.a(entityplayer);
      if (nbttagcompound != null && nbttagcompound.hasKey("bukkit")) {
         NBTTagCompound bukkit = nbttagcompound.getCompound("bukkit");
         s = bukkit.hasKeyOfType("lastKnownName", 8) ? bukkit.getString("lastKnownName") : s;
      }

      PlayerInitialSpawnEvent event = new PlayerInitialSpawnEvent(
         entityplayer.getBukkitEntity(),
         new Location(entityplayer.world.getWorld(), entityplayer.locX, entityplayer.locY, entityplayer.locZ, entityplayer.yaw, entityplayer.pitch)
      );
      this.server.server.getPluginManager().callEvent(event);
      Location newLoc = event.getSpawnLocation();
      WorldServer worldserver = ((CraftWorld)newLoc.getWorld()).getHandle();
      entityplayer.world = worldserver;
      entityplayer.locX = newLoc.getX();
      entityplayer.locY = newLoc.getY();
      entityplayer.locZ = newLoc.getZ();
      entityplayer.yaw = newLoc.getYaw();
      entityplayer.pitch = newLoc.getPitch();
      entityplayer.dimension = worldserver.dimension;
      entityplayer.spawnWorld = entityplayer.world.worldData.getName();
      entityplayer.spawnIn(worldserver);
      String s1 = "local";
      if (networkmanager.getSocketAddress() != null) {
         s1 = networkmanager.getSocketAddress().toString();
      }

      Player bukkitPlayer = entityplayer.getBukkitEntity();
      PlayerSpawnLocationEvent ev = new PlayerSpawnLocationEvent(bukkitPlayer, bukkitPlayer.getLocation());
      Bukkit.getPluginManager().callEvent(ev);
      Location loc = ev.getSpawnLocation();
      worldserver = ((CraftWorld)loc.getWorld()).getHandle();
      entityplayer.spawnIn(worldserver);
      entityplayer.setPosition(loc.getX(), loc.getY(), loc.getZ());
      entityplayer.setYawPitch(loc.getYaw(), loc.getPitch());
      WorldData worlddata = worldserver.getWorldData();
      BlockPosition blockposition = worldserver.getSpawn();
      entityplayer.playerInteractManager.b(worldserver.getWorldData().getGameType());
      PlayerConnection playerconnection = new PlayerConnection(this.server, networkmanager, entityplayer);
      playerconnection.sendPacket(
         new PacketPlayOutLogin(
            entityplayer.getId(),
            entityplayer.playerInteractManager.getGameMode(),
            worlddata.isHardcore(),
            worldserver.worldProvider.getDimension(),
            worldserver.getDifficulty(),
            Math.min(this.getMaxPlayers(), 60),
            worlddata.getType(),
            worldserver.getGameRules().getBoolean("reducedDebugInfo")
         )
      );
      entityplayer.getBukkitEntity().sendSupportedChannels();
      playerconnection.sendPacket(
         new PacketPlayOutCustomPayload("MC|Brand", new PacketDataSerializer(Unpooled.buffer()).a(this.getServer().getServerModName()))
      );
      playerconnection.sendPacket(new PacketPlayOutServerDifficulty(worlddata.getDifficulty(), true));
      playerconnection.sendPacket(new PacketPlayOutSpawnPosition(blockposition));
      playerconnection.sendPacket(new PacketPlayOutAbilities(entityplayer.abilities));
      playerconnection.sendPacket(new PacketPlayOutHeldItemSlot(entityplayer.inventory.itemInHandIndex));
      entityplayer.getStatisticManager().d();
      this.sendScoreboard((ScoreboardServer)worldserver.getScoreboard(), entityplayer);
      String joinMessage;
      if (!entityplayer.getName().equalsIgnoreCase(s)) {
         joinMessage = "§e" + LocaleI18n.a("multiplayer.player.joined.renamed", entityplayer.getName(), s);
      } else {
         joinMessage = "§e" + LocaleI18n.a("multiplayer.player.joined", entityplayer.getName());
      }

      this.onPlayerJoin(entityplayer, joinMessage);
      worldserver = this.server.getWorldServer(entityplayer.dimension);
      playerconnection.a(entityplayer.locX, entityplayer.locY, entityplayer.locZ, entityplayer.yaw, entityplayer.pitch);
      this.b(entityplayer, worldserver);
      if (!this.server.getResourcePack().isEmpty()) {
         entityplayer.setResourcePack(this.server.getResourcePack(), this.server.getResourcePackHash());
      }

      for (MobEffect effect : entityplayer.getEffects()) {
         playerconnection.sendPacket(new PacketPlayOutEntityEffect(entityplayer.getId(), effect));
      }

      entityplayer.syncInventory();
      if (nbttagcompound != null && nbttagcompound.hasKeyOfType("Riding", 10)) {
         Entity entity = EntityTypes.a(nbttagcompound.getCompound("Riding"), worldserver);
         if (entity != null) {
            entity.attachedToPlayer = true;
            worldserver.addEntity(entity);
            entityplayer.mount(entity);
            entity.attachedToPlayer = false;
         }
      }

      f.info(
         entityplayer.getName()
            + (BlazeConfig.ipConsoleLog.getValue() ? "[" + s1 + "]" : "")
            + " logged in with entity id "
            + entityplayer.getId()
            + " at (["
            + entityplayer.world.worldData.getName()
            + "]"
            + entityplayer.locX
            + ", "
            + entityplayer.locY
            + ", "
            + entityplayer.locZ
            + ")"
      );
   }

   public void sendScoreboard(ScoreboardServer scoreboardserver, EntityPlayer entityplayer) {
      List<ScoreboardObjective> list = Lists.newArrayList();

      for (ScoreboardTeam scoreboardTeam : scoreboardserver.getTeams()) {
         entityplayer.playerConnection.sendPacket(new PacketPlayOutScoreboardTeam(scoreboardTeam, 0));
      }

      for (int i = 0; i < 19; i++) {
         ScoreboardObjective scoreboardobjective = scoreboardserver.getObjectiveForSlot(i);
         if (scoreboardobjective != null && !list.contains(scoreboardobjective)) {
            for (Packet<PacketListenerPlayOut> packet : scoreboardserver.getScoreboardScorePacketsForObjective(scoreboardobjective)) {
               entityplayer.playerConnection.sendPacket(packet);
            }

            list.add(scoreboardobjective);
         }
      }

      scoreboardserver.addViewer(entityplayer);
   }

   public void setPlayerFileData(WorldServer[] aworldserver) {
      if (this.playerFileData == null) {
         this.playerFileData = aworldserver[0].getDataManager().getPlayerFileData();
         aworldserver[0].getWorldBorder().a(new IWorldBorderListener() {
            @Override
            public void a(WorldBorder worldborder, double d0) {
               PlayerList.this.sendAll(new PacketPlayOutWorldBorder(worldborder, PacketPlayOutWorldBorder.EnumWorldBorderAction.SET_SIZE));
            }

            @Override
            public void a(WorldBorder worldborder, double d0, double d1, long i) {
               PlayerList.this.sendAll(new PacketPlayOutWorldBorder(worldborder, PacketPlayOutWorldBorder.EnumWorldBorderAction.LERP_SIZE));
            }

            @Override
            public void a(WorldBorder worldborder, double d0, double d1) {
               PlayerList.this.sendAll(new PacketPlayOutWorldBorder(worldborder, PacketPlayOutWorldBorder.EnumWorldBorderAction.SET_CENTER));
            }

            @Override
            public void a(WorldBorder worldborder, int i) {
               PlayerList.this.sendAll(new PacketPlayOutWorldBorder(worldborder, PacketPlayOutWorldBorder.EnumWorldBorderAction.SET_WARNING_TIME));
            }

            @Override
            public void b(WorldBorder worldborder, int i) {
               PlayerList.this.sendAll(new PacketPlayOutWorldBorder(worldborder, PacketPlayOutWorldBorder.EnumWorldBorderAction.SET_WARNING_BLOCKS));
            }

            @Override
            public void b(WorldBorder worldborder, double d0) {
            }

            @Override
            public void c(WorldBorder worldborder, double d0) {
            }
         });
      }
   }

   public void a(EntityPlayer entityplayer, WorldServer worldserver) {
      WorldServer worldserver1 = entityplayer.u();
      if (worldserver != null) {
         worldserver.getPlayerChunkMap().removePlayer(entityplayer);
      }

      worldserver1.getPlayerChunkMap().addPlayer(entityplayer);
      worldserver1.chunkProviderServer.getChunkAt(MathHelper.floor(entityplayer.locX) >> 4, MathHelper.floor(entityplayer.locZ) >> 4);
   }

   public int d() {
      return PlayerChunkMap.getFurthestViewableBlock(this.s());
   }

   public NBTTagCompound a(EntityPlayer entityplayer) {
      return this.playerFileData.load(entityplayer);
   }

   protected void savePlayerFile(EntityPlayer entityplayer) {
      this.playerFileData.save(entityplayer);
      ServerStatisticManager serverstatisticmanager = this.o.get(entityplayer.getUniqueID());
      if (serverstatisticmanager != null) {
         serverstatisticmanager.b();
      }
   }

   public void onPlayerJoin(EntityPlayer entityPlayer, String joinMessage) {
      this.players.add(entityPlayer);
      this.playersByName.put(entityPlayer.getName(), entityPlayer);
      this.j.put(entityPlayer.getUniqueID(), entityPlayer);
      WorldServer worldserver = this.server.getWorldServer(entityPlayer.dimension);
      PlayerJoinEvent playerJoinEvent = new PlayerJoinEvent(this.cserver.getPlayer(entityPlayer), joinMessage);
      this.cserver.getPluginManager().callEvent(playerJoinEvent);
      joinMessage = playerJoinEvent.getJoinMessage();
      if (joinMessage != null && !joinMessage.isEmpty()) {
         IChatBaseComponent[] writeLazily;
         for (IChatBaseComponent line : writeLazily = CraftChatMessage.fromString(joinMessage)) {
            this.server.getPlayerList().sendAll(new PacketPlayOutChat(line));
         }
      }

      ChunkIOExecutor.adjustPoolSize(this.getPlayerCount());
      Blaze.getInstance().getMenuManager().handleJoin(playerJoinEvent);
      if (lol.deyo.blaze.configs.AnticheatConfigHolder.ENABLED) {
         lol.deyo.blaze.anticheat.ACManager.INSTANCE.handleJoin(entityPlayer);
      }
      PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer);
      CraftPlayer player = entityPlayer.getBukkitEntity();
      List<EntityPlayer> bulkSend = new ArrayList<>();
      boolean writeLazily = (Boolean)BlazeConfig.writeAddPlayerPacketsLazily.getValue();

      for (EntityPlayer onlinePlayer : this.players) {
         CraftPlayer online = onlinePlayer.getBukkitEntity();
         if (online.canSee(player)) {
            onlinePlayer.playerConnection.writeOrQueuePacket(packet, writeLazily);
         }

         if (player.canSee(online)) {
            bulkSend.add(onlinePlayer);
         }
      }

      PacketPlayOutPlayerInfo bulkPacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, bulkSend);
      entityPlayer.playerConnection.writeOrQueuePacket(bulkPacket, writeLazily);
      if (entityPlayer.world == worldserver && !worldserver.players.contains(entityPlayer)) {
         worldserver.addEntity(entityPlayer);
         this.a(entityPlayer, (WorldServer)null);
      }
   }

   public void d(EntityPlayer entityplayer) {
      entityplayer.u().getPlayerChunkMap().movePlayer(entityplayer);
      entityplayer.world.playerMap.move(entityplayer);
   }

   public String disconnect(EntityPlayer entityplayer) {
      CraftEventFactory.handleInventoryCloseEvent(entityplayer);
      Player bukkit = this.cserver.getPlayer(entityplayer);
      PlayerQuitEvent playerQuitEvent = new PlayerQuitEvent(bukkit, "§e" + entityplayer.getName() + " left the game.");
      this.cserver.getPluginManager().callEvent(playerQuitEvent);
      entityplayer.lastActivity = System.currentTimeMillis();
      entityplayer.getBukkitEntity().disconnect(playerQuitEvent.getQuitMessage());
      Blaze.getInstance().getKnockbackManager().getEditing().remove(entityplayer.getUniqueID());
      if (lol.deyo.blaze.configs.AnticheatConfigHolder.ENABLED) {
         lol.deyo.blaze.anticheat.ACManager.INSTANCE.handleQuit(entityplayer);
      }
      this.savePlayerFile(entityplayer);
      WorldServer worldserver = entityplayer.u();
      if (entityplayer.vehicle != null && !(entityplayer.vehicle instanceof EntityPlayer)) {
         worldserver.removeEntity(entityplayer.vehicle);
      }

      worldserver.kill(entityplayer);
      worldserver.getPlayerChunkMap().removePlayer(entityplayer);
      this.players.remove(entityplayer);
      this.playersByName.remove(entityplayer.getName());
      UUID uuid = entityplayer.getUniqueID();
      EntityPlayer entityplayer1 = this.j.get(uuid);
      if (entityplayer1 == entityplayer) {
         this.j.remove(uuid);
         this.o.remove(uuid);
      }

      CraftingManager craftingManager = CraftingManager.getInstance();
      CraftInventoryView lastView = (CraftInventoryView)craftingManager.lastCraftView;
      if (lastView != null && lastView.getHandle() instanceof ContainerPlayer && lastView.getPlayer() == bukkit) {
         craftingManager.lastCraftView = null;
      }

      PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entityplayer);

      for (EntityPlayer player : this.players) {
         player.playerConnection.sendPacket(packet);
      }

      this.cserver.getScoreboardManager().removePlayer(entityplayer.getBukkitEntity());
      ChunkIOExecutor.adjustPoolSize(this.getPlayerCount());
      return playerQuitEvent.getQuitMessage();
   }

   public EntityPlayer processLogin(GameProfile gameprofile, EntityPlayer player) {
      return player;
   }

   public EntityPlayer attemptLogin(LoginListener loginlistener, GameProfile gameprofile, String hostname) {
      UUID uuid = EntityHuman.a(gameprofile);
      ArrayList<EntityPlayer> arraylist = Lists.newArrayList();

      for (EntityPlayer player : this.players) {
         if (player.getUniqueID().equals(uuid)) {
            arraylist.add(player);
         }
      }

      for (EntityPlayer playerx : arraylist) {
         this.savePlayerFile(playerx);
         playerx.playerConnection.disconnect((String)LanguageConfig.loggedInAnotherLocation.getValue());
      }

      SocketAddress socketaddress = loginlistener.networkManager.getSocketAddress();
      EntityPlayer entity = new EntityPlayer(this.server, this.server.getWorldServer(0), gameprofile, new PlayerInteractManager(this.server.getWorldServer(0)));
      CraftPlayer craftPlayer = entity.getBukkitEntity();
      PlayerLoginEvent event = new PlayerLoginEvent(
         craftPlayer, hostname, ((InetSocketAddress)socketaddress).getAddress(), ((InetSocketAddress)loginlistener.networkManager.getRawAddress()).getAddress()
      );
      if (this.getProfileBans().isBanned(gameprofile) && !this.getProfileBans().get(gameprofile).hasExpired()) {
         GameProfileBanEntry gameprofilebanentry = this.k.get(gameprofile);
         String s = "You are banned from this server!\nReason: " + gameprofilebanentry.getReason();
         if (gameprofilebanentry.getExpires() != null) {
            s = s + "\nYour ban will be removed on " + g.format(gameprofilebanentry.getExpires());
         }

         if (!gameprofilebanentry.hasExpired()) {
            event.disallow(Result.KICK_BANNED, s);
         }
      } else if (!this.isWhitelisted(gameprofile)) {
         event.disallow(Result.KICK_WHITELIST, SpigotConfig.whitelistMessage);
      } else if (this.getIPBans().isBanned(socketaddress) && !this.getIPBans().get(socketaddress).hasExpired()) {
         IpBanEntry ipbanentry = this.l.get(socketaddress);
         String sx = "Your IP address is banned from this server!\nReason: " + ipbanentry.getReason();
         if (ipbanentry.getExpires() != null) {
            sx = sx + "\nYour ban will be removed on " + g.format(ipbanentry.getExpires());
         }

         event.disallow(Result.KICK_BANNED, sx);
      } else if (this.players.size() >= this.maxPlayers && !this.f(gameprofile)) {
         event.disallow(Result.KICK_FULL, SpigotConfig.serverFullMessage);
      }

      this.cserver.getPluginManager().callEvent(event);
      if (event.getResult() != Result.ALLOWED) {
         loginlistener.disconnect(event.getKickMessage());
         return null;
      } else {
         return entity;
      }
   }

   public EntityPlayer moveToWorld(EntityPlayer entityplayer, int i, boolean flag) {
      return this.moveToWorld(entityplayer, i, flag, null, true);
   }

   public EntityPlayer moveToWorld(EntityPlayer entityplayer, int i, boolean flag, Location location, boolean avoidSuffocation) {
      entityplayer.u().getTracker().untrackPlayer(entityplayer);
      entityplayer.u().getPlayerChunkMap().removePlayer(entityplayer);
      this.players.remove(entityplayer);
      this.playersByName.remove(entityplayer.getName());
      this.server.getWorldServer(entityplayer.dimension).removeEntity(entityplayer);
      BlockPosition blockposition = entityplayer.getBed();
      boolean flag1 = entityplayer.isRespawnForced();
      org.bukkit.World fromWorld = entityplayer.getBukkitEntity().getWorld();
      entityplayer.viewingCredits = false;
      entityplayer.copyTo(entityplayer, flag);
      entityplayer.d(entityplayer.getId());
      entityplayer.o(entityplayer);
      if (location == null) {
         boolean isBedSpawn = false;
         CraftWorld cworld = (CraftWorld)this.server.server.getWorld(entityplayer.spawnWorld);
         if (cworld != null && blockposition != null) {
            BlockPosition blockposition1 = EntityHuman.getBed(cworld.getHandle(), blockposition, flag1);
            if (blockposition1 != null) {
               isBedSpawn = true;
               location = new Location(cworld, blockposition1.getX() + 0.5, blockposition1.getY(), blockposition1.getZ() + 0.5);
            } else {
               entityplayer.setRespawnPosition(null, true);
               entityplayer.playerConnection.sendPacket(new PacketPlayOutGameStateChange(0, 0.0F));
            }
         }

         if (location == null) {
            cworld = (CraftWorld)this.server.server.getWorlds().get(0);
            blockposition = cworld.getHandle().getSpawn();
            location = new Location(cworld, blockposition.getX() + 0.5, blockposition.getY(), blockposition.getZ() + 0.5);
         }

         Player respawnPlayer = this.cserver.getPlayer(entityplayer);
         PlayerRespawnEvent respawnEvent = new PlayerRespawnEvent(respawnPlayer, location, isBedSpawn);
         this.cserver.getPluginManager().callEvent(respawnEvent);
         if (entityplayer.playerConnection.isDisconnected()) {
            return entityplayer;
         }

         location = respawnEvent.getRespawnLocation();
         entityplayer.reset();
      } else {
         location.setWorld(this.server.getWorldServer(i).getWorld());
      }

      WorldServer worldserver = ((CraftWorld)location.getWorld()).getHandle();
      entityplayer.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
      worldserver.chunkProviderServer.getChunkAt(MathHelper.floor(entityplayer.locX) >> 4, MathHelper.floor(entityplayer.locZ) >> 4);

      while (avoidSuffocation && !worldserver.getCubes(entityplayer, entityplayer.getBoundingBox()).isEmpty() && entityplayer.locY < 256.0) {
         entityplayer.setPosition(entityplayer.locX, entityplayer.locY + 1.0, entityplayer.locZ);
      }

      byte actualDimension = (byte)worldserver.getWorld().getEnvironment().getId();
      if (fromWorld.getEnvironment() == worldserver.getWorld().getEnvironment()) {
         entityplayer.playerConnection
            .sendPacket(
               new PacketPlayOutRespawn(
                  (byte)(actualDimension >= 0 ? -1 : 0),
                  worldserver.getDifficulty(),
                  worldserver.getWorldData().getType(),
                  entityplayer.playerInteractManager.getGameMode()
               )
            );
      }

      entityplayer.playerConnection
         .sendPacket(
            new PacketPlayOutRespawn(
               actualDimension, worldserver.getDifficulty(), worldserver.getWorldData().getType(), entityplayer.playerInteractManager.getGameMode()
            )
         );
      entityplayer.spawnIn(worldserver);
      entityplayer.dead = false;
      entityplayer.playerConnection
         .teleport(new Location(worldserver.getWorld(), entityplayer.locX, entityplayer.locY, entityplayer.locZ, entityplayer.yaw, entityplayer.pitch));
      entityplayer.setSneaking(false);
      entityplayer.playerConnection.sendPacket(new PacketPlayOutSpawnPosition(worldserver.getSpawn()));
      entityplayer.playerConnection.sendPacket(new PacketPlayOutExperience(entityplayer.exp, entityplayer.expTotal, entityplayer.expLevel));
      this.b(entityplayer, worldserver);
      if (!entityplayer.playerConnection.isDisconnected()) {
         worldserver.getPlayerChunkMap().addPlayer(entityplayer);
         worldserver.addEntity(entityplayer);
         this.players.add(entityplayer);
         this.playersByName.put(entityplayer.getName(), entityplayer);
         this.j.put(entityplayer.getUniqueID(), entityplayer);
      }

      this.updateClient(entityplayer);
      entityplayer.updateAbilities();

      for (MobEffect o1 : entityplayer.getEffects()) {
         entityplayer.playerConnection.sendPacket(new PacketPlayOutEntityEffect(entityplayer.getId(), o1));
      }

      entityplayer.setHealth(entityplayer.getHealth());
      if (fromWorld != location.getWorld()) {
         this.server.server.getPluginManager().callEvent(new PlayerChangedWorldEvent(entityplayer.getBukkitEntity(), fromWorld));
      }

      if (entityplayer.playerConnection.isDisconnected()) {
         this.savePlayerFile(entityplayer);
      }

      return entityplayer;
   }

   public void changeDimension(EntityPlayer entityplayer, int i, TeleportCause cause) {
      WorldServer exitWorld = null;
      if (entityplayer.dimension < 10) {
         for (WorldServer world : this.server.worlds) {
            if (world.dimension == i) {
               exitWorld = world;
            }
         }
      }

      Location enter = entityplayer.getBukkitEntity().getLocation();
      Location exit = null;
      boolean useTravelAgent = false;
      if (exitWorld != null) {
         if (cause == TeleportCause.END_PORTAL && i == 0) {
            exit = entityplayer.getBukkitEntity().getBedSpawnLocation();
            if (exit == null || ((CraftWorld)exit.getWorld()).getHandle().dimension != 0) {
               exit = exitWorld.getWorld().getSpawnLocation();
            }
         } else {
            exit = this.calculateTarget(enter, exitWorld);
            useTravelAgent = true;
         }
      }

      TravelAgent agent = exit != null ? (TravelAgent)((CraftWorld)exit.getWorld()).getHandle().getTravelAgent() : CraftTravelAgent.DEFAULT;
      agent.setCanCreatePortal(cause != TeleportCause.END_PORTAL);
      PlayerPortalEvent event = new PlayerPortalEvent(entityplayer.getBukkitEntity(), enter, exit, agent, cause);
      event.useTravelAgent(useTravelAgent);
      Bukkit.getServer().getPluginManager().callEvent(event);
      if (!event.isCancelled() && event.getTo() != null) {
         exit = cause != TeleportCause.END_PORTAL && event.useTravelAgent() ? event.getPortalTravelAgent().findOrCreate(event.getTo()) : event.getTo();
         if (exit != null) {
            exitWorld = ((CraftWorld)exit.getWorld()).getHandle();
            PlayerTeleportEvent tpEvent = new PlayerTeleportEvent(entityplayer.getBukkitEntity(), enter, exit, cause);
            Bukkit.getServer().getPluginManager().callEvent(tpEvent);
            if (!tpEvent.isCancelled() && tpEvent.getTo() != null) {
               Vector velocity = entityplayer.getBukkitEntity().getVelocity();
               boolean before = exitWorld.chunkProviderServer.forceChunkLoad;
               exitWorld.chunkProviderServer.forceChunkLoad = true;
               exitWorld.getTravelAgent().adjustExit(entityplayer, exit, velocity);
               exitWorld.chunkProviderServer.forceChunkLoad = before;
               this.moveToWorld(entityplayer, exitWorld.dimension, true, exit, false);
               if (entityplayer.motX != velocity.getX() || entityplayer.motY != velocity.getY() || entityplayer.motZ != velocity.getZ()) {
                  entityplayer.getBukkitEntity().setVelocity(velocity);
               }
            }
         }
      }
   }

   public Location calculateTarget(Location enter, World target) {
      WorldServer worldserver = ((CraftWorld)enter.getWorld()).getHandle();
      WorldServer worldserver1 = target.getWorld().getHandle();
      int i = worldserver.dimension;
      double y = enter.getY();
      float yaw = enter.getYaw();
      float pitch = enter.getPitch();
      double d0 = enter.getX();
      double d1 = enter.getZ();
      double d2 = 8.0;
      if (worldserver1.dimension == -1) {
         d0 = MathHelper.a(d0 / d2, worldserver1.getWorldBorder().b() + 16.0, worldserver1.getWorldBorder().d() - 16.0);
         d1 = MathHelper.a(d1 / d2, worldserver1.getWorldBorder().c() + 16.0, worldserver1.getWorldBorder().e() - 16.0);
      } else if (worldserver1.dimension == 0) {
         d0 = MathHelper.a(d0 * d2, worldserver1.getWorldBorder().b() + 16.0, worldserver1.getWorldBorder().d() - 16.0);
         d1 = MathHelper.a(d1 * d2, worldserver1.getWorldBorder().c() + 16.0, worldserver1.getWorldBorder().e() - 16.0);
      } else {
         BlockPosition blockposition;
         if (i == 1) {
            worldserver1 = this.server.worlds.get(0);
            blockposition = worldserver1.getSpawn();
         } else {
            blockposition = worldserver1.getDimensionSpawn();
         }

         d0 = blockposition.getX();
         y = blockposition.getY();
         d1 = blockposition.getZ();
      }

      if (i != 1) {
         d0 = MathHelper.clamp((int)d0, -29999872, 29999872);
         d1 = MathHelper.clamp((int)d1, -29999872, 29999872);
      }

      return new Location(worldserver1.getWorld(), d0, y, d1, yaw, pitch);
   }

   public void repositionEntity(Entity entity, Location exit, boolean portal) {
      WorldServer worldserver = (WorldServer)entity.world;
      WorldServer worldserver1 = ((CraftWorld)exit.getWorld()).getHandle();
      int i = worldserver.dimension;
      entity.setPositionRotation(exit.getX(), exit.getY(), exit.getZ(), exit.getYaw(), exit.getPitch());
      if (entity.isAlive()) {
         worldserver.entityJoinedWorld(entity, false);
      }

      if (i != 1 && entity.isAlive()) {
         if (portal) {
            Vector velocity = entity.getBukkitEntity().getVelocity();
            worldserver1.getTravelAgent().adjustExit(entity, exit, velocity);
            entity.setPositionRotation(exit.getX(), exit.getY(), exit.getZ(), exit.getYaw(), exit.getPitch());
            if (entity.motX != velocity.getX() || entity.motY != velocity.getY() || entity.motZ != velocity.getZ()) {
               entity.getBukkitEntity().setVelocity(velocity);
            }
         }

         worldserver1.addEntity(entity);
         worldserver1.entityJoinedWorld(entity, false);
      }

      entity.spawnIn(worldserver1);
   }

   public void tick() {
      if (++this.u > 600) {
         this.writeAllLazily(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_LATENCY, this.players));
         this.u = 0;
      }
   }

   public void sendAll(Packet<PacketListenerPlayOut> packet) {
      for (EntityPlayer p : this.players) {
         p.playerConnection.sendPacket(packet);
      }
   }

   public void writeAllLazily(Packet<PacketListenerPlayOut> packet) {
      for (EntityPlayer p : this.players) {
         p.playerConnection.queuePacketMainThread(packet);
      }
   }

   public void sendAll(Packet<PacketListenerPlayOut> packet, EntityHuman entityhuman) {
      if (entityhuman instanceof EntityPlayer) {
         for (EntityPlayer entityplayer : this.players) {
            if (entityplayer.getBukkitEntity().canSee(((EntityPlayer)entityhuman).getBukkitEntity())) {
               entityplayer.playerConnection.sendPacket(packet);
            }
         }
      } else {
         for (EntityPlayer entityplayerx : this.players) {
            entityplayerx.playerConnection.sendPacket(packet);
         }
      }
   }

   public void sendAllLazily(Packet<PacketListenerPlayOut> packet, EntityHuman entityhuman) {
      if (entityhuman instanceof EntityPlayer) {
         for (EntityPlayer entityplayer : this.players) {
            if (entityplayer.getBukkitEntity().canSee(((EntityPlayer)entityhuman).getBukkitEntity())) {
               entityplayer.playerConnection.queuePacketMainThread(packet);
            }
         }
      } else {
         for (EntityPlayer entityplayerx : this.players) {
            entityplayerx.playerConnection.queuePacketMainThread(packet);
         }
      }
   }

   public void sendAll(Packet<PacketListenerPlayOut> packet, World world) {
      for (EntityPlayer p : this.players) {
         p.playerConnection.sendPacket(packet);
      }
   }

   public void a(Packet<PacketListenerPlayOut> packet, int i) {
      for (EntityPlayer p : this.players) {
         if (p.dimension == i) {
            p.playerConnection.sendPacket(packet);
         }
      }
   }

   public void a(EntityHuman entityhuman, IChatBaseComponent ichatbasecomponent) {
      ScoreboardTeamBase scoreboardteambase = entityhuman.getScoreboardTeam();
      if (scoreboardteambase != null) {
         for (String s : scoreboardteambase.getPlayerNameSet()) {
            EntityPlayer entityplayer = this.getPlayer(s);
            if (entityplayer != null && entityplayer != entityhuman) {
               entityplayer.sendMessage(ichatbasecomponent);
            }
         }
      }
   }

   public void b(EntityHuman entityhuman, IChatBaseComponent ichatbasecomponent) {
      ScoreboardTeamBase scoreboardteambase = entityhuman.getScoreboardTeam();
      if (scoreboardteambase == null) {
         this.sendMessage(ichatbasecomponent);
      } else {
         for (EntityPlayer p : this.players) {
            if (p.getScoreboardTeam() != scoreboardteambase) {
               p.sendMessage(ichatbasecomponent);
            }
         }
      }
   }

   public String b(boolean flag) {
      StringBuilder s = new StringBuilder();
      List<EntityPlayer> arraylist = Lists.newArrayList(this.players);

      for (int i = 0; i < arraylist.size(); i++) {
         if (i > 0) {
            s.append(", ");
         }

         s.append(arraylist.get(i).getName());
         if (flag) {
            s.append(" (").append(arraylist.get(i).getUniqueID().toString()).append(")");
         }
      }

      return s.toString();
   }

   public String[] f() {
      String[] astring = new String[this.players.size()];

      for (int i = 0; i < this.players.size(); i++) {
         astring[i] = this.players.get(i).getName();
      }

      return astring;
   }

   public GameProfile[] g() {
      GameProfile[] agameprofile = new GameProfile[this.players.size()];

      for (int i = 0; i < this.players.size(); i++) {
         agameprofile[i] = this.players.get(i).getProfile();
      }

      return agameprofile;
   }

   public GameProfileBanList getProfileBans() {
      return this.k;
   }

   public IpBanList getIPBans() {
      return this.l;
   }

   public void addOp(GameProfile gameprofile) {
      this.operators.add(new OpListEntry(gameprofile, this.server.p(), this.operators.b(gameprofile)));
      Player player = this.server.server.getPlayer(gameprofile.getId());
      if (player != null) {
         player.recalculatePermissions();
      }
   }

   public void removeOp(GameProfile gameprofile) {
      this.operators.remove(gameprofile);
      Player player = this.server.server.getPlayer(gameprofile.getId());
      if (player != null) {
         player.recalculatePermissions();
      }
   }

   public boolean isWhitelisted(GameProfile gameprofile) {
      return !this.hasWhitelist || this.operators.d(gameprofile) || this.whitelist.d(gameprofile);
   }

   public boolean isOp(GameProfile gameprofile) {
      return this.operators.d(gameprofile);
   }

   public EntityPlayer getPlayer(String s) {
      return this.playersByName.get(s);
   }

   public void sendPacketNearby(double d0, double d1, double d2, double d3, int i, Packet<?> packet) {
      this.sendPacketNearby(null, d0, d1, d2, d3, i, packet);
   }

   public void sendPacketNearby(EntityHuman entityhuman, double d0, double d1, double d2, double d3, int i, Packet<?> packet) {
      double squared = d3 * d3;

      for (EntityPlayer entityplayer : this.players) {
         if ((!(entityhuman instanceof EntityPlayer) || entityplayer.getBukkitEntity().canSee(((EntityPlayer)entityhuman).getBukkitEntity()))
            && entityplayer != entityhuman
            && entityplayer.dimension == i) {
            double d4 = d0 - entityplayer.locX;
            double d5 = d1 - entityplayer.locY;
            double d6 = d2 - entityplayer.locZ;
            if (d4 * d4 + d5 * d5 + d6 * d6 < squared) {
               entityplayer.playerConnection.sendPacket(packet);
            }
         }
      }
   }

   public void savePlayers() {
      for (EntityPlayer p : this.players) {
         this.savePlayerFile(p);
      }
   }

   public void addWhitelist(GameProfile gameprofile) {
      this.whitelist.add(new WhiteListEntry(gameprofile));
   }

   public void removeWhitelist(GameProfile gameprofile) {
      this.whitelist.remove(gameprofile);
   }

   public WhiteList getWhitelist() {
      return this.whitelist;
   }

   public String[] getWhitelisted() {
      return this.whitelist.getEntries();
   }

   public OpList getOPs() {
      return this.operators;
   }

   public String[] n() {
      return this.operators.getEntries();
   }

   public void reloadWhitelist() {
   }

   public void b(EntityPlayer entityplayer, WorldServer worldserver) {
      WorldBorder worldborder = entityplayer.world.getWorldBorder();
      entityplayer.playerConnection.sendPacket(new PacketPlayOutWorldBorder(worldborder, PacketPlayOutWorldBorder.EnumWorldBorderAction.INITIALIZE));
      entityplayer.playerConnection
         .sendPacket(new PacketPlayOutUpdateTime(worldserver.getTime(), worldserver.getDayTime(), worldserver.getGameRules().getBoolean("doDaylightCycle")));
      if (worldserver.S()) {
         entityplayer.setPlayerWeather(WeatherType.DOWNFALL, false);
         entityplayer.updateWeather(-worldserver.p, worldserver.p, -worldserver.r, worldserver.r);
      }
   }

   public void updateClient(EntityPlayer entityplayer) {
      entityplayer.updateInventory(entityplayer.defaultContainer);
      entityplayer.getBukkitEntity().updateScaledHealth();
      entityplayer.playerConnection.sendPacket(new PacketPlayOutHeldItemSlot(entityplayer.inventory.itemInHandIndex));
   }

   public int getPlayerCount() {
      return this.players.size();
   }

   public int getMaxPlayers() {
      return this.maxPlayers;
   }

   public String[] getSeenPlayers() {
      return this.server.worlds.get(0).getDataManager().getPlayerFileData().getSeenPlayers();
   }

   public boolean getHasWhitelist() {
      return this.hasWhitelist;
   }

   public void setHasWhitelist(boolean flag) {
      this.hasWhitelist = flag;
   }

   public List<EntityPlayer> b(String s) {
      ArrayList<EntityPlayer> arraylist = Lists.newArrayList();

      for (EntityPlayer entityplayer : this.players) {
         if (entityplayer.w().equals(s)) {
            arraylist.add(entityplayer);
         }
      }

      return arraylist;
   }

   public int s() {
      return this.r;
   }

   public MinecraftServer getServer() {
      return this.server;
   }

   public NBTTagCompound t() {
      return null;
   }

   private void a(EntityPlayer entityplayer, EntityPlayer entityplayer1, World world) {
      if (entityplayer1 != null) {
         entityplayer.playerInteractManager.setGameMode(entityplayer1.playerInteractManager.getGameMode());
      }

      entityplayer.playerInteractManager.b(world.getWorldData().getGameType());
   }

   public void u() {
      for (EntityPlayer p : this.players) {
         p.playerConnection.disconnect(this.server.server.getShutdownMessage());
      }
   }

   public void sendMessage(IChatBaseComponent[] iChatBaseComponents) {
      for (IChatBaseComponent component : iChatBaseComponents) {
         this.sendMessage(component, true);
      }
   }

   public void sendMessage(IChatBaseComponent ichatbasecomponent, boolean flag) {
      this.server.sendMessage(ichatbasecomponent);
      this.sendAll(new PacketPlayOutChat(CraftChatMessage.fixComponent(ichatbasecomponent), (byte)(flag ? 1 : 0)));
   }

   public void sendMessage(IChatBaseComponent ichatbasecomponent) {
      this.sendMessage(ichatbasecomponent, true);
   }

   public ServerStatisticManager a(EntityHuman entityhuman) {
      UUID uuid = entityhuman.getUniqueID();
      ServerStatisticManager serverstatisticmanager = uuid == null ? null : this.o.get(uuid);
      if (serverstatisticmanager == null) {
         File file = new File(this.server.getWorldServer(0).getDataManager().getDirectory(), "stats");
         File file1 = new File(file, FastUUID.toString(uuid) + ".json");
         if (!file1.exists()) {
            File file2 = new File(file, entityhuman.getName() + ".json");
            if (file2.exists() && file2.isFile()) {
               file2.renameTo(file1);
            }
         }

         serverstatisticmanager = new ServerStatisticManager(this.server, file1);
         serverstatisticmanager.a();
         this.o.put(uuid, serverstatisticmanager);
      }

      return serverstatisticmanager;
   }

   public void a(int i) {
      this.r = i;
      if (this.server.worldServer != null) {
         for (int k = 0; k < this.server.worlds.size(); k++) {
            WorldServer worldserver = this.server.worlds.get(0);
            if (worldserver != null) {
               worldserver.getPlayerChunkMap().a(i);
            }
         }
      }
   }

   public List<EntityPlayer> v() {
      return this.players;
   }

   public EntityPlayer a(UUID uuid) {
      return this.j.get(uuid);
   }

   public boolean f(GameProfile gameprofile) {
      return false;
   }
}
