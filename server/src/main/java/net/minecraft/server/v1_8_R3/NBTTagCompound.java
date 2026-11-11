package net.minecraft.server.v1_8_R3;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

public class NBTTagCompound extends NBTBase {
   private final Object2ObjectOpenHashMap<String, NBTBase> map;

   public NBTTagCompound() {
      this.map = new Object2ObjectOpenHashMap(8, 0.8F);
   }

   public NBTTagCompound(Object2ObjectOpenHashMap<String, NBTBase> map) {
      this.map = map;
   }

   @Override
   void write(DataOutput dataoutput) throws IOException {
      for (String s : this.map.keySet()) {
         NBTBase nbtbase = (NBTBase)this.map.get(s);
         a(s, nbtbase, dataoutput);
      }

      dataoutput.writeByte(0);
   }

   @Override
   void load(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) throws IOException {
      nbtreadlimiter.a(384L);
      if (i > 512) {
         throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
      } else {
         this.map.clear();

         byte b0;
         while ((b0 = a(datainput, nbtreadlimiter)) != 0) {
            String s = b(datainput, nbtreadlimiter);
            nbtreadlimiter.a(224L + 16L * s.length());
            NBTBase nbtbase = a(b0, s, datainput, i + 1, nbtreadlimiter);
            if (this.map.put(s, nbtbase) != null) {
               nbtreadlimiter.a(288L);
            }
         }
      }
   }

   public Set<String> c() {
      return this.map.keySet();
   }

   @Override
   public byte getTypeId() {
      return 10;
   }

   public void set(String s, NBTBase nbtbase) {
      this.map.put(s, nbtbase);
   }

   public void setByte(String s, byte b0) {
      this.map.put(s, new NBTTagByte(b0));
   }

   public void setShort(String s, short short0) {
      this.map.put(s, new NBTTagShort(short0));
   }

   public void setInt(String s, int i) {
      this.map.put(s, new NBTTagInt(i));
   }

   public void setLong(String s, long i) {
      this.map.put(s, new NBTTagLong(i));
   }

   public void setFloat(String s, float f) {
      this.map.put(s, new NBTTagFloat(f));
   }

   public void setDouble(String s, double d0) {
      this.map.put(s, new NBTTagDouble(d0));
   }

   public void setString(String s, String s1) {
      this.map.put(s, new NBTTagString(s1));
   }

   public void setByteArray(String s, byte[] abyte) {
      this.map.put(s, new NBTTagByteArray(abyte));
   }

   public void setIntArray(String s, int[] aint) {
      this.map.put(s, new NBTTagIntArray(aint));
   }

   public void setBoolean(String s, boolean flag) {
      this.setByte(s, (byte)(flag ? 1 : 0));
   }

   public NBTBase get(String s) {
      return (NBTBase)this.map.get(s);
   }

   public byte b(String s) {
      NBTBase nbtbase = (NBTBase)this.map.get(s);
      return nbtbase != null ? nbtbase.getTypeId() : 0;
   }

   public boolean hasKey(String s) {
      return this.map.containsKey(s);
   }

   public boolean hasKeyOfType(String s, int i) {
      byte b0 = this.b(s);
      if (b0 == i) {
         return true;
      } else {
         return i != 99 ? false : b0 == 1 || b0 == 2 || b0 == 3 || b0 == 4 || b0 == 5 || b0 == 6;
      }
   }

   public byte getByte(String s) {
      try {
         return !this.hasKeyOfType(s, 99) ? 0 : ((NBTBase.NBTNumber)this.map.get(s)).f();
      } catch (ClassCastException var3) {
         return 0;
      }
   }

   public short getShort(String s) {
      try {
         return !this.hasKeyOfType(s, 99) ? 0 : ((NBTBase.NBTNumber)this.map.get(s)).e();
      } catch (ClassCastException var3) {
         return 0;
      }
   }

   public int getInt(String s) {
      try {
         return !this.hasKeyOfType(s, 99) ? 0 : ((NBTBase.NBTNumber)this.map.get(s)).d();
      } catch (ClassCastException var3) {
         return 0;
      }
   }

   public long getLong(String s) {
      try {
         return !this.hasKeyOfType(s, 99) ? 0L : ((NBTBase.NBTNumber)this.map.get(s)).c();
      } catch (ClassCastException var3) {
         return 0L;
      }
   }

   public float getFloat(String s) {
      try {
         return !this.hasKeyOfType(s, 99) ? 0.0F : ((NBTBase.NBTNumber)this.map.get(s)).h();
      } catch (ClassCastException var3) {
         return 0.0F;
      }
   }

   public double getDouble(String s) {
      try {
         return !this.hasKeyOfType(s, 99) ? 0.0 : ((NBTBase.NBTNumber)this.map.get(s)).g();
      } catch (ClassCastException var3) {
         return 0.0;
      }
   }

   public String getString(String s) {
      try {
         return !this.hasKeyOfType(s, 8) ? "" : ((NBTBase)this.map.get(s)).a_();
      } catch (ClassCastException var3) {
         return "";
      }
   }

