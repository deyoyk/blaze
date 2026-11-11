package net.minecraft.server.v1_8_R3;

public class BlockPotatoes extends BlockCrops {
   @Override
   protected Item l() {
      return Items.POTATO;
   }

   @Override
   protected Item n() {
      return Items.POTATO;
   }

   @Override
   public void dropNaturally(World world, BlockPosition blockposition, IBlockData iblockdata, float f, int i) {
      super.dropNaturally(world, blockposition, iblockdata, f, i);
      if (iblockdata.get(AGE) >= 7 && world.random.nextInt(50) == 0) {
         this.a(world, blockposition, new ItemStack(Items.POISONOUS_POTATO));
      }
   }
}
