package net.techcable.tacospigot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.ToIntBiFunction;
import org.junit.Test;

public class ImmutableArrayTableTest {
   @Test
   public void retainsTableLookupsAndBuildsColumnsLazily() throws Exception {
      Table<String, String, Integer> source = HashBasedTable.create();
      source.put("a", "x", 1);
      source.put("a", "y", 2);
      source.put("b", "x", 3);
      source.put("c", "z", 4);
      Indexer<String> rows = row -> row.charAt(0) - 'a';
      ToIntBiFunction<String, String> columns = (row, column) -> column.charAt(0) - 'x';
      ImmutableArrayTable<String, String, Integer> table = new ImmutableArrayTable<>(rows, columns, source);

      Field columnMapField = ImmutableArrayTable.class.getDeclaredField("columnMap");
      columnMapField.setAccessible(true);
      assertNull(columnMapField.get(table));

      assertEquals(Integer.valueOf(1), table.get("a", "x"));
      assertEquals(Integer.valueOf(4), table.get("c", "z"));
      assertEquals(4, table.size());
      assertTrue(table.contains("b", "x"));
      assertFalse(table.contains("b", "z"));
      assertEquals(2, table.row("a").size());
      assertEquals(4, table.cellSet().size());
      assertNull(columnMapField.get(table));

      Map<String, Integer> column = table.column("x");
      assertEquals(2, column.size());
      assertEquals(Integer.valueOf(1), column.get("a"));
      assertNotNull(columnMapField.get(table));
      assertTrue(table.containsColumn("z"));
      assertEquals(3, table.columnKeySet().size());
      assertEquals(3, table.columnMap().size());
   }
}
