package lol.deyo.blaze.utils.fanciful;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;

public final class ArrayWrapper<E> {
   private E[] _array;

   @SafeVarargs
   public ArrayWrapper(E... elements) {
      this.setArray((E[])elements);
   }

   public E[] getArray() {
      return this._array;
   }

   public void setArray(E[] array) {
      if (array == null) {
         throw new NullPointerException("The array must not be null.");
      } else {
         this._array = (E[])array;
      }
   }

   @Override
   public boolean equals(Object other) {
      return !(other instanceof ArrayWrapper) ? false : Arrays.equals(this._array, ((ArrayWrapper)other)._array);
   }

   @Override
   public int hashCode() {
      return Arrays.hashCode(this._array);
   }

   public static <T> T[] toArray(Iterable<? extends T> list, Class<T> c) {
      int size = -1;
      if (list instanceof Collection) {
         Collection coll = (Collection)list;
         size = coll.size();
      }

      if (size < 0) {
         size = 0;

         for (T element : list) {
            size++;
         }
      }

      Object[] result = (Object[])Array.newInstance(c, size);
      int i = 0;

      for (T element : list) {
         result[i++] = element;
      }

      return (T[])result;
   }
}
