package net.minecraft.server.v1_8_R3;

public abstract class EntityGolem extends EntityCreature implements IAnimal {
   public EntityGolem(World var1) {
      super(var1);
   }

   @Override
   protected String z() {
      return "none";
   }

   @Override
   protected String bo() {
      return "none";
   }

   @Override
   protected String bp() {
      return "none";
   }

   @Override
   public int w() {
      return 120;
   }

   @Override
   public boolean isTypeNotPersistent() {
      return false;
   }
}
