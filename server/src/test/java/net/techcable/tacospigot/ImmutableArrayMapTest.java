package net.techcable.tacospigot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class ImmutableArrayMapTest {
   @Test
   public void looksUpSequentialAndOutlyingIds() {
      Map<String, Integer> source = new HashMap<>();
      source.put("a", 10);
      source.put("b", 20);
      source.put("c", 30);
      source.put("d", 40);
      Indexer<String> indexer = key -> key.charAt(0) == 'a' ? 1 : key.charAt(0) == 'b' ? 5 : key.charAt(0) == 'c' ? 6 : key.charAt(0) == 'd' ? 9 : -1;
      ImmutableArrayMap<String, Integer> map = new ImmutableArrayMap<>(indexer, source);

      assertEquals(4, map.size());
      assertEquals(Integer.valueOf(10), map.get("a"));
      assertEquals(Integer.valueOf(20), map.get("b"));
      assertEquals(Integer.valueOf(30), map.get("c"));
      assertEquals(Integer.valueOf(40), map.get("d"));
      assertNull(map.get("z"));
      assertTrue(map.containsKey("a"));

      List<String> keys = new ArrayList<>();
      List<Integer> values = new ArrayList<>();
      map.forEach((key, value) -> {
         keys.add(key);
         values.add(value);
      });
      assertEquals("[a, b, c, d]", keys.toString());
      assertEquals("[10, 20, 30, 40]", values.toString());
      assertEquals(4, map.entrySet().size());
      assertEquals(4, map.keySet().size());
   }
}
