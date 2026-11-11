package lol.deyo.blaze.optimizations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

import net.minecraft.server.v1_8_R3.IBlockData;
import org.junit.Test;

public class ConstantRegistryIDTest {
   @Test
   public void resolvesEveryLegacyCombinedBlockId() {
      ConstantRegistryID registry = new ConstantRegistryID();
      IBlockData state = mock(IBlockData.class);
      registry.a(state, 4000);

      assertSame(state, registry.a(4000));
      assertEquals(4000, registry.b(state));
      assertNull(registry.a(4096));
   }
}
