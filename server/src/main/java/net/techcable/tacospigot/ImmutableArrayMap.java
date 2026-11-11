package net.techcable.tacospigot;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
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
   private final Object[] keys;
   private final int[] outlyingIds;
   private final int size;

   public ImmutableArrayMap(Indexer<K> getId, Map<K, V> map) {
      Preconditions.checkNotNull(getId, "Null getId function");
      Preconditions.checkNotNull(map, "Null map");
      this.indexer = getId;
      this.size = map.size();
      Object[] keys = new Object[this.size];
      Iterator<K> iterator = map.keySet().iterator();

      for (int i = 0; i < keys.length; i++) {
         Preconditions.checkArgument(iterator.hasNext(), "Expected %s entries but only got %s", new Object[]{this.size, i + 1});
         keys[i] = iterator.next();
      }

      Arrays.sort(keys, (key1, key2) -> Integer.compare(this.indexer.getId((K)key1), this.indexer.getId((K)key2)));
      Preconditions.checkArgument(!iterator.hasNext(), "Got more than expected %s entries", new Object[]{this.size});
      int[] ids = new int[keys.length];

      for (int i = 0; i < keys.length; i++) {
         ids[i] = this.indexer.getId((K)keys[i]);
      }

      int[] largestRangeOfSequentialValues = calculateLargestRangeOfSequentialValues(ids);
      int minIndex = largestRangeOfSequentialValues == null ? -1 : largestRangeOfSequentialValues[0];
      int maxIndex = largestRangeOfSequentialValues == null ? -1 : largestRangeOfSequentialValues[1];
      int sequentalRangeSize = largestRangeOfSequentialValues == null ? 0 : largestRangeOfSequentialValues[2];
      this.data = new Object[sequentalRangeSize];
      this.outlyingIds = new int[this.size - sequentalRangeSize];
      this.outlyingData = new Object[this.size - sequentalRangeSize];
      this.offset = sequentalRangeSize == 0 ? 0 : ids[minIndex];
      this.keys = keys;
      int outlyingIndex = 0;

      for (int i = 0; i < keys.length; i++) {
         K key = (K)keys[i];
         V value = map.get(key);
         int id = ids[i];
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
            return new Iterator<Entry<K, V>>() {
               private int index;

               @Override
               public boolean hasNext() {
                  return this.index < ImmutableArrayMap.this.keys.length;
               }

               @Override
               public Entry<K, V> next() {
                  K key = (K)ImmutableArrayMap.this.keys[this.index++];
                  return Maps.immutableEntry(key, ImmutableArrayMap.this.get(key));
               }

               @Override
               public void remove() {
                  throw new UnsupportedOperationException();
               }
            };
         }

         @Override
         public int size() {
            return ImmutableArrayMap.this.size;
         }
      };
   }

   @Override
   public Set<K> keySet() {
      return new AbstractSet<K>() {
         @Override
         public Iterator<K> iterator() {
            return new Iterator<K>() {
               private int index;

               @Override
               public boolean hasNext() {
                  return this.index < ImmutableArrayMap.this.keys.length;
               }

               @Override
               public K next() {
                  return (K)ImmutableArrayMap.this.keys[this.index++];
               }

               @Override
               public void remove() {
                  throw new UnsupportedOperationException();
               }
            };
         }

         @Override
         public int size() {
            return ImmutableArrayMap.this.size;
         }
      };
   }

   @Override
   public void forEach(BiConsumer<? super K, ? super V> action) {
      for (Object key : this.keys) {
         action.accept((K)key, this.get((K)key));
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
