package net.minecraft.server.v1_8_R3;

import com.mojang.authlib.GameProfile;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftIconCache;
import org.bukkit.entity.Player;
import org.bukkit.util.CachedServerIcon;
import org.jetbrains.annotations.NotNull;
import org.spigotmc.SpigotConfig;

public class PacketStatusListener implements PacketStatusInListener {
   private static final IChatBaseComponent a = new ChatComponentText("Status request has been handled.");
   private final MinecraftServer minecraftServer;
   private final NetworkManager networkManager;
   private boolean d;

   public PacketStatusListener(MinecraftServer minecraftserver, NetworkManager networkmanager) {
      this.minecraftServer = minecraftserver;
      this.networkManager = networkmanager;
   }

   @Override
   public void a(IChatBaseComponent ichatbasecomponent) {
   }

   @Override
   public void a(PacketStatusInStart packetstatusinstart) {
      if (this.d) {
         this.networkManager.close(a);
      } else {
         this.d = true;
         final Object[] players = this.minecraftServer.getPlayerList().players.toArray();

         class ServerListPingEvent extends org.bukkit.event.server.ServerListPingEvent {
            CraftIconCache icon = PacketStatusListener.this.minecraftServer.server.getServerIcon();

            ServerListPingEvent() {
               super(
                  ((InetSocketAddress)PacketStatusListener.this.networkManager.getSocketAddress()).getAddress(),
                  PacketStatusListener.this.minecraftServer.getMotd(),
                  PacketStatusListener.this.minecraftServer.getPlayerList().getMaxPlayers()
               );
            }

            public void setServerIcon(CachedServerIcon icon) {
               if (!(icon instanceof CraftIconCache)) {
                  throw new IllegalArgumentException(icon + " was not created by " + CraftServer.class);
               } else {
                  this.icon = (CraftIconCache)icon;
               }
            }

            @NotNull
            public Iterator<Player> iterator() throws UnsupportedOperationException {
               return new Iterator<Player>() {
                  int i;
                  int ret = Integer.MIN_VALUE;
                  EntityPlayer player;

                  @Override
                  public boolean hasNext() {
                     if (this.player != null) {
                        return true;
                     } else {
                        int length = players.length;

                        for (int i = this.i; i < length; i++) {
                           EntityPlayer player = (EntityPlayer)players[i];
                           if (player != null) {
                              this.i = i + 1;
                              this.player = player;
                              return true;
                           }
                        }

                        return false;
                     }
                  }

                  public Player next() {
                     if (!this.hasNext()) {
                        throw new NoSuchElementException();
                     } else {
                        EntityPlayer player = this.player;
                        this.player = null;
                        this.ret = this.i - 1;
                        return player.getBukkitEntity();
                     }
                  }

                  @Override
                  public void remove() {
                     int i = this.ret;
                     if (i >= 0 && players[i] != null) {
                        players[i] = null;
                     } else {
                        throw new IllegalStateException();
                     }
                  }
               };
            }
         }

         ServerListPingEvent event = new ServerListPingEvent();
         this.minecraftServer.server.getPluginManager().callEvent(event);
         List<GameProfile> profiles = new ArrayList<>(players.length);

         for (Object player : players) {
            if (player != null) {
               profiles.add(((EntityPlayer)player).getProfile());
            }
         }

         ServerPing.ServerPingPlayerSample playerSample = new ServerPing.ServerPingPlayerSample(event.getMaxPlayers(), profiles.size());
         if (!profiles.isEmpty()) {
            Collections.shuffle(profiles);
            profiles = profiles.subList(0, Math.min(profiles.size(), SpigotConfig.playerSample));
         }

         playerSample.a(profiles.toArray(new GameProfile[0]));
         ServerPing ping = new ServerPing();
         ping.setFavicon(event.icon.value);
         ping.setMOTD(new ChatComponentText(event.getMotd()));
         ping.setPlayerSample(playerSample);
         ping.setServerInfo(new ServerPing.ServerData(this.minecraftServer.getServerModName() + " " + this.minecraftServer.getVersion(), 47));
         this.networkManager.handle(new PacketStatusOutServerInfo(ping));
      }
   }

   @Override
   public void a(PacketStatusInPing packetstatusinping) {
      this.networkManager.handle(new PacketStatusOutPong(packetstatusinping.a()));
      this.networkManager.close(a);
   }
}
