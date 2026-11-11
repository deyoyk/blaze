package net.minecraft.server.v1_8_R3;

import io.netty.buffer.ByteBufInputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.spigotmc.LimitStream;

public class NBTCompressedStreamTools {
   public static NBTTagCompound a(InputStream inputstream) throws IOException {
      DataInputStream datainputstream = new DataInputStream(new BufferedInputStream(new GZIPInputStream(inputstream)));

      NBTTagCompound nbttagcompound;
      try {
         nbttagcompound = a(datainputstream, NBTReadLimiter.a);
      } finally {
         datainputstream.close();
      }

      return nbttagcompound;
   }

   public static void a(NBTTagCompound nbttagcompound, OutputStream outputstream) throws IOException {
      try (DataOutputStream dataoutputstream = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(outputstream)))) {
         a(nbttagcompound, (DataOutput)dataoutputstream);
      }
   }
   public static NBTTagCompound a(DataInputStream datainputstream) throws IOException {
      return a(datainputstream, NBTReadLimiter.a);
   }

   public static NBTTagCompound a(DataInput datainput, NBTReadLimiter nbtreadlimiter) throws IOException {
      if (datainput instanceof ByteBufInputStream) {
         datainput = new DataInputStream(new LimitStream((InputStream)datainput, nbtreadlimiter));
      }

      NBTBase nbtbase = a(datainput, 0, nbtreadlimiter);
      if (nbtbase instanceof NBTTagCompound) {
         return (NBTTagCompound)nbtbase;
      } else {
         throw new IOException("Root tag must be a named compound tag");
      }
   }

   public static void a(NBTTagCompound nbttagcompound, DataOutput dataoutput) throws IOException {
      a((NBTBase)nbttagcompound, dataoutput);
   }

   private static void a(NBTBase nbtbase, DataOutput dataoutput) throws IOException {
      dataoutput.writeByte(nbtbase.getTypeId());
      if (nbtbase.getTypeId() != 0) {
         dataoutput.writeUTF("");
         nbtbase.write(dataoutput);
      }
   }

   private static NBTBase a(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) throws IOException {
      byte b0 = datainput.readByte();
      if (b0 == 0) {
         return new NBTTagEnd();
      } else {
         datainput.readUTF();
         NBTBase nbtbase = NBTBase.createTag(b0);

         try {
            nbtbase.load(datainput, i, nbtreadlimiter);
            return nbtbase;
         } catch (IOException var8) {
            CrashReport crashreport = CrashReport.a(var8, "Loading NBT data");
            CrashReportSystemDetails crashreportsystemdetails = crashreport.a("NBT Tag");
            crashreportsystemdetails.a("Tag name", "[UNNAMED TAG]");
            crashreportsystemdetails.a("Tag type", b0);
            throw new ReportedException(crashreport);
         }
      }
   }
}
