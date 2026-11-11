package net.minecraft.server.v1_8_R3;

public class ItemFireworks extends Item {
   @Override
   public boolean interactWith(
      ItemStack itemstack, EntityHuman entityhuman, World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2
   ) {
      EntityFireworks entityfireworks = new EntityFireworks(world, blockposition.getX() + f, blockposition.getY() + f1, blockposition.getZ() + f2, itemstack);
      world.addEntity(entityfireworks);
      if (!entityhuman.abilities.canInstantlyBuild) {
         itemstack.count--;
      }

      return true;
   }
}
