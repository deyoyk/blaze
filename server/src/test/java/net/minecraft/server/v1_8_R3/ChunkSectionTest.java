package net.minecraft.server.v1_8_R3;

import static org.junit.Assert.assertEquals;

import lol.deyo.blaze.api.chunk.ChunkSectionSnapshot;
import org.junit.Test;

public class ChunkSectionTest {
   @Test
   public void storesBlocksCompactlyAndRestoresSnapshots() {
      ChunkSection section = new ChunkSection(0, true);
      assertEquals(0, section.getBlockId(0));

      int stoneIndex = 2 << 8 | 3 << 4 | 1;
      char[] values = new char[4096];
      values[stoneIndex] = 123;
      section.a(values);
      assertEquals(123, section.getBlockId(stoneIndex));
      section.setBlockId(1, (char)42);
      assertEquals(42, section.getBlockId(1));

      ChunkSectionSnapshot snapshot = section.createSnapshot();
      section.a(new char[4096]);
      assertEquals(0, section.getBlockId(stoneIndex));

      section.restoreSnapshot(snapshot);
      assertEquals(123, section.getBlockId(stoneIndex));

      char[] materialized = section.getIdArray();
      assertEquals(123, materialized[stoneIndex]);
      materialized[0] = 456;
      assertEquals(456, section.getBlockId(0));

      char[] restoredValues = new char[4096];
      restoredValues[0] = 789;
      section.a(restoredValues);
      assertEquals(789, section.getBlockId(0));
      assertEquals(0, section.getBlockId(1));
   }
}
