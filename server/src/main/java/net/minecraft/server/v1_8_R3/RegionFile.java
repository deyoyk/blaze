package net.minecraft.server.v1_8_R3;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.BitSet;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;
import net.jpountz.lz4.LZ4BlockInputStream;
import net.jpountz.lz4.LZ4BlockOutputStream;

public class RegionFile {
   private static final byte[] a = new byte[4096];
   private static final byte COMPRESSION_GZIP = 1;
   private static final byte COMPRESSION_ZLIB = 2;
   private static final byte COMPRESSION_UNCOMPRESSED = 3;
   private static final byte COMPRESSION_LZ4 = 4;
   private static final byte EXISTS = 1;
   private static final byte MISSING = 2;
   private static final int LZ4_BLOCK_SIZE = 16384;

   private RandomAccessFile c;
   private final int[] d = new int[1024];
   private BitSet freeSectors = new BitSet();
   private int sectorCount;
   private final byte[] existsCache = new byte[1024];

   private void cacheExistsState(int i, int j, boolean state) {
      this.existsCache[i + j * 32] = state ? EXISTS : MISSING;
   }

   private byte getExistsCache(int i, int j) {
      return this.existsCache[i + j * 32];
   }

   public RegionFile(File file) {
      try {
         this.c = new RandomAccessFile(file, "rw");
         if (this.c.length() < 8192L) {
            this.c.setLength(8192L);
         }

         long remainder = this.c.length() & 4095L;
         if (remainder != 0L) {
            this.c.seek(this.c.length());
            this.c.write(a, 0, (int)(4096L - remainder));
         }

         this.sectorCount = (int)(this.c.length() / 4096L);
         this.freeSectors.set(2, this.sectorCount);
         byte[] header = new byte[4096];
         this.c.seek(0L);
         this.c.readFully(header);
         IntBuffer headerAsInts = ByteBuffer.wrap(header).asIntBuffer();

         for (int index = 0; index < 1024; index++) {
            int entry = headerAsInts.get();
            this.d[index] = entry;
            int start = entry >> 8;
            int length = entry & 0xFF;
            if (entry != 0 && start + length <= this.sectorCount) {
               this.freeSectors.clear(start, start + length);
            }
         }
      } catch (IOException var9) {
         var9.printStackTrace();
      }
   }

   public boolean chunkExists(int i, int j) {
      if (this.d(i, j)) {
         return false;
      } else {
         byte cached = this.getExistsCache(i, j);
         if (cached != 0) {
            return cached == EXISTS;
         } else {
            synchronized (this) {
               try {
                  int entry = this.e(i, j);
                  if (entry == 0) {
                     return false;
                  }

                  int start = entry >> 8;
                  int length = entry & 0xFF;
                  if (start + length > this.sectorCount) {
                     return false;
                  }

                  this.c.seek(start * 4096L);
                  int byteCount = this.c.readInt();
                  if (byteCount > 4096 * length || byteCount <= 0) {
                     return false;
                  }

                  byte compression = this.c.readByte();
                  boolean foundChunk = compression == COMPRESSION_GZIP
                     || compression == COMPRESSION_ZLIB
                     || compression == COMPRESSION_UNCOMPRESSED
                     || compression == COMPRESSION_LZ4;
                  this.cacheExistsState(i, j, foundChunk);
                  return foundChunk;
               } catch (IOException var11) {
                  return false;
               }
            }
         }
      }
   }

