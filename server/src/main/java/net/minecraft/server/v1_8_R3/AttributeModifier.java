package net.minecraft.server.v1_8_R3;

import io.netty.util.internal.ThreadLocalRandom;
import java.util.Objects;
import java.util.UUID;
import org.apache.commons.lang3.Validate;

public class AttributeModifier {
   private final double a;
   private final int b;
   private final String c;
   private final UUID d;
   private boolean e = true;

   public AttributeModifier(String paramString, double paramDouble, int paramInt) {
      this(MathHelper.a(ThreadLocalRandom.current()), paramString, paramDouble, paramInt);
   }

   public AttributeModifier(UUID paramUUID, String paramString, double paramDouble, int paramInt) {
      this.d = paramUUID;
      this.c = paramString;
      this.a = paramDouble;
      this.b = paramInt;
      Validate.notEmpty(paramString, "Modifier name cannot be empty", new Object[0]);
      Validate.inclusiveBetween(0L, 2L, paramInt, "Invalid operation");
   }

   public UUID a() {
      return this.d;
   }

   public String b() {
      return this.c;
   }

   public int c() {
      return this.b;
   }

   public double d() {
      return this.a;
   }

   public boolean e() {
      return this.e;
   }

   public AttributeModifier a(boolean paramBoolean) {
      this.e = paramBoolean;
      return this;
   }

   @Override
   public boolean equals(Object paramObject) {
      if (this == paramObject) {
         return true;
      } else if (paramObject != null && this.getClass() == paramObject.getClass()) {
         AttributeModifier attributeModifier = (AttributeModifier)paramObject;
         return Objects.equals(this.d, attributeModifier.d);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.d != null ? this.d.hashCode() : 0;
   }

   @Override
   public String toString() {
      return "AttributeModifier{amount=" + this.a + ", operation=" + this.b + ", name='" + this.c + '\'' + ", id=" + this.d + ", serialize=" + this.e + '}';
   }
}
