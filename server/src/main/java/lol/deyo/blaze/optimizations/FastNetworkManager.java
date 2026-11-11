package lol.deyo.blaze.optimizations;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.v1_8_R3.Packet;

public class FastNetworkManager {
   private final List<Packet<?>>[] trackerQueue = new List[4];

   public FastNetworkManager() {
      for (int i = 0; i < 4; i++) {
         this.trackerQueue[i] = new ArrayList<>();
      }
   }

   public void writePacketLazily(Channel channel, Packet<?> packet) {
      if (channel != null && channel.isActive()) {
         channel.eventLoop().execute(() -> {
            ChannelFuture future = channel.write(packet);
            future.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
         });
      }
   }

   public void queueMainThreadPacket(Packet<?> packet) {
      this.trackerQueue[3].add(packet);
   }

   public void queueEntityTrackerPacket(Packet<?> packet, int trackerThread) {
      this.trackerQueue[trackerThread].add(packet);
   }

   public void flushAndWriteCurrentQueue(Channel channel) {
      if (channel != null && channel.isActive()) {
         List[] writingTrackerQueues = new List[4];

         for (int i = 0; i < this.trackerQueue.length; i++) {
            List<Packet<?>> packets = this.trackerQueue[i];
            writingTrackerQueues[i] = packets;
            this.trackerQueue[i] = new ArrayList<>(packets.size());
         }

         channel.eventLoop().execute(() -> {
            List[] var5 = writingTrackerQueues;
            int var4x = writingTrackerQueues.length;

            for (int var3x = 0; var3x < var4x; var3x++) {
               for (Object packet : var5[var3x]) {
                  ChannelFuture future = channel.write(packet);
                  future.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
               }
            }

            channel.flush();

            for (int ix = 0; ix < writingTrackerQueues.length; ix++) {
               writingTrackerQueues[ix].clear();
               writingTrackerQueues[ix] = null;
            }
         });
      }
   }
}
