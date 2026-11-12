package net.minecraft.server.v1_8_R3;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import lol.deyo.blaze.Blaze;
import lol.deyo.blaze.api.protocol.MovementHandler;
import lol.deyo.blaze.api.protocol.PacketHandler;
import lol.deyo.blaze.combat.CombatMetrics;
import lol.deyo.blaze.combat.CombatResult;
import lol.deyo.blaze.combat.CombatValidator;
import lol.deyo.blaze.combat.PingTracker;
import lol.deyo.blaze.combat.PlayerPositionHistory;
import lol.deyo.blaze.configs.BlazeConfig;
import lol.deyo.blaze.configs.LanguageConfig;
import lol.deyo.blaze.configs.extra.ConfigEdit;
import lol.deyo.blaze.knockback.extra.KnockbackEdit;
import lol.deyo.blaze.utils.QueuedTeleport;
import io.netty.buffer.Unpooled;
import io.netty.handler.flush.FlushConsolidationHandler;
import io.netty.util.concurrent.Future;
import it.unimi.dsi.fastutil.ints.Int2ShortMap;
import it.unimi.dsi.fastutil.ints.Int2ShortOpenHashMap;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftSign;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryView;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftChatMessage;
import org.bukkit.craftbukkit.v1_8_R3.util.LazyPlayerSet;
import org.bukkit.craftbukkit.v1_8_R3.util.Waitable;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.Recipe;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;
import org.github.paperspigot.PaperSpigotConfig;
import org.spigotmc.AsyncCatcher;
import org.spigotmc.SpigotConfig;

public class PlayerConnection implements PacketListenerPlayIn, IUpdatePlayerListBox {
   private static final Logger c = LogManager.getLogger();
   private static final Set<PacketPlayOutPosition.EnumPlayerTeleportFlags> TELEPORT_NO_ROT = Sets.immutableEnumSet(
      PacketPlayOutPosition.EnumPlayerTeleportFlags.X_ROT,
      new PacketPlayOutPosition.EnumPlayerTeleportFlags[]{PacketPlayOutPosition.EnumPlayerTeleportFlags.Y_ROT}
   );
   private static final AtomicIntegerFieldUpdater<PlayerConnection> chatSpamField = AtomicIntegerFieldUpdater.newUpdater(PlayerConnection.class, "chatThrottle");
   private static final int SURVIVAL_PLACE_DISTANCE_SQUARED = 36;
   private static final int CREATIVE_PLACE_DISTANCE_SQUARED = 49;
   private static final List<Integer> invalidItems = Arrays.asList(
      8,
      9,
      10,
      11,
      26,
      34,
      36,
      43,
      51,
      52,
      55,
      59,
      60,
      62,
      63,
      64,
      68,
      71,
      74,
      75,
      83,
      90,
      92,
      93,
      94,
      104,
      105,
      115,
      117,
      118,
      119,
      125,
      127,
      132,
      140,
      141,
      142,
      144
   );
   public final NetworkManager networkManager;
   protected final CraftServer server;
   private final MinecraftServer minecraftServer;
   private final Int2ShortMap n = new Int2ShortOpenHashMap();
   public EntityPlayer player;
   public final PingTracker pingTracker = new PingTracker();
   public final PlayerPositionHistory positionHistory = new PlayerPositionHistory();
   public long lastAttackPacketNanos;
   public boolean processedDisconnect;
   private int e;
   private int f;
   private int g;
   private int i;
   private long j;
   private long k;
   private volatile int chatThrottle;
   private int m;
   private double lastX;
   private double lastY;
   private double lastZ;
   private final Queue<QueuedTeleport> teleportQueue = new ArrayDeque<>();
   public int lastDropTick = MinecraftServer.currentTick;
   private int dropCount = 0;
   private double lastPosX = Double.MAX_VALUE;
   private double lastPosY = Double.MAX_VALUE;
   private double lastPosZ = Double.MAX_VALUE;
   private float lastPitch = Float.MAX_VALUE;
   private float lastYaw = Float.MAX_VALUE;
   private boolean justTeleported;
   private boolean hasMoved;
   private long lastPlace = -1L;
   private int packets = 0;

   public PlayerConnection(MinecraftServer minecraftserver, NetworkManager networkmanager, EntityPlayer entityplayer) {
      networkmanager.a(this);
      this.minecraftServer = minecraftserver;
      this.networkManager = networkmanager;
      this.player = entityplayer;
      entityplayer.playerConnection = this;
      this.server = minecraftserver.server;
      if ((Boolean)BlazeConfig.flushConsolidation.getValue() && networkmanager.channel != null) {
         networkmanager.channel.eventLoop().execute(() -> {
            if (networkmanager.channel.pipeline().get("flush_consolidation") == null) {
               networkmanager.channel.pipeline().addLast("flush_consolidation", new FlushConsolidationHandler(256, true));
            }
         });
      }
   }

   public CraftPlayer getPlayer() {
      return this.player.getBukkitEntity();
   }

   @Override
   public void c() {
      int pingInterval = (Integer)BlazeConfig.pingSampleIntervalTicks.getValue();
      if (pingInterval < 1) {
         pingInterval = 20;
      }
      if (++this.e - this.k > pingInterval) {
         this.k = this.e;
         this.j = this.d();
         this.i = (int)this.j;
         this.pingTracker.onKeepAliveSent(this.i, System.nanoTime());
         this.sendPacket(new PacketPlayOutKeepAlive(this.i));
      }

      int spam;
      do {
         spam = this.chatThrottle;
      } while (this.chatThrottle > 0 && !chatSpamField.compareAndSet(this, spam, spam - 1));

      if (this.m > 0) {
         this.m--;
      }

      if ((Boolean)BlazeConfig.playerIdleTimer.getValue()
         && this.player.D() > 0L
         && this.minecraftServer.getIdleTimeout() > 0
         && MinecraftServer.az() - this.player.D() > this.minecraftServer.getIdleTimeout() * 1000L * 60L) {
         this.player.resetIdleTimer();
         this.disconnect("You have been idle for too long!");
      }
   }

   public NetworkManager a() {
      return this.networkManager;
   }

   public void disconnect(String s) {
      PlayerKickEvent event = new PlayerKickEvent(
         this.player.getBukkitEntity().getPlayer(), s, EnumChatFormat.YELLOW + this.player.getName() + " left the game."
      );
      if (this.server.getServer().isRunning()) {
         this.server.getPluginManager().callEvent(event);
      }

      if (!event.isCancelled()) {
         ChatComponentText chatcomponenttext = new ChatComponentText(event.getReason());
         this.networkManager.a(new PacketPlayOutKickDisconnect(chatcomponenttext), future -> this.networkManager.close(chatcomponenttext));
         this.a(chatcomponenttext);
         this.networkManager.k();
         this.minecraftServer.postToMainThread(this.networkManager::l);
      }
   }

   @Override
   public void a(PacketPlayInSteerVehicle packetplayinsteervehicle) {
      if (packetplayinsteervehicle.a() >= Float.MAX_VALUE || packetplayinsteervehicle.b() >= Float.MAX_VALUE) {
         this.disconnect("Invalid vehicle move packet received");
      }

      PlayerConnectionUtils.ensureMainThread(packetplayinsteervehicle, this, this.player.u());
      this.player.a(packetplayinsteervehicle.a(), packetplayinsteervehicle.b(), packetplayinsteervehicle.c(), packetplayinsteervehicle.d());
   }

   private boolean b(PacketPlayInFlying packetplayinflying) {
      return this.isIllegal(packetplayinflying);
   }

   private boolean isIllegal(PacketPlayInFlying packetplayinflying) {
      return !Doubles.isFinite(packetplayinflying.x)
         || !Doubles.isFinite(packetplayinflying.y)
         || !Doubles.isFinite(packetplayinflying.z)
         || !Floats.isFinite(packetplayinflying.pitch)
         || !Floats.isFinite(packetplayinflying.yaw)
         || Math.abs(packetplayinflying.x) > 3.0E7
         || Math.abs(packetplayinflying.z) > 3.0E7;
   }

