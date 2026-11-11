package net.minecraft.server.v1_8_R3;

import com.google.common.base.Charsets;
import lol.deyo.blaze.optimizations.VarIntUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.ByteBufUtil;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import io.netty.util.ByteProcessor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;

public class PacketDataSerializer extends ByteBuf {
   private final ByteBuf a;

   public PacketDataSerializer(ByteBuf bytebuf) {
      this.a = bytebuf;
   }

   public static int a(int i) {
      for (int j = 1; j < 5; j++) {
         if ((i & -1 << j * 7) == 0) {
            return j;
         }
      }

      return 5;
   }

   public void a(byte[] abyte) {
      this.b(abyte.length);
      this.writeBytes(abyte);
   }

   public byte[] a() {
      return this.readByteArray(MinecraftServer.HAS_PROTOCOL_SUPPORT ? 33553408 : 32767);
   }

   public byte[] readByteArray(int limit) {
      int len = this.e();
      if (len > limit) {
         throw new DecoderException("The received a byte array longer than allowed " + len + " > " + limit);
      } else {
         byte[] abyte = new byte[len];
         this.readBytes(abyte);
         return abyte;
      }
   }

   public BlockPosition c() {
      return BlockPosition.fromLong(this.readLong());
   }

   public void a(BlockPosition blockposition) {
      this.writeLong(blockposition.asLong());
   }

   public IChatBaseComponent d() throws IOException {
      return IChatBaseComponent.ChatSerializer.a(this.c(32767));
   }

   public void a(IChatBaseComponent ichatbasecomponent) throws IOException {
      this.a(IChatBaseComponent.ChatSerializer.a(ichatbasecomponent));
   }

   public <T extends Enum<T>> T a(Class<T> oclass) {
      return oclass.getEnumConstants()[this.e()];
   }

   public void a(Enum<?> oenum) {
      this.b(oenum.ordinal());
   }

   public int e() {
      return VarIntUtil.readVarInt(this.a);
   }

   public long f() {
      long i = 0L;
      int j = 0;

      byte b0;
      do {
         b0 = this.readByte();
         i |= (long)(b0 & 127) << j++ * 7;
         if (j > 10) {
            throw new RuntimeException("VarLong too big");
         }
      } while ((b0 & 128) == 128);

      return i;
   }

   public void a(UUID uuid) {
      this.writeLong(uuid.getMostSignificantBits());
      this.writeLong(uuid.getLeastSignificantBits());
   }

   public UUID g() {
      return new UUID(this.readLong(), this.readLong());
   }

   public void b(int i) {
      VarIntUtil.writeVarInt(this.a, i);
   }

   public void b(long i) {
      while ((i & -128L) != 0L) {
         this.writeByte((int)(i & 127L) | 128);
         i >>>= 7;
      }

      this.writeByte((int)i);
   }

   public void a(NBTTagCompound nbttagcompound) {
      if (nbttagcompound == null) {
         this.writeByte(0);
      } else {
         try {
            NBTCompressedStreamTools.a(nbttagcompound, (java.io.OutputStream)new ByteBufOutputStream(this));
         } catch (Exception var3) {
            throw new EncoderException(var3);
         }
      }
   }

   public NBTTagCompound h() throws IOException {
      int i = this.readerIndex();
      byte b0 = this.readByte();
      if (b0 == 0) {
         return null;
      } else {
         this.readerIndex(i);
         return NBTCompressedStreamTools.a(new ByteBufInputStream(this), new NBTReadLimiter(56000L));
      }
   }

   public void a(ItemStack itemstack) {
      if (itemstack != null && itemstack.getItem() != null) {
         this.writeShort(Item.getId(itemstack.getItem()));
         this.writeByte(itemstack.count);
         this.writeShort(itemstack.getData());
         NBTTagCompound nbttagcompound = null;
         if (itemstack.getItem().usesDurability() || itemstack.getItem().p()) {
            itemstack = itemstack.cloneItemStack();
            CraftItemStack.setItemMeta(itemstack, CraftItemStack.getItemMeta(itemstack));
            nbttagcompound = itemstack.getTag();
         }

         this.a(nbttagcompound);
      } else {
         this.writeShort(-1);
      }
   }

