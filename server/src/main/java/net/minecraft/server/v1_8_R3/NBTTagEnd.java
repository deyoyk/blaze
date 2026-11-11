package net.minecraft.server.v1_8_R3;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagEnd extends NBTBase {
   NBTTagEnd() {
   }

   @Override
   void load(DataInput var1, int var2, NBTReadLimiter var3) throws IOException {
      var3.a(64L);
   }

   @Override
   void write(DataOutput var1) throws IOException {
   }

   @Override
   public byte getTypeId() {
      return 0;
   }

   @Override
   public String toString() {
      return "END";
   }

   @Override
   public NBTBase clone() {
      return new NBTTagEnd();
   }
}
