package net.minecraft.server.v1_8_R3;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import lol.deyo.blaze.Blaze;
import org.junit.BeforeClass;
import org.junit.Test;

public class BiomeBaseTest {
   @BeforeClass
   public static void bootstrap() throws Exception {
      Blaze blaze = mock(Blaze.class);
      when(blaze.getRedstoneOptimization()).thenReturn(new BlockRedstoneWire());
      Field instance = Blaze.class.getDeclaredField("instance");
      instance.setAccessible(true);
      instance.set(null, blaze);
      DispenserRegistry.c();
   }

   @Test
   public void invalidBiomeIdsFallBackWithoutSpamming() {
      assertNull(BiomeBase.getBiome(-1));
      assertSame(BiomeBase.PLAINS, BiomeBase.getBiome(-2, BiomeBase.PLAINS));
      assertSame(BiomeBase.OCEAN, BiomeBase.getBiome(BiomeBase.getBiomes().length, BiomeBase.OCEAN));
      assertNull(BiomeBase.getBiome(999));
      assertSame(BiomeBase.PLAINS, BiomeBase.getBiome(BiomeBase.PLAINS.id));
   }
}
