package net.minecraft.server.v1_8_R3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import org.junit.Test;

public class WatchableArrayMapTest {
   @Test
   public void storesSparseIdsWithoutBoxedCollections() {
      WatchableArrayMap map = new WatchableArrayMap();
      DataWatcher.WatchableObject first = new DataWatcher.WatchableObject(0, 0, "first");
      DataWatcher.WatchableObject last = new DataWatcher.WatchableObject(0, 22, "last");
      map.put(0, first);
      map.put(22, last);

      assertEquals(2, map.size());
      assertFalse(map.isEmpty());
      assertTrue(map.containsKey(0));
      assertTrue(map.containsKey(22));
      assertFalse(map.containsKey(5));
      assertFalse(map.containsKey(-1));
      assertSame(first, map.get(0));
      assertSame(last, map.get(22));
      assertNull(map.get(100));

      DataWatcher.WatchableObject replacement = new DataWatcher.WatchableObject(0, 22, "replacement");
      assertSame(last, map.put(Integer.valueOf(22), replacement));
      assertEquals(2, map.size());
      assertSame(replacement, map.get(22));

      assertSame(first, map.remove(0));
      assertEquals(1, map.size());
      assertNull(map.remove(100));
   }

   @Test
   public void viewsAreLiveAndRemovable() {
      WatchableArrayMap map = new WatchableArrayMap();
      map.put(0, new DataWatcher.WatchableObject(0, 0, "a"));
      map.put(2, new DataWatcher.WatchableObject(0, 2, "b"));
      Collection<DataWatcher.WatchableObject> values = map.values();

      Iterator<DataWatcher.WatchableObject> valueIterator = values.iterator();
      assertSame(map.get(0), valueIterator.next());
      valueIterator.remove();
      assertEquals(1, map.size());
      assertNull(map.get(0));

      Set<Integer> keys = map.keySet();
      assertEquals(1, keys.size());
      assertTrue(keys.contains(2));

      Set<Entry<Integer, DataWatcher.WatchableObject>> entries = map.entrySet();
      Iterator<Entry<Integer, DataWatcher.WatchableObject>> entryIterator = entries.iterator();
      Entry<Integer, DataWatcher.WatchableObject> entry = entryIterator.next();
      assertEquals(Integer.valueOf(2), entry.getKey());
      assertSame(map.get(2), entry.getValue());
      entryIterator.remove();
      assertTrue(map.isEmpty());
   }

   @Test
   public void cloneCopiesValuesWithoutSharingStorage() {
      WatchableArrayMap map = new WatchableArrayMap();
      DataWatcher.WatchableObject value = new DataWatcher.WatchableObject(0, 4, "value");
      map.put(4, value);

      WatchableArrayMap copy = map.clone();
      assertEquals(1, copy.size());
      assertEquals(value.b(), copy.get(4).b());
      assertEquals(value.a(), copy.get(4).a());
      copy.remove(4);
      assertTrue(copy.isEmpty());
      assertEquals(1, map.size());
   }
}
