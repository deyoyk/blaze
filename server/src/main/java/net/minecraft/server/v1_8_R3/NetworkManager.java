package net.minecraft.server.v1_8_R3;

import com.mojang.authlib.properties.Property;
import com.velocitypowered.natives.compression.VelocityCompressor;
import com.velocitypowered.natives.compression.VelocityCompressorFactory;
import com.velocitypowered.natives.util.Natives;
import lol.deyo.blaze.Blaze;
import lol.deyo.blaze.api.protocol.PacketHandler;
import lol.deyo.blaze.optimizations.FastNetworkManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalServerChannel;
import io.netty.handler.timeout.TimeoutException;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.SocketAddress;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetworkManager extends SimpleChannelInboundHandler<Packet<?>> {
   public static final AttributeKey<EnumProtocol> c = AttributeKey.valueOf("protocol");
   private static final Logger g = LogManager.getLogger();
   public Channel channel;
   public SocketAddress l;
   public UUID spoofedUUID;
   public Property[] spoofedProfile;
   public boolean preparing = true;
   private PacketListener m;
   private IChatBaseComponent n;
   private boolean p;
   public final FastNetworkManager fastNetworkManager = new FastNetworkManager();
   private final PacketEncoder encoder;
   private final PacketDecoder decoder;
   private EnumProtocol currentProtocol;

   public NetworkManager(EnumProtocolDirection h) {
      this.encoder = new PacketEncoder();
      this.decoder = new PacketDecoder();
   }

   public NetworkManager(PacketEncoder encoder, PacketDecoder decoder) {
      this.encoder = encoder;
      this.decoder = decoder;
   }

   private void setProtocol(EnumProtocol protocol) {
      this.currentProtocol = protocol;
      this.encoder.setProtocol(protocol);
      this.decoder.setProtocol(protocol);
      this.channel.attr(c).set(protocol);
      this.channel.config().setAutoRead(true);
   }

   private void writeAndFlush(EnumProtocol protocolSwitch, Packet<?> packet, GenericFutureListener<? extends Future<? super Void>>[] agenericfuturelistener) {
      if (protocolSwitch != null) {
         this.setProtocol(protocolSwitch);
      }

      ChannelFuture future = this.channel.writeAndFlush(packet);
      if (agenericfuturelistener != null) {
         future.addListeners(agenericfuturelistener);
      }

      future.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
   }

   public void channelActive(ChannelHandlerContext channelhandlercontext) throws Exception {
      super.channelActive(channelhandlercontext);
      this.channel = channelhandlercontext.channel();
      this.l = this.channel.remoteAddress();
      this.preparing = false;

      try {
         this.a(EnumProtocol.HANDSHAKING);
      } catch (Throwable var3) {
         var3.printStackTrace();
      }
   }

   public void a(EnumProtocol enumprotocol) {
      this.setProtocol(enumprotocol);
   }

   public void channelInactive(ChannelHandlerContext channelhandlercontext) {
      this.close(new ChatMessage("disconnect.endOfStream"));
   }

   public void exceptionCaught(ChannelHandlerContext channelhandlercontext, Throwable throwable) {
      ChatMessage chatmessage;
      if (throwable instanceof TimeoutException) {
         chatmessage = new ChatMessage("disconnect.timeout");
      } else {
         chatmessage = new ChatMessage("disconnect.genericReason", "Internal Exception: " + throwable);
      }

      this.close(chatmessage);
   }

   protected void a(ChannelHandlerContext channelhandlercontext, Packet packet) throws Exception {
      if (this.channel.isActive()) {
         try {
            boolean shouldReceive = true;
            if (this.m instanceof PlayerConnection) {
               PlayerConnection connection = (PlayerConnection)this.m;
               PacketHandler[] handlers = Blaze.getInstance().getPacketHandlerArray();
               for (int i = 0; i < handlers.length; i++) {
                  shouldReceive = handlers[i].handleReceivedPacket(connection, packet);
               }
            }

            if (shouldReceive) {
               if (packet instanceof PacketPlayInUseEntity) {
                  ((PacketPlayInUseEntity)packet).receiveNanos = System.nanoTime();
               }
               packet.a(this.m);
            }
         } catch (CancelledPacketHandleException var7) {
         }
      }
   }

   public void a(PacketListener packetlistener) {
      Validate.notNull(packetlistener, "packetListener", new Object[0]);
      this.m = packetlistener;
   }

   public void handle(Packet<?> packet) {
      if (this.channel != null && this.channel.isActive()) {
         this.a(packet, null);
      }
   }

   public void handleInstant(Packet<?> packet) {
      if (this.channel == null || !this.channel.isActive()) {
         return;
      }

      Runnable write = () -> {
         ChannelFuture future = this.channel.writeAndFlush(packet);
         future.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
      };
      if (this.channel.eventLoop().inEventLoop()) {
         write.run();
      } else {
         this.channel.eventLoop().execute(write);
      }
   }

   public void sendLoginSequence(Packet<?> success, int compressionThreshold, Runnable after) {
      if (this.channel == null || !this.channel.isActive()) {
         return;
      }

      this.channel.eventLoop().execute(() -> {
         if (compressionThreshold >= 0) {
            ChannelFuture compressionWrite = this.channel.writeAndFlush(new PacketLoginOutSetCompression(compressionThreshold));
            compressionWrite.addListener(future -> {
               if (future.isSuccess()) {
                  this.a(compressionThreshold);
                  ChannelFuture successWrite = this.channel.writeAndFlush(success);
                  successWrite.addListener(successFuture -> {
                     if (successFuture.isSuccess()) {
                        after.run();
                     }
                  });
               }
            });
         } else {
            ChannelFuture successWrite = this.channel.writeAndFlush(success);
            successWrite.addListener(future -> {
               if (future.isSuccess()) {
                  after.run();
               }
            });
         }
      });
   }

   public void writePacketLazily(Packet<?> packet) {
      this.fastNetworkManager.writePacketLazily(this.channel, packet);
   }

   public void flushAndWriteCurrentQueue() {
      this.fastNetworkManager.flushAndWriteCurrentQueue(this.channel);
   }

   @SafeVarargs
   public final void a(
      Packet<?> packet,
      GenericFutureListener<? extends Future<? super Void>> genericfuturelistener,
      GenericFutureListener<? extends Future<? super Void>>... agenericfuturelistener
   ) {
      if (this.channel != null && this.channel.isActive()) {
         this.a(packet, (GenericFutureListener<? extends Future<? super Void>>[])ArrayUtils.add(agenericfuturelistener, 0, genericfuturelistener));
      }
   }

   public void a(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>>[] agenericfuturelistener) {
      EnumProtocol protocolForPacket = EnumProtocol.getProtocolForPacket(packet);
      EnumProtocol switchProtocol;
      if (protocolForPacket != this.currentProtocol) {
         switchProtocol = protocolForPacket;
         this.channel.config().setAutoRead(false);
      } else {
         switchProtocol = null;
      }

      if (this.channel.eventLoop().inEventLoop()) {
         this.writeAndFlush(switchProtocol, packet, agenericfuturelistener);
      } else {
         this.channel.eventLoop().execute(() -> this.writeAndFlush(switchProtocol, packet, agenericfuturelistener));
      }
   }

   public void a() {
      if (this.m instanceof IUpdatePlayerListBox) {
         ((IUpdatePlayerListBox)this.m).c();
      }
   }

   public void close(IChatBaseComponent ichatbasecomponent) {
      this.preparing = false;
      if (this.channel.isActive()) {
         this.channel.close();
         this.n = ichatbasecomponent;
      }
   }

   public void a(SecretKey secretkey) {
      ChannelPipeline pp = this.channel.pipeline();
      pp.addBefore("splitter", "decrypt", new PacketDecrypter(MinecraftEncryption.a(2, secretkey)));
      pp.addBefore("prepender", "encrypt", new PacketEncrypter(MinecraftEncryption.a(1, secretkey)));
   }

   public void a(int compressionThreshold) {
      if (compressionThreshold >= 0) {
         VelocityCompressor compressor = ((VelocityCompressorFactory)Natives.compress.get()).create(-1);
         if (this.channel.pipeline().get("decompress") instanceof PacketDecompressor) {
            ((PacketDecompressor)this.channel.pipeline().get("decompress")).setThreshold(compressionThreshold);
         } else {
            this.channel.pipeline().addBefore("decoder", "decompress", new PacketDecompressor(compressor, compressionThreshold));
         }

         if (this.channel.pipeline().get("compress") instanceof PacketCompressor) {
            ((PacketCompressor)this.channel.pipeline().get("decompress")).setThreshold(compressionThreshold);
         } else {
            this.channel.pipeline().addBefore("encoder", "compress", new PacketCompressor(compressor, compressionThreshold));
         }
      } else {
         if (this.channel.pipeline().get("decompress") instanceof PacketDecompressor) {
            this.channel.pipeline().remove("decompress");
         }

         if (this.channel.pipeline().get("compress") instanceof PacketCompressor) {
            this.channel.pipeline().remove("compress");
         }
      }
   }

   public void l() {
      if (this.channel != null && !this.channel.isActive()) {
         if (!this.p) {
            this.p = true;
            if (this.n != null) {
               this.m.a(this.n);
            } else if (this.m != null) {
               this.m.a(new ChatComponentText("Disconnected"));
            }
         } else {
            g.warn("handleDisconnection() called twice");
         }
      }
   }

   protected void channelRead0(ChannelHandlerContext channelhandlercontext, Packet<?> object) throws Exception {
      this.a(channelhandlercontext, object);
   }

   public boolean c() {
      return this.channel instanceof LocalChannel || this.channel instanceof LocalServerChannel;
   }

   public boolean g() {
      return this.channel != null && this.channel.isActive();
   }

   public boolean h() {
      return this.channel == null;
   }

   public PacketListener getPacketListener() {
      return this.m;
   }

   public IChatBaseComponent j() {
      return this.n;
   }

   public void k() {
      this.channel.config().setAutoRead(false);
   }

   public SocketAddress getSocketAddress() {
      return this.l;
   }

   public SocketAddress getRawAddress() {
      return this.channel.remoteAddress();
   }
}
