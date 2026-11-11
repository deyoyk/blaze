package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class AttributeModifiable implements AttributeInstance {
   private final AttributeMapBase a;
   private final IAttribute b;
   private final Map<Integer, Set<AttributeModifier>> c = Maps.newHashMap();
   private final Map<String, Set<AttributeModifier>> d = Maps.newHashMap();
   private final Map<UUID, AttributeModifier> e = Maps.newHashMap();
   private double f;
   private boolean g = true;
   private double h;

   public AttributeModifiable(AttributeMapBase attributemapbase, IAttribute iattribute) {
      this.a = attributemapbase;
      this.b = iattribute;
      this.f = iattribute.b();

      for (int i = 0; i < 3; i++) {
         this.c.put(i, Sets.newHashSet());
      }
   }

   @Override
   public IAttribute getAttribute() {
      return this.b;
   }

   @Override
   public double b() {
      return this.f;
   }

   @Override
   public void setValue(double d0) {
      if (d0 != this.b()) {
         this.f = d0;
         this.f();
      }
   }

   @Override
   public Collection<AttributeModifier> a(int i) {
      return this.c.get(i);
   }

   @Override
   public Collection<AttributeModifier> c() {
      Set<AttributeModifier> hashset = Sets.newHashSet();

      for (int i = 0; i < 3; i++) {
         hashset.addAll(this.a(i));
      }

      return hashset;
   }

   @Override
   public AttributeModifier a(UUID uuid) {
      return this.e.get(uuid);
   }

   @Override
   public boolean a(AttributeModifier attributemodifier) {
      return this.e.get(attributemodifier.a()) != null;
   }

   @Override
   public void b(AttributeModifier attributemodifier) {
      if (this.a(attributemodifier.a()) != null) {
         throw new IllegalArgumentException("Modifier is already applied on this attribute!");
      } else {
         Set<AttributeModifier> object = this.d.computeIfAbsent(attributemodifier.b(), k -> Sets.newHashSet());
         this.c.get(attributemodifier.c()).add(attributemodifier);
         object.add(attributemodifier);
         this.e.put(attributemodifier.a(), attributemodifier);
         this.f();
      }
   }

   protected void f() {
      this.g = true;
      this.a.a(this);
   }

   @Override
   public void c(AttributeModifier attributemodifier) {
      for (int i = 0; i < 3; i++) {
         this.c.get(i).remove(attributemodifier);
      }

      Set<AttributeModifier> set1 = this.d.get(attributemodifier.b());
      if (set1 != null) {
         set1.remove(attributemodifier);
         if (set1.isEmpty()) {
            this.d.remove(attributemodifier.b());
         }
      }

      this.e.remove(attributemodifier.a());
      this.f();
   }

   @Override
   public double getValue() {
      if (this.g) {
         this.h = this.g();
         this.g = false;
      }

      return this.h;
   }

   private double g() {
      double d0 = this.b();

      for (AttributeModifier attributemodifier : this.b(0)) {
         d0 += attributemodifier.d();
      }

      double d1 = d0;

      for (AttributeModifier attributemodifier1 : this.b(1)) {
         d1 += d0 * attributemodifier1.d();
      }

      for (AttributeModifier attributemodifier1 : this.b(2)) {
         d1 *= 1.0 + attributemodifier1.d();
      }

      return this.b.a(d1);
   }

   private Collection<AttributeModifier> b(int i) {
      HashSet<AttributeModifier> hashset = Sets.newHashSet(this.a(i));

      for (IAttribute iattribute = this.b.d(); iattribute != null; iattribute = iattribute.d()) {
         AttributeInstance attributeinstance = this.a.a(iattribute);
         if (attributeinstance != null) {
            hashset.addAll(attributeinstance.a(i));
         }
      }

      return hashset;
   }
}
