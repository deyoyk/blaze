package net.techcable.tacospigot;

import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Table.Cell;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.ToIntBiFunction;
import javax.annotation.Nullable;

public class ImmutableArrayTable<R, C, V> implements Table<R, C, V> {
   private final ImmutableArrayMap<R, Map<C, V>> rowMap;
   private volatile ImmutableMap<C, Map<R, V>> columnMap;
   private final int size;

   public ImmutableArrayTable(Indexer<R> rowIndexer, ToIntBiFunction<R, C> columnGetId, Table<R, C, V> table) {
      Preconditions.checkNotNull(table, "Null table");
      Builder<R, Map<C, V>> rowMapBuilder = ImmutableMap.builder();
      int size = 0;

      for (Entry<R, Map<C, V>> rowEntry : table.rowMap().entrySet()) {
         R row = rowEntry.getKey();
         Preconditions.checkNotNull(row, "Null row");
         Builder<C, V> rowMapEntryBuilder = ImmutableMap.builder();

         for (Entry<C, V> rowEntryEntry : rowEntry.getValue().entrySet()) {
            rowMapEntryBuilder.put(rowEntryEntry);
         }

         rowMapBuilder.put(row, new ImmutableArrayMap<>(c -> columnGetId.applyAsInt(row, (C)c), rowMapEntryBuilder.build()));
         size += rowEntry.getValue().size();
      }

      this.rowMap = new ImmutableArrayMap<>(rowIndexer, rowMapBuilder.build());
      this.size = size;
   }

   private ImmutableMap<C, Map<R, V>> getColumnMap() {
      ImmutableMap<C, Map<R, V>> map = this.columnMap;
      if (map == null) {
         synchronized (this) {
            map = this.columnMap;
            if (map == null) {
               Map<C, Map<R, V>> builder = new HashMap<>();

               for (Entry<R, Map<C, V>> rowEntry : this.rowMap.entrySet()) {
                  R row = rowEntry.getKey();
                  for (Entry<C, V> columnEntry : rowEntry.getValue().entrySet()) {
                     builder.computeIfAbsent(columnEntry.getKey(), c -> new HashMap<>()).put(row, columnEntry.getValue());
                  }
               }

               map = ImmutableMap.copyOf(builder);
               this.columnMap = map;
            }
         }
      }

      return map;
   }

   public boolean contains(@Nullable Object rowKey, @Nullable Object columnKey) {
      Map<C, V> rowEntry = this.rowMap.get(rowKey);
      return rowEntry != null && rowEntry.containsKey(columnKey);
   }

   public boolean containsRow(@Nullable Object rowKey) {
      return this.rowMap.containsKey(rowKey);
   }

   public boolean containsColumn(@Nullable Object columnKey) {
      return this.getColumnMap().containsKey(columnKey);
   }

   public boolean containsValue(@Nullable Object value) {
      Preconditions.checkNotNull(value, "Null value");

      for (V v : this.values()) {
         if (v.equals(value)) {
            return true;
         }
      }

      return false;
   }

   public V get(@Nullable Object rowKey, @Nullable Object columnKey) {
      Map<C, V> rowEntry = this.rowMap.get(rowKey);
      return rowEntry != null ? rowEntry.get(columnKey) : null;
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public int size() {
      return this.size;
   }

   public Map<C, V> row(R rowKey) {
      return this.rowMap.get(rowKey);
   }

   public Map<R, V> column(C columnKey) {
      return (Map<R, V>)this.getColumnMap().get(columnKey);
   }

   public Set<Cell<R, C, V>> cellSet() {
      return new AbstractSet<Cell<R, C, V>>() {
         @Override
         public Iterator<Cell<R, C, V>> iterator() {
            return ImmutableArrayTable.this.new CellIterator();
         }

         @Override
         public int size() {
            return ImmutableArrayTable.this.size();
         }
      };
   }

   public Set<R> rowKeySet() {
      return this.rowMap.keySet();
   }

   public Set<C> columnKeySet() {
      return this.getColumnMap().keySet();
   }

   public Collection<V> values() {
      return Collections2.transform(this.cellSet(), Cell::getValue);
   }

   public Map<R, Map<C, V>> rowMap() {
      return this.rowMap;
   }

   public Map<C, Map<R, V>> columnMap() {
      return this.getColumnMap();
   }

   public void clear() {
      throw new UnsupportedOperationException();
   }

   public V put(R rowKey, C columnKey, V value) {
      throw new UnsupportedOperationException();
   }

   public void putAll(Table<? extends R, ? extends C, ? extends V> table) {
      throw new UnsupportedOperationException();
   }

   public V remove(@Nullable Object rowKey, @Nullable Object columnKey) {
      throw new UnsupportedOperationException();
   }

   private class CellIterator implements Iterator<Cell<R, C, V>> {
      final Iterator<Entry<R, Map<C, V>>> rowIterator = ImmutableArrayTable.this.rowMap.entrySet().iterator();
      Entry<R, Map<C, V>> rowEntry;
      Iterator<Entry<C, V>> columnIterator = Collections.emptyIterator();

      private CellIterator() {
      }

      @Override
      public boolean hasNext() {
         return this.rowIterator.hasNext() || this.columnIterator.hasNext();
      }

      public Cell<R, C, V> next() {
         if (!this.columnIterator.hasNext()) {
            this.rowEntry = this.rowIterator.next();
            this.columnIterator = this.rowEntry.getValue().entrySet().iterator();
         }

         Entry<C, V> columnEntry = this.columnIterator.next();
         return Tables.immutableCell(this.rowEntry.getKey(), columnEntry.getKey(), columnEntry.getValue());
      }

      @Override
      public void remove() {
         throw new UnsupportedOperationException();
      }
   }
}
