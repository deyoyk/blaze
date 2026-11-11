package net.minecraft.server.v1_8_R3;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagLong extends NBTBase.NBTNumber {
   private long data;

   NBTTagLong() {
   }

   public NBTTagLong(long var1) {
      this.data = var1;
   }

   @Override
   void write(DataOutput var1) throws IOException {
      var1.writeLong(this.data);
   }

   @Override
   void load(DataInput var1, int var2, NBTReadLimiter var3) throws IOException {
      var3.a(128L);
      this.data = var1.readLong();
   }

   @Override
   public byte getTypeId() {
      return 4;
   }

   @Override
   public String toString() {
      return "" + this.data + "L";
   }

   @Override
   public NBTBase clone() {
      return new NBTTagLong(this.data);
   }

   @Override
   public boolean equals(Object var1) {
      if (super.equals(var1)) {
         NBTTagLong var2 = (NBTTagLong)var1;
         return this.data == var2.data;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return super.hashCode() ^ (int)(this.data ^ this.data >>> 32);
   }

   @Override
   public long c() {
      return this.data;
   }

   @Override
   public int d() {
      return (int)(this.data & -1L);
   }

   @Override
   public short e() {
      return (short)(this.data & 65535L);
   }

   @Override
   public byte f() {
      return (byte)(this.data & 255L);
   }

   @Override
   public double g() {
      return this.data;
   }

   @Override
   public float h() {
      return (float)this.data;
   }
}
