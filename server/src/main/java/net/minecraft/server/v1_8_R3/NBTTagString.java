package net.minecraft.server.v1_8_R3;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagString extends NBTBase {
   private String data;

   public NBTTagString() {
      this.data = "";
   }

   public NBTTagString(String var1) {
      this.data = var1;
      if (var1 == null) {
         throw new IllegalArgumentException("Empty string not allowed");
      }
   }

   @Override
   void write(DataOutput var1) throws IOException {
      var1.writeUTF(this.data);
   }

   @Override
   void load(DataInput var1, int var2, NBTReadLimiter var3) throws IOException {
      var3.a(288L);
      this.data = var1.readUTF();
      var3.a(16 * this.data.length());
   }

   @Override
   public byte getTypeId() {
      return 8;
   }

   @Override
   public String toString() {
      return "\"" + this.data.replace("\"", "\\\"") + "\"";
   }

   @Override
   public NBTBase clone() {
      return new NBTTagString(this.data);
   }

   @Override
   public boolean isEmpty() {
      return this.data.isEmpty();
   }

   @Override
   public boolean equals(Object var1) {
      if (!super.equals(var1)) {
         return false;
      } else {
         NBTTagString var2 = (NBTTagString)var1;
         return this.data == null && var2.data == null || this.data != null && this.data.equals(var2.data);
      }
   }

   @Override
   public int hashCode() {
      return super.hashCode() ^ this.data.hashCode();
   }

   @Override
   public String a_() {
      return this.data;
   }
}
