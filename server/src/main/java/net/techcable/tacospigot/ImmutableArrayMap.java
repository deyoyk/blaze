package net.techcable.tacospigot;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BiConsumer;

public class ImmutableArrayMap<K, V> extends AbstractMap<K, V> implements Map<K, V> {
   private final Indexer<K> indexer;
   private final int offset;
   private final Object[] data;
   private final Object[] outlyingData;
   private final Entry<K, V>[] entries;
   private final int[] outlyingIds;
   private final int size;
   private final Comparator<K> keyComparator;

   public ImmutableArrayMap(Indexer<K> getId, Map<K, V> map) {
      Preconditions.checkNotNull(getId, "Null getId function");
      Preconditions.checkNotNull(map, "Null map");
      this.indexer = getId;
      this.size = map.size();
      this.keyComparator = new Comparator<K>() {
         @Override
         public int compare(K o1, K o2) {
            return Integer.compare(ImmutableArrayMap.this.indexer.getId(o1), ImmutableArrayMap.this.indexer.getId(o2));
         }
      };
      Entry<K, V>[] entries = new Entry[this.size];
      Iterator<Entry<K, V>> iterator = map.entrySet().iterator();

      for (int i = 0; i < entries.length; i++) {
         Preconditions.checkArgument(iterator.hasNext(), "Expected %s entries but only got %s", new Object[]{this.size, i + 1});
         Entry<K, V> entry = iterator.next();
         entries[i] = Maps.immutableEntry(entry.getKey(), entry.getValue());
      }

      Arrays.sort(entries, (entry1, entry2) -> this.keyComparator.compare(entry1.getKey(), entry2.getKey()));
      Preconditions.checkArgument(!iterator.hasNext(), "Got more than expected %s entries", new Object[]{this.size});
      int[] ids = Arrays.stream(entries).map(Entry::getKey).mapToInt(this.indexer::getId).toArray();
      int[] largestRangeOfSequentialValues = calculateLargestRangeOfSequentialValues(ids);
      int minIndex = largestRangeOfSequentialValues == null ? -1 : largestRangeOfSequentialValues[0];
      int maxIndex = largestRangeOfSequentialValues == null ? -1 : largestRangeOfSequentialValues[1];
      int sequentalRangeSize = largestRangeOfSequentialValues == null ? 0 : largestRangeOfSequentialValues[2];
      this.data = new Object[sequentalRangeSize];
      this.outlyingIds = new int[this.size - sequentalRangeSize];
      this.outlyingData = new Object[this.size - sequentalRangeSize];
      this.offset = sequentalRangeSize == 0 ? 0 : ids[minIndex];
      this.entries = entries;
      int outlyingIndex = 0;

      for (int i = 0; i < entries.length; i++) {
         Entry<K, V> entry = entries[i];
         K key = entry.getKey();
         V value = entry.getValue();
         int id = this.indexer.getId(key);
         Preconditions.checkArgument(id >= 0, "Negative id for %s: %s", new Object[]{key, id});
         if (i >= minIndex && i < maxIndex) {
            int index = id - this.offset;
            this.data[index] = value;
         } else {
            int index = outlyingIndex++;
            this.outlyingIds[index] = id;
            this.outlyingData[index] = value;
         }
      }
   }

   @Override
   public int size() {
      return this.size;
   }

   @Override
   public boolean containsKey(Object key) {
      return this.get(key) != null;
   }

   @Override
   public V get(Object key) {
      int id = this.indexer.getId((K)key);
      int index = id - this.offset;
      if (index >= 0 && index < this.data.length) {
         return (V)this.data[index];
      } else {
         int outlyingIndex = Arrays.binarySearch(this.outlyingIds, id);
         return (V)(outlyingIndex >= 0 && outlyingIndex < this.outlyingData.length ? this.outlyingData[outlyingIndex] : null);
      }
   }

   @Override
   public Set<Entry<K, V>> entrySet() {
      return new AbstractSet<Entry<K, V>>() {
         @Override
         public Iterator<Entry<K, V>> iterator() {
            return Arrays.asList(ImmutableArrayMap.this.entries).iterator();
         }

         @Override
         public int size() {
            return ImmutableArrayMap.this.size;
         }
      };
   }

   @Override
   public void forEach(BiConsumer<? super K, ? super V> action) {
      for (Entry<K, V> entry : this.entries) {
         action.accept(entry.getKey(), entry.getValue());
      }
   }

   @Override
   public void clear() {
      throw new UnsupportedOperationException();
   }

   @Override
   public V put(K key, V value) {
      throw new UnsupportedOperationException();
   }

   @Override
   public V remove(Object key) {
      throw new UnsupportedOperationException();
   }

   private static int[] calculateLargestRangeOfSequentialValues(int[] ids) {
      int largestRangeSize = 0;
      int[] largestRange = new int[3];

      for (int minIndex = 0; minIndex < ids.length; minIndex++) {
         int min = ids[minIndex];
         int lastNum = min;

         int maxIndex;
         for (maxIndex = minIndex + 1; maxIndex < ids.length; maxIndex++) {
            int max = ids[maxIndex];
            if (lastNum + 1 != max) {
               break;
            }

            lastNum = max;
         }

         int rangeSize = maxIndex - minIndex;
         if (rangeSize > largestRangeSize) {
            largestRange[0] = minIndex;
            largestRange[1] = maxIndex;
            largestRange[2] = rangeSize;
            largestRangeSize = rangeSize;
         }
      }

      return largestRangeSize == 0 ? null : largestRange;
   }
}
