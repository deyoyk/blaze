package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Lists;
import lol.deyo.blaze.configs.BlazeConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.concurrent.Future;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerConnection {
   private static final Logger e = LogManager.getLogger();
   private static final WriteBufferWaterMark SERVER_WRITE_MARK = new WriteBufferWaterMark(1048576, 2097152);
   private final MinecraftServer f;
   private final List<ChannelFuture> listeningChannels = Collections.synchronizedList(Lists.newArrayList());
   private final List<NetworkManager> connectedChannels = (List<NetworkManager>)((Boolean)BlazeConfig.networkBlockingJoin.getValue()
      ? Lists.<NetworkManager>newArrayList()
      : Collections.synchronizedList(Lists.<NetworkManager>newArrayList()));
   public volatile boolean d;
   public final Queue<NetworkManager> pending = new ConcurrentLinkedQueue<>();

   private void addPending() {
      if ((Boolean)BlazeConfig.networkBlockingJoin.getValue()) {
         NetworkManager manager;
         while ((manager = this.pending.poll()) != null) {
            this.connectedChannels.add(manager);
         }
      }
   }

   public ServerConnection(MinecraftServer minecraftserver) {
      this.f = minecraftserver;
      this.d = true;
   }

   public void a(InetAddress inetaddress, int i) throws IOException {
      synchronized (this.listeningChannels) {
         this.addPending();
         int parentNettyThreads = (Integer)BlazeConfig.parentNettyThreads.getValue();
         int childNettyThreads = (Integer)BlazeConfig.childNettyThreads.getValue();
         EventLoopGroup parentGroup = null;
         EventLoopGroup childGroup = null;
         Class<? extends ServerChannel> channel = null;
         if (childNettyThreads == 0) {
            childNettyThreads = Runtime.getRuntime().availableProcessors();
         }

         if (this.f.ai()) {
            if (Epoll.isAvailable()) {
               channel = EpollServerSocketChannel.class;
               parentGroup = new EpollEventLoopGroup(parentNettyThreads);
               childGroup = new EpollEventLoopGroup(childNettyThreads);
               e.info("Using Epoll channel type");
            } else if (KQueue.isAvailable()) {
               channel = KQueueServerSocketChannel.class;
               parentGroup = new KQueueEventLoopGroup(parentNettyThreads);
               childGroup = new KQueueEventLoopGroup(childNettyThreads);
               e.info("Using kqueue channel type");
            }
         }

         if (channel == null) {
            channel = NioServerSocketChannel.class;
            parentGroup = new NioEventLoopGroup(parentNettyThreads);
            childGroup = new NioEventLoopGroup(childNettyThreads);
            e.info("Using default channel type");
         }

         this.listeningChannels
            .add(
               ((ServerBootstrap)((ServerBootstrap)new ServerBootstrap().channel(channel))
                     .childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, SERVER_WRITE_MARK)
                     .childHandler(
                        new ChannelInitializer<Channel>() {
                           protected void initChannel(Channel channel) {
                              try {
                                 ChannelConfig config = channel.config();
                                 config.setOption(ChannelOption.TCP_NODELAY, true);
                                 config.setOption(ChannelOption.IP_TOS, 24);
                                 config.setOption(ChannelOption.TCP_FASTOPEN, 1);
                                 config.setOption(ChannelOption.TCP_FASTOPEN_CONNECT, true);
                                 config.setAllocator(ByteBufAllocator.DEFAULT);
                              } catch (ChannelException var5) {
                              }

                              PacketDecoder decoder = new PacketDecoder();
                              PacketEncoder encoder = new PacketEncoder();
                              channel.pipeline()
                                 .addLast("timeout", new ReadTimeoutHandler(30))
                                 .addLast("legacy_query", new LegacyPingHandler(ServerConnection.this))
                                 .addLast("splitter", new PacketSplitter())
                                 .addLast("decoder", decoder)
                                 .addLast("prepender", PacketPrepender.INSTANCE)
                                 .addLast("encoder", encoder);
                              NetworkManager networkmanager = new NetworkManager(encoder, decoder);
                              if ((Boolean)BlazeConfig.networkBlockingJoin.getValue()) {
                                 ServerConnection.this.pending.add(networkmanager);
                              } else {
                                 ServerConnection.this.connectedChannels.add(networkmanager);
                              }

                              channel.pipeline().addLast("packet_handler", networkmanager);
                              networkmanager.a(new HandshakeListener(ServerConnection.this.f, networkmanager));
                           }
                        }
                     )
                     .group(parentGroup, childGroup)
                     .localAddress(inetaddress, i))
                  .bind()
                  .syncUninterruptibly()
            );
      }
   }

   public void b() {
      this.d = false;

      for (ChannelFuture future : this.listeningChannels) {
         try {
            future.channel().close().sync();
         } catch (InterruptedException var4) {
            e.error("Interrupted whilst closing channel");
         }
      }
   }

   public void c() {
      synchronized (this.connectedChannels) {
         this.addPending();
         Iterator<NetworkManager> iterator = this.connectedChannels.iterator();

         while (iterator.hasNext()) {
            NetworkManager networkmanager = iterator.next();
            if (!networkmanager.h()) {
               if (networkmanager.g()) {
                  try {
                     networkmanager.a();
                  } catch (Exception var7) {
                     if (networkmanager.c()) {
                        CrashReport crashreport = CrashReport.a(var7, "Ticking memory connection");
                        CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Ticking connection");
                        crashreportsystemdetails.a("Connection", networkmanager::toString);
                        throw new ReportedException(crashreport);
                     }

                     e.warn("Failed to handle packet for {}", new Object[]{networkmanager.getSocketAddress(), var7});
                     ChatComponentText chatcomponenttext = new ChatComponentText("Internal server error");
                     networkmanager.a(new PacketPlayOutKickDisconnect(chatcomponenttext), future -> networkmanager.close(chatcomponenttext));
                     networkmanager.k();
                  }
               } else if (!networkmanager.preparing) {
                  iterator.remove();
                  networkmanager.l();
               }
            }
         }
      }
   }

   public MinecraftServer d() {
      return this.f;
   }
}
