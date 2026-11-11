package net.minecraft.server.v1_8_R3;

import com.google.common.base.Objects;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import org.jetbrains.annotations.NotNull;

public class WatchableArrayMap implements Map<Integer, DataWatcher.WatchableObject> {
   private DataWatcher.WatchableObject[] dataValues = new DataWatcher.WatchableObject[23];

   @Override
   public int size() {
      return this.dataValues.length;
   }

   @Override
   public boolean isEmpty() {
      for (DataWatcher.WatchableObject dataValue : this.dataValues) {
         if (dataValue != null) {
            return false;
         }
      }

      return true;
   }

   public boolean containsKey(int i) {
      return this.dataValues[i] != null;
   }

   @Override
   public boolean containsKey(Object key) {
      return key instanceof Integer && this.dataValues[(Integer)key] != null;
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
      return this.dataValues[i];
   }

   public DataWatcher.WatchableObject get(Object key) {
      return key instanceof Integer ? this.dataValues[(Integer)key] : null;
   }

   public void put(int i, DataWatcher.WatchableObject watchableObject) {
      this.dataValues[i] = watchableObject;
   }

   public DataWatcher.WatchableObject put(Integer key, DataWatcher.WatchableObject value) {
      int index = key;
      DataWatcher.WatchableObject old = this.dataValues[index];
      this.dataValues[index] = value;
      return old;
   }

   public DataWatcher.WatchableObject remove(Object key) {
      if (!(key instanceof Integer)) {
         return null;
      } else {
         int index = (Integer)key;
         DataWatcher.WatchableObject old = this.dataValues[index];
         this.dataValues[index] = null;
         return old;
      }
   }

   @Override
   public void putAll(Map m) {
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
      this.dataValues = new DataWatcher.WatchableObject[23];
   }

   @Override
   public Set<Integer> keySet() {
      Set<Integer> set = new HashSet<>();

      for (int i = 0; i < this.dataValues.length; i++) {
         if (this.dataValues[i] != null) {
            set.add(i);
         }
      }

      return set;
   }

   @Override
   public Collection<DataWatcher.WatchableObject> values() {
      Set<DataWatcher.WatchableObject> set = new HashSet<>();

      for (DataWatcher.WatchableObject dataValue : this.dataValues) {
         if (dataValue != null) {
            set.add(dataValue);
         }
      }

      return set;
   }

   @NotNull
   @Override
   public Set<Entry<Integer, DataWatcher.WatchableObject>> entrySet() {
      Set<Entry<Integer, DataWatcher.WatchableObject>> set = new HashSet<>();

      for (int i = 0; i < this.dataValues.length; i++) {
         DataWatcher.WatchableObject watchableObject = this.dataValues[i];
         if (watchableObject != null) {
            set.add(new SimpleEntry<>(i, watchableObject));
         }
      }

      return set;
   }

   public WatchableArrayMap clone() {
      WatchableArrayMap watchableArrayMap = new WatchableArrayMap();

      for (int i = 0; i < this.dataValues.length; i++) {
         DataWatcher.WatchableObject watchableObject = this.dataValues[i];
         if (watchableObject != null) {
            watchableArrayMap.dataValues[i] = watchableObject.b() instanceof ItemStack
               ? new DataWatcher.WatchableObject(watchableObject.c(), watchableObject.a(), ((ItemStack)watchableObject.b()).cloneItemStack())
               : watchableObject.clone();
         }
      }

      return watchableArrayMap;
   }
}
