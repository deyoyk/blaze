package net.minecraft.server.v1_8_R3;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.util.ByteProcessor;
import java.util.List;

public class PacketSplitter extends ByteToMessageDecoder {
   protected void decode(ChannelHandlerContext channelhandlercontext, ByteBuf in, List<Object> list) throws Exception {
      if (!channelhandlercontext.channel().isActive()) {
         in.clear();
      } else {
         int packetStart = in.forEachByte(ByteProcessor.FIND_NON_NUL);
         if (packetStart != -1) {
            in.readerIndex(packetStart);
            in.markReaderIndex();
            int preIndex = in.readerIndex();
            int length = readRawVarInt21(in);
            if (preIndex != in.readerIndex()) {
               if (length < 0) {
                  throw new CorruptedFrameException("Bad packet length");
               } else {
                  if (length > 0) {
                     if (in.readableBytes() < length) {
                        in.resetReaderIndex();
                     } else {
                        list.add(in.readRetainedSlice(length));
                     }
                  }
               }
            }
         }
      }
   }

   private static int readRawVarInt21(ByteBuf buffer) {
      if (buffer.readableBytes() < 4) {
         return readRawVarintSmallBuf(buffer);
      } else {
         int wholeOrMore = buffer.getIntLE(buffer.readerIndex());
         int atStop = ~wholeOrMore & 8421504;
         if (atStop == 0) {
            throw new CorruptedFrameException("VarInt too big");
         } else {
            int bitsToKeep = Integer.numberOfTrailingZeros(atStop) + 1;
            buffer.skipBytes(bitsToKeep >> 3);
            int preservedBytes = wholeOrMore & (atStop ^ atStop - 1);
            preservedBytes = preservedBytes & 8323199 | (preservedBytes & 32512) >> 1;
            return preservedBytes & 16383 | (preservedBytes & 1073676288) >> 2;
         }
      }
   }

   private static int readRawVarintSmallBuf(ByteBuf buffer) {
      if (!buffer.isReadable()) {
         return 0;
      } else {
         buffer.markReaderIndex();
         byte tmp = buffer.readByte();
         if (tmp >= 0) {
            return tmp;
         } else {
            int result = tmp & 127;
            if (!buffer.isReadable()) {
               buffer.resetReaderIndex();
               return 0;
            } else if ((tmp = buffer.readByte()) >= 0) {
               return result | tmp << 7;
            } else {
               result |= (tmp & 127) << 7;
               if (!buffer.isReadable()) {
                  buffer.resetReaderIndex();
                  return 0;
               } else {
                  return (tmp = buffer.readByte()) >= 0 ? result | tmp << 14 : result | (tmp & 127) << 14;
               }
            }
         }
      }
   }
}