   @Override
   public void a(PacketPlayInFlying packet) {
      PlayerConnectionUtils.ensureMainThread(packet, this, this.player.u());
      if (this.isIllegal(packet)) {
         this.disconnect("Invalid move packet received");
      } else {
         this.player.processItemQueue(packet);
         QueuedTeleport peek = this.teleportQueue.peek();
         if (peek != null) {
            if (packet.hasPos && packet.hasLook) {
               boolean correct = peek.isRelative() || peek.getX() == packet.x && Math.abs(peek.getY() - packet.y) < 0.001 && peek.getZ() == packet.z;
               if (correct) {
                  this.teleportQueue.remove();
                  this.lastX = peek.getX();
                  this.lastY = peek.getY();
                  this.lastZ = peek.getZ();
                  this.player.setPosition(peek.getX(), peek.getY(), peek.getZ());
               } else {
                  this.player.onGround = packet.f();
                  this.player.l();
               }
            } else {
               this.player.onGround = packet.f();
               this.player.l();
            }
         } else if (!this.teleportQueue.isEmpty()) {
            this.player.onGround = packet.f();
            this.player.l();
         } else if (!this.player.viewingCredits) {
            if (!this.player.dead) {
               WorldServer worldserver = this.minecraftServer.getWorldServer(this.player.dimension);
               CraftPlayer craftPlayer = this.getPlayer();
               if (!this.hasMoved) {
                  Location curPos = craftPlayer.getLocation();
                  this.lastPosX = curPos.getX();
                  this.lastPosY = curPos.getY();
                  this.lastPosZ = curPos.getZ();
                  this.lastYaw = curPos.getYaw();
                  this.lastPitch = curPos.getPitch();
                  this.hasMoved = true;
               }

               MovementHandler[] movementHandlers = Blaze.getInstance().getMovementHandlerArray();
               if (PlayerMoveEvent.getHandlerList().getRegisteredListeners().length > 0 || movementHandlers.length > 0) {
                  Location from = new Location(craftPlayer.getWorld(), this.lastPosX, this.lastPosY, this.lastPosZ, this.lastYaw, this.lastPitch);
                  Location to = craftPlayer.getLocation().clone();
                  if (packet.hasPos && packet.y != -999.0) {
                     to.setX(packet.x);
                     to.setY(packet.y);
                     to.setZ(packet.z);
                  }

                  if (packet.hasLook) {
                     to.setYaw(packet.yaw);
                     to.setPitch(packet.pitch);
                  }

                  double delta = Math.pow(this.lastPosX - to.getX(), 2.0) + Math.pow(this.lastPosY - to.getY(), 2.0) + Math.pow(this.lastPosZ - to.getZ(), 2.0);
                  float deltaAngle = Math.abs(this.lastYaw - to.getYaw()) + Math.abs(this.lastPitch - to.getPitch());
                  if (delta > 0.00390625 || deltaAngle > 10.0F) {
                     this.lastPosX = to.getX();
                     this.lastPosY = to.getY();
                     this.lastPosZ = to.getZ();
                     this.lastYaw = to.getYaw();
                     this.lastPitch = to.getPitch();
                     boolean hasLook = packet.hasLook && deltaAngle > 0.0F;
                     boolean hasPos = packet.hasPos && delta > 0.0;

                     for (int handlerIndex = 0; handlerIndex < movementHandlers.length; handlerIndex++) {
                        MovementHandler handler = movementHandlers[handlerIndex];
                        if (hasPos) {
                           handler.handleUpdateLocation(craftPlayer, from, to, packet);
                           if (from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ()) {
                              handler.handleBlockUpdateLocation(craftPlayer, from, to);
                           }
                        }

                        if (hasLook) {
                           handler.handleUpdateRotation(craftPlayer, from, to, packet);
                        }
                     }

                     if ((Boolean)BlazeConfig.firePlayerMoveEvent.getValue()) {
                        Location oldTo = to.clone();
                        PlayerMoveEvent event = new PlayerMoveEvent(craftPlayer, from, to);
                        this.server.getPluginManager().callEvent(event);
                        if (event.isCancelled()) {
                           this.player
                              .playerConnection
                              .sendPacket(new PacketPlayOutPosition(from.getX(), from.getY(), from.getZ(), 0.0F, 0.0F, TELEPORT_NO_ROT));
                           return;
                        }

                        if (!oldTo.equals(event.getTo()) && !event.isCancelled()) {
                           this.player.getBukkitEntity().teleport(event.getTo(), TeleportCause.UNKNOWN);
                           return;
                        }
                     }

                     if (!from.equals(this.getPlayer().getLocation()) && this.justTeleported) {
                        this.justTeleported = false;
                        return;
                     }
                  }
               }

               this.f = this.e;
               if (this.player.vehicle != null) {
                  float yaw = this.player.yaw;
                  float pitch = this.player.pitch;
                  this.player.vehicle.al();
                  double savedX = this.player.locX;
                  double savedY = this.player.locY;
                  double savedZ = this.player.locZ;
                  if (packet.hasLook) {
                     yaw = packet.yaw;
                     pitch = packet.pitch;
                  }

                  this.player.onGround = packet.f();
                  this.player.l();
                  this.player.setLocation(savedX, savedY, savedZ, yaw, pitch);
                  if (this.player.vehicle != null) {
                     this.player.vehicle.al();
                  }

                  this.minecraftServer.getPlayerList().d(this.player);
                  if (this.player.vehicle != null) {
                     this.player.vehicle.ai = true;
                     double diffSquared = 0.0;
                     if (packet.hasPos) {
                        double diffXPrevious = packet.x - this.lastX;
                        double diffYPrevious = packet.y - this.lastY;
                        double diffZPrevious = packet.z - this.lastZ;
                        diffSquared = diffXPrevious * diffXPrevious + diffYPrevious * diffYPrevious + diffZPrevious * diffZPrevious;
                     }

                     if (diffSquared > 4.0) {
                        Entity entity = this.player.vehicle;
                        this.player.playerConnection.sendPacket(new PacketPlayOutEntityTeleport(entity));
                        this.a(this.player.locX, this.player.locY, this.player.locZ, this.player.yaw, this.player.pitch);
                     }
                  }

                  this.lastX = this.player.locX;
                  this.lastY = this.player.locY;
                  this.lastZ = this.player.locZ;
                  worldserver.g(this.player);
               } else if (this.player.isSleeping()) {
                  this.player.l();
                  this.player.setLocation(this.lastX, this.lastY, this.lastZ, this.player.yaw, this.player.pitch);
                  worldserver.g(this.player);
               } else {
                  double savedYx = this.player.locY;
                  this.lastX = this.player.locX;
                  this.lastY = this.player.locY;
                  this.lastZ = this.player.locZ;
                  double locX = this.player.locX;
                  double locY = this.player.locY;
                  double locZ = this.player.locZ;
                  float yawx = this.player.yaw;
                  float pitchx = this.player.pitch;
                  if (packet.hasPos && packet.y == -999.0) {
                     packet.hasPos = false;
                  }

                  if (packet.hasPos) {
                     locX = packet.x;
                     locY = packet.y;
                     locZ = packet.z;
                  }

                  if (packet.hasLook) {
                     yawx = packet.yaw;
                     pitchx = packet.pitch;
                  }

                  this.player.l();
                  this.player.setLocation(this.lastX, this.lastY, this.lastZ, yawx, pitchx);
                  double phaseLeniency = (Double)BlazeConfig.playerPhaseLeniency.getValue();
                  double diffY = locY - this.player.locY;
                  if ((Boolean)BlazeConfig.playerMovedTooQuicklyCheck.getValue()) {
                     double diffX = locX - this.player.locX;
                     double diffZ = locZ - this.player.locZ;
                     double motionSquared = this.player.motX * this.player.motX + this.player.motY * this.player.motY + this.player.motZ * this.player.motZ;
                     double deltaSquared = diffX * diffX + diffY * diffY + diffZ * diffZ;
                     if (deltaSquared - motionSquared > SpigotConfig.movedTooQuicklyThreshold) {
                        c.warn("{} moved too quickly! {},{},{}", new Object[]{this.player.getName(), diffX, diffY, diffZ});
                        this.a(this.lastX, this.lastY, this.lastZ, this.player.yaw, this.player.pitch);
                        return;
                     }
                  }

                  boolean couldHavePhased = packet.hasPos && !this.player.noclip;
                  List<BlockPosition> oldCollisions = couldHavePhased
                     ? worldserver.getCollisionBlocks(this.player, this.player.getBoundingBox().shrink(phaseLeniency, phaseLeniency, phaseLeniency))
                     : null;
                  if (this.player.onGround && !packet.f() && diffY > 0.0) {
                     this.player.bF();
                  }

                  this.player.onGround = packet.f();
                  this.player.setLocation(locX, locY, locZ, yawx, pitchx);
                  this.player.checkMovement(locX - this.lastX, locY - this.lastY, locZ - this.lastZ);
                  if (couldHavePhased) {
                     List<BlockPosition> newCollisions = worldserver.getCollisionBlocks(
                        this.player, this.player.getBoundingBox().shrink(phaseLeniency, phaseLeniency, phaseLeniency)
                     );
                     if (newCollisions.size() > oldCollisions.size()
                        || worldserver.isThroughBlock(new Vec3D(this.lastX, this.lastY + 0.6, this.lastZ), new Vec3D(locX, locY + 0.6, locZ), oldCollisions)) {
                        this.a(this.lastX, this.lastY, this.lastZ, yawx, pitchx);
                        return;
                     }
                  }

                  AxisAlignedBB axisalignedbb = this.player.getBoundingBox().grow(0.0625, 0.0625, 0.0625).a(0.0, -0.55, 0.0);
                  if (this.minecraftServer.getAllowFlight() || this.player.abilities.canFly || worldserver.c(axisalignedbb)) {
                     this.g = 0;
                  } else if (diffY >= -0.03125) {
                     this.g++;
                     if (this.g > 80) {
                        c.warn("{} was kicked for floating too long!", new Object[]{this.player.getName()});
                        this.disconnect("Flying is not enabled on this server");
                        return;
                     }
                  }

                  this.minecraftServer.getPlayerList().d(this.player);
                  this.player.a(this.player.locY - savedYx, packet.f());
               }
            }
         }
      }
   }

   public void a(double d0, double d1, double d2, float f, float f1) {
      this.a(d0, d1, d2, f, f1, Collections.emptySet());
   }

   public void a(double d0, double d1, double d2, float f, float f1, Set<PacketPlayOutPosition.EnumPlayerTeleportFlags> set) {
      Player player = this.getPlayer();
      Location from = player.getLocation();
      double x = d0;
      double y = d1;
      double z = d2;
      float yaw = f;
      float pitch = f1;
      if (set.contains(PacketPlayOutPosition.EnumPlayerTeleportFlags.X)) {
         x = d0 + from.getX();
      }

      if (set.contains(PacketPlayOutPosition.EnumPlayerTeleportFlags.Y)) {
         y = d1 + from.getY();
      }

      if (set.contains(PacketPlayOutPosition.EnumPlayerTeleportFlags.Z)) {
         z = d2 + from.getZ();
      }

      if (set.contains(PacketPlayOutPosition.EnumPlayerTeleportFlags.Y_ROT)) {
         yaw = f + from.getYaw();
      }

      if (set.contains(PacketPlayOutPosition.EnumPlayerTeleportFlags.X_ROT)) {
         pitch = f1 + from.getPitch();
      }

      Location to = new Location(this.getPlayer().getWorld(), x, y, z, yaw, pitch);
      PlayerTeleportEvent event = new PlayerTeleportEvent(player, from.clone(), to.clone(), TeleportCause.UNKNOWN);
      this.server.getPluginManager().callEvent(event);
      if (event.isCancelled() || to.equals(event.getTo())) {
         set.clear();
         to = event.isCancelled() ? event.getFrom() : event.getTo();
         d0 = to.getX();
         d1 = to.getY();
         d2 = to.getZ();
         f = to.getYaw();
         f1 = to.getPitch();
      }

      this.internalTeleport(d0, d1, d2, f, f1, set);
   }

   public void teleport(Location dest) {
      this.teleport(dest, true);
   }

   public void teleport(Location dest, boolean rotation) {
      this.internalTeleport(
         dest.getX(),
         dest.getY(),
         dest.getZ(),
         rotation ? dest.getYaw() : 0.0F,
         rotation ? dest.getPitch() : 0.0F,
         rotation ? Collections.emptySet() : TELEPORT_NO_ROT
      );
   }

   private void internalTeleport(double d0, double d1, double d2, float f, float f1, Set<PacketPlayOutPosition.EnumPlayerTeleportFlags> set) {
      this.positionHistory.clear();
      if (Float.isNaN(f)) {
         f = 0.0F;
      }

      if (Float.isNaN(f1)) {
         f1 = 0.0F;
      }

      this.justTeleported = true;
      this.lastX = d0;
      this.lastY = d1;
      this.lastZ = d2;
      if (set.contains(PacketPlayOutPosition.EnumPlayerTeleportFlags.X)) {
         this.lastX = this.lastX + this.player.locX;
      }

      if (set.contains(PacketPlayOutPosition.EnumPlayerTeleportFlags.Y)) {
         this.lastY = this.lastY + this.player.locY;
      }

      if (set.contains(PacketPlayOutPosition.EnumPlayerTeleportFlags.Z)) {
         this.lastZ = this.lastZ + this.player.locZ;
      }

      float f2 = f;
      float f3 = f1;
      if (set.contains(PacketPlayOutPosition.EnumPlayerTeleportFlags.Y_ROT)) {
         f2 = f + this.player.yaw;
      }

      if (set.contains(PacketPlayOutPosition.EnumPlayerTeleportFlags.X_ROT)) {
         f3 = f1 + this.player.pitch;
      }

      this.lastPosX = this.lastX;
      this.lastPosY = this.lastY;
      this.lastPosZ = this.lastZ;
      this.lastYaw = f2;
      this.lastPitch = f3;
      this.player.setLocation(this.lastX, this.lastY, this.lastZ, f2, f3);
      this.player.playerConnection.sendPacket(new PacketPlayOutPosition(d0, d1, d2, f, f1, set));
   }

