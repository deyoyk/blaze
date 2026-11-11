package net.minecraft.server.v1_8_R3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class NibbleArrayTest {
   @Test
   public void allocatesOnlyWhenWrittenOrExposed() {
      NibbleArray array = new NibbleArray();
      assertEquals(0, array.a(0));
      assertEquals(0, array.a(4095));
      assertSame(array.raw(), array.raw());

      array.a(5, 7);
      assertEquals(7, array.a(5));
      assertEquals(2048, array.raw().length);

      byte[] exposed = array.a();
      exposed[0] = 0x21;
      assertEquals(1, array.a(0));
      assertEquals(2, array.a(1));
   }

   @Test
   public void copiesAreIndependent() {
      byte[] data = new byte[2048];
      data[0] = 0x21;
      NibbleArray array = new NibbleArray(data);
      assertSame(data, array.raw());

      NibbleArray copy = array.clone();
      assertNotSame(array.raw(), copy.raw());
      assertEquals(1, copy.a(0));
      assertEquals(2, copy.a(1));

      copy.a(0, 9);
      assertEquals(9, copy.a(0));
      assertEquals(1, array.a(0));
      assertEquals(0x21, array.raw()[0] & 0xFF);
   }

   @Test
   public void writesPreserveAdjacentValue() {
      NibbleArray array = new NibbleArray();
      array.a(0, 3);
      array.a(1, 9);
      assertEquals(3, array.a(0));
      assertEquals(9, array.a(1));
   }
}
