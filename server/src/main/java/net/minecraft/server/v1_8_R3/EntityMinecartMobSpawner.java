package net.minecraft.server.v1_8_R3;

public class EntityMinecartMobSpawner extends EntityMinecartAbstract {
   private final MobSpawnerAbstract a = new MobSpawnerAbstract() {
      @Override
      public void a(int var1) {
         EntityMinecartMobSpawner.this.world.broadcastEntityEffect(EntityMinecartMobSpawner.this, (byte)var1);
      }

      @Override
      public World a() {
         return EntityMinecartMobSpawner.this.world;
      }

      @Override
      public BlockPosition b() {
         return new BlockPosition(EntityMinecartMobSpawner.this);
      }
   };

   public EntityMinecartMobSpawner(World var1) {
      super(var1);
   }

   public EntityMinecartMobSpawner(World var1, double var2, double var4, double var6) {
      super(var1, var2, var4, var6);
   }

   @Override
   public EntityMinecartAbstract.EnumMinecartType s() {
      return EntityMinecartAbstract.EnumMinecartType.SPAWNER;
   }

   @Override
   public IBlockData u() {
      return Blocks.MOB_SPAWNER.getBlockData();
   }

   @Override
   protected void a(NBTTagCompound var1) {
      super.a(var1);
      this.a.a(var1);
   }

   @Override
   protected void b(NBTTagCompound var1) {
      super.b(var1);
      this.a.b(var1);
   }

   @Override
   public void t_() {
      super.t_();
      this.a.c();
   }
}