   @Override
   public void a(PacketPlayInBlockDig packetplayinblockdig) {
      PlayerConnectionUtils.ensureMainThread(packetplayinblockdig, this, this.player.u());
      if (!this.player.dead) {
         this.player.resetIdleTimer();
         switch (PlayerConnection.SyntheticClass_1.a[packetplayinblockdig.c().ordinal()]) {
            case 1:
               if (!this.player.isSpectator()) {
                  if (this.lastDropTick != MinecraftServer.currentTick) {
                     this.dropCount = 0;
                     this.lastDropTick = MinecraftServer.currentTick;
                  } else {
                     this.dropCount++;
                     if (this.dropCount >= 20) {
                        c.warn("{} dropped their items too quickly!", new Object[]{this.player.getName()});
                        this.disconnect("You dropped your items too quickly (Hacking?)");
                        return;
                     }
                  }

                  if ((Boolean)BlazeConfig.patchDropEatBug.getValue() && this.player.isEating()) {
                     this.player.bU();
                  }

                  this.player.a(false);
               }

               return;
            case 2:
               if (!this.player.isSpectator()) {
                  this.player.a(true);
               }

               return;
            case 3:
               this.player.bU();
               return;
            case 4:
            case 5:
            case 6:
               BlockPosition blockposition = packetplayinblockdig.a();
               double d0 = this.player.locX - (blockposition.getX() + 0.5);
               double d1 = this.player.locY - (blockposition.getY() + 0.5) + 1.5;
               double d2 = this.player.locZ - (blockposition.getZ() + 0.5);
               double d3 = d0 * d0 + d1 * d1 + d2 * d2;
               if (d3 <= 36.0) {
                  WorldServer worldserver = this.minecraftServer.getWorldServer(this.player.dimension);
                  Chunk chunk = worldserver.getChunkAtWorldCoords(blockposition);
                  IBlockData iblockdata = worldserver.getType(chunk, blockposition);
                  if (packetplayinblockdig.c() == PacketPlayInBlockDig.EnumPlayerDigType.START_DESTROY_BLOCK) {
                     if (!this.minecraftServer.a(worldserver, blockposition, this.player) && worldserver.getWorldBorder().a(blockposition)) {
                        this.player.playerInteractManager.a(chunk, blockposition, packetplayinblockdig.b());
                     } else {
                        CraftEventFactory.callPlayerInteractEvent(
                           this.player,
                           Action.LEFT_CLICK_BLOCK,
                           chunk.bukkitChunk.getBlock(blockposition.getX(), blockposition.getY(), blockposition.getZ()),
                           blockposition,
                           packetplayinblockdig.b(),
                           this.player.inventory.getItemInHand(),
                           false
                        );
                        this.sendPacket(new PacketPlayOutBlockChange(worldserver, blockposition));
                        TileEntity tileentity = worldserver.getTileEntity(chunk, blockposition, iblockdata.getBlock());
                        if (tileentity != null) {
                           this.sendPacket(tileentity.getUpdatePacket());
                        }
                     }
                  } else if (packetplayinblockdig.c() == PacketPlayInBlockDig.EnumPlayerDigType.STOP_DESTROY_BLOCK) {
                     this.player.playerInteractManager.a(chunk, iblockdata, blockposition);
                  } else if (packetplayinblockdig.c() == PacketPlayInBlockDig.EnumPlayerDigType.ABORT_DESTROY_BLOCK) {
                     this.player.playerInteractManager.e();
                     this.player.playerConnection.sendPacket(new PacketPlayOutBlockChange(blockposition, iblockdata));
                  }
               }

               return;
            default:
               throw new IllegalArgumentException("Invalid player action");
         }
      }
   }

   @Override
   public void a(PacketPlayInBlockPlace packetplayinblockplace) {
      PlayerConnectionUtils.ensureMainThread(packetplayinblockplace, this, this.player.u());
      WorldServer worldserver = this.minecraftServer.getWorldServer(this.player.dimension);
      boolean throttled = false;
      if (PaperSpigotConfig.interactLimitEnabled && this.lastPlace != -1L && packetplayinblockplace.timestamp - this.lastPlace < 30L && this.packets++ >= 4) {
         throttled = true;
      } else if (packetplayinblockplace.timestamp - this.lastPlace >= 30L || this.lastPlace == -1L) {
         this.lastPlace = BlazeConfig.optimizePlayerPrinter.getValue() ? -1L : packetplayinblockplace.timestamp;
         this.packets = 0;
      }

      if (!this.player.dead) {
         boolean always = false;
         ItemStack itemstack = this.player.inventory.getItemInHand();
         boolean flag = false;
         BlockPosition blockposition = packetplayinblockplace.a();
         EnumDirection enumdirection = EnumDirection.fromType1(packetplayinblockplace.getFace());
         this.player.resetIdleTimer();
         if (packetplayinblockplace.getFace() == 255) {
            if (itemstack == null) {
               return;
            }

            int itemstackAmount = itemstack.count;
            if (!throttled) {
               float f1 = this.player.pitch;
               float f2 = this.player.yaw;
               Vec3D vec3d = new Vec3D(this.player.locX, this.player.locY + this.player.getHeadHeight(), this.player.locZ);
               float f3 = MathHelper.cos(-f2 * (float) (Math.PI / 180.0) - (float) Math.PI);
               float f4 = MathHelper.sin(-f2 * (float) (Math.PI / 180.0) - (float) Math.PI);
               float f5 = -MathHelper.cos(-f1 * (float) (Math.PI / 180.0));
               double d3 = this.player.playerInteractManager.getGameMode() == WorldSettings.EnumGamemode.CREATIVE ? 5.0 : 4.5;
               Vec3D vec3d1 = vec3d.add(f4 * f5 * d3, MathHelper.sin(-f1 * (float) (Math.PI / 180.0)) * d3, f3 * f5 * d3);
               MovingObjectPosition movingobjectposition = this.player.world.rayTrace(vec3d, vec3d1, false);
               boolean cancelled;
               if (movingobjectposition == null || movingobjectposition.type != MovingObjectPosition.EnumMovingObjectType.BLOCK) {
                  PlayerInteractEvent event = CraftEventFactory.callPlayerInteractEvent(this.player, Action.RIGHT_CLICK_AIR, itemstack);
                  cancelled = event.useItemInHand() == Result.DENY;
               } else if (this.player.playerInteractManager.firedInteract) {
                  this.player.playerInteractManager.firedInteract = false;
                  cancelled = this.player.playerInteractManager.interactResult;
               } else {
                  PlayerInteractEvent event = CraftEventFactory.callPlayerInteractEvent(
                     this.player, Action.RIGHT_CLICK_BLOCK, movingobjectposition.a(), movingobjectposition.direction, itemstack, true
                  );
                  cancelled = event.useItemInHand() == Result.DENY;
               }

               if (cancelled) {
                  this.player.updateInventory(this.player.activeContainer);
               } else {
                  Item item = itemstack.getItem();
                  boolean shouldQueue = (Boolean)BlazeConfig.patchGhostBuckets.getValue() && item instanceof ItemBucket
                     || (Boolean)BlazeConfig.patchProjectileMisdirection.getValue()
                        && (item instanceof ItemFishingRod || item instanceof ItemEnderPearl || item instanceof ItemSnowball || item instanceof ItemEgg);
                  if (shouldQueue) {
                     this.player.addToUseItemQueue(itemstack);
                  } else {
                     this.player.playerInteractManager.useItem(this.player, this.player.world, itemstack);
                  }
               }
            }

            always = itemstack.count != itemstackAmount
               || itemstack.getItem() == Item.getItemOf(Blocks.WATERLILY)
               || itemstack.getItem().getName().contains("bucket");
         } else {
            Location eyeLoc = this.getPlayer().getEyeLocation();
            double reachDistance = NumberConversions.square(eyeLoc.getX() - blockposition.getX())
               + NumberConversions.square(eyeLoc.getY() - blockposition.getY())
               + NumberConversions.square(eyeLoc.getZ() - blockposition.getZ());
            if (reachDistance > (this.getPlayer().getGameMode() == GameMode.CREATIVE ? 49 : 36)) {
               return;
            }

            if (!worldserver.getWorldBorder().a(blockposition)) {
               return;
            }

            if (this.teleportQueue.isEmpty()
               && this.player.e(blockposition.getX() + 0.5, blockposition.getY() + 0.5, blockposition.getZ() + 0.5) < 64.0
               && !this.minecraftServer.a(worldserver, blockposition, this.player)
               && worldserver.getWorldBorder().a(blockposition)) {
               always = throttled
                  || !this.player
                     .playerInteractManager
                     .interact(
                        this.player,
                        worldserver,
                        itemstack,
                        blockposition,
                        enumdirection,
                        packetplayinblockplace.d(),
                        packetplayinblockplace.e(),
                        packetplayinblockplace.f()
                     );
            }

            flag = true;
         }

         if (flag) {
            this.sendPacket(new PacketPlayOutBlockChange(worldserver, blockposition));
            this.sendPacket(new PacketPlayOutBlockChange(worldserver, blockposition.shift(enumdirection)));
         }

         itemstack = this.player.inventory.getItemInHand();
         if (itemstack != null && itemstack.count == 0) {
            this.player.inventory.items[this.player.inventory.itemInHandIndex] = null;
            itemstack = null;
         }

         if (itemstack == null || itemstack.l() == 0) {
            this.player.g = true;
            this.player.inventory.items[this.player.inventory.itemInHandIndex] = ItemStack.b(this.player.inventory.items[this.player.inventory.itemInHandIndex]);
            Slot slot = this.player.activeContainer.getSlot(this.player.inventory, this.player.inventory.itemInHandIndex);
            this.player.activeContainer.b();
            this.player.g = false;
            if (!ItemStack.matches(this.player.inventory.getItemInHand(), packetplayinblockplace.getItemStack()) || always) {
               this.sendPacket(new PacketPlayOutSetSlot(this.player.activeContainer.windowId, slot.rawSlotIndex, this.player.inventory.getItemInHand()));
            }
         }
      }
   }

   @Override
   public void a(PacketPlayInSpectate packetplayinspectate) {
      PlayerConnectionUtils.ensureMainThread(packetplayinspectate, this, this.player.u());
      if (this.player.isSpectator()) {
         Entity entity = null;

         for (WorldServer worldserver : this.minecraftServer.worlds) {
            if (worldserver != null) {
               entity = packetplayinspectate.a(worldserver);
               if (entity != null) {
                  break;
               }
            }
         }

         if (entity != null) {
            this.player.setSpectatorTarget(this.player);
            this.player.mount(null);
            this.player.getBukkitEntity().teleport(entity.getBukkitEntity(), TeleportCause.SPECTATE);
         }
      }
   }

   @Override
   public void a(PacketPlayInResourcePackStatus packetplayinresourcepackstatus) {
      PlayerConnectionUtils.ensureMainThread(packetplayinresourcepackstatus, this, this.player.u());
      Status status = Status.values()[packetplayinresourcepackstatus.b.ordinal()];
      this.server.getPluginManager().callEvent(new PlayerResourcePackStatusEvent(this.getPlayer(), status, packetplayinresourcepackstatus.a));
   }

   @Override
   public void a(IChatBaseComponent ichatbasecomponent) {
      if (!this.processedDisconnect) {
         this.processedDisconnect = true;
         c.info("{} lost connection: {}", new Object[]{this.player.getName(), ichatbasecomponent.c()});
         this.player.q();
         String quitMessage = this.minecraftServer.getPlayerList().disconnect(this.player);
         if (quitMessage != null && !quitMessage.isEmpty()) {
            this.minecraftServer.getPlayerList().sendMessage(CraftChatMessage.fromString(quitMessage));
         }
      }
   }

