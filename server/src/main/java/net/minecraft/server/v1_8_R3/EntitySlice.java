package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Iterators;
import java.util.Collections;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.AbstractSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EntitySlice<T> extends AbstractSet<T> {
   private static final Set<Class<?>> a = Sets.newConcurrentHashSet();
   private final Map<Class<?>, List<T>> b = Maps.newHashMap();
   private final Set<Class<?>> c = Sets.newIdentityHashSet();
   private final Class<T> d;
   private final List<T> e = Lists.newArrayList();

   public EntitySlice(Class<T> oclass) {
      this.d = oclass;
      this.c.add(oclass);
      this.b.put(oclass, this.e);

      for (Class<?> aClass : a) {
         this.a(aClass);
      }
   }

   protected void a(Class<?> oclass) {
      a.add(oclass);

      for (T object : this.e) {
         if (oclass.isAssignableFrom(object.getClass())) {
            this.a(object, oclass);
         }
      }

      this.c.add(oclass);
   }

   protected Class<?> b(Class<?> oclass) {
      if (this.d.isAssignableFrom(oclass)) {
         if (!this.c.contains(oclass)) {
            this.a(oclass);
         }

         return oclass;
      } else {
         throw new IllegalArgumentException("Don't know how to search for " + oclass);
      }
   }

   @Override
   public boolean add(T t0) {
      for (Class<?> aClass : this.c) {
         if (aClass.isAssignableFrom(t0.getClass())) {
            this.a(t0, aClass);
         }
      }

      return true;
   }

   private void a(T t0, Class<?> oclass) {
      List<T> list = this.b.get(oclass);
      if (list == null) {
         this.b.put(oclass, Lists.<T>newArrayList(t0));
      } else {
         list.add(t0);
      }
   }

   @Override
   public boolean remove(Object object) {
      boolean flag = false;

      for (Class<?> oclass : this.c) {
         if (oclass.isAssignableFrom(object.getClass())) {
            List<T> list = this.b.get(oclass);
            if (list != null && list.remove(object)) {
               flag = true;
            }
         }
      }

      return flag;
   }

   @Override
   public boolean contains(Object object) {
      return Iterators.contains(this.c(object.getClass()).iterator(), object);
   }

   public <S> Iterable c(Class<S> oclass) {
      return () -> {
         List<T> list = this.b.get(this.b(oclass));
         return (Iterator)(list == null ? Collections.<T>emptyList().iterator() : Iterators.filter(list.iterator(), oclass));
      };
   }

   @Override
   public Iterator<T> iterator() {
      return this.e.isEmpty() ? Collections.<T>emptyList().iterator() : Iterators.unmodifiableIterator(this.e.iterator());
   }

   @Override
   public int size() {
      return this.e.size();
   }
}