   public DataInputStream a(int i, int j) {
      if (this.d(i, j)) {
         return null;
      } else {
         synchronized (this) {
            try {
               int entry = this.e(i, j);
               if (entry == 0) {
                  return null;
               }

               int start = entry >> 8;
               int length = entry & 0xFF;
               if (start + length > this.sectorCount) {
                  return null;
               }

               this.c.seek(start * 4096L);
               int byteCount = this.c.readInt();
               if (byteCount > 4096 * length || byteCount <= 0) {
                  return null;
               }

               byte compression = this.c.readByte();
               if (compression < COMPRESSION_GZIP || compression > COMPRESSION_LZ4) {
                  this.cacheExistsState(i, j, false);
                  return null;
               }

               byte[] data = new byte[byteCount - 1];
               this.c.readFully(data);
               this.cacheExistsState(i, j, true);
               if (compression == COMPRESSION_GZIP) {
                  return new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(data))));
               }

               if (compression == COMPRESSION_ZLIB) {
                  return new DataInputStream(new BufferedInputStream(new InflaterInputStream(new ByteArrayInputStream(data))));
               }

               if (compression == COMPRESSION_UNCOMPRESSED) {
                  return new DataInputStream(new ByteArrayInputStream(data));
               }

               return new DataInputStream(new LZ4BlockInputStream(new ByteArrayInputStream(data)));
            } catch (IOException var10) {
               return null;
            }
         }
      }
   }

   public DataOutputStream b(int i, int j) {
      return this.d(i, j) ? null : new DataOutputStream(new LZ4BlockOutputStream(new RegionFile.ChunkBuffer(i, j), LZ4_BLOCK_SIZE));
   }

   private int findFreeRun(int length) {
      int start = 2;

      while (start < this.sectorCount) {
         start = this.freeSectors.nextSetBit(start);
         if (start < 0) {
            return -1;
         }

         int end = Math.min(this.freeSectors.nextClearBit(start), this.sectorCount);
         if (end - start >= length) {
            return start;
         }

         start = end + 1;
      }

      return -1;
   }

   protected synchronized void a(int i, int j, byte[] abyte, int k) {
      try {
         int entry = this.e(i, j);
         int start = entry >> 8;
         int length = entry & 0xFF;
         int needed = (k + 5) / 4096 + 1;
         if (needed >= 256) {
            return;
         }

         if (start != 0 && length == needed) {
            this.a(start, abyte, k);
         } else {
            if (length > 0) {
               this.freeSectors.set(start, Math.min(start + length, this.sectorCount));
            }

            int freeRun = this.findFreeRun(needed);
            if (freeRun != -1) {
               start = freeRun;
               this.a(i, j, freeRun << 8 | needed);
               this.freeSectors.clear(start, start + needed);
               this.a(start, abyte, k);
            } else {
               this.c.seek(this.c.length());
               start = this.sectorCount;

               for (int sector = 0; sector < needed; sector++) {
                  this.c.write(a);
                  this.sectorCount++;
               }

               this.a(start, abyte, k);
               this.a(i, j, start << 8 | needed);
            }
         }

         this.b(i, j, (int)(MinecraftServer.az() / 1000L));
      } catch (IOException var13) {
         var13.printStackTrace();
      }
   }

   private void a(int i, byte[] abyte, int j) throws IOException {
      this.c.seek(i * 4096L);
      this.c.writeInt(j + 1);
      this.c.writeByte(COMPRESSION_LZ4);
      this.c.write(abyte, 0, j);
   }

   private boolean d(int i, int j) {
      return i < 0 || i >= 32 || j < 0 || j >= 32;
   }

   private int e(int i, int j) {
      return this.d[i + j * 32];
   }

   public boolean c(int i, int j) {
      return this.e(i, j) != 0;
   }

   private void a(int i, int j, int k) throws IOException {
      this.d[i + j * 32] = k;
      this.c.seek((i + j * 32L) * 4L);
      this.c.writeInt(k);
   }

   private void b(int i, int j, int k) throws IOException {
      this.c.seek(4096L + (i + j * 32L) * 4L);
      this.c.writeInt(k);
   }

   public void c() throws IOException {
      if (this.c != null) {
         this.c.close();
      }
   }

   class ChunkBuffer extends ByteArrayOutputStream {
      private final int b;
      private final int c;

      public ChunkBuffer(int i, int j) {
         super(8096);
         this.b = i;
         this.c = j;
      }

      @Override
      public void close() {
         RegionFile.this.a(this.b, this.c, this.buf, this.count);
      }
   }
}
