package net.minecraft.server.v1_8_R3;

import lol.deyo.blaze.optimizations.VarIntUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.io.IOException;
import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {
   private EnumProtocol protocol;

   protected void decode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, List<Object> list) throws Exception {
      if (bytebuf.isReadable()) {
         int packetId = VarIntUtil.readVarInt(bytebuf);
         Packet<?> packet = this.protocol.getServerBoundPacketForId(packetId);
         if (packet == null) {
            throw new IOException("Bad packet id " + packetId);
         } else {
            packet.a(new PacketDataSerializer(bytebuf));
            if (bytebuf.isReadable()) {
               throw new IOException(
                  "Packet "
                     + this.protocol.getProtocolId()
                     + "/"
                     + packetId
                     + " ("
                     + packet.getClass().getSimpleName()
                     + ") was larger than I expected, found "
                     + bytebuf.readableBytes()
                     + " bytes extra whilst reading packet "
                     + packetId
               );
            } else {
               list.add(packet);
            }
         }
      }
   }

   public void setProtocol(EnumProtocol protocol) {
      this.protocol = protocol;
   }
}
