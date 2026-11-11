package net.minecraft.server.v1_8_R3;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagDouble extends NBTBase.NBTNumber {
   private double data;

   NBTTagDouble() {
   }

   public NBTTagDouble(double var1) {
      this.data = var1;
   }

   @Override
   void write(DataOutput var1) throws IOException {
      var1.writeDouble(this.data);
   }

   @Override
   void load(DataInput var1, int var2, NBTReadLimiter var3) throws IOException {
      var3.a(128L);
      this.data = var1.readDouble();
   }

   @Override
   public byte getTypeId() {
      return 6;
   }

   @Override
   public String toString() {
      return "" + this.data + "d";
   }

   @Override
   public NBTBase clone() {
      return new NBTTagDouble(this.data);
   }

   @Override
   public boolean equals(Object var1) {
      if (super.equals(var1)) {
         NBTTagDouble var2 = (NBTTagDouble)var1;
         return this.data == var2.data;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      long var1 = Double.doubleToLongBits(this.data);
      return super.hashCode() ^ (int)(var1 ^ var1 >>> 32);
   }

   @Override
   public long c() {
      return (long)Math.floor(this.data);
   }

   @Override
   public int d() {
      return MathHelper.floor(this.data);
   }

   @Override
   public short e() {
      return (short)(MathHelper.floor(this.data) & 65535);
   }

   @Override
   public byte f() {
      return (byte)(MathHelper.floor(this.data) & 0xFF);
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