   public ItemStack i() throws IOException {
      ItemStack itemstack = null;
      short itemId = this.readShort();
      if (itemId >= 0) {
         itemstack = new ItemStack(Item.getById(itemId), this.readByte(), this.readShort());
         itemstack.setTag(this.h());
         if (itemstack.getTag() != null) {
            CraftItemStack.setItemMeta(itemstack, CraftItemStack.getItemMeta(itemstack));
         }
      }

      return itemstack;
   }

   public String c(int i) {
      int j = this.e();
      if (j > i * 4) {
         throw new DecoderException("The received encoded string buffer length is longer than maximum allowed (" + j + " > " + i * 4 + ")");
      } else if (j < 0) {
         throw new DecoderException("The received encoded string buffer length is less than zero! Weird string!");
      } else {
         String str = this.toString(this.readerIndex(), j, StandardCharsets.UTF_8);
         this.readerIndex(this.readerIndex() + j);
         if (str.length() > i) {
            throw new DecoderException("The received string length is longer than maximum allowed (" + j + " > " + i + ")");
         } else {
            return str;
         }
      }
   }

   public PacketDataSerializer a(String s) {
      int utf8Bytes = ByteBufUtil.utf8Bytes(s);
      if (utf8Bytes > 32767) {
         throw new EncoderException("String too big (was " + s.length() + " bytes encoded, max " + 32767 + ")");
      } else {
         this.b(utf8Bytes);
         this.writeCharSequence(s, Charsets.UTF_8);
         return this;
      }
   }

   public int capacity() {
      return this.a.capacity();
   }

   public ByteBuf capacity(int i) {
      return this.a.capacity(i);
   }

   public int maxCapacity() {
      return this.a.maxCapacity();
   }

   public ByteBufAllocator alloc() {
      return this.a.alloc();
   }

   public ByteOrder order() {
      return this.a.order();
   }

   public ByteBuf order(ByteOrder byteorder) {
      return this.a.order(byteorder);
   }

   public ByteBuf unwrap() {
      return this.a.unwrap();
   }

   public boolean isDirect() {
      return this.a.isDirect();
   }

   public boolean isReadOnly() {
      return this.a.isReadOnly();
   }

   public ByteBuf asReadOnly() {
      return this.a.asReadOnly();
   }

   public int readerIndex() {
      return this.a.readerIndex();
   }

   public ByteBuf readerIndex(int i) {
      return this.a.readerIndex(i);
   }

   public int writerIndex() {
      return this.a.writerIndex();
   }

   public ByteBuf writerIndex(int i) {
      return this.a.writerIndex(i);
   }

   public ByteBuf setIndex(int i, int j) {
      return this.a.setIndex(i, j);
   }

   public int readableBytes() {
      return this.a.readableBytes();
   }

   public int writableBytes() {
      return this.a.writableBytes();
   }

   public int maxWritableBytes() {
      return this.a.maxWritableBytes();
   }

   public boolean isReadable() {
      return this.a.isReadable();
   }

   public boolean isReadable(int i) {
      return this.a.isReadable(i);
   }

   public boolean isWritable() {
      return this.a.isWritable();
   }

   public boolean isWritable(int i) {
      return this.a.isWritable(i);
   }

   public ByteBuf clear() {
      return this.a.clear();
   }

   public ByteBuf markReaderIndex() {
      return this.a.markReaderIndex();
   }

   public ByteBuf resetReaderIndex() {
      return this.a.resetReaderIndex();
   }

   public ByteBuf markWriterIndex() {
      return this.a.markWriterIndex();
   }

   public ByteBuf resetWriterIndex() {
      return this.a.resetWriterIndex();
   }

   public ByteBuf discardReadBytes() {
      return this.a.discardReadBytes();
   }

