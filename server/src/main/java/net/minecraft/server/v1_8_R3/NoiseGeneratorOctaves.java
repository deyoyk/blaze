package net.minecraft.server.v1_8_R3;

import java.util.Arrays;
import java.util.Random;

public class NoiseGeneratorOctaves {
   private final NoiseGeneratorPerlin[] a;
   private final int b;

   public NoiseGeneratorOctaves(Random random, int i) {
      this.b = i;
      this.a = new NoiseGeneratorPerlin[i];

      for (int j = 0; j < i; j++) {
         this.a[j] = new NoiseGeneratorPerlin(random);
      }
   }

   public double[] a(double[] adouble, int i, int j, int k, int l, int i1, int j1, double d0, double d1, double d2) {
      if (adouble == null) {
         adouble = new double[l * i1 * j1];
      } else {
         Arrays.fill(adouble, 0.0);
      }

      double d3 = 1.0;

      for (int l1 = 0; l1 < this.b; l1++) {
         double d4 = i * d3 * d0;
         double d5 = j * d3 * d1;
         double d6 = k * d3 * d2;
         long i2 = MathHelper.d(d4);
         long j2 = MathHelper.d(d6);
         d4 -= i2;
         d6 -= j2;
         i2 %= 16777216L;
         j2 %= 16777216L;
         d4 += i2;
         d6 += j2;
         this.a[l1].a(adouble, d4, d5, d6, l, i1, j1, d0 * d3, d1 * d3, d2 * d3, d3);
         d3 /= 2.0;
      }

      return adouble;
   }

   public double[] a(double[] adouble, int i, int j, int k, int l, double d0, double d1, double d2) {
      return this.a(adouble, i, 10, j, k, 1, l, d0, 1.0, d1);
   }
}
