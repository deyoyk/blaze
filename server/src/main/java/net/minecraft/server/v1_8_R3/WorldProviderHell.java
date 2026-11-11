package net.minecraft.server.v1_8_R3;

public class WorldProviderHell extends WorldProvider {
   @Override
   public void b() {
      this.c = new WorldChunkManagerHell(BiomeBase.HELL, 0.0F);
      this.d = true;
      this.e = true;
      this.dimension = -1;
   }

   @Override
   protected void a() {
      float var1 = 0.1F;

      for (int var2 = 0; var2 <= 15; var2++) {
         float var3 = 1.0F - var2 / 15.0F;
         this.f[var2] = (1.0F - var3) / (var3 * 3.0F + 1.0F) * (1.0F - var1) + var1;
      }
   }

   @Override
   public IChunkProvider getChunkProvider() {
      return new ChunkProviderHell(this.b, this.b.getWorldData().shouldGenerateMapFeatures(), this.b.getSeed());
   }

   @Override
   public boolean d() {
      return false;
   }

   @Override
   public boolean canSpawn(int var1, int var2) {
      return false;
   }

   @Override
   public float a(long var1, float var3) {
      return 0.5F;
   }

   @Override
   public boolean e() {
      return false;
   }

   @Override
   public String getName() {
      return "Nether";
   }

   @Override
   public String getSuffix() {
      return "_nether";
   }

   @Override
   public WorldBorder getWorldBorder() {
      return new WorldBorder() {
         @Override
         public double getCenterX() {
            return super.getCenterX() / 8.0;
         }

         @Override
         public double getCenterZ() {
            return super.getCenterZ() / 8.0;
         }
      };
   }
}
