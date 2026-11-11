package net.minecraft.server.v1_8_R3;

public abstract class EntityWaterAnimal extends EntityInsentient implements IAnimal {
   public EntityWaterAnimal(World var1) {
      super(var1);
   }

   @Override
   public boolean aY() {
      return true;
   }

   @Override
   public boolean bR() {
      return true;
   }

   @Override
   public boolean canSpawn() {
      return this.world.a(this.getBoundingBox(), this);
   }

   @Override
   public int w() {
      return 120;
   }

   @Override
   public boolean isTypeNotPersistent() {
      return true;
   }

   @Override
   protected int getExpValue(EntityHuman var1) {
      return 1 + this.world.random.nextInt(3);
   }

   @Override
   public void K() {
      int var1 = this.getAirTicks();
      super.K();
      if (this.isAlive() && !this.V()) {
         this.setAirTicks(--var1);
         if (this.getAirTicks() == -20) {
            this.setAirTicks(0);
            this.damageEntity(DamageSource.DROWN, 2.0F);
         }
      } else {
         this.setAirTicks(300);
      }
   }

   @Override
   public boolean aL() {
      return false;
   }
}
