package net.minecraft.server.v1_8_R3;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagByte extends NBTBase.NBTNumber {
   private byte data;

   NBTTagByte() {
   }

   public NBTTagByte(byte var1) {
      this.data = var1;
   }

   @Override
   void write(DataOutput var1) throws IOException {
      var1.writeByte(this.data);
   }

   @Override
   void load(DataInput var1, int var2, NBTReadLimiter var3) throws IOException {
      var3.a(72L);
      this.data = var1.readByte();
   }

   @Override
   public byte getTypeId() {
      return 1;
   }

   @Override
   public String toString() {
      return "" + this.data + "b";
   }

   @Override
   public NBTBase clone() {
      return new NBTTagByte(this.data);
   }

   @Override
   public boolean equals(Object var1) {
      if (super.equals(var1)) {
         NBTTagByte var2 = (NBTTagByte)var1;
         return this.data == var2.data;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return super.hashCode() ^ this.data;
   }

   @Override
   public long c() {
      return this.data;
   }

   @Override
   public int d() {
      return this.data;
   }

   @Override
   public short e() {
      return this.data;
   }

   @Override
   public byte f() {
      return this.data;
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
