package net.minecraft.server.v1_8_R3;

public class ReusableByteArray {
   private final ThreadLocal<byte[]> arrays;

   public ReusableByteArray(final int initialSize) {
      this.arrays = new ThreadLocal<byte[]>() {
         protected byte[] initialValue() {
            return new byte[initialSize];
         }
      };
   }

   public byte[] get(int minSize) {
      byte[] array = this.arrays.get();
      if (array.length < minSize) {
         array = new byte[minSize];
         this.arrays.set(array);
      }

      return array;
   }
}
