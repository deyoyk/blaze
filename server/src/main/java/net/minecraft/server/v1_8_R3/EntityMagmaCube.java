package net.minecraft.server.v1_8_R3;

public class EntityMagmaCube extends EntitySlime {
   public EntityMagmaCube(World world) {
      super(world);
      this.fireProof = true;
   }

   @Override
   protected void initAttributes() {
      super.initAttributes();
      this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.2F);
   }

   @Override
   public boolean bR() {
      return this.world.getDifficulty() != EnumDifficulty.PEACEFUL;
   }

   @Override
   public boolean canSpawn() {
      return this.world.a(this.getBoundingBox(), this)
         && this.world.getCubes(this, this.getBoundingBox()).isEmpty()
         && !this.world.containsLiquid(this.getBoundingBox());
   }

   @Override
   public int br() {
      return this.getSize() * 3;
   }

   @Override
   public float c(float f) {
      return 1.0F;
   }

   @Override
   protected EnumParticle n() {
      return EnumParticle.FLAME;
   }

   @Override
   protected EntitySlime cf() {
      return new EntityMagmaCube(this.world);
   }

   @Override
   protected Item getLoot() {
      return Items.MAGMA_CREAM;
   }

   @Override
   protected void dropDeathLoot(boolean flag, int i) {
      Item item = this.getLoot();
      if (item != null && this.getSize() > 1) {
         int j = this.random.nextInt(4) - 2;
         if (i > 0) {
            j += this.random.nextInt(i + 1);
         }

         for (int k = 0; k < j; k++) {
            this.a(item, 1);
         }
      }
   }

   @Override
   public boolean isBurning() {
      return false;
   }

   @Override
   protected int cg() {
      return super.cg() * 4;
   }

   @Override
   protected void ch() {
      this.a *= 0.9F;
   }

   @Override
   protected void bF() {
      this.motY = 0.42F + this.getSize() * 0.1F;
      this.ai = true;
   }

   @Override
   protected void bH() {
      this.motY = 0.22F + this.getSize() * 0.05F;
      this.ai = true;
   }

   @Override
   public void e(float f, float f1) {
   }

   @Override
   protected boolean ci() {
      return true;
   }

   @Override
   protected int cj() {
      return super.cj() + 2;
   }

   @Override
   protected String ck() {
      return this.getSize() > 1 ? "mob.magmacube.big" : "mob.magmacube.small";
   }

   @Override
   protected boolean cl() {
      return true;
   }
}