   public ByteBuf discardSomeReadBytes() {
      return this.a.discardSomeReadBytes();
   }

   public ByteBuf ensureWritable(int i) {
      return this.a.ensureWritable(i);
   }

   public int ensureWritable(int i, boolean flag) {
      return this.a.ensureWritable(i, flag);
   }

   public boolean getBoolean(int i) {
      return this.a.getBoolean(i);
   }

   public byte getByte(int i) {
      return this.a.getByte(i);
   }

   public short getUnsignedByte(int i) {
      return this.a.getUnsignedByte(i);
   }

   public short getShort(int i) {
      return this.a.getShort(i);
   }

   public short getShortLE(int i) {
      return this.a.getShortLE(i);
   }

   public int getUnsignedShort(int i) {
      return this.a.getUnsignedShort(i);
   }

   public int getUnsignedShortLE(int i) {
      return this.a.getUnsignedShortLE(i);
   }

   public int getMedium(int i) {
      return this.a.getMedium(i);
   }

   public int getMediumLE(int i) {
      return this.a.getMediumLE(i);
   }

   public int getUnsignedMedium(int i) {
      return this.a.getUnsignedMedium(i);
   }

   public int getUnsignedMediumLE(int i) {
      return this.a.getUnsignedMediumLE(i);
   }

   public int getInt(int i) {
      return this.a.getInt(i);
   }

   public int getIntLE(int i) {
      return this.a.getIntLE(i);
   }

   public long getUnsignedInt(int i) {
      return this.a.getUnsignedInt(i);
   }

   public long getUnsignedIntLE(int i) {
      return this.a.getUnsignedIntLE(i);
   }

   public long getLong(int i) {
      return this.a.getLong(i);
   }

   public long getLongLE(int i) {
      return this.a.getLongLE(i);
   }

   public char getChar(int i) {
      return this.a.getChar(i);
   }

   public float getFloat(int i) {
      return this.a.getFloat(i);
   }

   public double getDouble(int i) {
      return this.a.getDouble(i);
   }

   public ByteBuf getBytes(int i, ByteBuf bytebuf) {
      return this.a.getBytes(i, bytebuf);
   }

   public ByteBuf getBytes(int i, ByteBuf bytebuf, int j) {
      return this.a.getBytes(i, bytebuf, j);
   }

   public ByteBuf getBytes(int i, ByteBuf bytebuf, int j, int k) {
      return this.a.getBytes(i, bytebuf, j, k);
   }

   public ByteBuf getBytes(int i, byte[] abyte) {
      return this.a.getBytes(i, abyte);
   }

   public ByteBuf getBytes(int i, byte[] abyte, int j, int k) {
      return this.a.getBytes(i, abyte, j, k);
   }

   public ByteBuf getBytes(int i, ByteBuffer bytebuffer) {
      return this.a.getBytes(i, bytebuffer);
   }

   public ByteBuf getBytes(int i, OutputStream outputstream, int j) throws IOException {
      return this.a.getBytes(i, outputstream, j);
   }

   public int getBytes(int i, GatheringByteChannel gatheringbytechannel, int j) throws IOException {
      return this.a.getBytes(i, gatheringbytechannel, j);
   }

   public int getBytes(int i, FileChannel fileChannel, long l, int i1) throws IOException {
      return this.a.getBytes(i, fileChannel, l, i1);
   }

   public CharSequence getCharSequence(int i, int i1, Charset charset) {
      return this.a.getCharSequence(i, i1, charset);
   }

   public ByteBuf setBoolean(int i, boolean flag) {
      return this.a.setBoolean(i, flag);
   }

   public ByteBuf setByte(int i, int j) {
      return this.a.setByte(i, j);
   }

   public ByteBuf setShort(int i, int j) {
      return this.a.setShort(i, j);
   }

   public ByteBuf setShortLE(int i, int i1) {
      return this.a.setShortLE(i, i1);
   }

   public ByteBuf setMedium(int i, int j) {
      return this.a.setMedium(i, j);
   }

