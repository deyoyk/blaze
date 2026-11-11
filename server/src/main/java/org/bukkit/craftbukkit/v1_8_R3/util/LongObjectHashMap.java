package org.bukkit.craftbukkit.v1_8_R3.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class LongObjectHashMap<V> implements Cloneable, Serializable {
   static final long serialVersionUID = 2841537710170573815L;
   private static final long EMPTY_KEY = Long.MIN_VALUE;
   private static final int BUCKET_SIZE = 4096;
   private transient long[][] keys;
   private transient V[][] values;
   private transient int modCount;
   private transient int size;

   public LongObjectHashMap() {
      this.initialize();
   }

   public LongObjectHashMap(Map<? extends Long, ? extends V> map) {
      this();
      this.putAll(map);
   }

   public int size() {
      return this.size;
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   public boolean containsKey(long key) {
      return this.get(key) != null;
   }

   public boolean containsValue(V value) {
      for (V val : this.values()) {
         if (val == value || val.equals(value)) {
            return true;
         }
      }

      return false;
   }

   public V get(long key) {
      int index = (int)(this.keyIndex(key) & 4095L);
      long[] inner = this.keys[index];
      if (inner == null) {
         return null;
      } else {
         for (int i = 0; i < inner.length; i++) {
            long innerKey = inner[i];
            if (innerKey == Long.MIN_VALUE) {
               return null;
            }

            if (innerKey == key) {
               return this.values[index][i];
            }
         }

         return null;
      }
   }

   public V put(long key, V value) {
      int index = (int)(this.keyIndex(key) & 4095L);
      long[] innerKeys = this.keys[index];
      Object[] innerValues = this.values[index];
      this.modCount++;
      if (innerKeys == null) {
         this.keys[index] = innerKeys = new long[8];
         Arrays.fill(innerKeys, Long.MIN_VALUE);
         this.values[index] = (V[])(innerValues = new Object[8]);
         innerKeys[0] = key;
         innerValues[0] = value;
      } else {
         int i;
         for (i = 0; i < innerKeys.length; i++) {
            if (innerKeys[i] == Long.MIN_VALUE) {
               this.size++;
               innerKeys[i] = key;
               innerValues[i] = value;
               return null;
            }

            if (innerKeys[i] == key) {
               V oldValue = (V)innerValues[i];
               innerKeys[i] = key;
               innerValues[i] = value;
               return oldValue;
            }
         }

         long[] var10;
         this.keys[index] = var10 = Arrays.copyOf(innerKeys, i << 1);
         Arrays.fill(var10, i, var10.length, Long.MIN_VALUE);
         Object[] var12;
         this.values[index] = (V[])(var12 = Arrays.copyOf(innerValues, i << 1));
         var10[i] = key;
         var12[i] = value;
      }

      this.size++;
      return null;
   }

   public V remove(long key) {
      int index = (int)(this.keyIndex(key) & 4095L);
      long[] inner = this.keys[index];
      if (inner == null) {
         return null;
      } else {
         for (int i = 0; i < inner.length && inner[i] != Long.MIN_VALUE; i++) {
            if (inner[i] == key) {
               V value = this.values[index][i];
               i++;

               while (i < inner.length && inner[i] != Long.MIN_VALUE) {
                  inner[i - 1] = inner[i];
                  this.values[index][i - 1] = this.values[index][i];
                  i++;
               }

               inner[i - 1] = Long.MIN_VALUE;
               this.values[index][i - 1] = null;
               this.size--;
               this.modCount++;
               return value;
            }
         }

         return null;
      }
   }

   public void putAll(Map<? extends Long, ? extends V> map) {
      for (Map.Entry entry : map.entrySet()) {
         this.put((Long)entry.getKey(), (V)entry.getValue());
      }
   }

   public void clear() {
      if (this.size != 0) {
         this.modCount++;
         this.size = 0;
         Arrays.fill(this.keys, null);
         Arrays.fill(this.values, null);
      }
   }

   public Set<Long> keySet() {
      return new LongObjectHashMap.KeySet();
   }

   public Collection<V> values() {
      return new LongObjectHashMap.ValueCollection();
   }

   @Deprecated
   public Set<Map.Entry<Long, V>> entrySet() {
      HashSet<Map.Entry<Long, V>> set = new HashSet<>();

      for (long key : this.keySet()) {
         set.add(new LongObjectHashMap.Entry(key, this.get(key)));
      }

      return set;
   }

   @Override
   public Object clone() throws CloneNotSupportedException {
      LongObjectHashMap clone = (LongObjectHashMap)super.clone();
      clone.clear();
      clone.initialize();

      for (long key : this.keySet()) {
         V value = this.get(key);
         clone.put(key, value);
      }

      return clone;
   }

   private void initialize() {
      this.keys = new long[4096][];
      this.values = (V[][])(new Object[4096][]);
   }

   private long keyIndex(long key) {
      key ^= key >>> 33;
      key *= -49064778989728563L;
      key ^= key >>> 33;
      key *= -4265267296055464877L;
      return key ^ key >>> 33;
   }

   private void writeObject(ObjectOutputStream outputStream) throws IOException {
      outputStream.defaultWriteObject();

      for (long key : this.keySet()) {
         V value = this.get(key);
         outputStream.writeLong(key);
         outputStream.writeObject(value);
      }

      outputStream.writeLong(Long.MIN_VALUE);
      outputStream.writeObject(null);
   }

   private void readObject(ObjectInputStream inputStream) throws ClassNotFoundException, IOException {
      inputStream.defaultReadObject();
      this.initialize();

      while (true) {
         long key = inputStream.readLong();
         V value = (V)inputStream.readObject();
         if (key == Long.MIN_VALUE && value == null) {
            return;
         }

         this.put(key, value);
      }
   }

   private class Entry implements Map.Entry<Long, V> {
      private final Long key;
      private V value;

      Entry(long k, V v) {
         this.key = k;
         this.value = v;
      }

      public Long getKey() {
         return this.key;
      }

      @Override
      public V getValue() {
         return this.value;
      }

      @Override
      public V setValue(V v) {
         V old = this.value;
         this.value = v;
         LongObjectHashMap.this.put(this.key, v);
         return old;
      }
   }

   private class KeyIterator implements Iterator<Long> {
      final LongObjectHashMap<V>.ValueIterator iterator = LongObjectHashMap.this.new ValueIterator();

      public KeyIterator() {
      }

      @Override
      public void remove() {
         this.iterator.remove();
      }

      @Override
      public boolean hasNext() {
         return this.iterator.hasNext();
      }

      public Long next() {
         this.iterator.next();
         return this.iterator.prevKey;
      }
   }

   private class KeySet extends AbstractSet<Long> {
      private KeySet() {
      }

      @Override
      public void clear() {
         LongObjectHashMap.this.clear();
      }

      @Override
      public int size() {
         return LongObjectHashMap.this.size();
      }

      @Override
      public boolean contains(Object key) {
         return key instanceof Long && LongObjectHashMap.this.containsKey((Long)key);
      }

      @Override
      public boolean remove(Object key) {
         return LongObjectHashMap.this.remove((Long)key) != null;
      }

      @Override
      public Iterator<Long> iterator() {
         return LongObjectHashMap.this.new KeyIterator();
      }
   }

   private class ValueCollection extends AbstractCollection<V> {
      private ValueCollection() {
      }

      @Override
      public void clear() {
         LongObjectHashMap.this.clear();
      }

      @Override
      public int size() {
         return LongObjectHashMap.this.size();
      }

      @Override
      public boolean contains(Object value) {
         return LongObjectHashMap.this.containsValue((V)value);
      }

      @Override
      public Iterator<V> iterator() {
         return LongObjectHashMap.this.new ValueIterator();
      }
   }

   private class ValueIterator implements Iterator<V> {
      private int count;
      private int index;
      private int innerIndex;
      private int expectedModCount;
      private long lastReturned = Long.MIN_VALUE;
      long prevKey = Long.MIN_VALUE;
      V prevValue;

      ValueIterator() {
         this.expectedModCount = LongObjectHashMap.this.modCount;
      }

      @Override
      public boolean hasNext() {
         return this.count < LongObjectHashMap.this.size;
      }

      @Override
      public void remove() {
         if (LongObjectHashMap.this.modCount != this.expectedModCount) {
            throw new ConcurrentModificationException();
         } else if (this.lastReturned == Long.MIN_VALUE) {
            throw new IllegalStateException();
         } else {
            this.count--;
            LongObjectHashMap.this.remove(this.lastReturned);
            this.lastReturned = Long.MIN_VALUE;
            this.expectedModCount = LongObjectHashMap.this.modCount;
         }
      }

      @Override
      public V next() {
         if (LongObjectHashMap.this.modCount != this.expectedModCount) {
            throw new ConcurrentModificationException();
         } else if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            long[][] keys = LongObjectHashMap.this.keys;
            this.count++;
            if (this.prevKey != Long.MIN_VALUE) {
               this.innerIndex++;
            }

            for (; this.index < keys.length; this.index++) {
               if (keys[this.index] != null) {
                  if (this.innerIndex < keys[this.index].length) {
                     long key = keys[this.index][this.innerIndex];
                     V value = LongObjectHashMap.this.values[this.index][this.innerIndex];
                     if (key != Long.MIN_VALUE) {
                        this.lastReturned = key;
                        this.prevKey = key;
                        this.prevValue = value;
                        return this.prevValue;
                     }
                  }

                  this.innerIndex = 0;
               }
            }

            throw new NoSuchElementException();
         }
      }
   }
}
