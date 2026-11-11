package org.spigotmc;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import net.minecraft.server.v1_8_R3.NBTReadLimiter;

public class LimitStream extends FilterInputStream {
   private final NBTReadLimiter limit;

   public LimitStream(InputStream is, NBTReadLimiter limit) {
      super(is);
      this.limit = limit;
   }

   @Override
   public int read() throws IOException {
      this.limit.a(8L);
      return super.read();
   }

   @Override
   public int read(byte[] b) throws IOException {
      this.limit.a(b.length * 8L);
      return super.read(b);
   }

   @Override
   public int read(byte[] b, int off, int len) throws IOException {
      this.limit.a(len * 8L);
      return super.read(b, off, len);
   }
}