   public ByteBuf setMediumLE(int i, int i1) {
      return this.a.setMediumLE(i, i1);
   }

   public ByteBuf setInt(int i, int j) {
      return this.a.setInt(i, j);
   }

   public ByteBuf setIntLE(int i, int i1) {
      return this.a.setIntLE(i, i1);
   }

   public ByteBuf setLong(int i, long j) {
      return this.a.setLong(i, j);
   }

   public ByteBuf setLongLE(int i, long l) {
      return this.a.setLongLE(i, l);
   }

   public ByteBuf setChar(int i, int j) {
      return this.a.setChar(i, j);
   }

   public ByteBuf setFloat(int i, float f) {
      return this.a.setFloat(i, f);
   }

   public ByteBuf setDouble(int i, double d0) {
      return this.a.setDouble(i, d0);
   }

   public ByteBuf setBytes(int i, ByteBuf bytebuf) {
      return this.a.setBytes(i, bytebuf);
   }

   public ByteBuf setBytes(int i, ByteBuf bytebuf, int j) {
      return this.a.setBytes(i, bytebuf, j);
   }

   public ByteBuf setBytes(int i, ByteBuf bytebuf, int j, int k) {
      return this.a.setBytes(i, bytebuf, j, k);
   }

   public ByteBuf setBytes(int i, byte[] abyte) {
      return this.a.setBytes(i, abyte);
   }

   public ByteBuf setBytes(int i, byte[] abyte, int j, int k) {
      return this.a.setBytes(i, abyte, j, k);
   }

   public ByteBuf setBytes(int i, ByteBuffer bytebuffer) {
      return this.a.setBytes(i, bytebuffer);
   }

   public int setBytes(int i, InputStream inputstream, int j) throws IOException {
      return this.a.setBytes(i, inputstream, j);
   }

   public int setBytes(int i, ScatteringByteChannel scatteringbytechannel, int j) throws IOException {
      return this.a.setBytes(i, scatteringbytechannel, j);
   }

   public int setBytes(int i, FileChannel fileChannel, long l, int i1) throws IOException {
      return this.a.setBytes(i, fileChannel, l, i1);
   }

   public ByteBuf setZero(int i, int j) {
      return this.a.setZero(i, j);
   }

   public int setCharSequence(int i, CharSequence charSequence, Charset charset) {
      return this.a.setCharSequence(i, charSequence, charset);
   }

   public boolean readBoolean() {
      return this.a.readBoolean();
   }

   public byte readByte() {
      return this.a.readByte();
   }

   public short readUnsignedByte() {
      return this.a.readUnsignedByte();
   }

   public short readShort() {
      return this.a.readShort();
   }

   public short readShortLE() {
      return this.a.readShortLE();
   }

   public int readUnsignedShort() {
      return this.a.readUnsignedShort();
   }

   public int readUnsignedShortLE() {
      return this.a.readUnsignedShortLE();
   }

   public int readMedium() {
      return this.a.readMedium();
   }

   public int readMediumLE() {
      return this.a.readMediumLE();
   }

   public int readUnsignedMedium() {
      return this.a.readUnsignedMedium();
   }

   public int readUnsignedMediumLE() {
      return this.a.readUnsignedMediumLE();
   }

   public int readInt() {
      return this.a.readInt();
   }

   public int readIntLE() {
      return this.a.readIntLE();
   }

   public long readUnsignedInt() {
      return this.a.readUnsignedInt();
   }

   public long readUnsignedIntLE() {
      return this.a.readUnsignedIntLE();
   }

   public long readLong() {
      return this.a.readLong();
   }

   public long readLongLE() {
      return this.a.readLongLE();
   }

   public char readChar() {
      return this.a.readChar();
   }

   public float readFloat() {
      return this.a.readFloat();
   }

   public double readDouble() {
      return this.a.readDouble();
   }

   public ByteBuf readBytes(int i) {
      return this.a.readBytes(i);
   }

