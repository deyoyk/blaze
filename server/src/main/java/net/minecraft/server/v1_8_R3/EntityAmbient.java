package net.minecraft.server.v1_8_R3;

public abstract class EntityAmbient extends EntityInsentient implements IAnimal {
   public EntityAmbient(World var1) {
      super(var1);
   }

   @Override
   public boolean cb() {
      return false;
   }

   @Override
   protected boolean a(EntityHuman var1) {
      return false;
   }
}
