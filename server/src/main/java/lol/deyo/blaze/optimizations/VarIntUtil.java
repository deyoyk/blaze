package lol.deyo.blaze.optimizations;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CorruptedFrameException;

public class VarIntUtil {
   private static final int MAXIMUM_VARINT_SIZE = 5;
   private static final int[] VAR_INT_LENGTHS = new int[65];

   static {
      for (int i = 0; i <= 32; i++) {
         VAR_INT_LENGTHS[i] = (int)Math.ceil((31.0 - (i - 1)) / 7.0);
      }

      VAR_INT_LENGTHS[32] = 1;
   }

   public static int readVarInt(ByteBuf buf) {
      int readable = buf.readableBytes();
      if (readable == 0) {
         throw new CorruptedFrameException("VarInt not readable");
      } else {
         int k = buf.readByte();
         if ((k & 128) != 128) {
            return k;
         } else {
            int maxRead = Math.min(5, readable);
            int i = k & 127;

            for (int j = 1; j < maxRead; j++) {
               int var6 = buf.readByte();
               i |= (var6 & 127) << j * 7;
               if ((var6 & 128) != 128) {
                  return i;
               }
            }

            throw new CorruptedFrameException("VarInt is invalid");
         }
      }
   }

   public static int varIntBytes(int value) {
      return VAR_INT_LENGTHS[Integer.numberOfLeadingZeros(value)];
   }

   public static void writeVarInt(ByteBuf buf, int value) {
      if ((value & -128) == 0) {
         buf.writeByte(value);
      } else if ((value & -16384) == 0) {
         int w = (value & 127 | 128) << 8 | value >>> 7;
         buf.writeShort(w);
      } else {
         writeVarIntFull(buf, value);
      }
   }

   private static void writeVarIntFull(ByteBuf buf, int value) {
      if ((value & -128) == 0) {
         buf.writeByte(value);
      } else if ((value & -16384) == 0) {
         int w = (value & 127 | 128) << 8 | value >>> 7;
         buf.writeShort(w);
      } else if ((value & -2097152) == 0) {
         int w = (value & 127 | 128) << 16 | (value >>> 7 & 127 | 128) << 8 | value >>> 14;
         buf.writeMedium(w);
      } else if ((value & -268435456) == 0) {
         int w = (value & 127 | 128) << 24 | (value >>> 7 & 127 | 128) << 16 | (value >>> 14 & 127 | 128) << 8 | value >>> 21;
         buf.writeInt(w);
      } else {
         int w = (value & 127 | 128) << 24 | (value >>> 7 & 127 | 128) << 16 | (value >>> 14 & 127 | 128) << 8 | value >>> 21 & 127 | 128;
         buf.writeInt(w);
         buf.writeByte(value >>> 28);
      }
   }
}
