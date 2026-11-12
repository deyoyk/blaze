package net.minecraft.server.v1_8_R3;

import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.properties.Property;
import lol.deyo.blaze.Blaze;
import io.netty.channel.ChannelFutureListener;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.logging.Level;
import javax.crypto.SecretKey;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

public class LoginListener implements PacketLoginInListener, IUpdatePlayerListBox {
   private static final Logger c = LogManager.getLogger();
   private static final Random random = new Random();
   public final NetworkManager networkManager;
   private final byte[] e = new byte[4];
   private final MinecraftServer server;
   private final String j;
   public String hostname = "";
   private LoginListener.EnumProtocolState g = LoginListener.EnumProtocolState.HELLO;
   private int h;
   private GameProfile i;
   private SecretKey loginKey;
   private Future<?> future;

   public LoginListener(MinecraftServer minecraftserver, NetworkManager networkmanager) {
      this.j = "";
      this.server = minecraftserver;
      this.networkManager = networkmanager;
      random.nextBytes(this.e);
   }

   @Override
   public void c() {
      if (this.h++ == 600) {
         this.future.cancel(true);
         this.disconnect("Took too long to log in");
      }
   }

   public void disconnect(String s) {
      try {
         c.info("Disconnecting {}: {}", new Object[]{this.d(), s});
         ChatComponentText chatcomponenttext = new ChatComponentText(s);
         this.networkManager.handle(new PacketLoginOutDisconnect(chatcomponenttext));
         this.networkManager.close(chatcomponenttext);
      } catch (Exception var3) {
         c.error("Error whilst disconnecting player", var3);
      }
   }

   public void initUUID() {
      UUID uuid;
      if (this.networkManager.spoofedUUID != null) {
         uuid = this.networkManager.spoofedUUID;
      } else {
         uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + this.i.getName()).getBytes(Charsets.UTF_8));
      }

      this.i = new GameProfile(uuid, this.i.getName());
      if (this.networkManager.spoofedProfile != null) {
         for (Property property : this.networkManager.spoofedProfile) {
            this.i.getProperties().put(property.getName(), property);
         }
      }
   }

   public void b() {
      MinecraftServer.getServer()
         .postToMainThread(
            () -> {
               EntityPlayer s = this.server.getPlayerList().attemptLogin(this, this.i, this.hostname);
               if (s != null) {
                  this.g = LoginListener.EnumProtocolState.ACCEPTED;
                  int compressionThreshold = this.server.aK();
                  if (this.networkManager.c()) {
                     compressionThreshold = -1;
                  }

                  this.networkManager.sendLoginSequence(new PacketLoginOutSuccess(this.i), compressionThreshold, () -> {
                     MinecraftServer.getServer().postToMainThread(() -> {
                        try {
                           this.server.getPlayerList().a(this.networkManager, this.server.getPlayerList().processLogin(this.i, s));
                        } catch (NullPointerException var4) {
                           s.getBukkitEntity().kickPlayer("Failed to process your login request.");
                        }
                     });
                  });
               }
            }
         );
   }

   @Override
   public void a(IChatBaseComponent ichatbasecomponent) {
      c.info("{} lost connection: {}", new Object[]{this.d(), ichatbasecomponent.c()});
   }

   public String d() {
      String socketAddress = this.networkManager == null
         ? null
         : (this.networkManager.getSocketAddress() == null ? null : this.networkManager.getSocketAddress().toString());
      return this.i != null ? this.i + " (" + socketAddress + ")" : socketAddress;
   }

   @Override
   public void a(PacketLoginInStart packetlogininstart) {
      Validate.validState(this.g == LoginListener.EnumProtocolState.HELLO, "Unexpected hello packet", new Object[0]);
      this.i = packetlogininstart.a();
      if (this.server.getOnlineMode() && !this.networkManager.c()) {
         this.g = LoginListener.EnumProtocolState.KEY;
         this.networkManager.handle(new PacketLoginOutEncryptionBegin(this.j, this.server.Q().getPublic(), this.e));
      } else {
         this.future = Blaze.getInstance().getThreadingManager().getAsyncExecutor().submit(() -> {
            try {
               this.initUUID();
               new LoginListener.LoginHandler().fireEvents();
            } catch (Exception var2) {
               this.disconnect("Failed to verify username!");
               this.server.server.getLogger().log(Level.WARNING, "Exception verifying " + this.i.getName(), (Throwable)var2);
            }
         });
      }
   }

   @Override
   public void a(PacketLoginInEncryptionBegin packetlogininencryptionbegin) {
      Validate.validState(this.g == LoginListener.EnumProtocolState.KEY, "Unexpected key packet", new Object[0]);
      PrivateKey privatekey = this.server.Q().getPrivate();
      if (!Arrays.equals(this.e, packetlogininencryptionbegin.b(privatekey))) {
         throw new IllegalStateException("Invalid nonce!");
      } else {
         this.loginKey = packetlogininencryptionbegin.a(privatekey);
         this.g = LoginListener.EnumProtocolState.AUTHENTICATING;
         this.networkManager.a(this.loginKey);
         this.future = Blaze.getInstance().getThreadingManager().getAsyncExecutor().submit(() -> {
            GameProfile gameprofile = this.i;

            try {
               String s = new BigInteger(Objects.requireNonNull(MinecraftEncryption.a(this.j, this.server.Q().getPublic(), this.loginKey))).toString(16);
               this.i = this.server.aD().hasJoinedServer(new GameProfile(null, gameprofile.getName()), s);
               if (this.i != null) {
                  if (!this.networkManager.g()) {
                     return;
                  }

                  new LoginListener.LoginHandler().fireEvents();
               } else {
                  this.disconnect("Failed to verify username!");
                  c.error("Username '{}' tried to join with an invalid session", new Object[]{gameprofile.getName()});
               }
            } catch (AuthenticationUnavailableException var3) {
               this.disconnect("Authentication servers are down. Please try again later, sorry!");
               c.error("Couldn't verify username because servers are unavailable");
            } catch (Exception var4) {
               this.disconnect("Failed to verify username!");
               this.server.server.getLogger().log(Level.WARNING, "Exception verifying " + gameprofile.getName(), (Throwable)var4);
            }
         });
      }
   }

   protected GameProfile a(GameProfile gameprofile) {
      UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + gameprofile.getName()).getBytes(Charsets.UTF_8));
      return new GameProfile(uuid, gameprofile.getName());
   }

   static enum EnumProtocolState {
      HELLO,
      KEY,
      AUTHENTICATING,
      READY_TO_ACCEPT,
      e,
      ACCEPTED;
   }

   public class LoginHandler {
      public void fireEvents() {
         AsyncPlayerPreLoginEvent event = new AsyncPlayerPreLoginEvent(
            LoginListener.this.i.getName(),
            ((InetSocketAddress)LoginListener.this.networkManager.getSocketAddress()).getAddress(),
            LoginListener.this.i.getId()
         );
         LoginListener.this.server.server.getPluginManager().callEvent(event);
         if (event.getLoginResult() != Result.ALLOWED) {
            LoginListener.this.disconnect(event.getKickMessage());
         } else {
            LoginListener.c.info("UUID of player {} is {}", new Object[]{LoginListener.this.i.getName(), LoginListener.this.i.getId()});
            LoginListener.this.g = LoginListener.EnumProtocolState.READY_TO_ACCEPT;
            LoginListener.this.b();
         }
      }
   }
}
