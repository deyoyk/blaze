package net.minecraft.server.v1_8_R3;

public class GenLayerRiverMix extends GenLayer {
   private final GenLayer c;
   private final GenLayer d;

   public GenLayerRiverMix(long i, GenLayer genlayer, GenLayer genlayer1) {
      super(i);
      this.c = genlayer;
      this.d = genlayer1;
   }

   @Override
   public void a(long i) {
      this.c.a(i);
      this.d.a(i);
      super.a(i);
   }

   @Override
   public int[] a(int i, int j, int k, int l) {
      int[] aint = this.c.a(i, j, k, l);
      int[] aint1 = this.d.a(i, j, k, l);
      int[] aint2 = IntCache.a(k * l);

      for (int i1 = 0; i1 < k * l; i1++) {
         int fetch = aint[i1];
         int fetch1 = aint1[i1];
         if (fetch == BiomeBase.OCEAN.id || fetch == BiomeBase.DEEP_OCEAN.id) {
            aint2[i1] = fetch;
         } else if (fetch1 == BiomeBase.RIVER.id) {
            if (fetch == BiomeBase.ICE_PLAINS.id) {
               aint2[i1] = BiomeBase.FROZEN_RIVER.id;
            } else if (fetch != BiomeBase.MUSHROOM_ISLAND.id && fetch != BiomeBase.MUSHROOM_SHORE.id) {
               aint2[i1] = fetch1 & 0xFF;
            } else {
               aint2[i1] = BiomeBase.MUSHROOM_SHORE.id;
            }
         } else {
            aint2[i1] = fetch;
         }
      }

      return aint2;
   }
}
