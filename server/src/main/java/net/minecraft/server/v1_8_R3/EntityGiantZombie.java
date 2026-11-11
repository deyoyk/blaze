package net.minecraft.server.v1_8_R3;

public class EntityGiantZombie extends EntityMonster {
   public EntityGiantZombie(World var1) {
      super(var1);
      this.setSize(this.width * 6.0F, this.length * 6.0F);
   }

   @Override
   public float getHeadHeight() {
      return 10.440001F;
   }

   @Override
   protected void initAttributes() {
      super.initAttributes();
      this.getAttributeInstance(GenericAttributes.maxHealth).setValue(100.0);
      this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.5);
      this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(50.0);
   }

   @Override
   public float a(BlockPosition var1) {
      return this.world.o(var1) - 0.5F;
   }
}
