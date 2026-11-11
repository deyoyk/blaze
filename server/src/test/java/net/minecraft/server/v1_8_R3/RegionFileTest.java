package net.minecraft.server.v1_8_R3;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.Random;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class RegionFileTest {
   @Rule
   public final TemporaryFolder folder = new TemporaryFolder();

   @Test
   public void writesAndReadsLz4Chunks() throws Exception {
      File file = this.folder.newFile("r.0.0.mca");
      byte[] payload = new byte[32768];
      new Random(1234L).nextBytes(payload);

      RegionFile region = new RegionFile(file);
      try {
         writeChunk(region, 0, 0, payload);
         assertTrue(region.chunkExists(0, 0));
         assertFalse(region.chunkExists(1, 0));
         assertEquals(4, compressionType(file, 0, 0));
         assertArrayEquals(payload, readChunk(region, 0, 0));
      } finally {
         region.c();
      }

      RegionFile reopened = new RegionFile(file);
      try {
         assertTrue(reopened.chunkExists(0, 0));
         assertArrayEquals(payload, readChunk(reopened, 0, 0));
         assertNull(reopened.a(1, 0));
      } finally {
         reopened.c();
      }
   }

   @Test
   public void readsLegacyGzipZlibAndUncompressedChunks() throws Exception {
      File file = this.folder.newFile("r.0.0.mca");
      byte[] zlibPayload = "legacy zlib payload".getBytes("UTF-8");
      byte[] gzipPayload = "legacy gzip payload".getBytes("UTF-8");
      byte[] rawPayload = "legacy raw payload".getBytes("UTF-8");
      writeLegacyChunk(file, 0, 0, zlibPayload, 2);
      writeLegacyChunk(file, 1, 0, gzipPayload, 1);
      writeLegacyChunk(file, 2, 0, rawPayload, 3);

      RegionFile region = new RegionFile(file);
      try {
         assertArrayEquals(zlibPayload, readChunk(region, 0, 0));
         assertArrayEquals(gzipPayload, readChunk(region, 1, 0));
         assertArrayEquals(rawPayload, readChunk(region, 2, 0));
      } finally {
         region.c();
      }
   }

   @Test
   public void reusesFreedSectorsInsteadOfGrowingFile() throws Exception {
      File file = this.folder.newFile("r.0.0.mca");
      byte[] first = new byte[24000];
      byte[] second = new byte[40000];
      Random random = new Random(5678L);
      random.nextBytes(first);
      random.nextBytes(second);

      RegionFile region = new RegionFile(file);
      try {
         writeChunk(region, 0, 0, first);
         long firstAllocation = sectorCount(file, 0, 0);
         writeChunk(region, 0, 0, second);
         long lengthAfterSecondWrite = file.length();
         assertTrue("the larger chunk should use more sectors", sectorCount(file, 0, 0) > firstAllocation);

         writeChunk(region, 0, 0, first);
         assertEquals("the freed first allocation should be reused", lengthAfterSecondWrite, file.length());
         assertArrayEquals(first, readChunk(region, 0, 0));
      } finally {
         region.c();
      }
   }

   private static void writeChunk(RegionFile region, int x, int z, byte[] payload) throws Exception {
      DataOutputStream output = region.b(x, z);
      assertNotNull(output);
      output.write(payload);
      output.close();
   }

   private static byte[] readChunk(RegionFile region, int x, int z) throws Exception {
      DataInputStream input = region.a(x, z);
      assertNotNull(input);
      try {
         byte[] payload = new byte[65536];
         int length = 0;
         int read;
         while ((read = input.read(payload, length, payload.length - length)) != -1) {
            length += read;
            if (length == payload.length) {
               payload = java.util.Arrays.copyOf(payload, payload.length * 2);
            }
         }

         return java.util.Arrays.copyOf(payload, length);
      } finally {
         input.close();
      }
   }

   private static int compressionType(File file, int x, int z) throws Exception {
      try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
         int entry = headerEntry(raf, x, z);
         raf.seek((long)(entry >> 8) * 4096L + 4L);
         return raf.readByte();
      }
   }

   private static int sectorCount(File file, int x, int z) throws Exception {
      try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
         return headerEntry(raf, x, z) & 0xFF;
      }
   }

   private static int headerEntry(RandomAccessFile raf, int x, int z) throws Exception {
      raf.seek((x + z * 32) * 4L);
      return raf.readInt();
   }

   private static void writeLegacyChunk(File file, int x, int z, byte[] payload, int compression) throws Exception {
      byte[] compressed;
      if (compression == 1) {
         ByteArrayOutputStream bytes = new ByteArrayOutputStream();
         try (GZIPOutputStream output = new GZIPOutputStream(bytes)) {
            output.write(payload);
         }
         compressed = bytes.toByteArray();
      } else if (compression == 2) {
         ByteArrayOutputStream bytes = new ByteArrayOutputStream();
         try (DeflaterOutputStream output = new DeflaterOutputStream(bytes)) {
            output.write(payload);
         }
         compressed = bytes.toByteArray();
      } else {
         compressed = payload;
      }

      try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
         if (raf.length() < 8192L) {
            raf.setLength(8192L);
         }

         int start = (int)(raf.length() / 4096L);
         raf.seek((x + z * 32) * 4L);
         raf.writeInt(start << 8 | 1);
         raf.seek((long)start * 4096L);
         raf.writeInt(compressed.length + 1);
         raf.writeByte(compression);
         raf.write(compressed);
         raf.setLength((long)(start + 1) * 4096L);
      }
   }
}
