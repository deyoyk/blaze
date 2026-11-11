package net.minecraft.server.v1_8_R3;

public class BlockMinecartTrack extends BlockMinecartTrackAbstract {
   public static final BlockStateEnum<BlockMinecartTrackAbstract.EnumTrackPosition> SHAPE = BlockStateEnum.of(
      "shape", BlockMinecartTrackAbstract.EnumTrackPosition.class
   );

   protected BlockMinecartTrack() {
      super(false);
      this.j(this.blockStateList.getBlockData().set(SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_SOUTH));
   }

   @Override
   protected void b(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
      if (block.isPowerSource() && new BlockMinecartTrackAbstract.MinecartTrackLogic(world, blockposition, iblockdata).a() == 3) {
         this.a(world, blockposition, iblockdata, false);
      }
   }

   @Override
   public IBlockState<BlockMinecartTrackAbstract.EnumTrackPosition> n() {
      return SHAPE;
   }

   @Override
   public IBlockData fromLegacyData(int i) {
      return this.getBlockData().set(SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.a(i));
   }

   @Override
   public int toLegacyData(IBlockData iblockdata) {
      return iblockdata.get(SHAPE).a();
   }

   @Override
   protected BlockStateList getStateList() {
      return new BlockStateList(this, SHAPE);
   }
}
