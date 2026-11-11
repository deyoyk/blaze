package net.minecraft.server.v1_8_R3;

public class WorldProviderTheEnd extends WorldProvider {
   @Override
   public void b() {
      this.c = new WorldChunkManagerHell(BiomeBase.SKY, 0.0F);
      this.dimension = 1;
      this.e = true;
   }

   @Override
   public IChunkProvider getChunkProvider() {
      return new ChunkProviderTheEnd(this.b, this.b.getSeed());
   }

   @Override
   public float a(long var1, float var3) {
      return 0.0F;
   }

   @Override
   public boolean e() {
      return false;
   }

   @Override
   public boolean d() {
      return false;
   }

   @Override
   public boolean canSpawn(int var1, int var2) {
      return this.b.c(new BlockPosition(var1, 0, var2)).getMaterial().isSolid();
   }

   @Override
   public BlockPosition h() {
      return new BlockPosition(100, 50, 0);
   }

   @Override
   public int getSeaLevel() {
      return 50;
   }

   @Override
   public String getName() {
      return "The End";
   }

   @Override
   public String getSuffix() {
      return "_end";
   }
}
