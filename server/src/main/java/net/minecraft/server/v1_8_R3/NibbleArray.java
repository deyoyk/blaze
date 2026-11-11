package net.minecraft.server.v1_8_R3;

public class NibbleArray {
   private static final byte[] EMPTY = new byte[2048];
   private byte[] a;

   public NibbleArray() {
      this.a = EMPTY;
   }

   public NibbleArray(byte[] abyte) {
      this.a = abyte;
      if (abyte.length != 2048) {
         throw new IllegalArgumentException("ChunkNibbleArrays should be 2048 bytes not: " + abyte.length);
      }
   }

   public int a(int i, int j, int k) {
      return this.a(j << 8 | k << 4 | i);
   }

   public void a(int i, int j, int k, int l) {
      this.a(j << 8 | k << 4 | i, l);
   }

   public int a(int i) {
      int j = i >> 1;
      return this.a[j] >> ((i & 1) << 2) & 15;
   }

   public void a(int i, int j) {
      if (this.a == EMPTY) {
         this.a = new byte[2048];
      }

      int k = i >> 1;
      int shift = (i & 1) << 2;
      this.a[k] = (byte)(this.a[k] & ~(15 << shift) | (j & 15) << shift);
   }

   public byte[] a() {
      if (this.a == EMPTY) {
         this.a = new byte[2048];
      }

      return this.a;
   }

   public byte[] raw() {
      return this.a;
   }

   public NibbleArray clone() {
      return new NibbleArray((byte[])this.a.clone());
   }
}
