package net.minecraft.server.v1_8_R3;

import com.google.common.base.Objects;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import org.jetbrains.annotations.NotNull;

public class WatchableArrayMap implements Map<Integer, DataWatcher.WatchableObject>, Iterable<DataWatcher.WatchableObject> {
   private static final int DEFAULT_CAPACITY = 8;
   private DataWatcher.WatchableObject[] dataValues = new DataWatcher.WatchableObject[DEFAULT_CAPACITY];
   private int size;

   private void ensureCapacity(int index) {
      if (index >= this.dataValues.length) {
         this.dataValues = Arrays.copyOf(this.dataValues, Math.max(DEFAULT_CAPACITY, index + 1));
      }
   }

   @Override
   public int size() {
      return this.size;
   }

   @Override
   public boolean isEmpty() {
      return this.size == 0;
   }

   public boolean containsKey(int i) {
      return i >= 0 && i < this.dataValues.length && this.dataValues[i] != null;
   }

   @Override
   public boolean containsKey(Object key) {
      return key instanceof Integer && this.containsKey((Integer)key);
   }

   @Override
   public boolean containsValue(Object value) {
      for (DataWatcher.WatchableObject dataValue : this.dataValues) {
         if (Objects.equal(dataValue, value)) {
            return true;
         }
      }

      return false;
   }

   public DataWatcher.WatchableObject get(int i) {
      return i >= 0 && i < this.dataValues.length ? this.dataValues[i] : null;
   }

   @Override
   public DataWatcher.WatchableObject get(Object key) {
      return key instanceof Integer ? this.get((Integer)key) : null;
   }

   public void put(int i, DataWatcher.WatchableObject watchableObject) {
      this.ensureCapacity(i);
      if (this.dataValues[i] == null) {
         this.size++;
      }

      this.dataValues[i] = watchableObject;
   }

   @Override
   public DataWatcher.WatchableObject put(Integer key, DataWatcher.WatchableObject value) {
      int index = key;
      this.ensureCapacity(index);
      DataWatcher.WatchableObject old = this.dataValues[index];
      if (old == null) {
         this.size++;
      }

      this.dataValues[index] = value;
      return old;
   }

   @Override
   public DataWatcher.WatchableObject remove(Object key) {
      if (!(key instanceof Integer)) {
         return null;
      } else {
         int index = (Integer)key;
         if (index >= 0 && index < this.dataValues.length) {
            DataWatcher.WatchableObject old = this.dataValues[index];
            if (old != null) {
               this.dataValues[index] = null;
               this.size--;
            }

            return old;
         } else {
            return null;
         }
      }
   }

   @Override
   public void putAll(@NotNull Map m) {
      for (Object object : m.entrySet()) {
         if (object instanceof Entry) {
            Entry entry = (Entry)object;
            Object key = entry.getKey();
            Object value = entry.getValue();
            if (key instanceof Integer && value instanceof DataWatcher.WatchableObject) {
               this.put((Integer)key, (DataWatcher.WatchableObject)value);
            }
         }
      }
   }

   @Override
   public void clear() {
      Arrays.fill(this.dataValues, null);
      this.size = 0;
   }

   @Override
   public Set<Integer> keySet() {
      return new AbstractSet<Integer>() {
         @Override
         public Iterator<Integer> iterator() {
            return new Iterator<Integer>() {
               private int nextIndex;
               private int lastIndex = -1;

               @Override
               public boolean hasNext() {
                  while (this.nextIndex < WatchableArrayMap.this.dataValues.length
                     && WatchableArrayMap.this.dataValues[this.nextIndex] == null) {
                     this.nextIndex++;
                  }

                  return this.nextIndex < WatchableArrayMap.this.dataValues.length;
               }

               @Override
               public Integer next() {
                  if (!this.hasNext()) {
                     throw new NoSuchElementException();
                  }

                  this.lastIndex = this.nextIndex;
                  return this.nextIndex++;
               }

               @Override
               public void remove() {
                  if (this.lastIndex < 0) {
                     throw new IllegalStateException();
                  }

                  if (WatchableArrayMap.this.dataValues[this.lastIndex] != null) {
                     WatchableArrayMap.this.dataValues[this.lastIndex] = null;
                     WatchableArrayMap.this.size--;
                  }

                  this.lastIndex = -1;
               }
            };
         }

         @Override
         public int size() {
            return WatchableArrayMap.this.size;
         }
      };
   }

