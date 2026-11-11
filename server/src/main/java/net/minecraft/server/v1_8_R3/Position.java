package net.minecraft.server.v1_8_R3;

public class Position implements IPosition {
   protected final double a;
   protected final double b;
   protected final double c;

   public Position(double var1, double var3, double var5) {
      this.a = var1;
      this.b = var3;
      this.c = var5;
   }

   @Override
   public double getX() {
      return this.a;
   }

   @Override
   public double getY() {
      return this.b;
   }

   @Override
   public double getZ() {
      return this.c;
   }
}
