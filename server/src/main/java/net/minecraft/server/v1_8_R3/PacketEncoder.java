package net.minecraft.server.v1_8_R3;

import lol.deyo.blaze.optimizations.VarIntUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.io.IOException;

public class PacketEncoder extends MessageToByteEncoder<Packet<?>> {
   private EnumProtocol protocol;

   protected void encode(ChannelHandlerContext channelHandlerContext, Packet<?> packet, ByteBuf byteBuf) throws Exception {
      int packetId = this.protocol.getClientBoundPacketId(packet);
      if (packetId == -1) {
         throw new IOException("Can't serialize unregistered packet");
      } else {
         VarIntUtil.writeVarInt(byteBuf, packetId);

         try {
            packet.b(new PacketDataSerializer(byteBuf));
         } catch (Throwable var6) {
         }
      }
   }

   public void setProtocol(EnumProtocol protocol) {
      this.protocol = protocol;
   }
}