   public void sendPacket(Packet<?> packet) {
      if (packet != null && !this.processedDisconnect) {
         if (packet instanceof PacketPlayOutChat) {
            EntityHuman.EnumChatVisibility vis = this.player.getChatFlags();
            if (vis == EntityHuman.EnumChatVisibility.HIDDEN || vis == EntityHuman.EnumChatVisibility.SYSTEM && !((PacketPlayOutChat)packet).b()) {
               return;
            }
         } else if (packet instanceof PacketPlayOutSpawnPosition) {
            PacketPlayOutSpawnPosition spawnPos = (PacketPlayOutSpawnPosition)packet;
            this.player.compassTarget = new Location(this.getPlayer().getWorld(), spawnPos.position.getX(), spawnPos.position.getY(), spawnPos.position.getZ());
         }

         if (packet instanceof PacketPlayOutPosition) {
            PacketPlayOutPosition positionUpdate = (PacketPlayOutPosition)packet;
            boolean relative = positionUpdate.getFlags().contains(PacketPlayOutPosition.EnumPlayerTeleportFlags.X)
               || positionUpdate.getFlags().contains(PacketPlayOutPosition.EnumPlayerTeleportFlags.Y)
               || positionUpdate.getFlags().contains(PacketPlayOutPosition.EnumPlayerTeleportFlags.Z);
            AsyncCatcher.catchOp("Teleportation should never happen async");
            this.teleportQueue.add(new QueuedTeleport(positionUpdate.getX(), positionUpdate.getY(), positionUpdate.getZ(), relative));
         }

         if (this.shouldSend(packet)) {
            this.networkManager.handle(packet);
         }

         if (packet instanceof PacketPlayOutOpenWindow) {
            PacketPlayOutTransaction packetplayouttransaction = new PacketPlayOutTransaction(
               ((PacketPlayOutOpenWindow)packet).getWindowId(), (short)32767, false
            );
            this.sendPacket(packetplayouttransaction);
         }
      }
   }

   public void writePacketLazily(Packet<?> packet) {
      if (this.shouldSend(packet)) {
         this.networkManager.writePacketLazily(packet);
      }
   }

   public void writeOrQueuePacket(Packet<?> packet, boolean write) {
      if (write) {
         this.writePacketLazily(packet);
      } else {
         this.queuePacketMainThread(packet);
      }
   }

   public void queuePacketMainThread(Packet<?> packet) {
      if (this.shouldSend(packet)) {
         this.networkManager.fastNetworkManager.queueMainThreadPacket(packet);
      }
   }

   public void queueEntityTrackerPacket(Packet<?> packet, int trackerThread) {
      if (this.shouldSend(packet)) {
         this.networkManager.fastNetworkManager.queueEntityTrackerPacket(packet, trackerThread);
      }
   }

   private boolean shouldSend(Packet<?> packet) {
      try {
         boolean shouldSend = true;
         PacketHandler[] handlers = Blaze.getInstance().getPacketHandlerArray();
         for (int i = 0; i < handlers.length; i++) {
            shouldSend = handlers[i].handleSentPacket(this, packet);
         }

         return shouldSend;
      } catch (Throwable var5) {
         CrashReport crashreport = CrashReport.a(var5, "Sending packet");
         CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Packet being sent");
         crashreportsystemdetails.a("Packet class", () -> packet.getClass().getCanonicalName());
         throw new ReportedException(crashreport);
      }
   }

   @Override
   public void a(PacketPlayInHeldItemSlot packetplayinhelditemslot) {
      if (!this.player.dead) {
         PlayerConnectionUtils.ensureMainThread(packetplayinhelditemslot, this, this.player.u());
         int slot = packetplayinhelditemslot.a();
         if (slot >= 0 && slot < PlayerInventory.getHotbarSize()) {
            PlayerItemHeldEvent event = new PlayerItemHeldEvent(this.getPlayer(), this.player.inventory.itemInHandIndex, slot);
            this.server.getPluginManager().callEvent(event);
            this.player.resetIdleTimer();
            if (event.isCancelled()) {
               this.sendPacket(new PacketPlayOutHeldItemSlot(this.player.inventory.itemInHandIndex));
               return;
            }

            this.player.inventory.itemInHandIndex = slot;
         } else {
            c.warn("{} tried to set an invalid carried item", new Object[]{this.player.getName()});
            this.disconnect("Invalid hotbar selection (Hacking?)");
         }
      }
   }

   @Override
   public void a(PacketPlayInChat packetplayinchat) {
      boolean isSync = packetplayinchat.a().startsWith("/");
      if (isSync) {
         PlayerConnectionUtils.ensureMainThread(packetplayinchat, this, this.player.u());
      }

      if (!this.player.dead && this.player.getChatFlags() != EntityHuman.EnumChatVisibility.HIDDEN) {
         this.player.resetIdleTimer();
         final String s = StringUtils.normalizeSpace(packetplayinchat.a());

         for (int i = 0; i < s.length(); i++) {
            if (!SharedConstants.isAllowedChatCharacter(s.charAt(i))) {
               if (!isSync) {
                  Waitable<?> waitable = new Waitable<Object>() {
                     protected Object evaluate() {
                        PlayerConnection.this.disconnect("Illegal characters in chat");
                        return null;
                     }
                  };
                  this.minecraftServer.postToMainThread(waitable);

                  try {
                     waitable.get();
                  } catch (InterruptedException var12) {
                     Thread.currentThread().interrupt();
                  } catch (ExecutionException var13) {
                     throw new RuntimeException(var13);
                  }
               } else {
                  this.disconnect("Illegal characters in chat");
               }

               return;
            }
         }

         if (isSync) {
            try {
               this.minecraftServer.server.playerCommandState = true;
               this.handleCommand(s);
            } finally {
               this.minecraftServer.server.playerCommandState = false;
            }
         } else if (s.isEmpty()) {
            c.warn("{} tried to send an empty message", new Object[]{this.player.getName()});
         } else if (this.getPlayer().isConversing()) {
            this.minecraftServer.postToMainThread(new Waitable<Object>() {
               protected Object evaluate() {
                  PlayerConnection.this.getPlayer().acceptConversationInput(s);
                  return null;
               }
            });
         } else if (this.player.getChatFlags() == EntityHuman.EnumChatVisibility.SYSTEM) {
            ChatMessage chatmessage = new ChatMessage("chat.cannotSend");
            chatmessage.getChatModifier().setColor(EnumChatFormat.RED);
            this.sendPacket(new PacketPlayOutChat(chatmessage));
         } else {
            this.chat(s, true);
         }

         boolean counted = true;

         for (String exclude : SpigotConfig.spamExclusions) {
            if (exclude != null && s.startsWith(exclude)) {
               counted = false;
               break;
            }
         }

         if (counted && chatSpamField.addAndGet(this, 20) > 200 && !this.minecraftServer.getPlayerList().isOp(this.player.getProfile())) {
            if (!isSync) {
               Waitable<Object> waitable = new Waitable<Object>() {
                  protected Object evaluate() {
                     PlayerConnection.this.disconnect((String)LanguageConfig.disconnectSpam.getValue());
                     return null;
                  }
               };
               this.minecraftServer.postToMainThread(waitable);

               try {
                  waitable.get();
               } catch (InterruptedException var14) {
                  Thread.currentThread().interrupt();
               } catch (ExecutionException var15) {
                  throw new RuntimeException(var15);
               }
            } else {
               this.disconnect((String)LanguageConfig.disconnectSpam.getValue());
            }
         }
      } else {
         ChatMessage chatmessage = new ChatMessage("chat.cannotSend");
         chatmessage.getChatModifier().setColor(EnumChatFormat.RED);
         this.sendPacket(new PacketPlayOutChat(chatmessage));
      }
   }

   public void chat(final String s, boolean async) {
      if (!s.isEmpty() && this.player.getChatFlags() != EntityHuman.EnumChatVisibility.HIDDEN) {
         if (!async && s.startsWith("/")) {
            if (!Bukkit.isPrimaryThread()) {
               MinecraftServer.LOGGER.log(Level.ERROR, "Command Dispatched Async: " + s);
               MinecraftServer.LOGGER
                  .log(Level.ERROR, "Please notify author of plugin causing this execution to fix this bug! see: http://bit.ly/1oSiM6C", new Throwable());
               Waitable<Object> wait = new Waitable<Object>() {
                  protected Object evaluate() {
                     PlayerConnection.this.chat(s, false);
                     return null;
                  }
               };
               this.minecraftServer.postToMainThread(wait);

               try {
                  wait.get();
                  return;
               } catch (InterruptedException var12) {
                  Thread.currentThread().interrupt();
               } catch (Exception var13) {
                  throw new RuntimeException("Exception processing chat command", var13.getCause());
               }
            }

            this.handleCommand(s);
         } else if (this.player.getChatFlags() != EntityHuman.EnumChatVisibility.SYSTEM) {
            Player player = this.getPlayer();
            AsyncPlayerChatEvent event = new AsyncPlayerChatEvent(async, player, s, new LazyPlayerSet());
            ConfigEdit configEdit = (ConfigEdit)Blaze.getInstance().getConfigEdits().get(player.getUniqueId());
            if (configEdit != null) {
               configEdit.handleChat(event);
            }

            KnockbackEdit edit = (KnockbackEdit)Blaze.getInstance().getKnockbackManager().getEditing().get(player.getUniqueId());
            if (edit != null) {
               edit.handleChat(event);
            }

            this.server.getPluginManager().callEvent(event);
            if (PlayerChatEvent.getHandlerList().getRegisteredListeners().length != 0) {
               final PlayerChatEvent queueEvent = new PlayerChatEvent(player, event.getMessage(), event.getFormat(), event.getRecipients());
               queueEvent.setCancelled(event.isCancelled());
               Waitable<Object> waitable = new Waitable<Object>() {
                  protected Object evaluate() {
                     Bukkit.getPluginManager().callEvent(queueEvent);
                     if (queueEvent.isCancelled()) {
                        return null;
                     } else {
                        String message = String.format(queueEvent.getFormat(), queueEvent.getPlayer().getDisplayName(), queueEvent.getMessage());
                        PlayerConnection.this.minecraftServer.console.sendMessage(message);
                        if (((LazyPlayerSet)queueEvent.getRecipients()).isLazy()) {
                           for (EntityPlayer player : PlayerConnection.this.minecraftServer.getPlayerList().players) {
                              player.sendMessage(CraftChatMessage.fromString(message));
                           }
                        } else {
                           for (Player player : queueEvent.getRecipients()) {
                              player.sendMessage(message);
                           }
                        }

                        return null;
                     }
                  }
               };
               if (async) {
                  this.minecraftServer.postToMainThread(waitable);
               } else {
                  waitable.run();
               }

               try {
                  waitable.get();
               } catch (InterruptedException var10) {
                  Thread.currentThread().interrupt();
               } catch (ExecutionException var11) {
                  throw new RuntimeException("Exception processing chat event", var11.getCause());
               }
            } else {
               if (event.isCancelled()) {
                  return;
               }

               String formatted = String.format(event.getFormat(), event.getPlayer().getDisplayName(), event.getMessage());
               this.minecraftServer.console.sendMessage(formatted);
               if (((LazyPlayerSet)event.getRecipients()).isLazy()) {
                  for (EntityPlayer recipient : this.minecraftServer.getPlayerList().players) {
                     recipient.sendMessage(CraftChatMessage.fromString(formatted));
                  }
               } else {
                  for (Player recipient : event.getRecipients()) {
                     recipient.sendMessage(formatted);
                  }
               }
            }
         }
      }
   }

