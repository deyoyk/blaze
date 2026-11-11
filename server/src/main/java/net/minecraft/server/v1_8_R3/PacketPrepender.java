package net.minecraft.server.v1_8_R3;

import lol.deyo.blaze.optimizations.VarIntUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.util.List;

@Sharable
public class PacketPrepender extends MessageToMessageEncoder<ByteBuf> {
   public static final PacketPrepender INSTANCE = new PacketPrepender();

   protected void encode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) {
      int readableBytes = buf.readableBytes();
      ByteBuf varIntBuffer = ctx.alloc().buffer(VarIntUtil.varIntBytes(readableBytes));
      VarIntUtil.writeVarInt(varIntBuffer, readableBytes);
      out.add(varIntBuffer);
      out.add(buf.retain());
   }
}
