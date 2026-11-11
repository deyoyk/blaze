package net.minecraft.server.v1_8_R3;

import java.util.Random;

public class BlockStainedGlass extends BlockHalfTransparent {
   public static final BlockStateEnum<EnumColor> COLOR = BlockStateEnum.of("color", EnumColor.class);

   public BlockStainedGlass(Material material) {
      super(material, false);
      this.j(this.blockStateList.getBlockData().set(COLOR, EnumColor.WHITE));
      this.a(CreativeModeTab.b);
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
   public int a(Random random) {
      return 0;
   }

   @Override
   protected boolean I() {
      return true;
   }

   @Override
   public boolean d() {
      return false;
   }

   @Override
   public IBlockData fromLegacyData(int i) {
      return this.getBlockData().set(COLOR, EnumColor.fromColorIndex(i));
   }

   @Override
   public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
      BlockBeacon.f(world, blockposition);
   }

   @Override
   public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
      BlockBeacon.f(world, blockposition);
   }

   @Override
   public int toLegacyData(IBlockData iblockdata) {
      return iblockdata.get(COLOR).getColorIndex();
   }

   @Override
   protected BlockStateList getStateList() {
      return new BlockStateList(this, COLOR);
   }
}
