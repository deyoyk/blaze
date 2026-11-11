package net.minecraft.server.v1_8_R3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.Iterator;
import org.junit.Test;

public class RegistryIDTest {
   @Test
   public void storesIdsInAGrowableArray() {
      RegistryID<String> registry = new RegistryID<>();
      registry.a("low", 2);
      registry.a("high", 4000);

      assertSame("low", registry.a(2));
      assertSame("high", registry.a(4000));
      assertNull(registry.a(1));
      assertNull(registry.a(4001));
      assertEquals(2, registry.b("low"));
      assertEquals(4000, registry.b("high"));

      Iterator<String> iterator = registry.iterator();
      assertEquals("low", iterator.next());
      assertEquals("high", iterator.next());
      assertEquals(false, iterator.hasNext());
   }
}
