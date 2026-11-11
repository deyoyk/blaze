package net.minecraft.server.v1_8_R3;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagFloat extends NBTBase.NBTNumber {
   private float data;

   NBTTagFloat() {
   }

   public NBTTagFloat(float var1) {
      this.data = var1;
   }

   @Override
   void write(DataOutput var1) throws IOException {
      var1.writeFloat(this.data);
   }

   @Override
   void load(DataInput var1, int var2, NBTReadLimiter var3) throws IOException {
      var3.a(96L);
      this.data = var1.readFloat();
   }

   @Override
   public byte getTypeId() {
      return 5;
   }

   @Override
   public String toString() {
      return "" + this.data + "f";
   }

   @Override
   public NBTBase clone() {
      return new NBTTagFloat(this.data);
   }

   @Override
   public boolean equals(Object var1) {
      if (super.equals(var1)) {
         NBTTagFloat var2 = (NBTTagFloat)var1;
         return this.data == var2.data;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return super.hashCode() ^ Float.floatToIntBits(this.data);
   }

   @Override
   public long c() {
      return (long)this.data;
   }

   @Override
   public int d() {
      return MathHelper.d(this.data);
   }

   @Override
   public short e() {
      return (short)(MathHelper.d(this.data) & 65535);
   }

   @Override
   public byte f() {
      return (byte)(MathHelper.d(this.data) & 0xFF);
   }

   @Override
   public double g() {
      return this.data;
   }

   @Override
   public float h() {
      return this.data;
   }
}
