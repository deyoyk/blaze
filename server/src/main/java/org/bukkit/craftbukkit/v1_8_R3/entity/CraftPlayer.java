package org.bukkit.craftbukkit.v1_8_R3.entity;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.mojang.authlib.GameProfile;
import lol.deyo.blaze.Blaze;
import lol.deyo.blaze.api.knockback.IKnockback;
import lol.deyo.blaze.knockback.Knockback;
import io.netty.buffer.Unpooled;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.jafama.FastMath;
import net.md_5.bungee.api.chat.BaseComponent;
import net.minecraft.server.v1_8_R3.AttributeInstance;
import net.minecraft.server.v1_8_R3.AttributeMapServer;
import net.minecraft.server.v1_8_R3.AttributeModifiable;
import net.minecraft.server.v1_8_R3.AttributeRanged;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.Container;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.EntityTracker;
import net.minecraft.server.v1_8_R3.EntityTrackerEntry;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.IAttribute;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.MapIcon;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketDataSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockChange;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutCustomPayload;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityVelocity;
import net.minecraft.server.v1_8_R3.PacketPlayOutGameStateChange;
import net.minecraft.server.v1_8_R3.PacketPlayOutMap;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedSoundEffect;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnPosition;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutUpdateAttributes;
import net.minecraft.server.v1_8_R3.PacketPlayOutUpdateHealth;
import net.minecraft.server.v1_8_R3.PacketPlayOutUpdateSign;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldEvent;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import net.minecraft.server.v1_8_R3.WorldServer;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;
import net.minecraft.server.v1_8_R3.WorldSettings.EnumGamemode;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ManuallyAbandonedConversationCanceller;
import org.bukkit.craftbukkit.v1_8_R3.CraftEffect;
import org.bukkit.craftbukkit.v1_8_R3.CraftOfflinePlayer;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftSound;
import org.bukkit.craftbukkit.v1_8_R3.CraftStatistic;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftSign;
import org.bukkit.craftbukkit.v1_8_R3.conversations.ConversationTracker;
import org.bukkit.craftbukkit.v1_8_R3.map.CraftMapView;
import org.bukkit.craftbukkit.v1_8_R3.map.RenderData;
import org.bukkit.craftbukkit.v1_8_R3.scoreboard.CraftScoreboard;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftChatMessage;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerUnregisterChannelEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.map.MapCursor;
import org.bukkit.map.MapView;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.StandardMessenger;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;
import org.github.paperspigot.Title;
import org.spigotmc.AsyncCatcher;

@DelegateDeserialization(CraftOfflinePlayer.class)
public class CraftPlayer extends CraftHumanEntity implements Player {
   private long firstPlayed;
   private long lastPlayed = 0L;
   private boolean hasPlayedBefore = false;
   private final ConversationTracker conversationTracker = new ConversationTracker();
   private final Set<String> channels = new HashSet<>();
   private final Set<UUID> hiddenPlayers = new HashSet<>();
   private int hash = 0;
   private double health = 20.0;
   private boolean scaledHealth = false;
   private double healthScale = 20.0;
   private final Player.Spigot spigot = new Player.Spigot() {
      @Override
      public InetSocketAddress getRawAddress() {
         return (InetSocketAddress)CraftPlayer.this.getHandle().playerConnection.networkManager.getRawAddress();
      }

      @Override
      public boolean getCollidesWithEntities() {
         return CraftPlayer.this.getHandle().collidesWithEntities;
      }

      @Override
      public void setCollidesWithEntities(boolean collides) {
         CraftPlayer.this.getHandle().collidesWithEntities = collides;
         CraftPlayer.this.getHandle().k = collides;
      }

      @Override
      public void respawn() {
         if (CraftPlayer.this.getHealth() <= 0.0 && CraftPlayer.this.isOnline()) {
            CraftPlayer.this.server.getServer().getPlayerList().moveToWorld(CraftPlayer.this.getHandle(), 0, false);
         }
      }

      @Override
      public void playEffect(
         org.bukkit.Location location,
         org.bukkit.Effect effect,
         int id,
         int data,
         float offsetX,
         float offsetY,
         float offsetZ,
         float speed,
         int particleCount,
         int radius
      ) {
         Validate.notNull(location, "Location cannot be null");
         Validate.notNull(effect, "Effect cannot be null");
         Validate.notNull(location.getWorld(), "World cannot be null");
         Packet packet;
         if (effect.getType() != org.bukkit.Effect.Type.PARTICLE) {
            int packetData = effect.getId();
            packet = new PacketPlayOutWorldEvent(packetData, new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()), id, false);
         } else {
            int[] extra = null;
            EnumParticle particle;
            if ((particle = (EnumParticle)EnumParticle.EFFECT_TO_PARTICLE.get(effect)) != null && effect.getData() != null) {
               if (effect.getData().equals(org.bukkit.Material.class)) {
                  extra = new int[]{id};
               } else {
                  extra = new int[]{data << 12 | id & 4095};
               }
            }

            if (extra == null) {
               extra = EnumParticle.EMPTY_ARRAY;
            }

            packet = new PacketPlayOutWorldParticles(
               particle, true, (float)location.getX(), (float)location.getY(), (float)location.getZ(), offsetX, offsetY, offsetZ, speed, particleCount, extra
            );
         }

         radius *= radius;
         if (CraftPlayer.this.getHandle().playerConnection != null) {
            if (location.getWorld().equals(CraftPlayer.this.getWorld())) {
               int distance = (int)CraftPlayer.this.getLocation().distanceSquared(location);
               if (distance <= radius) {
                  CraftPlayer.this.getHandle().playerConnection.sendPacket(packet);
               }
            }
         }
      }

      @Override
      public String getLocale() {
         return CraftPlayer.this.getHandle().locale;
      }

      @Override
      public Set<Player> getHiddenPlayers() {
         Set<Player> players = new HashSet<>();

         for (UUID uuid : CraftPlayer.this.hiddenPlayers) {
            players.add(CraftPlayer.this.getServer().getPlayer(uuid));
         }

         return Collections.unmodifiableSet(players);
      }

      @Override
      public void sendMessage(BaseComponent component) {
         this.sendMessage(component);
      }

      @Override
      public void sendMessage(BaseComponent... components) {
         if (CraftPlayer.this.getHandle().playerConnection != null) {
            PacketPlayOutChat packet = new PacketPlayOutChat();
            packet.components = components;
            CraftPlayer.this.getHandle().playerConnection.sendPacket(packet);
         }
      }

      @Override
      public boolean getAffectsSpawning() {
         return CraftPlayer.this.getHandle().affectsSpawning;
      }

      @Override
      public void setAffectsSpawning(boolean affects) {
         CraftPlayer.this.getHandle().affectsSpawning = affects;
      }

