package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AttributeMapServer extends AttributeMapBase {
   private final Set<AttributeInstance> e = Sets.newHashSet();
   protected final Map<String, AttributeInstance> d = new HashMap<>();

   public AttributeModifiable e(IAttribute paramIAttribute) {
      return (AttributeModifiable)super.a(paramIAttribute);
   }

   public AttributeModifiable b(String paramString) {
      AttributeInstance attributeInstance = super.a(paramString);
      if (attributeInstance == null) {
         attributeInstance = this.d.get(paramString);
      }

      return (AttributeModifiable)attributeInstance;
   }

   @Override
   public AttributeInstance b(IAttribute paramIAttribute) {
      AttributeInstance attributeInstance = super.b(paramIAttribute);
      if (paramIAttribute instanceof AttributeRanged && ((AttributeRanged)paramIAttribute).g() != null) {
         this.d.put(((AttributeRanged)paramIAttribute).g(), attributeInstance);
      }

      return attributeInstance;
   }

   @Override
   protected AttributeInstance c(IAttribute paramIAttribute) {
      return new AttributeModifiable(this, paramIAttribute);
   }

   @Override
   public void a(AttributeInstance paramAttributeInstance) {
      if (paramAttributeInstance.getAttribute().c()) {
         this.e.add(paramAttributeInstance);
      }

      for (IAttribute iAttribute : this.c.get(paramAttributeInstance.getAttribute())) {
         AttributeModifiable attributeModifiable = this.e(iAttribute);
         if (attributeModifiable != null) {
            attributeModifiable.f();
         }
      }
   }

   public Set<AttributeInstance> getAttributes() {
      return this.e;
   }

   public Collection<AttributeInstance> c() {
      HashSet<AttributeInstance> hashSet = Sets.newHashSet();

      for (AttributeInstance attributeInstance : this.a()) {
         if (attributeInstance.getAttribute().c()) {
            hashSet.add(attributeInstance);
         }
      }

      return hashSet;
   }

   public Collection<AttributeInstance> getUsefulAttributes(boolean horse) {
      Set<AttributeInstance> hashset = new HashSet<>();

      for (AttributeInstance attributeinstance : this.a()) {
         if (attributeinstance.getAttribute().c()
            && attributeinstance.getAttribute().b() != attributeinstance.getValue()
            && (attributeinstance.getAttribute() != GenericAttributes.MOVEMENT_SPEED || horse)) {
            hashset.add(attributeinstance);
         }
      }

      return hashset;
   }
}
