package org.bukkit.craftbukkit.v1_8_R3.util;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class HashTreeSet<V extends Comparable<?>> {
   private final Set<V> hash = new ObjectOpenHashSet();
   private final TreeSet<V> tree = new TreeSet<>();

   public int size() {
      return this.hash.size();
   }

   public boolean isEmpty() {
      return this.hash.isEmpty();
   }

   public boolean contains(V o) {
      return this.hash.contains(o);
   }

   public Iterator<V> iterator() {
      return new Iterator<V>() {
         private final Iterator<V> it = HashTreeSet.this.tree.iterator();
         private V last;

         @Override
         public boolean hasNext() {
            return this.it.hasNext();
         }

         public V next() {
            return this.last = this.it.next();
         }

         @Override
         public void remove() {
            if (this.last == null) {
               throw new IllegalStateException();
            } else {
               this.it.remove();
               HashTreeSet.this.hash.remove(this.last);
               this.last = null;
            }
         }
      };
   }

   public Object[] toArray() {
      return this.hash.toArray();
   }

   public Object[] toArray(Object[] a) {
      return this.hash.toArray(a);
   }

   public boolean add(V e) {
      this.hash.add(e);
      return this.tree.add(e);
   }

   public boolean remove(V o) {
      this.hash.remove(o);
      return this.tree.remove(o);
   }

   public boolean addAll(Collection<V> c) {
      this.tree.addAll(c);
      return this.hash.addAll(c);
   }

   public void clear() {
      this.hash.clear();
      this.tree.clear();
   }

   public V first() {
      return this.tree.first();
   }
}
