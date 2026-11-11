package net.minecraft.server.v1_8_R3;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import org.junit.Test;

public class ChunkSectionStorageTest {
   @Test
   public void newStorageIsUniformAir() {
      ChunkSectionStorage storage = new ChunkSectionStorage();
      assertTrue(storage.isUniform());
      assertEquals(1, storage.paletteSize());
      for (int index = 0; index < ChunkSectionStorage.SIZE; index++) {
         assertEquals(0, storage.get(index));
      }
   }

   @Test
   public void settingBlocksCreatesAndExtendsPalette() {
      ChunkSectionStorage storage = new ChunkSectionStorage();
      storage.set(0, (char)5);
      assertFalse(storage.isUniform());
      assertFalse(storage.isDirect());
      assertTrue(storage.isCompact());
      assertEquals(2, storage.paletteSize());
      assertEquals(5, storage.get(0));
      assertEquals(0, storage.get(1));

      storage.set(1, (char)9);
      storage.set(2, (char)5);
      assertEquals(3, storage.paletteSize());
      assertEquals(9, storage.get(1));
      assertEquals(5, storage.get(2));
   }

   @Test
   public void fallsBackToDirectStorageForLargePalettes() {
      ChunkSectionStorage storage = new ChunkSectionStorage();
      for (int index = 0; index < 300; index++) {
         storage.set(index, (char)(index + 1));
      }

      assertTrue(storage.isDirect());
      for (int index = 0; index < 300; index++) {
         assertEquals(index + 1, storage.get(index));
      }

      storage.set(400, (char)999);
      assertEquals(999, storage.get(400));
   }

   @Test
   public void setValuesChoosesTheSmallestRepresentation() {
      char[] uniform = new char[ChunkSectionStorage.SIZE];
      Arrays.fill(uniform, (char)3);
      ChunkSectionStorage storage = new ChunkSectionStorage(uniform);
      assertTrue(storage.isUniform());
      assertEquals(3, storage.get(1234));

      char[] small = new char[ChunkSectionStorage.SIZE];
      small[0] = 1;
      small[1] = 2;
      small[2] = 1;
      storage.setValues(small);
      assertFalse(storage.isUniform());
      assertFalse(storage.isDirect());
      assertTrue(storage.isCompact());
      assertEquals(1, storage.get(0));
      assertEquals(2, storage.get(1));

      char[] large = new char[ChunkSectionStorage.SIZE];
      for (int index = 0; index < large.length; index++) {
         large[index] = (char)(index + 1);
      }

      storage.setValues(large);
      assertTrue(storage.isDirect());
      assertSame(large, storage.materialize());
   }

   @Test
   public void expandsCompactIndicesWhenThePaletteGrows() {
      ChunkSectionStorage storage = new ChunkSectionStorage();
      for (int index = 0; index < 15; index++) {
         storage.set(index, (char)(index + 1));
      }

      assertTrue(storage.isCompact());
      storage.set(15, (char)16);
      assertFalse(storage.isCompact());
      assertFalse(storage.isDirect());
      for (int index = 0; index < 15; index++) {
         assertEquals(index + 1, storage.get(index));
      }

      assertEquals(16, storage.get(15));
      assertEquals(0, storage.get(16));
   }

   @Test
   public void copyAndMaterializeExposeTheSameBlocks() {
      char[] values = new char[ChunkSectionStorage.SIZE];
      values[0] = 4;
      values[1] = 7;
      ChunkSectionStorage storage = new ChunkSectionStorage(values);
      assertArrayEquals(values, storage.copy());

      char[] materialized = storage.materialize();
      assertTrue(storage.isDirect());
      assertArrayEquals(values, materialized);
      materialized[2] = 11;
      assertEquals(11, storage.get(2));
   }
}