   public ByteBuf readSlice(int i) {
      return this.a.readSlice(i);
   }

   public ByteBuf readRetainedSlice(int i) {
      return this.a.readRetainedSlice(i);
   }

   public ByteBuf readBytes(ByteBuf bytebuf) {
      return this.a.readBytes(bytebuf);
   }

   public ByteBuf readBytes(ByteBuf bytebuf, int i) {
      return this.a.readBytes(bytebuf, i);
   }

   public ByteBuf readBytes(ByteBuf bytebuf, int i, int j) {
      return this.a.readBytes(bytebuf, i, j);
   }

   public ByteBuf readBytes(byte[] abyte) {
      return this.a.readBytes(abyte);
   }

   public ByteBuf readBytes(byte[] abyte, int i, int j) {
      return this.a.readBytes(abyte, i, j);
   }

   public ByteBuf readBytes(ByteBuffer bytebuffer) {
      return this.a.readBytes(bytebuffer);
   }

   public ByteBuf readBytes(OutputStream outputstream, int i) throws IOException {
      return this.a.readBytes(outputstream, i);
   }

   public int readBytes(GatheringByteChannel gatheringbytechannel, int i) throws IOException {
      return this.a.readBytes(gatheringbytechannel, i);
   }

   public CharSequence readCharSequence(int i, Charset charset) {
      return this.a.readCharSequence(i, charset);
   }

   public int readBytes(FileChannel fileChannel, long l, int i) throws IOException {
      return this.a.readBytes(fileChannel, l, i);
   }

   public ByteBuf skipBytes(int i) {
      return this.a.skipBytes(i);
   }

   public ByteBuf writeBoolean(boolean flag) {
      return this.a.writeBoolean(flag);
   }

   public ByteBuf writeByte(int i) {
      return this.a.writeByte(i);
   }

   public ByteBuf writeShort(int i) {
      return this.a.writeShort(i);
   }

   public ByteBuf writeShortLE(int i) {
      return this.a.writeShortLE(i);
   }

   public ByteBuf writeMedium(int i) {
      return this.a.writeMedium(i);
   }

   public ByteBuf writeMediumLE(int i) {
      return this.a.writeMediumLE(i);
   }

   public ByteBuf writeInt(int i) {
      return this.a.writeInt(i);
   }

   public ByteBuf writeIntLE(int i) {
      return this.a.writeIntLE(i);
   }

   public ByteBuf writeLong(long i) {
      return this.a.writeLong(i);
   }

   public ByteBuf writeLongLE(long l) {
      return this.a.writeLongLE(l);
   }

   public ByteBuf writeChar(int i) {
      return this.a.writeChar(i);
   }

   public ByteBuf writeFloat(float f) {
      return this.a.writeFloat(f);
   }

   public ByteBuf writeDouble(double d0) {
      return this.a.writeDouble(d0);
   }

   public ByteBuf writeBytes(ByteBuf bytebuf) {
      return this.a.writeBytes(bytebuf);
   }

   public ByteBuf writeBytes(ByteBuf bytebuf, int i) {
      return this.a.writeBytes(bytebuf, i);
   }

   public ByteBuf writeBytes(ByteBuf bytebuf, int i, int j) {
      return this.a.writeBytes(bytebuf, i, j);
   }

   public ByteBuf writeBytes(byte[] abyte) {
      return this.a.writeBytes(abyte);
   }

   public ByteBuf writeBytes(byte[] abyte, int i, int j) {
      return this.a.writeBytes(abyte, i, j);
   }

   public ByteBuf writeBytes(ByteBuffer bytebuffer) {
      return this.a.writeBytes(bytebuffer);
   }

   public int writeBytes(InputStream inputstream, int i) throws IOException {
      return this.a.writeBytes(inputstream, i);
   }

   public int writeBytes(ScatteringByteChannel scatteringbytechannel, int i) throws IOException {
      return this.a.writeBytes(scatteringbytechannel, i);
   }

