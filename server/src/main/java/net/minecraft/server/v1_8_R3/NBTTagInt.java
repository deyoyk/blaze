package net.minecraft.server.v1_8_R3;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagInt extends NBTBase.NBTNumber {
   private int data;

   NBTTagInt() {
   }

   public NBTTagInt(int var1) {
      this.data = var1;
   }

   @Override
   void write(DataOutput var1) throws IOException {
      var1.writeInt(this.data);
   }

   @Override
   void load(DataInput var1, int var2, NBTReadLimiter var3) throws IOException {
      var3.a(96L);
      this.data = var1.readInt();
   }

   @Override
   public byte getTypeId() {
      return 3;
   }

   @Override
   public String toString() {
      return "" + this.data;
   }

   @Override
   public NBTBase clone() {
      return new NBTTagInt(this.data);
   }

   @Override
   public boolean equals(Object var1) {
      if (super.equals(var1)) {
         NBTTagInt var2 = (NBTTagInt)var1;
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
      return (short)(this.data & 65535);
   }

   @Override
   public byte f() {
      return (byte)(this.data & 0xFF);
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
