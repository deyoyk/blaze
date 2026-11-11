package lol.deyo.blaze.optimizations;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import org.jetbrains.annotations.NotNull;

public class OptimizedNonEmptyList<T> implements List<T> {
   private boolean isEmpty = true;

   @Override
   public int size() {
      return this.isEmpty ? 0 : 1;
   }

   @Override
   public boolean isEmpty() {
      return this.isEmpty;
   }

   @Override
   public boolean contains(Object o) {
      throw new IllegalStateException();
   }

   @NotNull
   @Override
   public Iterator<T> iterator() {
      throw new IllegalStateException();
   }

   @NotNull
   @Override
   public Object[] toArray() {
      throw new IllegalStateException();
   }

   @Override
   public boolean add(Object object) {
      this.isEmpty = false;
      return true;
   }

   @Override
   public boolean remove(Object o) {
      throw new IllegalStateException();
   }

   @Override
   public boolean addAll(@NotNull Collection c) {
      this.isEmpty = false;
      return true;
   }

   @Override
   public boolean addAll(int index, @NotNull Collection c) {
      this.isEmpty = false;
      return true;
   }

   @Override
   public void clear() {
      this.isEmpty = true;
   }

   @Override
   public T get(int index) {
      throw new IllegalStateException();
   }

   @Override
   public T set(int index, Object element) {
      throw new IllegalStateException();
   }

   @Override
   public void add(int index, Object element) {
      throw new IllegalStateException();
   }

   @Override
   public T remove(int index) {
      throw new IllegalStateException();
   }

   @Override
   public int indexOf(Object o) {
      throw new IllegalStateException();
   }

   @Override
   public int lastIndexOf(Object o) {
      throw new IllegalStateException();
   }

   @NotNull
   @Override
   public ListIterator<T> listIterator() {
      throw new IllegalStateException();
   }

   @NotNull
   @Override
   public ListIterator<T> listIterator(int index) {
      throw new IllegalStateException();
   }

   @NotNull
   @Override
   public List<T> subList(int fromIndex, int toIndex) {
      throw new IllegalStateException();
   }

   @Override
   public boolean retainAll(@NotNull Collection c) {
      throw new IllegalStateException();
   }

   @Override
   public boolean removeAll(@NotNull Collection c) {
      throw new IllegalStateException();
   }

   @Override
   public boolean containsAll(@NotNull Collection c) {
      throw new IllegalStateException();
   }

   @NotNull
   @Override
   public Object[] toArray(@NotNull Object[] a) {
      throw new IllegalStateException();
   }
}
