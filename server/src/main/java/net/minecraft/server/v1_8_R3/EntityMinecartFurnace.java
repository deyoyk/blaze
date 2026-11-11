package net.minecraft.server.v1_8_R3;

public class EntityMinecartFurnace extends EntityMinecartAbstract {
   private int c;
   public double a;
   public double b;

   public EntityMinecartFurnace(World world) {
      super(world);
   }

   public EntityMinecartFurnace(World world, double d0, double d1, double d2) {
      super(world, d0, d1, d2);
   }

   @Override
   public EntityMinecartAbstract.EnumMinecartType s() {
      return EntityMinecartAbstract.EnumMinecartType.FURNACE;
   }

   @Override
   protected void h() {
      super.h();
      this.datawatcher.a(16, (byte)0);
   }

   @Override
   public void t_() {
      super.t_();
      if (this.c > 0) {
         this.c--;
      }

      if (this.c <= 0) {
         this.a = this.b = 0.0;
      }

      this.i(this.c > 0);
      if (this.j() && this.random.nextInt(4) == 0) {
         this.world.addParticle(EnumParticle.SMOKE_LARGE, this.locX, this.locY + 0.8, this.locZ, 0.0, 0.0, 0.0, EnumParticle.EMPTY_ARRAY);
      }
   }

   @Override
   protected double m() {
      return 0.2;
   }

   @Override
   public void a(DamageSource damagesource) {
      super.a(damagesource);
      if (!damagesource.isExplosion() && this.world.getGameRules().getBoolean("doEntityDrops")) {
         this.a(new ItemStack(Blocks.FURNACE, 1), 0.0F);
      }
   }

   @Override
   protected void a(int x, int y, int z, IBlockData iblockdata) {
      super.a(x, y, z, iblockdata);
      double d0 = this.a * this.a + this.b * this.b;
      if (d0 > 1.0E-4 && this.motX * this.motX + this.motZ * this.motZ > 0.001) {
         d0 = MathHelper.sqrt(d0);
         double d1 = MathHelper.sqrt(this.motX * this.motX + this.motZ * this.motZ);
         this.a = this.motX / d1 * d0;
         this.b = this.motZ / d1 * d0;
      }
   }

   @Override
   protected void o() {
      double d0 = this.a * this.a + this.b * this.b;
      if (d0 > 1.0E-4) {
         d0 = MathHelper.sqrt(d0);
         this.a /= d0;
         this.b /= d0;
         double d1 = 1.0;
         this.motX *= 0.8F;
         this.motY *= 0.0;
         this.motZ *= 0.8F;
         this.motX = this.motX + this.a * d1;
         this.motZ = this.motZ + this.b * d1;
      } else {
         this.motX *= 0.98F;
         this.motY *= 0.0;
         this.motZ *= 0.98F;
      }

      super.o();
   }

   @Override
   public boolean e(EntityHuman entityhuman) {
      ItemStack itemstack = entityhuman.inventory.getItemInHand();
      if (itemstack != null && itemstack.getItem() == Items.COAL) {
         if (!entityhuman.abilities.canInstantlyBuild && --itemstack.count == 0) {
            entityhuman.inventory.setItem(entityhuman.inventory.itemInHandIndex, null);
         }

         this.c += 3600;
      }

      this.a = this.locX - entityhuman.locX;
      this.b = this.locZ - entityhuman.locZ;
      return true;
   }

   @Override
   protected void b(NBTTagCompound nbttagcompound) {
      super.b(nbttagcompound);
      nbttagcompound.setDouble("PushX", this.a);
      nbttagcompound.setDouble("PushZ", this.b);
      nbttagcompound.setShort("Fuel", (short)this.c);
   }

   @Override
   protected void a(NBTTagCompound nbttagcompound) {
      super.a(nbttagcompound);
      this.a = nbttagcompound.getDouble("PushX");
      this.b = nbttagcompound.getDouble("PushZ");
      this.c = nbttagcompound.getShort("Fuel");
   }

   protected boolean j() {
      return (this.datawatcher.getByte(16) & 1) != 0;
   }

   protected void i(boolean flag) {
      if (flag) {
         this.datawatcher.watch(16, (byte)(this.datawatcher.getByte(16) | 1));
      } else {
         this.datawatcher.watch(16, (byte)(this.datawatcher.getByte(16) & -2));
      }
   }

   @Override
   public IBlockData u() {
      return (this.j() ? Blocks.LIT_FURNACE : Blocks.FURNACE).getBlockData().set(BlockFurnace.FACING, EnumDirection.NORTH);
   }
}
