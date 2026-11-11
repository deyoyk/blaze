package lol.deyo.blaze.utils;

import com.google.common.collect.Iterators;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

public final class LinkedArraySet<E> implements Set<E> {
   private final ArrayList<E> list = new ArrayList<>();

   public LinkedArraySet() {
   }

   public LinkedArraySet(Iterable<? extends E> elements) {
      Iterators.addAll(this.list, elements.iterator());
   }

   @Override
   public boolean add(E e) {
      return this.list.add(e);
   }

   @Override
   public boolean remove(Object o) {
      return this.list.remove(o);
   }

   public E remove(int o) {
      return this.list.remove(o);
   }

   @Override
   public boolean containsAll(@NotNull Collection<?> c) {
      return this.list.containsAll(c);
   }

   @Override
   public void clear() {
      this.list.clear();
   }

   public E get(int index) {
      return this.list.get(index);
   }

   @Override
   public boolean removeAll(@NotNull Collection<?> c) {
      return this.list.removeAll(c);
   }

   @Override
   public boolean retainAll(@NotNull Collection<?> c) {
      return this.list.retainAll(c);
   }

   @Override
   public boolean addAll(Collection<? extends E> c) {
      boolean modified = false;

      for (E e : c) {
         if (this.add(e)) {
            modified = true;
         }
      }

      return modified;
   }

   @Override
   public int size() {
      return this.list.size();
   }

   @Override
   public boolean isEmpty() {
      return this.list.isEmpty();
   }

   @Override
   public boolean contains(Object o) {
      return this.list.contains(o);
   }

   @NotNull
   @Override
   public Iterator<E> iterator() {
      return this.list.iterator();
   }

   @NotNull
   @Override
   public Object[] toArray() {
      return this.list.toArray();
   }

   @NotNull
   @Override
   public <T> T[] toArray(@NotNull T[] a) {
      return (T[])this.list.toArray(a);
   }

   public int indexOf(E o) {
      return this.list.indexOf(o);
   }
}
