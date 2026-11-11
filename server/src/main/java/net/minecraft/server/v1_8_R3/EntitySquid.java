package net.minecraft.server.v1_8_R3;

import java.util.Random;

public class EntitySquid extends EntityWaterAnimal {
   private final Random random = new Random();
   public float a;
   public float b;
   public float c;
   public float bk;
   public float bl;
   public float bm;
   public float bn;
   public float bo;
   private float bp;
   private float bq;
   private float br;
   private float bs;
   private float bt;
   private float bu;

   public EntitySquid(World world) {
      super(world);
      this.setSize(0.95F, 0.95F);
      this.bq = 1.0F / (this.random.nextFloat() + 1.0F) * 0.2F;
      this.goalSelector.a(0, new EntitySquid.PathfinderGoalSquid(this));
   }

   @Override
   protected void initAttributes() {
      super.initAttributes();
      this.getAttributeInstance(GenericAttributes.maxHealth).setValue(10.0);
   }

   @Override
   public float getHeadHeight() {
      return this.length * 0.5F;
   }

   @Override
   protected String z() {
      return null;
   }

   @Override
   protected String bo() {
      return null;
   }

   @Override
   protected String bp() {
      return null;
   }

   @Override
   protected float bB() {
      return 0.4F;
   }

   @Override
   protected Item getLoot() {
      return null;
   }

   @Override
   protected boolean s_() {
      return false;
   }

   @Override
   protected void dropDeathLoot(boolean flag, int i) {
      int j = this.random.nextInt(3 + i) + 1;

      for (int k = 0; k < j; k++) {
         this.a(new ItemStack(Items.DYE, 1, EnumColor.BLACK.getInvColorIndex()), 0.0F);
      }
   }

   @Override
   public void m() {
      super.m();
      this.b = this.a;
      this.bk = this.c;
      this.bm = this.bl;
      this.bo = this.bn;
      this.bl = this.bl + this.bq;
      if (this.bl > Math.PI * 2) {
         this.bl = (float)(this.bl - (Math.PI * 2));
         if (this.random.nextInt(10) == 0) {
            this.bq = 1.0F / (this.random.nextFloat() + 1.0F) * 0.2F;
         }

         this.world.broadcastEntityEffect(this, (byte)19);
      }

      if (this.inWater) {
         if (this.bl < (float) Math.PI) {
            float f = this.bl / (float) Math.PI;
            this.bn = MathHelper.sin(f * f * (float) Math.PI) * (float) Math.PI * 0.25F;
            if (f > 0.75) {
               this.bp = 1.0F;
               this.br = 1.0F;
            } else {
               this.br *= 0.8F;
            }
         } else {
            this.bn = 0.0F;
            this.bp *= 0.9F;
            this.br *= 0.99F;
         }

         this.motX = this.bs * this.bp;
         this.motY = this.bt * this.bp;
         this.motZ = this.bu * this.bp;
         float f = MathHelper.sqrt(this.motX * this.motX + this.motZ * this.motZ);
         this.aI = this.aI + (-((float)MathHelper.b(this.motX, this.motZ)) * 180.0F / (float) Math.PI - this.aI) * 0.1F;
         this.yaw = this.aI;
         this.c = (float)(this.c + Math.PI * this.br * 1.5);
         this.a = this.a + (-((float)MathHelper.b(f, this.motY)) * 180.0F / (float) Math.PI - this.a) * 0.1F;
      } else {
         this.bn = MathHelper.e(MathHelper.sin(this.bl)) * (float) Math.PI * 0.25F;
         this.motX = 0.0;
         this.motY -= 0.08;
         this.motY *= 0.98F;
         this.motZ = 0.0;
         this.a = (float)(this.a + (-90.0F - this.a) * 0.02);
      }
   }

   @Override
   public void g(float f, float f1) {
      this.move(this.motX, this.motY, this.motZ);
   }

   @Override
   public boolean bR() {
      return this.locY > this.world.paperSpigotConfig.squidMinSpawnHeight && this.locY < this.world.paperSpigotConfig.squidMaxSpawnHeight && super.bR();
   }

   public void b(float f, float f1, float f2) {
      this.bs = f;
      this.bt = f1;
      this.bu = f2;
   }

   public boolean n() {
      return this.bs != 0.0F || this.bt != 0.0F || this.bu != 0.0F;
   }

   static class PathfinderGoalSquid extends PathfinderGoal {
      private final EntitySquid a;

      public PathfinderGoalSquid(EntitySquid entitysquid) {
         this.a = entitysquid;
      }

      @Override
      public boolean a() {
         return true;
      }

      @Override
      public void e() {
         int i = this.a.bh();
         if (i > 100) {
            this.a.b(0.0F, 0.0F, 0.0F);
         } else if (this.a.bc().nextInt(50) == 0 || !this.a.inWater || !this.a.n()) {
            float f = this.a.bc().nextFloat() * (float) Math.PI * 2.0F;
            float f1 = MathHelper.cos(f) * 0.2F;
            float f2 = -0.1F + this.a.bc().nextFloat() * 0.2F;
            float f3 = MathHelper.sin(f) * 0.2F;
            this.a.b(f1, f2, f3);
         }
      }
   }
}
