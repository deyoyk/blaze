package net.minecraft.server.v1_8_R3;

public class BlockStainedGlassPane extends BlockThin {
   public static final BlockStateEnum<EnumColor> COLOR = BlockStateEnum.of("color", EnumColor.class);

   public BlockStainedGlassPane() {
      super(Material.SHATTERABLE, false);
      this.j(this.blockStateList.getBlockData().set(NORTH, false).set(EAST, false).set(SOUTH, false).set(WEST, false).set(COLOR, EnumColor.WHITE));
      this.a(CreativeModeTab.c);
   }

   @Override
   public int getDropData(IBlockData iblockdata) {
      return iblockdata.get(COLOR).getColorIndex();
   }

   @Override
   public MaterialMapColor g(IBlockData iblockdata) {
      return iblockdata.get(COLOR).e();
   }

   @Override
   public IBlockData fromLegacyData(int i) {
      return this.getBlockData().set(COLOR, EnumColor.fromColorIndex(i));
   }

   @Override
   public int toLegacyData(IBlockData iblockdata) {
      return iblockdata.get(COLOR).getColorIndex();
   }

   @Override
   protected BlockStateList getStateList() {
      return new BlockStateList(this, NORTH, EAST, WEST, SOUTH, COLOR);
   }

   @Override
   public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
      BlockBeacon.f(world, blockposition);
   }

   @Override
   public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
      BlockBeacon.f(world, blockposition);
   }
}
