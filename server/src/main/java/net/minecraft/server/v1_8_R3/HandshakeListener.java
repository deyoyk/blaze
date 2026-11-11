package net.minecraft.server.v1_8_R3;

import com.google.gson.Gson;
import com.mojang.authlib.properties.Property;
import com.mojang.util.UUIDTypeAdapter;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.text.MessageFormat;
import java.util.Map.Entry;
import org.apache.logging.log4j.LogManager;
import org.spigotmc.SpigotConfig;

public class HandshakeListener implements PacketHandshakingInListener {
   private static final Gson gson = new Gson();
   private static final Object2LongOpenHashMap<InetAddress> throttleTracker = new Object2LongOpenHashMap();
   private static int throttleCounter = 0;
   private final MinecraftServer a;
   private final NetworkManager b;

   public HandshakeListener(MinecraftServer minecraftserver, NetworkManager networkmanager) {
      this.a = minecraftserver;
      this.b = networkmanager;
   }

   @Override
   public void a(PacketHandshakingInSetProtocol packethandshakinginsetprotocol) {
      switch (HandshakeListener.SyntheticClass_1.a[packethandshakinginsetprotocol.a().ordinal()]) {
         case 1:
            this.b.a(EnumProtocol.LOGIN);

            try {
               long currentTime = System.currentTimeMillis();
               long connectionThrottle = MinecraftServer.getServer().server.getConnectionThrottle();
               InetAddress address = ((InetSocketAddress)this.b.getSocketAddress()).getAddress();
               if (throttleTracker.containsKey(address)
                  && !"127.0.0.1".equals(address.getHostAddress())
                  && currentTime - throttleTracker.getLong(address) < connectionThrottle) {
                  throttleTracker.put(address, currentTime);
                  ChatComponentText chatcomponenttext = new ChatComponentText("Connection throttled! Please wait before reconnecting.");
                  this.b.handle(new PacketLoginOutDisconnect(chatcomponenttext));
                  this.b.close(chatcomponenttext);
                  return;
               }

               throttleTracker.put(address, currentTime);
               throttleCounter++;
               if (throttleCounter > 200) {
                  throttleCounter = 0;
                  throttleTracker.entrySet().removeIf(inetAddressLongEntry -> (Long)inetAddressLongEntry.getValue() > connectionThrottle);
               }
            } catch (Throwable var8) {
               LogManager.getLogger().debug("Failed to check connection throttle", var8);
            }

            int ver = packethandshakinginsetprotocol.b();
            if (ver > 47) {
               ChatComponentText chatcomponenttext = new ChatComponentText(MessageFormat.format(SpigotConfig.outdatedServerMessage, "1.8.8"));
               this.b.handle(new PacketLoginOutDisconnect(chatcomponenttext));
               this.b.close(chatcomponenttext);
            } else if (ver < 47) {
               ChatComponentText chatcomponenttext = new ChatComponentText(MessageFormat.format(SpigotConfig.outdatedClientMessage, "1.8.8"));
               this.b.handle(new PacketLoginOutDisconnect(chatcomponenttext));
               this.b.close(chatcomponenttext);
            } else {
               this.b.a(new LoginListener(this.a, this.b));
               if (SpigotConfig.bungee) {
                  String[] split = packethandshakinginsetprotocol.hostname.split("\u0000");
                  if (split.length != 3 && split.length != 4) {
                     ChatComponentText chatcomponenttext = new ChatComponentText(
                        "If you wish to use IP forwarding, please enable it in your BungeeCord config as well!"
                     );
                     this.b.handle(new PacketLoginOutDisconnect(chatcomponenttext));
                     this.b.close(chatcomponenttext);
                     return;
                  }

                  packethandshakinginsetprotocol.hostname = split[0];
                  this.b.l = new InetSocketAddress(split[1], ((InetSocketAddress)this.b.getSocketAddress()).getPort());
                  this.b.spoofedUUID = UUIDTypeAdapter.fromString(split[2]);
                  if (split.length == 4) {
                     this.b.spoofedProfile = (Property[])gson.fromJson(split[3], Property[].class);
                  }
               }

               ((LoginListener)this.b.getPacketListener()).hostname = packethandshakinginsetprotocol.hostname + ":" + packethandshakinginsetprotocol.port;
            }
            break;
         case 2:
            this.b.a(EnumProtocol.STATUS);
            this.b.a(new PacketStatusListener(this.a, this.b));
            break;
         default:
            throw new UnsupportedOperationException("Invalid intention " + packethandshakinginsetprotocol.a());
      }
   }

   @Override
   public void a(IChatBaseComponent ichatbasecomponent) {
   }

   static class SyntheticClass_1 {
      static final int[] a = new int[EnumProtocol.values().length];

      static {
         try {
            a[EnumProtocol.LOGIN.ordinal()] = 1;
         } catch (NoSuchFieldError var2) {
         }

         try {
            a[EnumProtocol.STATUS.ordinal()] = 2;
         } catch (NoSuchFieldError var1) {
         }
      }
   }
}
