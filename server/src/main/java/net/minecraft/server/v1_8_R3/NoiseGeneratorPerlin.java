package net.minecraft.server.v1_8_R3;

import java.util.Random;

public class NoiseGeneratorPerlin {
   private final int[] d = new int[512];
   public double a;
   public double b;
   public double c;
   private static final double[] e = new double[]{1.0, -1.0, 1.0, -1.0, 1.0, -1.0, 1.0, -1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, -1.0, 0.0};
   private static final double[] f = new double[]{1.0, 1.0, -1.0, -1.0, 0.0, 0.0, 0.0, 0.0, 1.0, -1.0, 1.0, -1.0, 1.0, -1.0, 1.0, -1.0};
   private static final double[] g = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 1.0, -1.0, -1.0, 1.0, 1.0, -1.0, -1.0, 0.0, 1.0, 0.0, -1.0};
   private static final double[] h = new double[]{1.0, -1.0, 1.0, -1.0, 1.0, -1.0, 1.0, -1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, -1.0, 0.0};
   private static final double[] i = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 1.0, -1.0, -1.0, 1.0, 1.0, -1.0, -1.0, 0.0, 1.0, 0.0, -1.0};

   public NoiseGeneratorPerlin() {
      this(new Random());
   }

   public NoiseGeneratorPerlin(Random random) {
      this.a = random.nextDouble() * 256.0;
      this.b = random.nextDouble() * 256.0;
      this.c = random.nextDouble() * 256.0;
      int i = 0;

      while (i < 256) {
         this.d[i] = i++;
      }

      for (int var5 = 0; var5 < 256; var5++) {
         int j = random.nextInt(256 - var5) + var5;
         int k = this.d[var5];
         this.d[var5] = this.d[j];
         this.d[j] = k;
         this.d[var5 + 256] = this.d[var5];
      }
   }

   public final double b(double d0, double d1, double d2) {
      return d1 + d0 * (d2 - d1);
   }

   public final double a(int i, double d0, double d1) {
      int j = i & 15;
      return h[j] * d0 + NoiseGeneratorPerlin.i[j] * d1;
   }

   public final double a(int i, double d0, double d1, double d2) {
      int j = i & 15;
      return e[j] * d0 + f[j] * d1 + g[j] * d2;
   }

   public void a(double[] adouble, double d0, double d1, double d2, int i, int j, int k, double d3, double d4, double d5, double d6) {
      if (j == 1) {
         int i2 = 0;
         double d13 = 1.0 / d6;

         for (int k2 = 0; k2 < i; k2++) {
            double d7 = d0 + k2 * d3 + this.a;
            int l2 = (int)d7;
            if (d7 < l2) {
               l2--;
            }

            int i3 = l2 & 0xFF;
            d7 -= l2;
            double d8 = d7 * d7 * d7 * (d7 * (d7 * 6.0 - 15.0) + 10.0);

            for (int j1 = 0; j1 < k; j1++) {
               double d9 = d2 + j1 * d5 + this.c;
               int k1 = (int)d9;
               if (d9 < k1) {
                  k1--;
               }

               int l1 = k1 & 0xFF;
               d9 -= k1;
               double d10 = d9 * d9 * d9 * (d9 * (d9 * 6.0 - 15.0) + 10.0);
               int l = this.d[i3];
               int j3 = this.d[l] + l1;
               int k3 = this.d[i3 + 1];
               int i1 = this.d[k3] + l1;
               double d11 = this.b(d8, this.a(this.d[j3], d7, d9), this.a(this.d[i1], d7 - 1.0, 0.0, d9));
               double d12 = this.b(d8, this.a(this.d[j3 + 1], d7, 0.0, d9 - 1.0), this.a(this.d[i1 + 1], d7 - 1.0, 0.0, d9 - 1.0));
               double d14 = this.b(d10, d11, d12);
               int j2 = i2++;
               adouble[j2] += d14 * d13;
            }
         }
      } else {
         int l = 0;
         double d15 = 1.0 / d6;
         int i1 = -1;
         double d16 = 0.0;
         double d7 = 0.0;
         double d17 = 0.0;
         double d8 = 0.0;

         for (int j1 = 0; j1 < i; j1++) {
            double d9 = d0 + j1 * d3 + this.a;
            int k1 = (int)d9;
            if (d9 < k1) {
               k1--;
            }

            int l1 = k1 & 0xFF;
            d9 -= k1;
            double d10 = d9 * d9 * d9 * (d9 * (d9 * 6.0 - 15.0) + 10.0);

            for (int l3 = 0; l3 < k; l3++) {
               double d18 = d2 + l3 * d5 + this.c;
               int i4 = (int)d18;
               if (d18 < i4) {
                  i4--;
               }

               int j4 = i4 & 0xFF;
               d18 -= i4;
               double d19 = d18 * d18 * d18 * (d18 * (d18 * 6.0 - 15.0) + 10.0);

               for (int k4 = 0; k4 < j; k4++) {
                  double d20 = d1 + k4 * d4 + this.b;
                  int l4 = (int)d20;
                  if (d20 < l4) {
                     l4--;
                  }

                  int i5 = l4 & 0xFF;
                  d20 -= l4;
                  double d21 = d20 * d20 * d20 * (d20 * (d20 * 6.0 - 15.0) + 10.0);
                  if (k4 == 0 || i5 != i1) {
                     i1 = i5;
                     int j5 = this.d[l1] + i5;
                     int k5 = this.d[j5] + j4;
                     int l5 = this.d[j5 + 1] + j4;
                     int i6 = this.d[l1 + 1] + i5;
                     int i2 = this.d[i6] + j4;
                     int j6 = this.d[i6 + 1] + j4;
                     d16 = this.b(d10, this.a(this.d[k5], d9, d20, d18), this.a(this.d[i2], d9 - 1.0, d20, d18));
                     d7 = this.b(d10, this.a(this.d[l5], d9, d20 - 1.0, d18), this.a(this.d[j6], d9 - 1.0, d20 - 1.0, d18));
                     d17 = this.b(d10, this.a(this.d[k5 + 1], d9, d20, d18 - 1.0), this.a(this.d[i2 + 1], d9 - 1.0, d20, d18 - 1.0));
                     d8 = this.b(d10, this.a(this.d[l5 + 1], d9, d20 - 1.0, d18 - 1.0), this.a(this.d[j6 + 1], d9 - 1.0, d20 - 1.0, d18 - 1.0));
                  }

                  double d22 = this.b(d21, d16, d7);
                  double d23 = this.b(d21, d17, d8);
                  double d24 = this.b(d19, d22, d23);
                  int j2 = l++;
                  adouble[j2] += d24 * d15;
               }
            }
         }
      }
   }
}
