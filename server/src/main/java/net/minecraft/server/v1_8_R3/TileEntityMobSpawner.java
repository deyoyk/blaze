package net.minecraft.server.v1_8_R3;

public class TileEntityMobSpawner extends TileEntity implements IUpdatePlayerListBox {
   public final MobSpawnerAbstract a = new MobSpawnerAbstract() {
      @Override
      public void a(int i) {
         TileEntityMobSpawner.this.world.playBlockAction(TileEntityMobSpawner.this.position, Blocks.MOB_SPAWNER, i, 0);
      }

      @Override
      public World a() {
         return TileEntityMobSpawner.this.world;
      }

      @Override
      public BlockPosition b() {
         return TileEntityMobSpawner.this.position;
      }

      @Override
      public void a(MobSpawnerAbstract.a mobspawnerabstract_a) {
         super.a(mobspawnerabstract_a);
         if (this.a() != null) {
            this.a().notify(TileEntityMobSpawner.this.position);
         }
      }
   };

   @Override
   public void a(NBTTagCompound nbttagcompound) {
      super.a(nbttagcompound);
      this.a.a(nbttagcompound);
   }

   @Override
   public void b(NBTTagCompound nbttagcompound) {
      super.b(nbttagcompound);
      this.a.b(nbttagcompound);
   }

   @Override
   public void c() {
      this.a.c();
   }

   @Override
   public Packet<?> getUpdatePacket() {
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      this.b(nbttagcompound);
      nbttagcompound.remove("SpawnPotentials");
      return new PacketPlayOutTileEntityData(this.position, 1, nbttagcompound);
   }

   @Override
   public boolean c(int i, int j) {
      return this.a.b(i) || super.c(i, j);
   }

   @Override
   public boolean F() {
      return true;
   }

   public MobSpawnerAbstract getSpawner() {
      return this.a;
   }
}
