package net.minecraft.server.v1_8_R3;

public class BlockFloorSign extends BlockSign {
   public static final BlockStateInteger ROTATION = BlockStateInteger.of("rotation", 0, 15);

   public BlockFloorSign() {
      this.j(this.blockStateList.getBlockData().set(ROTATION, 0));
   }

   @Override
   public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
      if (!world.getType(blockposition.down()).getBlock().getMaterial().isBuildable()) {
         this.b(world, blockposition, iblockdata, 0);
         world.setAir(blockposition);
      }

      super.doPhysics(world, blockposition, iblockdata, block);
   }

   @Override
   public IBlockData fromLegacyData(int i) {
      return this.getBlockData().set(ROTATION, i);
   }

   @Override
   public int toLegacyData(IBlockData iblockdata) {
      return iblockdata.get(ROTATION);
   }

   @Override
   protected BlockStateList getStateList() {
      return new BlockStateList(this, ROTATION);
   }
}
