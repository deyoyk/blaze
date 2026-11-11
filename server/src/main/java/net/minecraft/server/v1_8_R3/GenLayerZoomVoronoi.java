package net.minecraft.server.v1_8_R3;

public class GenLayerZoomVoronoi extends GenLayer {
   public GenLayerZoomVoronoi(long i, GenLayer genlayer) {
      super(i);
      super.a = genlayer;
   }

   @Override
   public int[] a(int i, int j, int k, int l) {
      i -= 2;
      j -= 2;
      int i1 = i >> 2;
      int j1 = j >> 2;
      int k1 = (k >> 2) + 2;
      int l1 = (l >> 2) + 2;
      int[] aint = this.a.a(i1, j1, k1, l1);
      int i2 = k1 - 1 << 2;
      int j2 = l1 - 1 << 2;
      int[] aint1 = IntCache.a(i2 * j2);

      for (int l2 = 0; l2 < l1 - 1; l2++) {
         int k2 = 0;
         int i3 = aint[k2 + l2 * k1];

         for (int j3 = aint[k2 + (l2 + 1) * k1]; k2 < k1 - 1; k2++) {
            this.a((long)k2 + i1 << 2, (long)l2 + j1 << 2);
            double d1 = (this.a(1024) / 1024.0 - 0.5) * 3.6;
            double d2 = (this.a(1024) / 1024.0 - 0.5) * 3.6;
            this.a((long)k2 + i1 + 1L << 2, (long)l2 + j1 << 2);
            double d3 = (this.a(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
            double d4 = (this.a(1024) / 1024.0 - 0.5) * 3.6;
            this.a((long)k2 + i1 << 2, (long)l2 + j1 + 1L << 2);
            double d5 = (this.a(1024) / 1024.0 - 0.5) * 3.6;
            double d6 = (this.a(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
            this.a((long)k2 + i1 + 1L << 2, (long)l2 + j1 + 1L << 2);
            double d7 = (this.a(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
            double d8 = (this.a(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
            int k3 = aint[k2 + 1 + l2 * k1] & 0xFF;
            int l3 = aint[k2 + 1 + (l2 + 1) * k1] & 0xFF;

            for (int i4 = 0; i4 < 4; i4++) {
               int j4 = ((l2 << 2) + i4) * i2 + (k2 << 2);

               for (int k4 = 0; k4 < 4; k4++) {
                  double d9 = (i4 - d2) * (i4 - d2) + (k4 - d1) * (k4 - d1);
                  double d10 = (i4 - d4) * (i4 - d4) + (k4 - d3) * (k4 - d3);
                  double d11 = (i4 - d6) * (i4 - d6) + (k4 - d5) * (k4 - d5);
                  double d12 = (i4 - d8) * (i4 - d8) + (k4 - d7) * (k4 - d7);
                  if (d9 < d10 && d9 < d11 && d9 < d12) {
                     aint1[j4++] = i3;
                  } else if (d10 < d9 && d10 < d11 && d10 < d12) {
                     aint1[j4++] = k3;
                  } else if (d11 < d9 && d11 < d10 && d11 < d12) {
                     aint1[j4++] = j3;
                  } else {
                     aint1[j4++] = l3;
                  }
               }
            }

            i3 = k3;
            j3 = l3;
         }
      }

      int[] aint2 = IntCache.a(k * l);

      for (int k2 = 0; k2 < l; k2++) {
         System.arraycopy(aint1, (k2 + (j & 3)) * i2 + (i & 3), aint2, k2 * k, k);
      }

      return aint2;
   }
}
