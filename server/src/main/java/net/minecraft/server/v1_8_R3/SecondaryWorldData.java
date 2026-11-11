package net.minecraft.server.v1_8_R3;

public class SecondaryWorldData extends WorldData {
   private final WorldData b;

   public SecondaryWorldData(WorldData var1) {
      this.b = var1;
   }

   @Override
   public NBTTagCompound a() {
      return this.b.a();
   }

   @Override
   public NBTTagCompound a(NBTTagCompound var1) {
      return this.b.a(var1);
   }

   @Override
   public long getSeed() {
      return this.b.getSeed();
   }

   @Override
   public int c() {
      return this.b.c();
   }

   @Override
   public int d() {
      return this.b.d();
   }

   @Override
   public int e() {
      return this.b.e();
   }

   @Override
   public long getTime() {
      return this.b.getTime();
   }

   @Override
   public long getDayTime() {
      return this.b.getDayTime();
   }

   @Override
   public NBTTagCompound i() {
      return this.b.i();
   }

   @Override
   public String getName() {
      return this.b.getName();
   }

   @Override
   public int l() {
      return this.b.l();
   }

   @Override
   public boolean isThundering() {
      return this.b.isThundering();
   }

   @Override
   public int getThunderDuration() {
      return this.b.getThunderDuration();
   }

   @Override
   public boolean hasStorm() {
      return this.b.hasStorm();
   }

   @Override
   public int getWeatherDuration() {
      return this.b.getWeatherDuration();
   }

   @Override
   public WorldSettings.EnumGamemode getGameType() {
      return this.b.getGameType();
   }

   @Override
   public void setTime(long var1) {
   }

   @Override
   public void setDayTime(long var1) {
   }

   @Override
   public void setSpawn(BlockPosition var1) {
   }

   @Override
   public void a(String var1) {
   }

   @Override
   public void e(int var1) {
   }

   @Override
   public void setThundering(boolean var1) {
   }

   @Override
   public void setThunderDuration(int var1) {
   }

   @Override
   public void setStorm(boolean var1) {
   }

   @Override
   public void setWeatherDuration(int var1) {
   }

   @Override
   public boolean shouldGenerateMapFeatures() {
      return this.b.shouldGenerateMapFeatures();
   }

   @Override
   public boolean isHardcore() {
      return this.b.isHardcore();
   }

   @Override
   public WorldType getType() {
      return this.b.getType();
   }

   @Override
   public void a(WorldType var1) {
   }

   @Override
   public boolean v() {
      return this.b.v();
   }

   @Override
   public void c(boolean var1) {
   }

   @Override
   public boolean w() {
      return this.b.w();
   }

   @Override
   public void d(boolean var1) {
   }

   @Override
   public GameRules x() {
      return this.b.x();
   }

   @Override
   public EnumDifficulty getDifficulty() {
      return this.b.getDifficulty();
   }

   @Override
   public void setDifficulty(EnumDifficulty var1) {
   }

   @Override
   public boolean isDifficultyLocked() {
      return this.b.isDifficultyLocked();
   }

   @Override
   public void e(boolean var1) {
   }
}