   @Override
   public Collection<DataWatcher.WatchableObject> values() {
      return new AbstractCollection<DataWatcher.WatchableObject>() {
         @NotNull
         @Override
         public Iterator<DataWatcher.WatchableObject> iterator() {
            return WatchableArrayMap.this.iterator();
         }

         @Override
         public int size() {
            return WatchableArrayMap.this.size;
         }
      };
   }

   @NotNull
   @Override
   public Set<Entry<Integer, DataWatcher.WatchableObject>> entrySet() {
      return new AbstractSet<Entry<Integer, DataWatcher.WatchableObject>>() {
         @Override
         public Iterator<Entry<Integer, DataWatcher.WatchableObject>> iterator() {
            return new Iterator<Entry<Integer, DataWatcher.WatchableObject>>() {
               private int nextIndex;
               private int lastIndex = -1;

               @Override
               public boolean hasNext() {
                  while (this.nextIndex < WatchableArrayMap.this.dataValues.length
                     && WatchableArrayMap.this.dataValues[this.nextIndex] == null) {
                     this.nextIndex++;
                  }

                  return this.nextIndex < WatchableArrayMap.this.dataValues.length;
               }

               @Override
               public Entry<Integer, DataWatcher.WatchableObject> next() {
                  if (!this.hasNext()) {
                     throw new NoSuchElementException();
                  }

                  this.lastIndex = this.nextIndex;
                  return new SimpleEntry<>(this.nextIndex++, WatchableArrayMap.this.dataValues[this.lastIndex]);
               }

               @Override
               public void remove() {
                  if (this.lastIndex < 0) {
                     throw new IllegalStateException();
                  }

                  if (WatchableArrayMap.this.dataValues[this.lastIndex] != null) {
                     WatchableArrayMap.this.dataValues[this.lastIndex] = null;
                     WatchableArrayMap.this.size--;
                  }

                  this.lastIndex = -1;
               }
            };
         }

         @Override
         public int size() {
            return WatchableArrayMap.this.size;
         }
      };
   }

   @NotNull
   @Override
   public Iterator<DataWatcher.WatchableObject> iterator() {
      return new Iterator<DataWatcher.WatchableObject>() {
         private int nextIndex;
         private int lastIndex = -1;

         @Override
         public boolean hasNext() {
            while (this.nextIndex < WatchableArrayMap.this.dataValues.length
               && WatchableArrayMap.this.dataValues[this.nextIndex] == null) {
               this.nextIndex++;
            }

            return this.nextIndex < WatchableArrayMap.this.dataValues.length;
         }

         @Override
         public DataWatcher.WatchableObject next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            }

            this.lastIndex = this.nextIndex;
            return WatchableArrayMap.this.dataValues[this.nextIndex++];
         }

         @Override
         public void remove() {
            if (this.lastIndex < 0) {
               throw new IllegalStateException();
            }

            if (WatchableArrayMap.this.dataValues[this.lastIndex] != null) {
               WatchableArrayMap.this.dataValues[this.lastIndex] = null;
               WatchableArrayMap.this.size--;
            }

            this.lastIndex = -1;
         }
      };
   }

   public WatchableArrayMap clone() {
      WatchableArrayMap watchableArrayMap = new WatchableArrayMap();

      for (int i = 0; i < this.dataValues.length; i++) {
         DataWatcher.WatchableObject watchableObject = this.dataValues[i];
         if (watchableObject != null) {
            watchableArrayMap.dataValues[i] = watchableObject.b() instanceof ItemStack
               ? new DataWatcher.WatchableObject(watchableObject.c(), watchableObject.a(), ((ItemStack)watchableObject.b()).cloneItemStack())
               : watchableObject.clone();
            watchableArrayMap.size++;
         }
      }

      return watchableArrayMap;
   }
}
