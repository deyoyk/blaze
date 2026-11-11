package net.minecraft.server.v1_8_R3;

import com.google.common.base.Objects;
import org.jetbrains.annotations.NotNull;

public class BaseBlockPosition implements Comparable<BaseBlockPosition> {
   public static final BaseBlockPosition ZERO = new BaseBlockPosition(0, 0, 0);
   protected int a;
   protected int c;
   protected int d;

   public BaseBlockPosition(int i, int j, int k) {
      this.a = i;
      this.c = j;
      this.d = k;
   }

   public BaseBlockPosition(double d0, double d1, double d2) {
      this(MathHelper.floor(d0), MathHelper.floor(d1), MathHelper.floor(d2));
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof BaseBlockPosition)) {
         return false;
      } else {
         BaseBlockPosition baseblockposition = (BaseBlockPosition)object;
         return this.a == baseblockposition.getX() && this.c == baseblockposition.getY() && this.d == baseblockposition.getZ();
      }
   }

   @Override
   public int hashCode() {
      return (this.c + this.d * 31) * 31 + this.a;
   }

   public int g(BaseBlockPosition baseblockposition) {
      return this.c == baseblockposition.getY()
         ? (this.d == baseblockposition.getZ() ? this.a - baseblockposition.getX() : this.d - baseblockposition.getZ())
         : this.c - baseblockposition.getY();
   }

   public void setX(int x) {
      this.a = x;
   }

   public void setY(int y) {
      this.c = y;
   }

   public void setZ(int z) {
      this.d = z;
   }

   public int getX() {
      return this.a;
   }

   public int getY() {
      return this.c;
   }

   public int getZ() {
      return this.d;
   }

   public BaseBlockPosition d(BaseBlockPosition baseblockposition) {
      return new BaseBlockPosition(
         this.c * baseblockposition.getZ() - this.d * baseblockposition.getY(),
         this.d * baseblockposition.getX() - this.a * baseblockposition.getZ(),
         this.a * baseblockposition.getY() - this.c * baseblockposition.getX()
      );
   }

   public double c(double d0, double d1, double d2) {
      double d3 = this.a - d0;
      double d4 = this.c - d1;
      double d5 = this.d - d2;
      return d3 * d3 + d4 * d4 + d5 * d5;
   }

   public double d(double d0, double d1, double d2) {
      double d3 = this.a + 0.5 - d0;
      double d4 = this.c + 0.5 - d1;
      double d5 = this.d + 0.5 - d2;
      return d3 * d3 + d4 * d4 + d5 * d5;
   }

   public double i(BaseBlockPosition baseblockposition) {
      return this.c(baseblockposition.getX(), baseblockposition.getY(), baseblockposition.getZ());
   }

   public double i(int x, int y, int z) {
      return this.c(x, y, z);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).add("x", this.a).add("y", this.c).add("z", this.d).toString();
   }

   public int compareTo(@NotNull BaseBlockPosition object) {
      return this.g(object);
   }
}
