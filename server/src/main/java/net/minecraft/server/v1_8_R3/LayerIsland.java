package net.minecraft.server.v1_8_R3;

import java.util.Arrays;

public class LayerIsland extends GenLayer {
   private final World world;

   public LayerIsland(long i, World world) {
      super(i);
      this.world = world;
   }

   @Override
   public int[] a(int i, int j, int k, int l) {
      int[] aint = IntCache.a(k * l);
      if (!(Boolean)this.world.generatorConfig.generateOceans.getValue()) {
         Arrays.fill(aint, 1);
         return aint;
      } else {
         for (int i1 = 0; i1 < l; i1++) {
            for (int j1 = 0; j1 < k; j1++) {
               this.a(i + j1, j + i1);
               aint[j1 + i1 * k] = this.a(10) == 0 ? 1 : 0;
            }
         }

         if (i > -k && i <= 0 && j > -l && j <= 0) {
            aint[-i + -j * k] = 1;
         }

         return aint;
      }
   }
}