   public int writeBytes(FileChannel fileChannel, long l, int i) throws IOException {
      return this.a.writeBytes(fileChannel, l, i);
   }

   public ByteBuf writeZero(int i) {
      return this.a.writeZero(i);
   }

   public int writeCharSequence(CharSequence charSequence, Charset charset) {
      return this.a.writeCharSequence(charSequence, charset);
   }

   public int indexOf(int i, int j, byte b0) {
      return this.a.indexOf(i, j, b0);
   }

   public int bytesBefore(byte b0) {
      return this.a.bytesBefore(b0);
   }

   public int bytesBefore(int i, byte b0) {
      return this.a.bytesBefore(i, b0);
   }

   public int bytesBefore(int i, int j, byte b0) {
      return this.a.bytesBefore(i, j, b0);
   }

   public int forEachByte(ByteProcessor byteProcessor) {
      return this.a.forEachByte(byteProcessor);
   }

   public int forEachByte(int i, int i1, ByteProcessor byteProcessor) {
      return this.a.forEachByte(i, i1, byteProcessor);
   }

   public int forEachByteDesc(ByteProcessor byteProcessor) {
      return this.a.forEachByteDesc(byteProcessor);
   }

   public int forEachByteDesc(int i, int i1, ByteProcessor byteProcessor) {
      return this.a.forEachByteDesc(i, i1, byteProcessor);
   }

   public ByteBuf copy() {
      return this.a.copy();
   }

   public ByteBuf copy(int i, int j) {
      return this.a.copy(i, j);
   }

   public ByteBuf slice() {
      return this.a.slice();
   }

   public ByteBuf retainedSlice() {
      return this.a.retainedSlice();
   }

   public ByteBuf slice(int i, int j) {
      return this.a.slice(i, j);
   }

   public ByteBuf retainedSlice(int i, int i1) {
      return this.a.retainedSlice(i, i1);
   }

   public ByteBuf duplicate() {
      return this.a.duplicate();
   }

   public ByteBuf retainedDuplicate() {
      return this.a.retainedDuplicate();
   }

   public int nioBufferCount() {
      return this.a.nioBufferCount();
   }

   public ByteBuffer nioBuffer() {
      return this.a.nioBuffer();
   }

   public ByteBuffer nioBuffer(int i, int j) {
      return this.a.nioBuffer(i, j);
   }

   public ByteBuffer internalNioBuffer(int i, int j) {
      return this.a.internalNioBuffer(i, j);
   }

   public ByteBuffer[] nioBuffers() {
      return this.a.nioBuffers();
   }

   public ByteBuffer[] nioBuffers(int i, int j) {
      return this.a.nioBuffers(i, j);
   }

   public boolean hasArray() {
      return this.a.hasArray();
   }

   public byte[] array() {
      return this.a.array();
   }

   public int arrayOffset() {
      return this.a.arrayOffset();
   }

   public boolean hasMemoryAddress() {
      return this.a.hasMemoryAddress();
   }

   public long memoryAddress() {
      return this.a.memoryAddress();
   }

   public String toString(Charset charset) {
      return this.a.toString(charset);
   }

   public String toString(int i, int j, Charset charset) {
      return this.a.toString(i, j, charset);
   }

   public int hashCode() {
      return this.a.hashCode();
   }

   public boolean equals(Object object) {
      return this.a.equals(object);
   }

   public int compareTo(ByteBuf bytebuf) {
      return this.a.compareTo(bytebuf);
   }

   public String toString() {
      return this.a.toString();
   }

   public ByteBuf retain(int i) {
      return this.a.retain(i);
   }

   public ByteBuf retain() {
      return this.a.retain();
   }

   public ByteBuf touch() {
      return this.a.touch();
   }

   public ByteBuf touch(Object o) {
      return this.a.touch(o);
   }

   public int refCnt() {
      return this.a.refCnt();
   }

   public boolean release() {
      return this.a.release();
   }

   public boolean release(int i) {
      return this.a.release(i);
   }
}
