package net.minecraft.server.v1_8_R3;

public enum EnumCreatureType {
   MONSTER(IMonster.class, 70, Material.AIR, false, false),
   CREATURE(EntityAnimal.class, 10, Material.AIR, true, true),
   AMBIENT(EntityAmbient.class, 15, Material.AIR, true, false),
   WATER_CREATURE(EntityWaterAnimal.class, 5, Material.WATER, true, false);

   private final Class<? extends IAnimal> e;
   private final int f;
   private final Material g;
   private final boolean h;
   private final boolean i;

   private EnumCreatureType(Class<? extends IAnimal> var3, int var4, Material var5, boolean var6, boolean var7) {
      this.e = var3;
      this.f = var4;
      this.g = var5;
      this.h = var6;
      this.i = var7;
   }

   public boolean matches(Entity entity) {
      return this.a().isAssignableFrom(entity.getClass());
   }

   public Class<? extends IAnimal> a() {
      return this.e;
   }

   public int b() {
      return this.f;
   }

   public boolean d() {
      return this.h;
   }

   public boolean e() {
      return this.i;
   }
}
