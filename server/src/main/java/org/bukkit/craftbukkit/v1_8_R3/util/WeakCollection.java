package org.bukkit.craftbukkit.v1_8_R3.util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.commons.lang.Validate;

public final class WeakCollection<T> implements Collection<T> {
   static final Object NO_VALUE = new Object();
   private final Collection<WeakReference<T>> collection = new ArrayList<>();

   @Override
   public boolean add(T value) {
      Validate.notNull(value, "Cannot add null value");
      return this.collection.add(new WeakReference<>(value));
   }

   @Override
   public boolean addAll(Collection<? extends T> collection) {
      Collection<WeakReference<T>> values = this.collection;
      boolean ret = false;

      for (T value : collection) {
         Validate.notNull(value, "Cannot add null value");
         ret |= values.add(new WeakReference<>(value));
      }

      return ret;
   }

   @Override
   public void clear() {
      this.collection.clear();
   }

   @Override
   public boolean contains(Object object) {
      if (object == null) {
         return false;
      } else {
         for (T compare : this) {
            if (object.equals(compare)) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public boolean containsAll(Collection<?> collection) {
      return this.toCollection().containsAll(collection);
   }

   @Override
   public boolean isEmpty() {
      return !this.iterator().hasNext();
   }

   @Override
   public Iterator<T> iterator() {
      return new Iterator<T>() {
         final Iterator<WeakReference<T>> it = WeakCollection.this.collection.iterator();
         Object value = WeakCollection.NO_VALUE;

         @Override
         public boolean hasNext() {
            Object value = this.value;
            if (value != null && value != WeakCollection.NO_VALUE) {
               return true;
            } else {
               Iterator<WeakReference<T>> it = this.it;

               while (it.hasNext()) {
                  WeakReference<T> ref = it.next();
                  value = ref.get();
                  if (value != null) {
                     this.value = value;
                     return true;
                  }

                  it.remove();
               }

               return false;
            }
         }

         @Override
         public T next() throws NoSuchElementException {
            if (!this.hasNext()) {
               throw new NoSuchElementException("No more elements");
            } else {
               T value = (T)this.value;
               this.value = WeakCollection.NO_VALUE;
               return value;
            }
         }

         @Override
         public void remove() throws IllegalStateException {
            if (this.value != WeakCollection.NO_VALUE) {
               throw new IllegalStateException("No last element");
            } else {
               this.value = null;
               this.it.remove();
            }
         }
      };
   }

   @Override
   public boolean remove(Object object) {
      if (object == null) {
         return false;
      } else {
         Iterator<T> it = this.iterator();

         while (it.hasNext()) {
            if (object.equals(it.next())) {
               it.remove();
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public boolean removeAll(Collection<?> collection) {
      Iterator<T> it = this.iterator();
      boolean ret = false;

      while (it.hasNext()) {
         if (collection.contains(it.next())) {
            ret = true;
            it.remove();
         }
      }

      return ret;
   }

   @Override
   public boolean retainAll(Collection<?> collection) {
      Iterator<T> it = this.iterator();
      boolean ret = false;

      while (it.hasNext()) {
         if (!collection.contains(it.next())) {
            ret = true;
            it.remove();
         }
      }

      return ret;
   }

   @Override
   public int size() {
      int s = 0;

      for (T ignored : this) {
         s++;
      }

      return s;
   }

   @Override
   public Object[] toArray() {
      return this.toArray(new Object[0]);
   }

   @Override
   public <T> T[] toArray(T[] array) {
      return (T[])this.toCollection().toArray(array);
   }

   private Collection<T> toCollection() {
      return new ArrayList<>(this);
   }
}
