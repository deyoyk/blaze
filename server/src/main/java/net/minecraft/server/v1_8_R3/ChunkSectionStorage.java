package net.minecraft.server.v1_8_R3;

import java.util.Arrays;

final class ChunkSectionStorage {
   static final int SIZE = 4096;
   private static final int UNIFORM = 0;
   private static final int PALETTE = 1;
   private static final int DIRECT = 2;

   private int mode = UNIFORM;
   private char uniformValue;
   private char[] palette;
   private int paletteSize;
   private byte[] indices;
   private boolean compact;
   private char[] values;

   ChunkSectionStorage() {
   }

   ChunkSectionStorage(char[] initialValues) {
      this.setValues(initialValues);
   }

   char get(int index) {
      if (this.mode == UNIFORM) {
         return this.uniformValue;
      } else if (this.mode == PALETTE) {
         return this.palette[this.getIndex(index)];
      } else {
         return this.values[index];
      }
   }

   void set(int index, char value) {
      if (this.mode == UNIFORM) {
         if (value == this.uniformValue) {
            return;
         }

         this.palette = new char[16];
         this.palette[0] = this.uniformValue;
         this.palette[1] = value;
         this.paletteSize = 2;
         this.indices = new byte[SIZE / 2];
         this.compact = true;
         this.setIndex(index, 1);
         this.mode = PALETTE;
         return;
      }

      if (this.mode == PALETTE) {
         int paletteIndex = this.find(value);
         if (paletteIndex >= 0) {
            this.setIndex(index, paletteIndex);
            return;
         }

         if (this.paletteSize == 256) {
            this.convertToDirect(index, value);
            return;
         }

         if (this.compact && this.paletteSize == 16) {
            this.expandIndices();
         }

         if (this.paletteSize == this.palette.length) {
            this.palette = Arrays.copyOf(this.palette, this.palette.length * 2);
         }

         this.palette[this.paletteSize] = value;
         this.setIndex(index, this.paletteSize);
         this.paletteSize++;
         return;
      }

      this.values[index] = value;
   }

   char[] copy() {
      char[] copy = new char[SIZE];
      if (this.mode == UNIFORM) {
         Arrays.fill(copy, this.uniformValue);
      } else if (this.mode == PALETTE) {
         for (int index = 0; index < SIZE; index++) {
            copy[index] = this.palette[this.getIndex(index)];
         }
      } else {
         System.arraycopy(this.values, 0, copy, 0, SIZE);
      }

      return copy;
   }

   char[] materialize() {
      if (this.mode == DIRECT) {
         return this.values;
      }

      this.values = this.copy();
      this.palette = null;
      this.indices = null;
      this.compact = false;
      this.mode = DIRECT;
      return this.values;
   }

   void setValues(char[] source) {
      if (source.length != SIZE) {
         throw new IllegalArgumentException("Chunk block arrays should be " + SIZE + " chars not: " + source.length);
      }

      char[] newPalette = new char[16];
      int count = 0;
      boolean compact = true;
      byte[] newIndices = new byte[SIZE / 2];

      for (int index = 0; index < SIZE; index++) {
         char value = source[index];
         int paletteIndex = indexOf(newPalette, count, value);
         if (paletteIndex < 0) {
            if (count == 256) {
               this.values = source;
               this.palette = null;
               this.indices = null;
               this.compact = false;
               this.mode = DIRECT;
               return;
            }

            if (compact && count == 16) {
               byte[] expanded = new byte[SIZE];
               for (int i = 0; i < SIZE; i++) {
                  expanded[i] = (byte)getIndex(newIndices, true, i);
               }

               newIndices = expanded;
               compact = false;
            }

            if (count == newPalette.length) {
               newPalette = Arrays.copyOf(newPalette, newPalette.length * 2);
            }

            newPalette[count] = value;
            paletteIndex = count++;
         }

         setIndex(newIndices, compact, index, paletteIndex);
      }

      if (count == 1) {
         this.uniformValue = newPalette[0];
         this.palette = null;
         this.indices = null;
         this.compact = false;
         this.mode = UNIFORM;
      } else {
         this.palette = newPalette;
         this.paletteSize = count;
         this.indices = newIndices;
         this.compact = compact;
         this.mode = PALETTE;
      }
   }

   private int getIndex(int index) {
      if (this.compact) {
         return this.indices[index >> 1] >> ((index & 1) << 2) & 15;
      } else {
         return this.indices[index] & 0xFF;
      }
   }

   private void setIndex(int index, int value) {
      setIndex(this.indices, this.compact, index, value);
   }

   private static int getIndex(byte[] indices, boolean compact, int index) {
      if (compact) {
         return indices[index >> 1] >> ((index & 1) << 2) & 15;
      } else {
         return indices[index] & 0xFF;
      }
   }

   private static void setIndex(byte[] indices, boolean compact, int index, int value) {
      if (compact) {
         int slot = index >> 1;
         int shift = (index & 1) << 2;
         indices[slot] = (byte)(indices[slot] & ~(15 << shift) | (value & 15) << shift);
      } else {
         indices[index] = (byte)value;
      }
   }

   private void expandIndices() {
      byte[] expanded = new byte[SIZE];

      for (int index = 0; index < SIZE; index++) {
         expanded[index] = (byte)this.getIndex(index);
      }

      this.indices = expanded;
      this.compact = false;
   }

   private int find(char value) {
      return indexOf(this.palette, this.paletteSize, value);
   }

   private static int indexOf(char[] palette, int size, char value) {
      for (int index = 0; index < size; index++) {
         if (palette[index] == value) {
            return index;
         }
      }

      return -1;
   }

   private void convertToDirect(int index, char value) {
      this.values = this.copy();
      this.values[index] = value;
      this.palette = null;
      this.indices = null;
      this.compact = false;
      this.mode = DIRECT;
   }

   int paletteSize() {
      return this.mode == UNIFORM ? 1 : this.mode == PALETTE ? this.paletteSize : 256;
   }

   boolean isUniform() {
      return this.mode == UNIFORM;
   }

   boolean isDirect() {
      return this.mode == DIRECT;
   }

   boolean isCompact() {
      return this.mode == PALETTE && this.compact;
   }
}
