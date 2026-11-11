package lol.deyo.blaze.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

public final class IndexedLinkedHashSet<E> implements Set<E> {
   private final ArrayList<E> list = new ArrayList<>();
   private final HashSet<E> set = new HashSet<>();

   @Override
   public boolean add(E e) {
      return this.set.add(e) ? this.list.add(e) : false;
   }

   @Override
   public boolean remove(Object o) {
      return this.set.remove(o) ? this.list.remove(o) : false;
   }

   @Override
   public boolean containsAll(@NotNull Collection<?> c) {
      return this.set.containsAll(c);
   }

   @Override
   public void clear() {
      this.set.clear();
      this.list.clear();
   }

   public E get(int index) {
      return this.list.get(index);
   }

   @Override
   public boolean removeAll(@NotNull Collection<?> c) {
      return this.set.removeAll(c) ? this.list.removeAll(c) : true;
   }

   @Override
   public boolean retainAll(@NotNull Collection<?> c) {
      return this.set.retainAll(c) ? this.list.retainAll(c) : false;
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
      return this.set.size();
   }

   @Override
   public boolean isEmpty() {
      return this.set.isEmpty();
   }

   @Override
   public boolean contains(Object o) {
      return this.set.contains(o);
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