   public byte[] getByteArray(String s) {
      try {
         return !this.hasKeyOfType(s, 7) ? new byte[0] : ((NBTTagByteArray)this.map.get(s)).c();
      } catch (ClassCastException var3) {
         throw new ReportedException(this.a(s, 7, var3));
      }
   }

   public int[] getIntArray(String s) {
      try {
         return !this.hasKeyOfType(s, 11) ? new int[0] : ((NBTTagIntArray)this.map.get(s)).c();
      } catch (ClassCastException var3) {
         throw new ReportedException(this.a(s, 11, var3));
      }
   }

   public NBTTagCompound getCompound(String s) {
      try {
         return !this.hasKeyOfType(s, 10) ? new NBTTagCompound() : (NBTTagCompound)this.map.get(s);
      } catch (ClassCastException var3) {
         throw new ReportedException(this.a(s, 10, var3));
      }
   }

   public NBTTagList getList(String s, int i) {
      try {
         if (this.b(s) != 9) {
            return new NBTTagList();
         } else {
            NBTTagList nbttaglist = (NBTTagList)this.map.get(s);
            return !nbttaglist.isEmpty() && nbttaglist.f() != i ? new NBTTagList() : nbttaglist;
         }
      } catch (ClassCastException var4) {
         throw new ReportedException(this.a(s, 9, var4));
      }
   }

   public boolean getBoolean(String s) {
      return this.getByte(s) != 0;
   }

   public void remove(String s) {
      this.map.remove(s);
   }

   @Override
   public String toString() {
      StringBuilder stringbuilder = new StringBuilder("{");
      Iterator<Entry<String, NBTBase>> iterator = this.map.entrySet().iterator();

      while (iterator.hasNext()) {
         Entry<String, NBTBase> entry = iterator.next();
         if (stringbuilder.length() != 1) {
            stringbuilder.append(',');
         }

         stringbuilder.append(entry.getKey()).append(':').append(entry.getValue());
      }

      return stringbuilder.append('}').toString();
   }

   @Override
   public boolean isEmpty() {
      return this.map.isEmpty();
   }

   private CrashReport a(String s, int i, ClassCastException classcastexception) {
      CrashReport crashreport = CrashReport.a(classcastexception, "Reading NBT data");
      CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Corrupt NBT tag", 1);
      crashreportsystemdetails.a("Tag type found", () -> NBTBase.a[((NBTBase)this.map.get(s)).getTypeId()]);
      crashreportsystemdetails.a("Tag type expected", () -> NBTBase.a[i]);
      crashreportsystemdetails.a("Tag name", s);
      return crashreport;
   }

   @Override
   public NBTBase clone() {
      Object2ObjectOpenHashMap<String, NBTBase> ret = new Object2ObjectOpenHashMap(this.map.size(), 0.8F);
      ObjectIterator<it.unimi.dsi.fastutil.objects.Object2ObjectMap.Entry<String, NBTBase>> iterator = this.map.object2ObjectEntrySet().fastIterator();

      while (iterator.hasNext()) {
         Entry<String, NBTBase> entry = (Entry<String, NBTBase>)iterator.next();
         ret.put(entry.getKey(), entry.getValue().clone());
      }

      return new NBTTagCompound(ret);
   }

   @Override
   public boolean equals(Object object) {
      if (super.equals(object)) {
         NBTTagCompound nbttagcompound = (NBTTagCompound)object;
         return this.map.equals(nbttagcompound.map);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return super.hashCode() ^ this.map.hashCode();
   }

   private static void a(String s, NBTBase nbtbase, DataOutput dataoutput) throws IOException {
      dataoutput.writeByte(nbtbase.getTypeId());
      if (nbtbase.getTypeId() != 0) {
         dataoutput.writeUTF(s);
         nbtbase.write(dataoutput);
      }
   }

   private static byte a(DataInput datainput, NBTReadLimiter nbtreadlimiter) throws IOException {
      return datainput.readByte();
   }

   private static String b(DataInput datainput, NBTReadLimiter nbtreadlimiter) throws IOException {
      return datainput.readUTF();
   }

   static NBTBase a(byte b0, String s, DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) throws IOException {
      NBTBase nbtbase = NBTBase.createTag(b0);

      try {
         nbtbase.load(datainput, i, nbtreadlimiter);
         return nbtbase;
      } catch (IOException var9) {
         CrashReport crashreport = CrashReport.a(var9, "Loading NBT data");
         CrashReportSystemDetails crashreportsystemdetails = crashreport.a("NBT Tag");
         crashreportsystemdetails.a("Tag name", s);
         crashreportsystemdetails.a("Tag type", b0);
         throw new ReportedException(crashreport);
      }
   }

   public void a(NBTTagCompound nbttagcompound) {
      for (String s : nbttagcompound.map.keySet()) {
         NBTBase nbtbase = (NBTBase)nbttagcompound.map.get(s);
         if (nbtbase.getTypeId() == 10) {
            if (this.hasKeyOfType(s, 10)) {
               NBTTagCompound nbttagcompound1 = this.getCompound(s);
               nbttagcompound1.a((NBTTagCompound)nbtbase);
            } else {
               this.set(s, nbtbase.clone());
            }
         } else {
            this.set(s, nbtbase.clone());
         }
      }
   }
}