      @Override
      public int getViewDistance() {
         return CraftPlayer.this.getHandle().viewDistance;
      }

      @Override
      public void setViewDistance(int viewDistance) {
         ((WorldServer)CraftPlayer.this.getHandle().world).getPlayerChunkMap().updateViewDistance(CraftPlayer.this.getHandle(), viewDistance);
      }

      @Override
      public int getPing() {
         return CraftPlayer.this.getHandle().ping;
      }
   };

   public CraftPlayer(CraftServer server, EntityPlayer entity) {
      super(server, entity);
      this.firstPlayed = System.currentTimeMillis();
   }

   @Override
   public void sendPacket(Object packet) {
      if (packet instanceof Packet) {
         this.getHandle().playerConnection.sendPacket((Packet)packet);
      }
   }

   @Override
   public void setKnockbackProfile(IKnockback knockbackProfile) {
      this.getHandle().setKnockbackProfile((Knockback)knockbackProfile);
   }

   @Override
   public IKnockback getKnockbackProfile() {
      return this.getHandle().getKnockbackProfile();
   }

   @Override
   public void setVelocity(Vector vel) {
      this.setVelocityDirect(vel);
      PlayerVelocityEvent event = new PlayerVelocityEvent(this, vel.clone());
      this.getServer().getPluginManager().callEvent(event);
      if (!event.isCancelled()) {
         this.setVelocityDirect(event.getVelocity());
         this.getHandle().playerConnection.sendPacket(new PacketPlayOutEntityVelocity(this.getHandle()));
      }
   }

   public void setVelocityDirect(Vector vel) {
      this.setVelocityDirect(vel, false);
   }

   public void setVelocityDirect(Vector vel, boolean changeFallDistance) {
      this.entity.motX = vel.getX();
      this.entity.motY = vel.getY();
      this.entity.motZ = vel.getZ();
      if (changeFallDistance && this.entity.motY > 0.0) {
         this.entity.fallDistance = 0.0F;
      }
   }

   public GameProfile getProfile() {
      return this.getHandle().getProfile();
   }

   @Override
   public boolean isOp() {
      return this.server.getHandle().isOp(this.getProfile());
   }

   @Override
   public void setOp(boolean value) {
      if (value != this.isOp()) {
         if (value) {
            this.server.getHandle().addOp(this.getProfile());
         } else {
            this.server.getHandle().removeOp(this.getProfile());
         }

         this.perm.recalculatePermissions();
      }
   }

   @Override
   public boolean isOnline() {
      return this.server.getPlayer(this.getUniqueId()) != null;
   }

   @Override
   public InetSocketAddress getAddress() {
      if (this.getHandle().playerConnection == null) {
         return null;
      } else {
         SocketAddress addr = this.getHandle().playerConnection.networkManager.getSocketAddress();
         return addr instanceof InetSocketAddress ? (InetSocketAddress)addr : null;
      }
   }

   @Override
   public double getEyeHeight() {
      return this.getEyeHeight(false);
   }

   @Override
   public double getEyeHeight(boolean ignoreSneaking) {
      if (ignoreSneaking) {
         return 1.62;
      } else {
         return this.isSneaking() ? 1.54 : 1.62;
      }
   }

   @Override
   public void sendRawMessage(String message) {
      if (this.getHandle().playerConnection != null) {
         IChatBaseComponent[] var5;
         for (IChatBaseComponent component : var5 = CraftChatMessage.fromString(message)) {
            this.getHandle().playerConnection.sendPacket(new PacketPlayOutChat(component));
         }
      }
   }

   @Override
   public void sendMessage(String message) {
      if (!this.conversationTracker.isConversingModaly()) {
         this.sendRawMessage(message);
      }
   }

   @Override
   public void sendMessage(String[] messages) {
      for (String message : messages) {
         this.sendMessage(message);
      }
   }

   @Override
   public void sendMessage(BaseComponent component) {
      this.sendMessage(component);
   }

   @Override
   public void sendMessage(BaseComponent... components) {
      if (this.getHandle().playerConnection != null) {
         PacketPlayOutChat packet = new PacketPlayOutChat();
         packet.components = components;
         this.getHandle().playerConnection.sendPacket(packet);
      }
   }

   public void sendFormattedMessage(String message, Object... params) {
      if (this.getHandle().playerConnection != null) {
         message = MessageFormat.format(message, params);

         IChatBaseComponent[] var6;
         for (IChatBaseComponent component : var6 = CraftChatMessage.fromString(message)) {
            this.getHandle().playerConnection.sendPacket(new PacketPlayOutChat(component));
         }
      }
   }

   @Override
   public void setPlayerListHeaderFooter(BaseComponent[] header, BaseComponent[] footer) {
      PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();
      packet.header = header;
      packet.footer = footer;
      this.getHandle().playerConnection.sendPacket(packet);
   }

   @Override
   public void setPlayerListHeaderFooter(BaseComponent header, BaseComponent footer) {
      this.setPlayerListHeaderFooter(header == null ? null : new BaseComponent[]{header}, footer == null ? null : new BaseComponent[]{footer});
   }

   @Override
   public void setTitleTimes(int fadeInTicks, int stayTicks, int fadeOutTicks) {
      this.getHandle().playerConnection.sendPacket(new PacketPlayOutTitle(EnumTitleAction.TIMES, (IChatBaseComponent)null, fadeInTicks, stayTicks, fadeOutTicks));
   }

   @Override
   public void setSubtitle(BaseComponent[] subtitle) {
      this.getHandle().playerConnection.sendPacket(new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, subtitle, 0, 0, 0));
   }

   @Override
   public void setSubtitle(BaseComponent subtitle) {
      this.setSubtitle(new BaseComponent[]{subtitle});
   }

   @Override
   public void showTitle(BaseComponent[] title) {
      this.getHandle().playerConnection.sendPacket(new PacketPlayOutTitle(EnumTitleAction.TITLE, title, 0, 0, 0));
   }

   @Override
   public void showTitle(BaseComponent title) {
      this.showTitle(new BaseComponent[]{title});
   }

   @Override
   public void showTitle(BaseComponent[] title, BaseComponent[] subtitle, int fadeInTicks, int stayTicks, int fadeOutTicks) {
      this.setTitleTimes(fadeInTicks, stayTicks, fadeOutTicks);
      this.setSubtitle(subtitle);
      this.showTitle(title);
   }

   @Override
   public void showTitle(BaseComponent title, BaseComponent subtitle, int fadeInTicks, int stayTicks, int fadeOutTicks) {
      this.setTitleTimes(fadeInTicks, stayTicks, fadeOutTicks);
      this.setSubtitle(subtitle);
      this.showTitle(title);
   }

   @Override
   public void sendTitle(Title title) {
      Preconditions.checkNotNull(title, "Title is null");
      this.setTitleTimes(title.getFadeIn(), title.getStay(), title.getFadeOut());
      this.setSubtitle(title.getSubtitle() == null ? new BaseComponent[0] : title.getSubtitle());
      this.showTitle(title.getTitle());
   }

   @Override
   public void updateTitle(Title title) {
      Preconditions.checkNotNull(title, "Title is null");
      this.setTitleTimes(title.getFadeIn(), title.getStay(), title.getFadeOut());
      if (title.getSubtitle() != null) {
         this.setSubtitle(title.getSubtitle());
      }

      this.showTitle(title.getTitle());
   }

   @Override
   public void hideTitle() {
      this.getHandle().playerConnection.sendPacket(new PacketPlayOutTitle(EnumTitleAction.CLEAR, (IChatBaseComponent)null, 0, 0, 0));
   }

   @Override
   public String getDisplayName() {
      return this.getHandle().displayName;
   }

   @Override
   public void setDisplayName(String name) {
      this.getHandle().displayName = name == null ? this.getName() : name;
   }

   @Override
   public String getPlayerListName() {
      return this.getHandle().listName == null ? this.getName() : CraftChatMessage.fromComponent(this.getHandle().listName);
   }

   @Override
   public void setPlayerListName(String name) {
      if (name == null) {
         name = this.getName();
      }

      this.getHandle().listName = name.equals(this.getName()) ? null : CraftChatMessage.fromString(name)[0];

      for (EntityPlayer player : this.server.getHandle().players) {
         if (player.getBukkitEntity().canSee(this)) {
            player.playerConnection.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.UPDATE_DISPLAY_NAME, new EntityPlayer[]{this.getHandle()}));
         }
      }
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof org.bukkit.OfflinePlayer)) {
         return false;
      } else {
         org.bukkit.OfflinePlayer other = (org.bukkit.OfflinePlayer)obj;
         if (this.getUniqueId() != null && other.getUniqueId() != null) {
            boolean uuidEquals = this.getUniqueId().equals(other.getUniqueId());
            boolean idEquals = true;
            if (other instanceof CraftPlayer) {
               idEquals = this.getEntityId() == ((CraftPlayer)other).getEntityId();
            }

            return uuidEquals && idEquals;
         } else {
            return false;
         }
      }
   }

   @Override
   public void kickPlayer(String message) {
      AsyncCatcher.catchOp("player kick");
      if (this.getHandle().playerConnection != null) {
         this.getHandle().playerConnection.disconnect(message == null ? "" : message);
      }
   }

   @Override
   public void setCompassTarget(org.bukkit.Location loc) {
      if (this.getHandle().playerConnection != null) {
         this.getHandle().playerConnection.sendPacket(new PacketPlayOutSpawnPosition(new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())));
      }
   }

   @Override
   public org.bukkit.Location getCompassTarget() {
      return this.getHandle().compassTarget;
   }

   @Override
   public void chat(String msg) {
      if (this.getHandle().playerConnection != null) {
         this.getHandle().playerConnection.chat(msg, false);
      }
   }

   @Override
   public boolean performCommand(String command) {
      return this.server.dispatchCommand(this, command);
   }

   @Override
   public void playNote(org.bukkit.Location loc, byte instrument, byte note) {
      if (this.getHandle().playerConnection != null) {
         String instrumentName = null;
         switch (instrument) {
            case 0:
               instrumentName = "harp";
               break;
            case 1:
               instrumentName = "bd";
               break;
            case 2:
               instrumentName = "snare";
               break;
            case 3:
               instrumentName = "hat";
               break;
            case 4:
               instrumentName = "bassattack";
         }

         float f = (float)FastMath.pow(2.0, (note - 12.0) / 12.0);
         this.getHandle()
            .playerConnection
            .sendPacket(new PacketPlayOutNamedSoundEffect("note." + instrumentName, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), 3.0F, f));
      }
   }

   @Override
   public void playNote(org.bukkit.Location loc, org.bukkit.Instrument instrument, org.bukkit.Note note) {
      if (this.getHandle().playerConnection != null) {
         String instrumentName = null;
         switch (instrument) {
            case PIANO:
               instrumentName = "harp";
               break;
            case BASS_DRUM:
               instrumentName = "bd";
               break;
            case SNARE_DRUM:
               instrumentName = "snare";
               break;
            case STICKS:
               instrumentName = "hat";
               break;
            case BASS_GUITAR:
               instrumentName = "bassattack";
         }

         float f = (float)FastMath.pow(2.0, (note.getId() - 12.0) / 12.0);
         this.getHandle()
            .playerConnection
            .sendPacket(new PacketPlayOutNamedSoundEffect("note." + instrumentName, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), 3.0F, f));
      }
   }

   @Override
   public void playSound(org.bukkit.Location loc, org.bukkit.Sound sound, float volume, float pitch) {
      if (sound != null) {
         this.playSound(loc, CraftSound.getSound(sound), volume, pitch);
      }
   }

   @Override
   public void playSound(org.bukkit.Location loc, String sound, float volume, float pitch) {
      if (loc != null && sound != null && this.getHandle().playerConnection != null) {
         double x = loc.getBlockX() + 0.5;
         double y = loc.getBlockY() + 0.5;
         double z = loc.getBlockZ() + 0.5;
         PacketPlayOutNamedSoundEffect packet = new PacketPlayOutNamedSoundEffect(sound, x, y, z, volume, pitch);
         this.getHandle().playerConnection.sendPacket(packet);
      }
   }

   @Override
   public void playEffect(org.bukkit.Location loc, org.bukkit.Effect effect, int data) {
      if (this.getHandle().playerConnection != null) {
         this.spigot().playEffect(loc, effect, data, 0, 0.0F, 0.0F, 0.0F, 1.0F, 1, 64);
      }
   }

   @Override
   public <T> void playEffect(org.bukkit.Location loc, org.bukkit.Effect effect, T data) {
      if (data != null) {
         Validate.isTrue(data.getClass().isAssignableFrom(effect.getData()), "Wrong kind of data for this effect!");
      } else {
         Validate.isTrue(effect.getData() == null, "Wrong kind of data for this effect!");
      }

      int datavalue = data == null ? 0 : CraftEffect.getDataValue(effect, data);
      this.playEffect(loc, effect, datavalue);
   }

   @Override
   public void sendBlockChange(org.bukkit.Location loc, org.bukkit.Material material, byte data) {
      this.sendBlockChange(loc, material.getId(), data);
   }

   @Override
   public void sendBlockChange(org.bukkit.Location loc, int material, byte data) {
      if (this.getHandle().playerConnection != null) {
         PacketPlayOutBlockChange packet = new PacketPlayOutBlockChange(
            ((CraftWorld)loc.getWorld()).getHandle(), new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())
         );
         packet.block = CraftMagicNumbers.getBlock(material).fromLegacyData(data);
         this.getHandle().playerConnection.sendPacket(packet);
      }
   }

   @Override
   public void sendSignChange(org.bukkit.Location loc, String[] lines) {
      if (this.getHandle().playerConnection != null) {
         if (lines == null) {
            lines = new String[4];
         }

         Validate.notNull(loc, "Location can not be null");
         if (lines.length < 4) {
            throw new IllegalArgumentException("Must have at least 4 lines");
         } else {
            IChatBaseComponent[] components = CraftSign.sanitizeLines(lines);
            this.getHandle()
               .playerConnection
               .sendPacket(
                  new PacketPlayOutUpdateSign(this.getHandle().world, new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()), components)
               );
         }
      }
   }

   @Override
   public boolean sendChunkChange(org.bukkit.Location loc, int sx, int sy, int sz, byte[] data) {
      if (this.getHandle().playerConnection == null) {
         return false;
      } else {
         throw new NotImplementedException("Chunk changes do not yet work");
      }
   }

   @Override
   public void sendActionBar(String message) {
      this.getHandle().playerConnection.sendPacket(new PacketPlayOutChat(new ChatComponentText(message), (byte)2));
   }

   @Override
   public void sendMap(MapView map) {
      if (this.getHandle().playerConnection != null) {
         RenderData data = ((CraftMapView)map).render(this);
         Collection<MapIcon> icons = new ArrayList<>();

         for (MapCursor cursor : data.cursors) {
            if (cursor.isVisible()) {
               icons.add(new MapIcon(cursor.getRawType(), cursor.getX(), cursor.getY(), cursor.getDirection()));
            }
         }

         PacketPlayOutMap packet = new PacketPlayOutMap(map.getId(), map.getScale().getValue(), icons, data.buffer, 0, 0, 0, 0);
         this.getHandle().playerConnection.sendPacket(packet);
      }
   }

   @Override
   public boolean teleport(org.bukkit.Location location, PlayerTeleportEvent.TeleportCause cause) {
      EntityPlayer entity = this.getHandle();
      if (this.getHealth() == 0.0 || entity.dead) {
         return false;
      } else if (entity.playerConnection == null || entity.playerConnection.isDisconnected()) {
         return false;
      } else if (entity.passenger != null) {
         return false;
      } else {
         org.bukkit.Location from = this.getLocation();
         PlayerTeleportEvent event = new PlayerTeleportEvent(this, from, location, cause);
         this.server.getPluginManager().callEvent(event);
         if (event.isCancelled()) {
            return false;
         } else {
            entity.mount(null);
            from = event.getFrom();
            org.bukkit.Location to = event.getTo();
            WorldServer fromWorld = ((CraftWorld)from.getWorld()).getHandle();
            WorldServer toWorld = ((CraftWorld)to.getWorld()).getHandle();
            if (this.getHandle().activeContainer != this.getHandle().defaultContainer) {
               this.getHandle().closeInventory();
            }

            if (fromWorld == toWorld) {
               entity.playerConnection.teleport(to);
            } else {
               this.server.getHandle().moveToWorld(entity, toWorld.dimension, true, to, false);
            }

            return true;
         }
      }
   }

   @Override
   public void setSneaking(boolean sneak) {
      this.getHandle().setSneaking(sneak);
   }

   @Override
   public boolean isSneaking() {
      return this.getHandle().isSneaking();
   }

   @Override
   public boolean isSprinting() {
      return this.getHandle().isSprinting();
   }

   @Override
   public void setSprinting(boolean sprinting) {
      this.getHandle().setSprinting(sprinting);
   }

   @Override
   public void loadData() {
      this.server.getHandle().playerFileData.load(this.getHandle());
   }

   @Override
   public void saveData() {
      this.server.getHandle().playerFileData.save(this.getHandle());
   }

   @Deprecated
   @Override
   public void updateInventory() {
      this.getHandle().updateInventory(this.getHandle().activeContainer);
   }

   @Override
   public void setSleepingIgnored(boolean isSleeping) {
      this.getHandle().fauxSleeping = isSleeping;
      ((CraftWorld)this.getWorld()).getHandle().checkSleepStatus();
   }

   @Override
   public boolean isSleepingIgnored() {
      return this.getHandle().fauxSleeping;
   }

   @Override
   public void awardAchievement(org.bukkit.Achievement achievement) {
      Validate.notNull(achievement, "Achievement cannot be null");
      if (achievement.hasParent() && !this.hasAchievement(achievement.getParent())) {
         this.awardAchievement(achievement.getParent());
      }

      this.getHandle().getStatisticManager().setStatistic(this.getHandle(), CraftStatistic.getNMSAchievement(achievement), 1);
      this.getHandle().getStatisticManager().updateStatistics(this.getHandle());
   }

   @Override
   public void removeAchievement(org.bukkit.Achievement achievement) {
      Validate.notNull(achievement, "Achievement cannot be null");

      org.bukkit.Achievement[] var5;
      for (org.bukkit.Achievement achieve : var5 = org.bukkit.Achievement.values()) {
         if (achieve.getParent() == achievement && this.hasAchievement(achieve)) {
            this.removeAchievement(achieve);
         }
      }

      this.getHandle().getStatisticManager().setStatistic(this.getHandle(), CraftStatistic.getNMSAchievement(achievement), 0);
   }

   @Override
   public boolean hasAchievement(org.bukkit.Achievement achievement) {
      Validate.notNull(achievement, "Achievement cannot be null");
      return this.getHandle().getStatisticManager().hasAchievement(CraftStatistic.getNMSAchievement(achievement));
   }

   @Override
   public void incrementStatistic(org.bukkit.Statistic statistic) {
      this.incrementStatistic(statistic, 1);
   }

   @Override
   public void decrementStatistic(org.bukkit.Statistic statistic) {
      this.decrementStatistic(statistic, 1);
   }

   @Override
   public int getStatistic(org.bukkit.Statistic statistic) {
      Validate.notNull(statistic, "Statistic cannot be null");
      Validate.isTrue(statistic.getType() == org.bukkit.Statistic.Type.UNTYPED, "Must supply additional paramater for this statistic");
      return this.getHandle().getStatisticManager().getStatisticValue(CraftStatistic.getNMSStatistic(statistic));
   }

   @Override
   public void incrementStatistic(org.bukkit.Statistic statistic, int amount) {
      Validate.isTrue(amount > 0, "Amount must be greater than 0");
      this.setStatistic(statistic, this.getStatistic(statistic) + amount);
   }

   @Override
   public void decrementStatistic(org.bukkit.Statistic statistic, int amount) {
      Validate.isTrue(amount > 0, "Amount must be greater than 0");
      this.setStatistic(statistic, this.getStatistic(statistic) - amount);
   }

   @Override
   public void setStatistic(org.bukkit.Statistic statistic, int newValue) {
      Validate.notNull(statistic, "Statistic cannot be null");
      Validate.isTrue(statistic.getType() == org.bukkit.Statistic.Type.UNTYPED, "Must supply additional paramater for this statistic");
      Validate.isTrue(newValue >= 0, "Value must be greater than or equal to 0");
      net.minecraft.server.v1_8_R3.Statistic nmsStatistic = CraftStatistic.getNMSStatistic(statistic);
      this.getHandle().getStatisticManager().setStatistic(this.getHandle(), nmsStatistic, newValue);
   }

   @Override
   public void incrementStatistic(org.bukkit.Statistic statistic, org.bukkit.Material material) {
      this.incrementStatistic(statistic, material, 1);
   }

   @Override
   public void decrementStatistic(org.bukkit.Statistic statistic, org.bukkit.Material material) {
      this.decrementStatistic(statistic, material, 1);
   }

   @Override
   public int getStatistic(org.bukkit.Statistic statistic, org.bukkit.Material material) {
      Validate.notNull(statistic, "Statistic cannot be null");
      Validate.notNull(material, "Material cannot be null");
      Validate.isTrue(
         statistic.getType() == org.bukkit.Statistic.Type.BLOCK || statistic.getType() == org.bukkit.Statistic.Type.ITEM,
         "This statistic does not take a Material parameter"
      );
      net.minecraft.server.v1_8_R3.Statistic nmsStatistic = CraftStatistic.getMaterialStatistic(statistic, material);
      Validate.notNull(nmsStatistic, "The supplied Material does not have a corresponding statistic");
      return this.getHandle().getStatisticManager().getStatisticValue(nmsStatistic);
   }

   @Override
   public void incrementStatistic(org.bukkit.Statistic statistic, org.bukkit.Material material, int amount) {
      Validate.isTrue(amount > 0, "Amount must be greater than 0");
      this.setStatistic(statistic, material, this.getStatistic(statistic, material) + amount);
   }

   @Override
   public void decrementStatistic(org.bukkit.Statistic statistic, org.bukkit.Material material, int amount) {
      Validate.isTrue(amount > 0, "Amount must be greater than 0");
      this.setStatistic(statistic, material, this.getStatistic(statistic, material) - amount);
   }

   @Override
   public void setStatistic(org.bukkit.Statistic statistic, org.bukkit.Material material, int newValue) {
      Validate.notNull(statistic, "Statistic cannot be null");
      Validate.notNull(material, "Material cannot be null");
      Validate.isTrue(newValue >= 0, "Value must be greater than or equal to 0");
      Validate.isTrue(
         statistic.getType() == org.bukkit.Statistic.Type.BLOCK || statistic.getType() == org.bukkit.Statistic.Type.ITEM,
         "This statistic does not take a Material parameter"
      );
      net.minecraft.server.v1_8_R3.Statistic nmsStatistic = CraftStatistic.getMaterialStatistic(statistic, material);
      Validate.notNull(nmsStatistic, "The supplied Material does not have a corresponding statistic");
      this.getHandle().getStatisticManager().setStatistic(this.getHandle(), nmsStatistic, newValue);
   }

   @Override
   public void incrementStatistic(org.bukkit.Statistic statistic, EntityType entityType) {
      this.incrementStatistic(statistic, entityType, 1);
   }

   @Override
   public void decrementStatistic(org.bukkit.Statistic statistic, EntityType entityType) {
      this.decrementStatistic(statistic, entityType, 1);
   }

   @Override
   public int getStatistic(org.bukkit.Statistic statistic, EntityType entityType) {
      Validate.notNull(statistic, "Statistic cannot be null");
      Validate.notNull(entityType, "EntityType cannot be null");
      Validate.isTrue(statistic.getType() == org.bukkit.Statistic.Type.ENTITY, "This statistic does not take an EntityType parameter");
      net.minecraft.server.v1_8_R3.Statistic nmsStatistic = CraftStatistic.getEntityStatistic(statistic, entityType);
      Validate.notNull(nmsStatistic, "The supplied EntityType does not have a corresponding statistic");
      return this.getHandle().getStatisticManager().getStatisticValue(nmsStatistic);
   }

   @Override
   public void incrementStatistic(org.bukkit.Statistic statistic, EntityType entityType, int amount) {
      Validate.isTrue(amount > 0, "Amount must be greater than 0");
      this.setStatistic(statistic, entityType, this.getStatistic(statistic, entityType) + amount);
   }

   @Override
   public void decrementStatistic(org.bukkit.Statistic statistic, EntityType entityType, int amount) {
      Validate.isTrue(amount > 0, "Amount must be greater than 0");
      this.setStatistic(statistic, entityType, this.getStatistic(statistic, entityType) - amount);
   }

   @Override
   public void setStatistic(org.bukkit.Statistic statistic, EntityType entityType, int newValue) {
      Validate.notNull(statistic, "Statistic cannot be null");
      Validate.notNull(entityType, "EntityType cannot be null");
      Validate.isTrue(newValue >= 0, "Value must be greater than or equal to 0");
      Validate.isTrue(statistic.getType() == org.bukkit.Statistic.Type.ENTITY, "This statistic does not take an EntityType parameter");
      net.minecraft.server.v1_8_R3.Statistic nmsStatistic = CraftStatistic.getEntityStatistic(statistic, entityType);
      Validate.notNull(nmsStatistic, "The supplied EntityType does not have a corresponding statistic");
      this.getHandle().getStatisticManager().setStatistic(this.getHandle(), nmsStatistic, newValue);
   }

   @Override
   public void setPlayerTime(long time, boolean relative) {
      this.getHandle().timeOffset = time;
      this.getHandle().relativeTime = relative;
   }

   @Override
   public long getPlayerTimeOffset() {
      return this.getHandle().timeOffset;
   }

   @Override
   public long getPlayerTime() {
      return this.getHandle().getPlayerTime();
   }

   @Override
   public boolean isPlayerTimeRelative() {
      return this.getHandle().relativeTime;
   }

   @Override
   public void resetPlayerTime() {
      this.setPlayerTime(0L, true);
   }

   @Override
   public void setPlayerWeather(org.bukkit.WeatherType type) {
      this.getHandle().setPlayerWeather(type, true);
   }

   @Override
   public org.bukkit.WeatherType getPlayerWeather() {
      return this.getHandle().getPlayerWeather();
   }

   @Override
   public void resetPlayerWeather() {
      this.getHandle().resetPlayerWeather();
   }

   @Override
   public boolean isBanned() {
      return this.server.getBanList(org.bukkit.BanList.Type.NAME).isBanned(this.getName());
   }

   @Override
   public void setBanned(boolean value) {
      if (value) {
         this.server.getBanList(org.bukkit.BanList.Type.NAME).addBan(this.getName(), null, null, null);
      } else {
         this.server.getBanList(org.bukkit.BanList.Type.NAME).pardon(this.getName());
      }
   }

   @Override
   public boolean isWhitelisted() {
      return this.server.getHandle().getWhitelist().isWhitelisted(this.getProfile());
   }

   @Override
   public void setWhitelisted(boolean value) {
      if (value) {
         this.server.getHandle().addWhitelist(this.getProfile());
      } else {
         this.server.getHandle().removeWhitelist(this.getProfile());
      }
   }

   @Override
   public void setGameMode(org.bukkit.GameMode mode) {
      if (this.getHandle().playerConnection != null) {
         if (mode == null) {
            throw new IllegalArgumentException("Mode cannot be null");
         } else {
            if (mode != this.getGameMode()) {
               PlayerGameModeChangeEvent event = new PlayerGameModeChangeEvent(this, mode);
               this.server.getPluginManager().callEvent(event);
               if (event.isCancelled()) {
                  return;
               }

               this.getHandle().setSpectatorTarget(this.getHandle());
               this.getHandle().playerInteractManager.setGameMode(EnumGamemode.getById(mode.getValue()));
               this.getHandle().fallDistance = 0.0F;
               this.getHandle().playerConnection.sendPacket(new PacketPlayOutGameStateChange(3, mode.getValue()));
            }
         }
      }
   }

   @Override
   public org.bukkit.GameMode getGameMode() {
      return org.bukkit.GameMode.getByValue(this.getHandle().playerInteractManager.getGameMode().getId());
   }

   @Override
   public void giveExp(int exp) {
      this.getHandle().giveExp(exp);
   }

   @Override
   public void giveExpLevels(int levels) {
      this.getHandle().levelDown(levels);
   }

   @Override
   public float getExp() {
      return this.getHandle().exp;
   }

   @Override
   public void setExp(float exp) {
      this.getHandle().exp = exp;
      this.getHandle().lastSentExp = -1;
   }

   @Override
   public int getLevel() {
      return this.getHandle().expLevel;
   }

   @Override
   public void setLevel(int level) {
      this.getHandle().expLevel = level;
      this.getHandle().lastSentExp = -1;
   }

   @Override
   public int getTotalExperience() {
      return this.getHandle().expTotal;
   }

   @Override
   public void setTotalExperience(int exp) {
      this.getHandle().expTotal = exp;
   }

   @Override
   public float getExhaustion() {
      return this.getHandle().getFoodData().exhaustionLevel;
   }

   @Override
   public void setExhaustion(float value) {
      this.getHandle().getFoodData().exhaustionLevel = value;
   }

   @Override
   public float getSaturation() {
      return this.getHandle().getFoodData().saturationLevel;
   }

   @Override
   public void setSaturation(float value) {
      this.getHandle().getFoodData().saturationLevel = value;
   }

   @Override
   public int getFoodLevel() {
      return this.getHandle().getFoodData().foodLevel;
   }

   @Override
   public void setFoodLevel(int value) {
      this.getHandle().getFoodData().foodLevel = value;
   }

   @Override
   public org.bukkit.Location getBedSpawnLocation() {
      org.bukkit.World world = this.getServer().getWorld(this.getHandle().spawnWorld);
      BlockPosition bed = this.getHandle().getBed();
      if (world != null && bed != null) {
         bed = EntityHuman.getBed(((CraftWorld)world).getHandle(), bed, this.getHandle().isRespawnForced());
         if (bed != null) {
            return new org.bukkit.Location(world, bed.getX(), bed.getY(), bed.getZ());
         }
      }

      return null;
   }

   @Override
   public void setBedSpawnLocation(org.bukkit.Location location) {
      this.setBedSpawnLocation(location, false);
   }

   @Override
   public void setBedSpawnLocation(org.bukkit.Location location, boolean override) {
      if (location == null) {
         this.getHandle().setRespawnPosition(null, override);
      } else {
         this.getHandle().setRespawnPosition(new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()), override);
         this.getHandle().spawnWorld = location.getWorld().getName();
      }
   }

   @Override
   public void hidePlayer(Player player) {
      Validate.notNull(player, "hidden player cannot be null");
      if (this.getHandle().playerConnection != null && !this.equals(player) && !this.hiddenPlayers.contains(player.getUniqueId())) {
         this.hiddenPlayers.add(player.getUniqueId());
         EntityTracker tracker = ((WorldServer)this.entity.world).tracker;
         EntityPlayer other = ((CraftPlayer)player).getHandle();
         EntityTrackerEntry entry = (EntityTrackerEntry)tracker.trackedEntities.get(other.getId());
         if (entry != null) {
            entry.clear(this.getHandle());
         }

         this.getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, new EntityPlayer[]{other}));
      }
   }

   @Override
   public void showPlayer(Player player) {
      Validate.notNull(player, "shown player cannot be null");
      if (this.getHandle().playerConnection != null && !this.equals(player) && this.hiddenPlayers.contains(player.getUniqueId())) {
         this.hiddenPlayers.remove(player.getUniqueId());
         EntityTracker tracker = ((WorldServer)this.entity.world).tracker;
         EntityPlayer entityPlayer = this.getHandle();
         EntityPlayer other = ((CraftPlayer)player).getHandle();
         entityPlayer.playerConnection.writePacketLazily(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, new EntityPlayer[]{other}));
         EntityTrackerEntry entry = (EntityTrackerEntry)tracker.trackedEntities.get(other.getId());
         if (entry != null && !entry.trackedPlayers.contains(this.getHandle())) {
            entry.updatePlayer(this.getHandle());
         }
      }
   }

   @Override
   public boolean canSee(Player player) {
      return !this.hiddenPlayers.contains(player.getUniqueId());
   }

   @Override
   public Map<String, Object> serialize() {
      Map<String, Object> result = new LinkedHashMap<>();
      result.put("name", this.getName());
      return result;
   }

   @Override
   public Player getPlayer() {
      return this;
   }

   public EntityPlayer getHandle() {
      return (EntityPlayer)this.entity;
   }

   public void setHandle(EntityPlayer entity) {
      super.setHandle(entity);
   }

   @Override
   public String toString() {
      return "CraftPlayer{name=" + this.getName() + '}';
   }

   @Override
   public int hashCode() {
      if (this.hash == 0 || this.hash == 485) {
         this.hash = 485 + (this.getUniqueId() != null ? this.getUniqueId().hashCode() : 0);
      }

      return this.hash;
   }

   @Override
   public long getFirstPlayed() {
      return this.firstPlayed;
   }

   @Override
   public long getLastPlayed() {
      return this.lastPlayed;
   }

   @Override
   public boolean hasPlayedBefore() {
      return this.hasPlayedBefore;
   }

   public void setFirstPlayed(long firstPlayed) {
      this.firstPlayed = firstPlayed;
   }

   public void readExtraData(NBTTagCompound nbttagcompound) {
      this.hasPlayedBefore = true;
      if (nbttagcompound.hasKey("bukkit")) {
         NBTTagCompound data = nbttagcompound.getCompound("bukkit");
         if (data.hasKey("firstPlayed")) {
            this.firstPlayed = data.getLong("firstPlayed");
            this.lastPlayed = data.getLong("lastPlayed");
         }

         if (data.hasKey("newExp")) {
            EntityPlayer handle = this.getHandle();
            handle.newExp = data.getInt("newExp");
            handle.newTotalExp = data.getInt("newTotalExp");
            handle.newLevel = data.getInt("newLevel");
            handle.expToDrop = data.getInt("expToDrop");
            handle.keepLevel = data.getBoolean("keepLevel");
         }

         if (data.hasKey("profile")) {
            this.setKnockbackProfile(Blaze.getInstance().getKnockbackManager().getByName(data.getString("profile")));
         }
      }
   }

   public void setExtraData(NBTTagCompound nbttagcompound) {
      if (!nbttagcompound.hasKey("bukkit")) {
         nbttagcompound.set("bukkit", new NBTTagCompound());
      }

      NBTTagCompound data = nbttagcompound.getCompound("bukkit");
      EntityPlayer handle = this.getHandle();
      data.setInt("newExp", handle.newExp);
      data.setInt("newTotalExp", handle.newTotalExp);
      data.setInt("newLevel", handle.newLevel);
      data.setInt("expToDrop", handle.expToDrop);
      data.setBoolean("keepLevel", handle.keepLevel);
      data.setLong("firstPlayed", this.getFirstPlayed());
      data.setLong("lastPlayed", System.currentTimeMillis());
      data.setString("lastKnownName", handle.getName());
      Knockback knockback = this.getHandle().knockbackProfile;
      if (knockback != null) {
         data.setString("profile", knockback.getName());
      }
   }

   @Override
   public boolean beginConversation(Conversation conversation) {
      return this.conversationTracker.beginConversation(conversation);
   }

   @Override
   public void abandonConversation(Conversation conversation) {
      this.conversationTracker.abandonConversation(conversation, new ConversationAbandonedEvent(conversation, new ManuallyAbandonedConversationCanceller()));
   }

   @Override
   public void abandonConversation(Conversation conversation, ConversationAbandonedEvent details) {
      this.conversationTracker.abandonConversation(conversation, details);
   }

   @Override
   public void acceptConversationInput(String input) {
      this.conversationTracker.acceptConversationInput(input);
   }

   @Override
   public boolean isConversing() {
      return this.conversationTracker.isConversing();
   }

   @Override
   public void sendPluginMessage(Plugin source, String channel, byte[] message) {
      StandardMessenger.validatePluginMessage(this.server.getMessenger(), source, channel, message);
      if (this.getHandle().playerConnection != null) {
         if (this.channels.contains(channel)) {
            PacketPlayOutCustomPayload packet = new PacketPlayOutCustomPayload(channel, new PacketDataSerializer(Unpooled.wrappedBuffer(message)));
            this.getHandle().playerConnection.sendPacket(packet);
         }
      }
   }

   @Override
   public void setTexturePack(String url) {
      this.setResourcePack(url);
   }

   @Override
   public void setResourcePack(String url) {
      Validate.notNull(url, "Resource pack URL cannot be null");
      this.getHandle().setResourcePack(url, "null");
   }

   public void addChannel(String channel) {
      Preconditions.checkState(this.channels.size() < 128, "Too many channels registered");
      if (this.channels.add(channel)) {
         this.server.getPluginManager().callEvent(new PlayerRegisterChannelEvent(this, channel));
      }
   }

   public void removeChannel(String channel) {
      if (this.channels.remove(channel)) {
         this.server.getPluginManager().callEvent(new PlayerUnregisterChannelEvent(this, channel));
      }
   }

   @Override
   public Set<String> getListeningPluginChannels() {
      return ImmutableSet.copyOf(this.channels);
   }

   public void sendSupportedChannels() {
      if (this.getHandle().playerConnection != null) {
         Set<String> listening = this.server.getMessenger().getIncomingChannels();
         if (!listening.isEmpty()) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            for (String channel : listening) {
               try {
                  stream.write(channel.getBytes(StandardCharsets.UTF_8));
                  stream.write(0);
               } catch (IOException var6) {
                  Logger.getLogger(CraftPlayer.class.getName())
                     .log(Level.SEVERE, "Could not send Plugin Channel REGISTER to " + this.getName(), (Throwable)var6);
               }
            }

            this.getHandle()
               .playerConnection
               .sendPacket(new PacketPlayOutCustomPayload("REGISTER", new PacketDataSerializer(Unpooled.wrappedBuffer(stream.toByteArray()))));
         }
      }
   }

   @Override
   public EntityType getType() {
      return EntityType.PLAYER;
   }

   @Override
   public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
      this.server.getPlayerMetadata().setMetadata(this, metadataKey, newMetadataValue);
   }

   @Override
   public List<MetadataValue> getMetadata(String metadataKey) {
      return this.server.getPlayerMetadata().getMetadata(this, metadataKey);
   }

   @Override
   public boolean hasMetadata(String metadataKey) {
      return this.server.getPlayerMetadata().hasMetadata(this, metadataKey);
   }

   @Override
   public void removeMetadata(String metadataKey, Plugin owningPlugin) {
      this.server.getPlayerMetadata().removeMetadata(this, metadataKey, owningPlugin);
   }

   @Override
   public boolean setWindowProperty(InventoryView.Property prop, int value) {
      Container container = this.getHandle().activeContainer;
      if (container.getBukkitView().getType() != prop.getType()) {
         return false;
      } else {
         this.getHandle().setContainerData(container, prop.getId(), value);
         return true;
      }
   }

   public void disconnect(String reason) {
      this.conversationTracker.abandonAllConversations();
      this.perm.clearPermissions();
   }

   @Override
   public boolean isFlying() {
      return this.getHandle().abilities.isFlying;
   }

   @Override
   public void setFlying(boolean value) {
      boolean needsUpdate = this.getHandle().abilities.canFly ^ value;
      if (!this.getAllowFlight() && value) {
         throw new IllegalArgumentException("Cannot make player fly if getAllowFlight() is false");
      } else {
         this.getHandle().abilities.isFlying = value;
         if (needsUpdate) {
            this.getHandle().updateAbilities();
         }
      }
   }

   @Override
   public boolean getAllowFlight() {
      return this.getHandle().abilities.canFly;
   }

   @Override
   public void setAllowFlight(boolean value) {
      if (this.isFlying() && !value) {
         this.getHandle().abilities.isFlying = false;
      }

      this.getHandle().abilities.canFly = value;
      this.getHandle().updateAbilities();
   }

   @Override
   public int getNoDamageTicks() {
      return this.getHandle().invulnerableTicks > 0
         ? Math.max(this.getHandle().invulnerableTicks, this.getHandle().noDamageTicks)
         : this.getHandle().noDamageTicks;
   }

   @Override
   public void setFlySpeed(float value) {
      this.validateSpeed(value);
      EntityPlayer player = this.getHandle();
      player.abilities.flySpeed = Math.max(value, 1.0E-4F) / 2.0F;
      player.updateAbilities();
   }

   @Override
   public void setWalkSpeed(float value) {
      this.validateSpeed(value);
      EntityPlayer player = this.getHandle();
      player.abilities.walkSpeed = Math.max(value, 1.0E-4F) / 2.0F;
      player.updateAbilities();
   }

   @Override
   public float getFlySpeed() {
      return this.getHandle().abilities.flySpeed * 2.0F;
   }

   @Override
   public float getWalkSpeed() {
      return this.getHandle().abilities.walkSpeed * 2.0F;
   }

   private void validateSpeed(float value) {
      if (value < 0.0F) {
         if (value < -1.0F) {
            throw new IllegalArgumentException(value + " is too low");
         }
      } else if (value > 1.0F) {
         throw new IllegalArgumentException(value + " is too high");
      }
   }

   @Override
   public void setMaxHealth(double amount) {
      super.setMaxHealth(amount);
      this.health = Math.min(this.health, amount);
      this.getHandle().triggerHealthUpdate();
   }

   @Override
   public void resetMaxHealth() {
      super.resetMaxHealth();
      this.getHandle().triggerHealthUpdate();
   }

   public CraftScoreboard getScoreboard() {
      return this.server.getScoreboardManager().getPlayerBoard(this);
   }

   @Override
   public void setScoreboard(Scoreboard scoreboard) {
      Validate.notNull(scoreboard, "Scoreboard cannot be null");
      PlayerConnection playerConnection = this.getHandle().playerConnection;
      if (playerConnection == null) {
         throw new IllegalStateException("Cannot set scoreboard yet");
      } else {
         this.server.getScoreboardManager().setPlayerBoard(this, scoreboard);
      }
   }

   @Override
   public void setHealthScale(double value) {
      Validate.isTrue((float)value > 0.0F, "Must be greater than 0");
      this.healthScale = value;
      this.scaledHealth = true;
      this.updateScaledHealth();
   }

   @Override
   public double getHealthScale() {
      return this.healthScale;
   }

   @Override
   public void setHealthScaled(boolean scale) {
      if (this.scaledHealth != (this.scaledHealth = scale)) {
         this.updateScaledHealth();
      }
   }

   @Override
   public boolean isHealthScaled() {
      return this.scaledHealth;
   }

   public float getScaledHealth() {
      return (float)(this.isHealthScaled() ? this.getHealth() * this.getHealthScale() / this.getMaxHealth() : this.getHealth());
   }

   @Override
   public double getHealth() {
      return this.health;
   }

   public void setRealHealth(double health) {
      this.health = health;
   }

   public void updateScaledHealth() {
      AttributeMapServer attributemapserver = (AttributeMapServer)this.getHandle().getAttributeMap();
      Set set = attributemapserver.getAttributes();
      this.injectScaledMaxHealth(set, true);
      this.getHandle().getDataWatcher().watch(6, this.getScaledHealth());
      this.getHandle()
         .playerConnection
         .sendPacket(
            new PacketPlayOutUpdateHealth(
               this.getScaledHealth(), this.getHandle().getFoodData().getFoodLevel(), this.getHandle().getFoodData().getSaturationLevel()
            )
         );
      this.getHandle().playerConnection.sendPacket(new PacketPlayOutUpdateAttributes(this.getHandle().getId(), set));
      set.clear();
      this.getHandle().maxHealthCache = this.getMaxHealth();
   }

   public void injectScaledMaxHealth(Collection collection, boolean force) {
      if (this.scaledHealth || force) {
         for (Object genericInstance : collection) {
            IAttribute attribute = ((AttributeInstance)genericInstance).getAttribute();
            if (attribute.getName().equals("generic.maxHealth")) {
               collection.remove(genericInstance);
               break;
            }
         }

         double healthMod = this.scaledHealth ? this.healthScale : this.getMaxHealth();
         if (healthMod >= Float.MAX_VALUE || healthMod <= 0.0) {
            healthMod = 20.0;
            this.getServer().getLogger().warning(this.getName() + " tried to crash the server with a large health attribute");
         }

         collection.add(
            new AttributeModifiable(
               this.getHandle().getAttributeMap(), new AttributeRanged(null, "generic.maxHealth", healthMod, 0.0, Float.MAX_VALUE).a("Max Health").a(true)
            )
         );
      }
   }

   @Override
   public Entity getSpectatorTarget() {
      net.minecraft.server.v1_8_R3.Entity followed = this.getHandle().C();
      return followed == this.getHandle() ? null : followed.getBukkitEntity();
   }

   @Override
   public void setSpectatorTarget(Entity entity) {
      Preconditions.checkArgument(this.getGameMode() == org.bukkit.GameMode.SPECTATOR, "Player must be in spectator mode");
      this.getHandle().setSpectatorTarget(entity == null ? null : ((CraftEntity)entity).getHandle());
   }

   @Override
   public void sendTitle(String title, String subtitle) {
      if (title != null) {
         PacketPlayOutTitle packetTitle = new PacketPlayOutTitle(EnumTitleAction.TITLE, CraftChatMessage.fromString(title)[0]);
         this.getHandle().playerConnection.sendPacket(packetTitle);
      }

      if (subtitle != null) {
         PacketPlayOutTitle packetSubtitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, CraftChatMessage.fromString(subtitle)[0]);
         this.getHandle().playerConnection.sendPacket(packetSubtitle);
      }
   }

   @Override
   public void resetTitle() {
      PacketPlayOutTitle packetReset = new PacketPlayOutTitle(EnumTitleAction.RESET, (IChatBaseComponent)null);
      this.getHandle().playerConnection.sendPacket(packetReset);
   }

   @Override
   public Player.Spigot spigot() {
      return this.spigot;
   }

   @Override
   public int getPing() {
      return this.getHandle().ping;
   }
}
