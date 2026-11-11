package net.minecraft.server.v1_8_R3;

import java.util.Calendar;

public class EntityBat extends EntityAmbient {
   private BlockPosition a;

   public EntityBat(World world) {
      super(world);
      this.setSize(0.5F, 0.9F);
      this.setAsleep(true);
   }

   @Override
   protected void h() {
      super.h();
      this.datawatcher.a(16, (byte)0);
   }

   @Override
   protected float bB() {
      return 0.1F;
   }

   @Override
   protected float bC() {
      return super.bC() * 0.95F;
   }

   @Override
   protected String z() {
      return this.isAsleep() && this.random.nextInt(4) != 0 ? null : "mob.bat.idle";
   }

   @Override
   protected String bo() {
      return "mob.bat.hurt";
   }

   @Override
   protected String bp() {
      return "mob.bat.death";
   }

   @Override
   public boolean ae() {
      return false;
   }

   @Override
   protected void s(Entity entity) {
   }

   @Override
   protected void bL() {
   }

   @Override
   protected void initAttributes() {
      super.initAttributes();
      this.getAttributeInstance(GenericAttributes.maxHealth).setValue(6.0);
   }

   public boolean isAsleep() {
      return (this.datawatcher.getByte(16) & 1) != 0;
   }

   public void setAsleep(boolean flag) {
      byte b0 = this.datawatcher.getByte(16);
      if (flag) {
         this.datawatcher.watch(16, (byte)(b0 | 1));
      } else {
         this.datawatcher.watch(16, (byte)(b0 & -2));
      }
   }

   @Override
   public void t_() {
      super.t_();
      if (this.isAsleep()) {
         this.motX = this.motY = this.motZ = 0.0;
         this.locY = MathHelper.floor(this.locY) + 1.0 - this.length;
      } else {
         this.motY *= 0.6F;
      }
   }

   @Override
   protected void E() {
      super.E();
      BlockPosition blockposition = new BlockPosition(this);
      if (this.isAsleep()) {
         if (!this.world.getType(MathHelper.floor(this.locX), MathHelper.floor(this.locY) + 1, MathHelper.floor(this.locZ)).getBlock().isOccluding()) {
            this.setAsleep(false);
            this.world.a(null, 1015, blockposition, 0);
         } else {
            if (this.random.nextInt(200) == 0) {
               this.aK = this.random.nextInt(360);
            }

            if (this.world.findNearbyPlayer(this, 4.0) != null) {
               this.setAsleep(false);
               this.world.a(null, 1015, blockposition, 0);
            }
         }
      } else {
         if (this.a != null && (!this.world.isEmpty(this.a) || this.a.getY() < 1)) {
            this.a = null;
         }

         if (this.a == null || this.random.nextInt(30) == 0 || this.a.c(this.locX, this.locY, this.locZ) < 4.0) {
            this.a = new BlockPosition(
               (int)this.locX + this.random.nextInt(7) - this.random.nextInt(7),
               (int)this.locY + this.random.nextInt(6) - 2,
               (int)this.locZ + this.random.nextInt(7) - this.random.nextInt(7)
            );
         }

         double d0 = this.a.getX() + 0.5 - this.locX;
         double d1 = this.a.getY() + 0.1 - this.locY;
         double d2 = this.a.getZ() + 0.5 - this.locZ;
         this.motX = this.motX + (Math.signum(d0) * 0.5 - this.motX) * 0.1F;
         this.motY = this.motY + (Math.signum(d1) * 0.7F - this.motY) * 0.1F;
         this.motZ = this.motZ + (Math.signum(d2) * 0.5 - this.motZ) * 0.1F;
         float f = (float)(MathHelper.b(this.motZ, this.motX) * 180.0 / (float) Math.PI) - 90.0F;
         float f1 = MathHelper.g(f - this.yaw);
         this.ba = 0.5F;
         this.yaw += f1;
         if (this.random.nextInt(100) == 0
            && this.world.getType(MathHelper.floor(this.locX), MathHelper.floor(this.locY) + 1, MathHelper.floor(this.locZ)).getBlock().isOccluding()) {
            this.setAsleep(true);
         }
      }
   }

   @Override
   protected boolean s_() {
      return false;
   }

   @Override
   public void e(float f, float f1) {
   }

   @Override
   protected void a(double d0, boolean flag, Block block, BlockPosition blockposition) {
   }

   @Override
   public boolean aI() {
      return true;
   }

   @Override
   public boolean damageEntity(DamageSource damagesource, float f) {
      if (this.isInvulnerable(damagesource)) {
         return false;
      } else {
         if (this.isAsleep()) {
            this.setAsleep(false);
         }

         return super.damageEntity(damagesource, f);
      }
   }

   @Override
   public void a(NBTTagCompound nbttagcompound) {
      super.a(nbttagcompound);
      this.datawatcher.watch(16, nbttagcompound.getByte("BatFlags"));
   }

   @Override
   public void b(NBTTagCompound nbttagcompound) {
      super.b(nbttagcompound);
      nbttagcompound.setByte("BatFlags", this.datawatcher.getByte(16));
   }

   @Override
   public boolean bR() {
      BlockPosition blockposition = new BlockPosition(this.locX, this.getBoundingBox().b, this.locZ);
      if (blockposition.getY() >= this.world.F()) {
         return false;
      } else {
         int i = this.world.getLightLevel(blockposition);
         byte b0 = 4;
         if (this.a(this.world.Y())) {
            b0 = 7;
         } else if (this.random.nextBoolean()) {
            return false;
         }

         return i <= this.random.nextInt(b0) && super.bR();
      }
   }

   private boolean a(Calendar calendar) {
      return calendar.get(2) + 1 == 10 && calendar.get(5) >= 20 || calendar.get(2) + 1 == 11 && calendar.get(5) <= 3;
   }

   @Override
   public float getHeadHeight() {
      return this.length / 2.0F;
   }
}
