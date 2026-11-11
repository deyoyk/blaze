package lol.deyo.blaze.optimizations;

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.AbstractReferenceList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

public final class ObjectMapList<T> extends AbstractReferenceList<T> implements Set<T> {
   private final Int2IntOpenHashMap objectToIndex;
   private static final Object[] EMPTY_LIST = new Object[0];
   private T[] elements = (T[])EMPTY_LIST;
   private int count;

   public ObjectMapList() {
      this(2, 0.8F);
   }

   public ObjectMapList(int expectedSize, float loadFactor) {
      this.objectToIndex = new Int2IntOpenHashMap(expectedSize, loadFactor);
      this.objectToIndex.defaultReturnValue(Integer.MIN_VALUE);
   }

   @Override
   public int size() {
      return this.count;
   }

   public int indexOf(Object object) {
      return this.objectToIndex.get(object.hashCode());
   }

   public int lastIndexOf(Object object) {
      return super.indexOf(object);
   }

   @Override
   public boolean remove(Object object) {
      int index = this.objectToIndex.remove(object.hashCode());
      if (index == Integer.MIN_VALUE) {
         return false;
      } else {
         int endIndex = --this.count;
         T end = this.elements[endIndex];
         if (index != endIndex) {
            this.objectToIndex.put(end.hashCode(), index);
         }

         this.elements[index] = end;
         this.elements[endIndex] = null;
         return true;
      }
   }

   @Override
   public boolean add(T object) {
      int count = this.count;
      int currIndex = this.objectToIndex.putIfAbsent(object.hashCode(), count);
      if (currIndex != Integer.MIN_VALUE) {
         return false;
      } else {
         Object[] list = this.elements;
         if (list.length == count) {
            list = this.elements = (T[])Arrays.copyOf(list, (int)Math.max(4L, (long)count << 1));
         }

         list[count] = object;
         this.count = count + 1;
         return true;
      }
   }

   public void add(int index, T object) {
      int currIndex = this.objectToIndex.putIfAbsent(object.hashCode(), index);
      if (currIndex == Integer.MIN_VALUE) {
         int count = this.count;
         Object[] list = this.elements;
         if (list.length == count) {
            list = this.elements = (T[])Arrays.copyOf(list, (int)Math.max(4L, (long)count << 1));
         }

         System.arraycopy(list, index, list, index + 1, count - index);
         list[index] = object;
         this.count = count + 1;
      }
   }

   public T get(int index) {
      return this.elements[index];
   }

   @Override
   public boolean isEmpty() {
      return this.count == 0;
   }

   @Override
   public void clear() {
      this.objectToIndex.clear();
      Arrays.fill(this.elements, 0, this.count, null);
      this.count = 0;
   }

   @NotNull
   @Override
   public Object[] toArray() {
      return Arrays.copyOf(this.elements, this.count);
   }

   public ObjectSpliterator<T> spliterator() {
      return super.spliterator();
   }

   @NotNull
   public ObjectListIterator<T> iterator() {
      return new ObjectMapList.Iterator(0);
   }

   private class Iterator implements ObjectListIterator<T> {
      T lastRet;
      int current;

      Iterator(int index) {
         this.current = index;
      }

      public int nextIndex() {
         return this.current + 1;
      }

      public int previousIndex() {
         return this.current - 1;
      }

      public boolean hasNext() {
         return this.current < ObjectMapList.this.count;
      }

      public boolean hasPrevious() {
         return this.current > 0;
      }

      public T next() {
         if (this.current >= ObjectMapList.this.count) {
            throw new NoSuchElementException();
         } else {
            return this.lastRet = ObjectMapList.this.elements[this.current++];
         }
      }

      public T previous() {
         if (this.current < 0) {
            throw new NoSuchElementException();
         } else {
            return this.lastRet = ObjectMapList.this.elements[--this.current];
         }
      }

      public void remove() {
         T lastRet = this.lastRet;
         if (lastRet == null) {
            throw new IllegalStateException();
         } else {
            this.lastRet = null;
            ObjectMapList.this.remove(lastRet);
            this.current--;
         }
      }
   }
}
