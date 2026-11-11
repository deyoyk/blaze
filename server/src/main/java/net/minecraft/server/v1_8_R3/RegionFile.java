package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Lists;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

public class RegionFile {
   private static final byte[] a = new byte[4096];
   private RandomAccessFile c;
   private final int[] d = new int[1024];
   private List<Boolean> f;
   private final Boolean[] existsCache = new Boolean[1024];

   private void cacheExistsState(int i, int j, boolean state) {
      this.existsCache[i + j * 32] = state;
   }

   private Boolean getExistsCache(int i, int j) {
      return this.existsCache[i + j * 32];
   }

   public RegionFile(File file) {
      try {
         this.c = new RandomAccessFile(file, "rw");
         if (this.c.length() < 4096L) {
            this.c.write(a);
            this.c.write(a);
         }

         if ((this.c.length() & 4095L) != 0L) {
            for (int i = 0; i < (this.c.length() & 4095L); i++) {
               this.c.write(0);
            }
         }

         int i = (int)this.c.length() / 4096;
         this.f = Lists.newArrayListWithCapacity(i);

         for (int j = 0; j < i; j++) {
            this.f.add(true);
         }

         this.f.set(0, false);
         this.f.set(1, false);
         this.c.seek(0L);
         ByteBuffer header = ByteBuffer.allocate(8192);

         while (header.hasRemaining()) {
            if (this.c.getChannel().read(header) == -1) {
               throw new EOFException();
            }
         }

         ((Buffer)header).clear();
         IntBuffer headerAsInts = header.asIntBuffer();

         for (int var10 = 0; var10 < 1024; var10++) {
            int k = headerAsInts.get();
            this.d[var10] = k;
            if (k != 0 && (k >> 8) + (k & 0xFF) <= this.f.size()) {
               for (int l = 0; l < (k & 0xFF); l++) {
                  this.f.set((k >> 8) + l, false);
               }
            }
         }
      } catch (IOException var8) {
         var8.printStackTrace();
      }
   }

   public boolean chunkExists(int i, int j) {
      if (this.d(i, j)) {
         return false;
      } else {
         Boolean cached = this.getExistsCache(i, j);
         if (cached != null) {
            return cached;
         } else {
            synchronized (this) {
               boolean var10000;
               try {
                  int k = this.e(i, j);
                  if (k == 0) {
                     return false;
                  }

                  int l = k >> 8;
                  int i1 = k & 0xFF;
                  if (l + i1 > this.f.size()) {
                     return false;
                  }

                  this.c.seek(l * 4096L);
                  int j1 = this.c.readInt();
                  if (j1 > 4096 * i1 || j1 <= 0) {
                     return false;
                  }

                  byte b0 = this.c.readByte();
                  boolean foundChunk = b0 == 1 || b0 == 2;
                  this.cacheExistsState(i, j, foundChunk);
                  var10000 = foundChunk;
               } catch (IOException var11) {
                  return false;
               }

               return var10000;
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
               int k = this.e(i, j);
               if (k == 0) {
                  return null;
               }

               int l = k >> 8;
               int i1 = k & 0xFF;
               if (l + i1 > this.f.size()) {
                  return null;
               }

               this.c.seek(l * 4096L);
               int j1 = this.c.readInt();
               if (j1 > 4096 * i1) {
                  return null;
               }

               if (j1 <= 0) {
                  return null;
               }

               byte b0 = this.c.readByte();
               if (b0 == 1) {
                  this.cacheExistsState(i, j, true);
                  byte[] abyte = new byte[j1 - 1];
                  this.c.read(abyte);
                  return new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(abyte))));
               }

               if (b0 == 2) {
                  this.cacheExistsState(i, j, true);
                  byte[] abyte = new byte[j1 - 1];
                  this.c.read(abyte);
                  return new DataInputStream(new BufferedInputStream(new InflaterInputStream(new ByteArrayInputStream(abyte))));
               }

               this.cacheExistsState(i, j, false);
            } catch (IOException var10) {
               return null;
            }

            return null;
         }
      }
   }

   public DataOutputStream b(int i, int j) {
      return this.d(i, j) ? null : new DataOutputStream(new BufferedOutputStream(new DeflaterOutputStream(new RegionFile.ChunkBuffer(i, j))));
   }

   protected synchronized void a(int i, int j, byte[] abyte, int k) {
      try {
         int l = this.e(i, j);
         int i1 = l >> 8;
         int j1 = l & 0xFF;
         int k1 = (k + 5) / 4096 + 1;
         if (k1 >= 256) {
            return;
         }

         if (i1 != 0 && j1 == k1) {
            this.a(i1, abyte, k);
         } else {
            for (int l1 = 0; l1 < j1; l1++) {
               this.f.set(i1 + l1, true);
            }

            int var15 = this.f.indexOf(true);
            int i2 = 0;
            if (var15 != -1) {
               for (int j2 = var15; j2 < this.f.size(); j2++) {
                  if (i2 != 0) {
                     if (this.f.get(j2)) {
                        i2++;
                     } else {
                        i2 = 0;
                     }
                  } else if (this.f.get(j2)) {
                     var15 = j2;
                     i2 = 1;
                  }

                  if (i2 >= k1) {
                     break;
                  }
               }
            }

            if (i2 >= k1) {
               i1 = var15;
               this.a(i, j, var15 << 8 | k1);

               for (int j2 = 0; j2 < k1; j2++) {
                  this.f.set(i1 + j2, false);
               }

               this.a(i1, abyte, k);
            } else {
               this.c.seek(this.c.length());
               i1 = this.f.size();

               for (int j2 = 0; j2 < k1; j2++) {
                  this.c.write(a);
                  this.f.add(false);
               }

               this.a(i1, abyte, k);
               this.a(i, j, i1 << 8 | k1);
            }
         }

         this.b(i, j, (int)(MinecraftServer.az() / 1000L));
      } catch (IOException var12) {
         var12.printStackTrace();
      }
   }

   private void a(int i, byte[] abyte, int j) throws IOException {
      this.c.seek(i * 4096L);
      this.c.writeInt(j + 1);
      this.c.writeByte(2);
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