   private void handleCommand(String s) {
      if (SpigotConfig.logCommands) {
         c.info("{} issued server command: {}", new Object[]{this.player.getName(), s});
      }

      CraftPlayer player = this.getPlayer();
      PlayerCommandPreprocessEvent event = new PlayerCommandPreprocessEvent(player, s, new LazyPlayerSet());
      this.server.getPluginManager().callEvent(event);
      this.getPlayer().getHandle().lastActivity = System.currentTimeMillis();
      if (!event.isCancelled()) {
         try {
            this.server.dispatchCommand(event.getPlayer(), event.getMessage().substring(1));
         } catch (org.bukkit.command.CommandException var5) {
            player.sendMessage(ChatColor.RED + "An internal error occurred while attempting to perform this command");
            java.util.logging.Logger.getLogger(PlayerConnection.class.getName()).log(java.util.logging.Level.SEVERE, null, var5);
         }
      }
   }

   @Override
   public void a(PacketPlayInArmAnimation packetplayinarmanimation) {
      if (!this.player.dead) {
         PlayerConnectionUtils.ensureMainThread(packetplayinarmanimation, this, this.player.u());
         this.player.resetIdleTimer();
         this.player.checkBackpacking();
         float f1 = this.player.pitch;
         float f2 = this.player.yaw;
         Vec3D vec3d = new Vec3D(this.player.locX, this.player.locY + this.player.getHeadHeight(), this.player.locZ);
         float f5 = -MathHelper.cos(-f1 * (float) (Math.PI / 180.0));
         double d3 = this.player.playerInteractManager.getGameMode() == WorldSettings.EnumGamemode.CREATIVE ? 5.0 : 4.5;
         MovingObjectPosition movingobjectposition = this.player
            .world
            .rayTrace(
               vec3d,
               vec3d.add(
                  MathHelper.sin(-f2 * (float) (Math.PI / 180.0) - (float) Math.PI) * f5 * d3,
                  MathHelper.sin(-f1 * (float) (Math.PI / 180.0)) * d3,
                  MathHelper.cos(-f2 * (float) (Math.PI / 180.0) - (float) Math.PI) * f5 * d3
               ),
               false
            );
         if (movingobjectposition == null || movingobjectposition.type != MovingObjectPosition.EnumMovingObjectType.BLOCK) {
            CraftEventFactory.callPlayerInteractEvent(this.player, Action.LEFT_CLICK_AIR, this.player.inventory.getItemInHand());
         }

         PlayerAnimationEvent event = new PlayerAnimationEvent(this.getPlayer());
         this.server.getPluginManager().callEvent(event);
         if (!event.isCancelled()) {
            this.player.bw();
         }
      }
   }

   @Override
   public void a(PacketPlayInEntityAction packetplayinentityaction) {
      PlayerConnectionUtils.ensureMainThread(packetplayinentityaction, this, this.player.u());
      if (!this.player.dead) {
         switch (packetplayinentityaction.b()) {
            case START_SNEAKING:
            case STOP_SNEAKING:
               PlayerToggleSneakEvent event = new PlayerToggleSneakEvent(
                  this.getPlayer(), packetplayinentityaction.b() == PacketPlayInEntityAction.EnumPlayerAction.START_SNEAKING
               );
               this.server.getPluginManager().callEvent(event);
               if (event.isCancelled()) {
                  return;
               }
            case STOP_SLEEPING:
            default:
               break;
            case START_SPRINTING:
            case STOP_SPRINTING:
               PlayerToggleSprintEvent e2 = new PlayerToggleSprintEvent(
                  this.getPlayer(), packetplayinentityaction.b() == PacketPlayInEntityAction.EnumPlayerAction.START_SPRINTING
               );
               this.server.getPluginManager().callEvent(e2);
               if (e2.isCancelled()) {
                  return;
               }
         }

         this.player.resetIdleTimer();
         switch (PlayerConnection.SyntheticClass_1.b[packetplayinentityaction.b().ordinal()]) {
            case 1:
               this.player.checkBackpacking();
               this.player.setSneaking(true);
               break;
            case 2:
               this.player.setSneaking(false);
               break;
            case 3:
               this.player.setSprinting(true);
               this.player.checkBackpacking();
               if (this.player.isBlocking() || (Boolean)BlazeConfig.patchDropEatBug.getValue() && this.player.isEating()) {
                  this.player.bU();
               }
               break;
            case 4:
               this.player.setSneaking(false);
               this.player.setSprinting(false);
               break;
            case 5:
               this.player.a(false, true, true);
               break;
            case 6:
               if (this.player.vehicle instanceof EntityHorse) {
                  ((EntityHorse)this.player.vehicle).v(packetplayinentityaction.c());
               }
               break;
            case 7:
               if (this.player.vehicle instanceof EntityHorse) {
                  ((EntityHorse)this.player.vehicle).g(this.player);
               }
               break;
            default:
               throw new IllegalArgumentException("Invalid client command!");
         }
      }
   }

   @Override
   public void a(PacketPlayInUseEntity packetplayinuseentity) {
      if (!this.player.dead) {
         PlayerConnectionUtils.ensureMainThread(packetplayinuseentity, this, this.player.u());
         WorldServer worldserver = this.minecraftServer.getWorldServer(this.player.dimension);
         Entity entity = packetplayinuseentity.a(worldserver);
         if (entity == this.player && !this.player.isSpectator()) {
            this.disconnect("Cannot interact with self!");
         } else {
            this.player.resetIdleTimer();
            if (entity != null) {
               if (packetplayinuseentity.a() == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK) {
                  if (entity instanceof EntityItem
                     || entity instanceof EntityExperienceOrb
                     || entity instanceof EntityArrow
                     || entity == this.player && !this.player.isSpectator()) {
                     this.disconnect("Attempting to attack an invalid entity");
                     this.minecraftServer.warning("Player " + this.player.getName() + " tried to attack an invalid entity");
                     return;
                  }

                  ItemStack attackItem = this.player.inventory.getItemInHand();
                  CombatResult combat = null;
                  if ((Boolean)BlazeConfig.combatLagCompensation.getValue()) {
                     combat = CombatValidator.validate(this.player, entity, packetplayinuseentity);
                     CombatMetrics.record(this.player, entity instanceof EntityPlayer ? (EntityPlayer)entity : null, combat);
                     if (!combat.accepted) {
                        return;
                     }
                  } else if (this.player.h(entity) >= 81.0) {
                     return;
                  }

                  this.player.attack(entity, combat);
                  if (attackItem != null && attackItem.count <= -1) {
                     this.player.updateInventory(this.player.activeContainer);
                  }
                  return;
               }

               double d0 = 81.0;
               if (this.player.h(entity) < d0) {
                  ItemStack itemInHand = this.player.inventory.getItemInHand();
                  if (packetplayinuseentity.a() == PacketPlayInUseEntity.EnumEntityUseAction.INTERACT
                     || packetplayinuseentity.a() == PacketPlayInUseEntity.EnumEntityUseAction.INTERACT_AT) {
                     boolean triggerLeashUpdate = itemInHand != null && itemInHand.getItem() == Items.LEAD && entity instanceof EntityInsentient;
                     Item origItem = this.player.inventory.getItemInHand() == null ? null : this.player.inventory.getItemInHand().getItem();
                     PlayerInteractEntityEvent event;
                     if (packetplayinuseentity.a() == PacketPlayInUseEntity.EnumEntityUseAction.INTERACT) {
                        event = new PlayerInteractEntityEvent(this.getPlayer(), entity.getBukkitEntity());
                     } else {
                        Vec3D target = packetplayinuseentity.b();
                        event = new PlayerInteractAtEntityEvent(this.getPlayer(), entity.getBukkitEntity(), new Vector(target.a, target.b, target.c));
                     }

                     this.server.getPluginManager().callEvent(event);
                     if (triggerLeashUpdate
                        && (
                           event.isCancelled()
                              || this.player.inventory.getItemInHand() == null
                              || this.player.inventory.getItemInHand().getItem() != Items.LEAD
                        )) {
                        this.sendPacket(new PacketPlayOutAttachEntity(1, entity, ((EntityInsentient)entity).getLeashHolder()));
                     }

                     if (event.isCancelled() || this.player.inventory.getItemInHand() == null || this.player.inventory.getItemInHand().getItem() != origItem) {
                        this.sendPacket(new PacketPlayOutEntityMetadata(entity.getId(), entity.datawatcher, true));
                     }

                     if (event.isCancelled()) {
                        return;
                     }
                  }

                  if (packetplayinuseentity.a() == PacketPlayInUseEntity.EnumEntityUseAction.INTERACT) {
                     this.player.u(entity);
                     if (itemInHand != null && itemInHand.count <= -1) {
                        this.player.updateInventory(this.player.activeContainer);
                     }
                  } else if (packetplayinuseentity.a() == PacketPlayInUseEntity.EnumEntityUseAction.INTERACT_AT) {
                     entity.a(this.player, packetplayinuseentity.b());
                     if (itemInHand != null && itemInHand.count <= -1) {
                        this.player.updateInventory(this.player.activeContainer);
                     }
                  }
               }
            }
         }
      }
   }

   @Override
   public void a(PacketPlayInClientCommand packetplayinclientcommand) {
      PlayerConnectionUtils.ensureMainThread(packetplayinclientcommand, this, this.player.u());
      this.player.resetIdleTimer();
      PacketPlayInClientCommand.EnumClientCommand packetplayinclientcommand_enumclientcommand = packetplayinclientcommand.a();
      switch (PlayerConnection.SyntheticClass_1.c[packetplayinclientcommand_enumclientcommand.ordinal()]) {
         case 1:
            if (this.player.viewingCredits) {
               this.minecraftServer.getPlayerList().changeDimension(this.player, 0, TeleportCause.END_PORTAL);
            } else if (this.player.u().getWorldData().isHardcore()) {
               GameProfileBanEntry gameprofilebanentry = new GameProfileBanEntry(
                  this.player.getProfile(), null, "(You just lost the game)", null, "Death in Hardcore"
               );
               this.minecraftServer.getPlayerList().getProfileBans().add(gameprofilebanentry);
               this.player.playerConnection.disconnect("You have died. Game over, man, it's game over!");
            } else if (this.player.getHealth() <= 0.0F) {
               this.player = this.minecraftServer.getPlayerList().moveToWorld(this.player, 0, false);
            }
            break;
         case 2:
            this.player.getStatisticManager().a(this.player);
            break;
         case 3:
            this.player.inventoryOpen = true;
      }
   }

   @Override
   public void a(PacketPlayInCloseWindow packetplayinclosewindow) {
      if (!this.player.dead) {
         PlayerConnectionUtils.ensureMainThread(packetplayinclosewindow, this, this.player.u());
         this.player.inventoryOpen = false;
         if (packetplayinclosewindow.id == this.player.activeContainer.windowId) {
            CraftEventFactory.handleInventoryCloseEvent(this.player);
            this.player.p();
            if (this.player.isAnimating()) {
               this.player.bU();
            }
         }
      }
   }

   @Override
   public void a(PacketPlayInWindowClick packetplayinwindowclick) {
      if (!this.player.dead) {
         PlayerConnectionUtils.ensureMainThread(packetplayinwindowclick, this, this.player.u());
         this.player.resetIdleTimer();
         if (this.player.activeContainer.windowId == packetplayinwindowclick.a() && this.player.activeContainer.c(this.player)) {
            int b = packetplayinwindowclick.b();
            boolean cancelled = this.player.isSpectator();
            if (b < 0 && b != -999 && b != -1) {
               return;
            }

            InventoryView inventory = this.player.activeContainer.getBukkitView();
            int maxSlots = inventory.countSlots();
            if (inventory.getBottomInventory().getType() == InventoryType.PLAYER && inventory.getTopInventory().getType() == InventoryType.CRAFTING) {
               maxSlots += 4;
            }

            if (b >= maxSlots) {
               return;
            }

            SlotType type = CraftInventoryView.getSlotType(inventory, b);
            ClickType click = ClickType.UNKNOWN;
            InventoryAction action = InventoryAction.UNKNOWN;
            ItemStack itemstack = null;
            int c = packetplayinwindowclick.c();
            int f = packetplayinwindowclick.f();
            if (b == -1) {
               type = SlotType.OUTSIDE;
               click = c == 0 ? ClickType.WINDOW_BORDER_LEFT : ClickType.WINDOW_BORDER_RIGHT;
               action = InventoryAction.NOTHING;
            } else if (f == 0) {
               if (c == 0) {
                  click = ClickType.LEFT;
               } else if (c == 1) {
                  click = ClickType.RIGHT;
               }

               if (c == 0 || c == 1) {
                  action = InventoryAction.NOTHING;
                  if (b < 0) {
                     if (this.player.inventory.getCarried() != null) {
                        action = c == 0 ? InventoryAction.DROP_ALL_CURSOR : InventoryAction.DROP_ONE_CURSOR;
                     }
                  } else {
                     Slot slot = this.player.activeContainer.getSlot(b);
                     if (slot != null) {
                        ItemStack clickedItem = slot.getItem();
                        ItemStack cursor = this.player.inventory.getCarried();
                        if (clickedItem == null) {
                           if (cursor != null) {
                              action = c == 0 ? InventoryAction.PLACE_ALL : InventoryAction.PLACE_ONE;
                           }
                        } else if (slot.isAllowed(this.player)) {
                           if (cursor == null) {
                              action = c == 0 ? InventoryAction.PICKUP_ALL : InventoryAction.PICKUP_HALF;
                           } else if (slot.isAllowed(cursor)) {
                              if (clickedItem.doMaterialsMatch(cursor) && ItemStack.equals(clickedItem, cursor)) {
                                 int toPlace = c == 0 ? cursor.count : 1;
                                 toPlace = Math.min(toPlace, clickedItem.getMaxStackSize() - clickedItem.count);
                                 toPlace = Math.min(toPlace, slot.inventory.getMaxStackSize() - clickedItem.count);
                                 if (toPlace == 1) {
                                    action = InventoryAction.PLACE_ONE;
                                 } else if (toPlace == cursor.count) {
                                    action = InventoryAction.PLACE_ALL;
                                 } else if (toPlace < 0) {
                                    action = toPlace != -1 ? InventoryAction.PICKUP_SOME : InventoryAction.PICKUP_ONE;
                                 } else if (toPlace != 0) {
                                    action = InventoryAction.PLACE_SOME;
                                 }
                              } else if (cursor.count <= slot.getMaxStackSize()) {
                                 action = InventoryAction.SWAP_WITH_CURSOR;
                              }
                           } else if (cursor.getItem() == clickedItem.getItem()
                              && (!cursor.usesData() || cursor.getData() == clickedItem.getData())
                              && ItemStack.equals(cursor, clickedItem)
                              && clickedItem.count >= 0
                              && clickedItem.count + cursor.count <= cursor.getMaxStackSize()) {
                              action = InventoryAction.PICKUP_ALL;
                           }
                        }
                     }
                  }
               }
            } else if (f == 1) {
               if (c == 0) {
                  click = ClickType.SHIFT_LEFT;
               } else if (c == 1) {
                  click = ClickType.SHIFT_RIGHT;
               }

               if (c == 0 || c == 1) {
                  if (b < 0) {
                     action = InventoryAction.NOTHING;
                  } else {
                     Slot slot = this.player.activeContainer.getSlot(b);
                     if (slot != null && slot.isAllowed(this.player) && slot.hasItem()) {
                        action = InventoryAction.MOVE_TO_OTHER_INVENTORY;
                     } else {
                        action = InventoryAction.NOTHING;
                     }
                  }
               }
            } else if (f == 2) {
               if (c >= 0 && c < 9) {
                  click = ClickType.NUMBER_KEY;
                  Slot clickedSlot = this.player.activeContainer.getSlot(b);
                  if (clickedSlot.isAllowed(this.player)) {
                     ItemStack hotbar = this.player.inventory.getItem(c);
                     boolean canCleanSwap = hotbar == null || clickedSlot.inventory == this.player.inventory && clickedSlot.isAllowed(hotbar);
                     if (clickedSlot.hasItem()) {
                        if (canCleanSwap) {
                           action = InventoryAction.HOTBAR_SWAP;
                        } else {
                           int firstEmptySlot = this.player.inventory.getFirstEmptySlotIndex();
                           if (firstEmptySlot > -1) {
                              action = InventoryAction.HOTBAR_MOVE_AND_READD;
                           } else {
                              action = InventoryAction.NOTHING;
                           }
                        }
                     } else if (!clickedSlot.hasItem() && hotbar != null && clickedSlot.isAllowed(hotbar)) {
                        action = InventoryAction.HOTBAR_SWAP;
                     } else {
                        action = InventoryAction.NOTHING;
                     }
                  } else {
                     action = InventoryAction.NOTHING;
                  }
               }
            } else if (f == 3) {
               if (c == 2) {
                  click = ClickType.MIDDLE;
                  if (b == -999) {
                     action = InventoryAction.NOTHING;
                  } else {
                     Slot slot = this.player.activeContainer.getSlot(b);
                     if (slot != null && slot.hasItem() && this.player.abilities.canInstantlyBuild && this.player.inventory.getCarried() == null) {
                        action = InventoryAction.CLONE_STACK;
                     } else {
                        action = InventoryAction.NOTHING;
                     }
                  }
               }
            } else if (f == 4) {
               if (b >= 0) {
                  if (c == 0) {
                     click = ClickType.DROP;
                     Slot slot = this.player.activeContainer.getSlot(b);
                     if (slot != null
                        && slot.hasItem()
                        && slot.isAllowed(this.player)
                        && slot.getItem() != null
                        && slot.getItem().getItem() != Item.getItemOf(Blocks.AIR)) {
                        action = InventoryAction.DROP_ONE_SLOT;
                     } else {
                        action = InventoryAction.NOTHING;
                     }
                  } else if (c == 1) {
                     click = ClickType.CONTROL_DROP;
                     Slot slot = this.player.activeContainer.getSlot(b);
                     if (slot != null
                        && slot.hasItem()
                        && slot.isAllowed(this.player)
                        && slot.getItem() != null
                        && slot.getItem().getItem() != Item.getItemOf(Blocks.AIR)) {
                        action = InventoryAction.DROP_ALL_SLOT;
                     } else {
                        action = InventoryAction.NOTHING;
                     }
                  }
               } else {
                  click = ClickType.LEFT;
                  if (c == 1) {
                     click = ClickType.RIGHT;
                  }

                  action = InventoryAction.NOTHING;
               }
            } else if (f == 5) {
               itemstack = this.player.activeContainer.clickItem(b, c, 5, this.player);
            } else if (f == 6) {
               click = ClickType.DOUBLE_CLICK;
               action = InventoryAction.NOTHING;
               if (packetplayinwindowclick.b() >= 0 && this.player.inventory.getCarried() != null) {
                  ItemStack cursor = this.player.inventory.getCarried();
                  if (inventory.getTopInventory().contains(org.bukkit.Material.getMaterial(Item.getId(cursor.getItem())))
                     || inventory.getBottomInventory().contains(org.bukkit.Material.getMaterial(Item.getId(cursor.getItem())))) {
                     action = InventoryAction.COLLECT_TO_CURSOR;
                  }
               }
            }

            if (f != 5) {
               InventoryClickEvent event;
               if (click == ClickType.NUMBER_KEY) {
                  event = new InventoryClickEvent(inventory, type, b, click, action, c);
               } else {
                  event = new InventoryClickEvent(inventory, type, b, click, action);
               }

               Inventory top = inventory.getTopInventory();
               if (packetplayinwindowclick.b() == 0 && top instanceof CraftingInventory) {
                  Recipe recipe = ((CraftingInventory)top).getRecipe();
                  if (recipe != null) {
                     if (click == ClickType.NUMBER_KEY) {
                        event = new CraftItemEvent(recipe, inventory, type, b, click, action, c);
                     } else {
                        event = new CraftItemEvent(recipe, inventory, type, b, click, action);
                     }
                  }
               }

               event.setCancelled(
                  cancelled || (Boolean)BlazeConfig.oldEnchantingSystem.getValue() && inventory.getType() == InventoryType.ENCHANTING && b == 1
               );
               this.server.getPluginManager().callEvent(event);
               Blaze.getInstance().getMenuManager().handleClick(event);
               switch (event.getResult()) {
                  case DENY:
                     switch (action) {
                        case NOTHING:
                        default:
                           break;
                        case PICKUP_ALL:
                        case MOVE_TO_OTHER_INVENTORY:
                        case HOTBAR_MOVE_AND_READD:
                        case HOTBAR_SWAP:
                        case COLLECT_TO_CURSOR:
                        case UNKNOWN:
                           this.player.updateInventory(this.player.activeContainer);
                           break;
                        case PICKUP_SOME:
                        case PICKUP_HALF:
                        case PICKUP_ONE:
                        case PLACE_ALL:
                        case PLACE_SOME:
                        case PLACE_ONE:
                        case SWAP_WITH_CURSOR:
                           this.sendPacket(new PacketPlayOutSetSlot(-1, -1, this.player.inventory.getCarried()));
                           this.sendPacket(new PacketPlayOutSetSlot(this.player.activeContainer.windowId, b, this.player.activeContainer.getSlot(b).getItem()));
                           break;
                        case DROP_ALL_CURSOR:
                        case DROP_ONE_CURSOR:
                        case CLONE_STACK:
                           this.sendPacket(new PacketPlayOutSetSlot(-1, -1, this.player.inventory.getCarried()));
                           break;
                        case DROP_ALL_SLOT:
                        case DROP_ONE_SLOT:
                           this.sendPacket(new PacketPlayOutSetSlot(this.player.activeContainer.windowId, b, this.player.activeContainer.getSlot(b).getItem()));
                     }

                     return;
                  case DEFAULT:
                  case ALLOW:
                     itemstack = this.player.activeContainer.clickItem(b, c, f, this.player);
                     if (itemstack != null
                        && (
                           itemstack.getItem() == Items.LAVA_BUCKET && PaperSpigotConfig.stackableLavaBuckets
                              || itemstack.getItem() == Items.WATER_BUCKET && PaperSpigotConfig.stackableWaterBuckets
                              || itemstack.getItem() == Items.MILK_BUCKET && PaperSpigotConfig.stackableMilkBuckets
                        )) {
                        if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                           this.player.updateInventory(this.player.activeContainer);
                        } else {
                           this.sendPacket(new PacketPlayOutSetSlot(-1, -1, this.player.inventory.getCarried()));
                           this.sendPacket(new PacketPlayOutSetSlot(this.player.activeContainer.windowId, b, this.player.activeContainer.getSlot(b).getItem()));
                        }
                     }
                  default:
                     if (event instanceof CraftItemEvent) {
                        this.player.updateInventory(this.player.activeContainer);
                     }
               }
            }

            if (ItemStack.matches(packetplayinwindowclick.e(), itemstack)) {
               this.sendPacket(new PacketPlayOutTransaction(packetplayinwindowclick.a(), packetplayinwindowclick.d(), true));
               this.player.g = true;
               this.player.activeContainer.b();
               this.player.broadcastCarriedItem();
               this.player.g = false;
            } else {
               this.n.put(this.player.activeContainer.windowId, packetplayinwindowclick.d());
               this.sendPacket(new PacketPlayOutTransaction(packetplayinwindowclick.a(), packetplayinwindowclick.d(), false));
               this.player.activeContainer.a(this.player, false);
               ArrayList<ItemStack> arraylist1 = Lists.newArrayList();

               for (int j = 0; j < this.player.activeContainer.c.size(); j++) {
                  arraylist1.add(this.player.activeContainer.c.get(j).getItem());
               }

               this.player.a(this.player.activeContainer, arraylist1);
            }
         }
      }
   }

   @Override
   public void a(PacketPlayInEnchantItem packetplayinenchantitem) {
      PlayerConnectionUtils.ensureMainThread(packetplayinenchantitem, this, this.player.u());
      this.player.resetIdleTimer();
      if (this.player.activeContainer.windowId == packetplayinenchantitem.a() && this.player.activeContainer.c(this.player) && !this.player.isSpectator()) {
         this.player.activeContainer.a(this.player, packetplayinenchantitem.b());
         this.player.activeContainer.b();
      }
   }

   @Override
   public void a(PacketPlayInSetCreativeSlot packetplayinsetcreativeslot) {
      PlayerConnectionUtils.ensureMainThread(packetplayinsetcreativeslot, this, this.player.u());
      if (this.player.playerInteractManager.isCreative()) {
         boolean flag = packetplayinsetcreativeslot.a() < 0;
         ItemStack itemstack = packetplayinsetcreativeslot.getItemStack();
         if (itemstack != null && itemstack.hasTag() && itemstack.getTag().hasKeyOfType("BlockEntityTag", 10)) {
            NBTTagCompound nbttagcompound = itemstack.getTag().getCompound("BlockEntityTag");
            if (nbttagcompound.hasKey("x") && nbttagcompound.hasKey("y") && nbttagcompound.hasKey("z")) {
               BlockPosition blockposition = new BlockPosition(nbttagcompound.getInt("x"), nbttagcompound.getInt("y"), nbttagcompound.getInt("z"));
               TileEntity tileentity = this.player.world.getTileEntity(blockposition);
               if (tileentity != null) {
                  NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                  tileentity.b(nbttagcompound1);
                  nbttagcompound1.remove("x");
                  nbttagcompound1.remove("y");
                  nbttagcompound1.remove("z");
                  itemstack.a("BlockEntityTag", nbttagcompound1);
               }
            }
         }

         boolean flag1 = packetplayinsetcreativeslot.a() >= 1 && packetplayinsetcreativeslot.a() < 36 + PlayerInventory.getHotbarSize();
         boolean flag2 = itemstack == null
            || itemstack.getItem() != null && (!invalidItems.contains(Item.getId(itemstack.getItem())) || !SpigotConfig.filterCreativeItems);
         boolean flag3 = itemstack == null || itemstack.getData() >= 0 && itemstack.count <= 64 && itemstack.count > 0;
         if (flag
            || flag1
               && !ItemStack.matches(
                  this.player.defaultContainer.getSlot(packetplayinsetcreativeslot.a()).getItem(), packetplayinsetcreativeslot.getItemStack()
               )) {
            HumanEntity player = this.player.getBukkitEntity();
            InventoryView inventory = new CraftInventoryView(player, player.getInventory(), this.player.defaultContainer);
            org.bukkit.inventory.ItemStack item = CraftItemStack.asBukkitCopy(packetplayinsetcreativeslot.getItemStack());
            SlotType type = SlotType.QUICKBAR;
            if (flag) {
               type = SlotType.OUTSIDE;
            } else if (packetplayinsetcreativeslot.a() < 36) {
               if (packetplayinsetcreativeslot.a() >= 5 && packetplayinsetcreativeslot.a() < 9) {
                  type = SlotType.ARMOR;
               } else {
                  type = SlotType.CONTAINER;
               }
            }

            InventoryCreativeEvent event = new InventoryCreativeEvent(inventory, type, flag ? -999 : packetplayinsetcreativeslot.a(), item);
            this.server.getPluginManager().callEvent(event);
            itemstack = CraftItemStack.asNMSCopy(event.getCursor());
            switch (event.getResult()) {
               case DENY:
                  if (packetplayinsetcreativeslot.a() >= 0) {
                     this.sendPacket(
                        new PacketPlayOutSetSlot(
                           this.player.defaultContainer.windowId,
                           packetplayinsetcreativeslot.a(),
                           this.player.defaultContainer.getSlot(packetplayinsetcreativeslot.a()).getItem()
                        )
                     );
                     this.sendPacket(new PacketPlayOutSetSlot(-1, -1, null));
                  }

                  return;
               case DEFAULT:
               default:
                  break;
               case ALLOW:
                  flag3 = true;
                  flag2 = true;
            }
         }

         if (flag1 && flag2 && flag3) {
            this.player.defaultContainer.setItem(packetplayinsetcreativeslot.a(), itemstack);
            this.player.defaultContainer.a(this.player, true);
         } else if (flag && flag2 && flag3 && this.m < 200) {
            this.m += 20;
            EntityItem entityitem = this.player.drop(itemstack, true);
            if (entityitem != null) {
               entityitem.j();
            }
         }
      }
   }

   @Override
   public void a(PacketPlayInTransaction packetplayintransaction) {
      if (!this.player.dead) {
         PlayerConnectionUtils.ensureMainThread(packetplayintransaction, this, this.player.u());
         short oshort = this.n.getOrDefault(this.player.activeContainer.windowId, (short)-32768);
         if (packetplayintransaction.b() == 32767) {
            this.player.inventoryOpen = true;
         }

         if (oshort != -32768
            && packetplayintransaction.b() == oshort
            && this.player.activeContainer.windowId == packetplayintransaction.a()
            && !this.player.activeContainer.c(this.player)
            && !this.player.isSpectator()) {
            this.player.activeContainer.a(this.player, true);
         }
      }
   }

   @Override
   public void a(PacketPlayInUpdateSign packetplayinupdatesign) {
      if (!this.player.dead) {
         PlayerConnectionUtils.ensureMainThread(packetplayinupdatesign, this, this.player.u());
         this.player.resetIdleTimer();
         WorldServer worldserver = this.minecraftServer.getWorldServer(this.player.dimension);
         BlockPosition blockposition = packetplayinupdatesign.a();
         if (worldserver.isLoaded(blockposition)) {
            TileEntity tileentity = worldserver.getTileEntity(blockposition);
            if (!(tileentity instanceof TileEntitySign)) {
               return;
            }

            TileEntitySign tileentitysign = (TileEntitySign)tileentity;
            if (!tileentitysign.b() || tileentitysign.c() != this.player) {
               this.minecraftServer.warning("Player " + this.player.getName() + " just tried to change non-editable sign");
               this.sendPacket(new PacketPlayOutUpdateSign(tileentity.world, packetplayinupdatesign.a(), tileentitysign.lines));
               return;
            }

            IChatBaseComponent[] aichatbasecomponent = packetplayinupdatesign.b();
            Player player = this.server.getPlayer(this.player);
            String[] lines = new String[4];

            for (int i = 0; i < aichatbasecomponent.length; i++) {
               String line = EnumChatFormat.a(aichatbasecomponent[i].c());
               if (line.getBytes(StandardCharsets.UTF_8).length <= 47) {
                  lines[i] = line;
               }
            }

            BlockPosition a = packetplayinupdatesign.a();
            SignChangeEvent event = new SignChangeEvent(player.getWorld().getBlockAt(a.getX(), a.getY(), a.getZ()), this.server.getPlayer(this.player), lines);
            this.server.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
               System.arraycopy(CraftSign.sanitizeLines(event.getLines()), 0, tileentitysign.lines, 0, 4);
               tileentitysign.isEditable = false;
            }

            tileentitysign.update();
            worldserver.notify(blockposition);
         }
      }
   }

   @Override
   public void a(PacketPlayInKeepAlive packetplayinkeepalive) {
      if (packetplayinkeepalive.a() == this.i) {
         this.pingTracker.onKeepAliveResponse(this.i, System.nanoTime());
         this.player.ping = this.pingTracker.getPingMillis();
      }
   }

   private long d() {
      return System.nanoTime() / 1000000L;
   }

   @Override
   public void a(PacketPlayInAbilities packetplayinabilities) {
      PlayerConnectionUtils.ensureMainThread(packetplayinabilities, this, this.player.u());
      if (this.player.abilities.canFly && this.player.abilities.isFlying != packetplayinabilities.isFlying()) {
         PlayerToggleFlightEvent event = new PlayerToggleFlightEvent(this.server.getPlayer(this.player), packetplayinabilities.isFlying());
         this.server.getPluginManager().callEvent(event);
         if (!event.isCancelled()) {
            this.player.abilities.isFlying = packetplayinabilities.isFlying();
         } else {
            this.player.updateAbilities();
         }
      }
   }

   @Override
   public void a(PacketPlayInTabComplete packetplayintabcomplete) {
      PlayerConnectionUtils.ensureMainThread(packetplayintabcomplete, this, this.player.u());
      if (chatSpamField.addAndGet(this, 10) > 500 && !this.minecraftServer.getPlayerList().isOp(this.player.getProfile())) {
         this.disconnect((String)LanguageConfig.disconnectSpam.getValue());
      } else {
         List<String> arraylist = this.minecraftServer.tabCompleteCommand(this.player, packetplayintabcomplete.a(), packetplayintabcomplete.b());
         this.sendPacket(new PacketPlayOutTabComplete(arraylist.toArray(new String[0])));
      }
   }

   @Override
   public void a(PacketPlayInSettings packetplayinsettings) {
      PlayerConnectionUtils.ensureMainThread(packetplayinsettings, this, this.player.u());
      this.player.a(packetplayinsettings);
   }

   @Override
   public void a(PacketPlayInCustomPayload packetplayincustompayload) {
      PlayerConnectionUtils.ensureMainThread(packetplayincustompayload, this, this.player.u());
      PacketDataSerializer pds = packetplayincustompayload.b();
      String tag = packetplayincustompayload.a();
      int capacity = pds.capacity();
      if (tag == null || tag.trim().isEmpty()) {
         this.disconnect("Invalid payload tag!");
      } else if (capacity <= 8192 && capacity >= 1) {
         if (pds.refCnt() < 1) {
            this.disconnect("refCnt is lower than 1! (" + pds.refCnt() + ")");
         } else if (pds.readableBytes() > 8192) {
            this.disconnect("Too big packet! (" + pds.readableBytes() + ")");
         } else {
            label177: {
               switch (tag.hashCode()) {
                  case -751882236:
                     if (tag.equals("MC|ItemName")) {
                        if (this.player.activeContainer instanceof ContainerAnvil) {
                           ContainerAnvil containeranvil = (ContainerAnvil)this.player.activeContainer;
                           if (pds.readableBytes() >= 1) {
                              String s1 = SharedConstants.a(pds.c(32767));
                              if (s1.length() <= 30) {
                                 containeranvil.a(s1);
                              }
                           } else {
                              containeranvil.a("");
                           }
                        }
                        break label177;
                     }
                     break;
                  case -592728129:
                     if (tag.equals("MC|AdvCdm")) {
                        if (!this.minecraftServer.getEnableCommandBlock()) {
                           this.player.sendMessage(new ChatMessage("advMode.notEnabled"));
                           break label177;
                        } else if (this.player.getBukkitEntity().isOp() && this.player.abilities.canInstantlyBuild) {
                           try {
                              byte b0 = pds.readByte();
                              CommandBlockListenerAbstract commandblocklistenerabstract = null;
                              if (b0 == 0) {
                                 TileEntity tileentity = this.player.world.getTileEntity(new BlockPosition(pds.readInt(), pds.readInt(), pds.readInt()));
                                 if (tileentity instanceof TileEntityCommand) {
                                    commandblocklistenerabstract = ((TileEntityCommand)tileentity).getCommandBlock();
                                 }
                              } else if (b0 == 1) {
                                 Entity entity = this.player.world.a(pds.readInt());
                                 if (entity instanceof EntityMinecartCommandBlock) {
                                    commandblocklistenerabstract = ((EntityMinecartCommandBlock)entity).getCommandBlock();
                                 }
                              }

                              String s = pds.c(pds.readableBytes());
                              boolean flag = pds.readBoolean();
                              if (commandblocklistenerabstract != null) {
                                 commandblocklistenerabstract.setCommand(s);
                                 commandblocklistenerabstract.a(flag);
                                 if (!flag) {
                                    commandblocklistenerabstract.b((IChatBaseComponent)null);
                                 }

                                 commandblocklistenerabstract.h();
                                 this.player.sendMessage(new ChatMessage("advMode.setCommand.success", s));
                              }
                              break label177;
                           } catch (Exception var12) {
                              c.error("Couldn't set command block", var12);
                              this.disconnect("Invalid CommandBlock data!");
                              return;
                           }
                        } else {
                           this.player.sendMessage(new ChatMessage("advMode.notAllowed"));
                           break label177;
                        }
                     }
                     break;
                  case -563769974:
                     if (tag.equals("MC|Beacon")) {
                        if (this.player.activeContainer instanceof ContainerBeacon) {
                           try {
                              ContainerBeacon containerbeacon = (ContainerBeacon)this.player.activeContainer;
                              Slot slot = containerbeacon.getSlot(0);
                              if (slot.hasItem()) {
                                 slot.a(1);
                                 IInventory iinventory = containerbeacon.e();
                                 iinventory.b(1, pds.readInt());
                                 iinventory.b(2, pds.readInt());
                                 iinventory.update();
                              }
                           } catch (Exception var11) {
                              c.error("Couldn't set beacon", var11);
                              this.disconnect("Invalid beacon data!");
                              return;
                           }
                        }
                        break label177;
                     }
                     break;
                  case -296231034:
                     if (tag.equals("MC|BEdit")) {
                        PacketDataSerializer packetdataserializer = new PacketDataSerializer(Unpooled.wrappedBuffer(pds));

                        try {
                           ItemStack itemstackx = packetdataserializer.i();
                           if (itemstackx == null) {
                              return;
                           }

                           if (!ItemBookAndQuill.b(itemstackx.getTag())) {
                              throw new IOException("Invalid book tag!");
                           }

                           ItemStack itemstack1 = this.player.inventory.getItemInHand();
                           if (itemstack1 != null && itemstackx.getItem() == Items.WRITABLE_BOOK && itemstackx.getItem() == itemstack1.getItem()) {
                              ItemStack newBook = itemstack1.cloneItemStack();
                              if (!newBook.hasTag()) {
                                 newBook.setTag(new NBTTagCompound());
                              }

                              newBook.getTag().set("pages", itemstackx.getTag().getList("pages", 8));
                           }
                           break label177;
                        } catch (Exception var15) {
                           c.error("Couldn't handle book info", var15);
                           this.disconnect("Invalid book data!");
                           return;
                        }
                     }
                     break;
                  case -295809223:
                     if (tag.equals("MC|BSign")) {
                        PacketDataSerializer packetdataserializer = new PacketDataSerializer(Unpooled.wrappedBuffer(pds));

                        try {
                           ItemStack itemstack = packetdataserializer.i();
                           if (itemstack == null) {
                              return;
                           }

                           if (!ItemWrittenBook.b(itemstack.getTag())) {
                              throw new IOException("Invalid book tag!");
                           }

                           ItemStack itemstack1 = this.player.inventory.getItemInHand();
                           if (itemstack1 != null && itemstack.getItem() == Items.WRITTEN_BOOK && itemstack1.getItem() == Items.WRITABLE_BOOK) {
                              ItemStack newBook = itemstack1.cloneItemStack();
                              NBTTagCompound tag1;
                              if (!newBook.hasTag()) {
                                 newBook.setTag(tag1 = new NBTTagCompound());
                              } else {
                                 tag1 = newBook.getTag();
                              }

                              tag1.set("author", new NBTTagString(this.player.getName()));
                              tag1.set("title", new NBTTagString(itemstack.getTag().getString("title")));
                              tag1.set("pages", itemstack.getTag().getList("pages", 8));
                              newBook.setItem(Items.WRITTEN_BOOK);
                              CraftEventFactory.handleEditBookEvent(this.player, newBook);
                           }
                           break label177;
                        } catch (Exception var14) {
                           c.error("Couldn't sign book", var14);
                           this.disconnect("Invalid book data!");
                           return;
                        }
                     }
                     break;
                  case -278283530:
                     if (tag.equals("MC|TrSel")) {
                        try {
                           Container container = this.player.activeContainer;
                           if (container instanceof ContainerMerchant) {
                              ((ContainerMerchant)container).d(pds.readInt());
                           }
                           break label177;
                        } catch (Exception var13) {
                           c.error("Couldn't select trade", var13);
                           this.disconnect("Invalid trade data!");
                           return;
                        }
                     }
                     break;
                  case 92413603:
                     if (tag.equals("REGISTER")) {
                        String[] channels = pds.toString(Charsets.UTF_8).split("\u0000");
                        if (channels.length > 124) {
                           this.disconnect("Too many channels");
                           return;
                        }

                        String[] tag1 = channels;
                        int var37 = channels.length;
                        int var30 = 0;

                        while (true) {
                           if (var30 >= var37) {
                              break label177;
                           }

                           String channel = tag1[var30];
                           this.getPlayer().addChannel(channel);
                           var30++;
                        }
                     }
                     break;
                  case 1321107516:
                     if (tag.equals("UNREGISTER")) {
                        String[] flag;
                        int iinventory = (flag = pds.toString(Charsets.UTF_8).split("\u0000")).length;
                        int channel = 0;

                        while (true) {
                           if (channel >= iinventory) {
                              break label177;
                           }

                           String channelx = flag[channel];
                           this.getPlayer().removeChannel(channelx);
                           channel++;
                        }
                     }
               }

               byte[] data = new byte[pds.readableBytes()];
               pds.readBytes(data);
               this.server.getMessenger().dispatchIncomingMessage(this.player.getBukkitEntity(), tag, data);
            }

            pds.release();
         }
      } else {
         this.disconnect("Invaid payload capacity! (" + capacity + ")");
      }
   }

   public boolean isDisconnected() {
      return !this.player.joining && !this.networkManager.channel.config().isAutoRead();
   }

   static class SyntheticClass_1 {
      static final int[] a;
      static final int[] b;
      static final int[] c = new int[PacketPlayInClientCommand.EnumClientCommand.values().length];

      static {
         try {
            c[PacketPlayInClientCommand.EnumClientCommand.PERFORM_RESPAWN.ordinal()] = 1;
         } catch (NoSuchFieldError var16) {
         }

         try {
            c[PacketPlayInClientCommand.EnumClientCommand.REQUEST_STATS.ordinal()] = 2;
         } catch (NoSuchFieldError var15) {
         }

         try {
            c[PacketPlayInClientCommand.EnumClientCommand.OPEN_INVENTORY_ACHIEVEMENT.ordinal()] = 3;
         } catch (NoSuchFieldError var14) {
         }

         b = new int[PacketPlayInEntityAction.EnumPlayerAction.values().length];

         try {
            b[PacketPlayInEntityAction.EnumPlayerAction.START_SNEAKING.ordinal()] = 1;
         } catch (NoSuchFieldError var13) {
         }

         try {
            b[PacketPlayInEntityAction.EnumPlayerAction.STOP_SNEAKING.ordinal()] = 2;
         } catch (NoSuchFieldError var12) {
         }

         try {
            b[PacketPlayInEntityAction.EnumPlayerAction.START_SPRINTING.ordinal()] = 3;
         } catch (NoSuchFieldError var11) {
         }

         try {
            b[PacketPlayInEntityAction.EnumPlayerAction.STOP_SPRINTING.ordinal()] = 4;
         } catch (NoSuchFieldError var10) {
         }

         try {
            b[PacketPlayInEntityAction.EnumPlayerAction.STOP_SLEEPING.ordinal()] = 5;
         } catch (NoSuchFieldError var9) {
         }

         try {
            b[PacketPlayInEntityAction.EnumPlayerAction.RIDING_JUMP.ordinal()] = 6;
         } catch (NoSuchFieldError var8) {
         }

         try {
            b[PacketPlayInEntityAction.EnumPlayerAction.OPEN_INVENTORY.ordinal()] = 7;
         } catch (NoSuchFieldError var7) {
         }

         a = new int[PacketPlayInBlockDig.EnumPlayerDigType.values().length];

         try {
            a[PacketPlayInBlockDig.EnumPlayerDigType.DROP_ITEM.ordinal()] = 1;
         } catch (NoSuchFieldError var6) {
         }

         try {
            a[PacketPlayInBlockDig.EnumPlayerDigType.DROP_ALL_ITEMS.ordinal()] = 2;
         } catch (NoSuchFieldError var5) {
         }

         try {
            a[PacketPlayInBlockDig.EnumPlayerDigType.RELEASE_USE_ITEM.ordinal()] = 3;
         } catch (NoSuchFieldError var4) {
         }

         try {
            a[PacketPlayInBlockDig.EnumPlayerDigType.START_DESTROY_BLOCK.ordinal()] = 4;
         } catch (NoSuchFieldError var3) {
         }

         try {
            a[PacketPlayInBlockDig.EnumPlayerDigType.ABORT_DESTROY_BLOCK.ordinal()] = 5;
         } catch (NoSuchFieldError var2) {
         }

         try {
            a[PacketPlayInBlockDig.EnumPlayerDigType.STOP_DESTROY_BLOCK.ordinal()] = 6;
         } catch (NoSuchFieldError var1) {
         }
      }
   }
}
