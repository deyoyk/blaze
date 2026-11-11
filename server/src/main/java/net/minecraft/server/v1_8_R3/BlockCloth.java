package net.minecraft.server.v1_8_R3;

public class BlockCloth extends Block {
   public static final BlockStateEnum<EnumColor> COLOR = BlockStateEnum.of("color", EnumColor.class);

   public BlockCloth(Material material) {
      super(material);
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
   public IBlockData fromLegacyData(int i) {
      return this.getBlockData().set(COLOR, EnumColor.fromColorIndex(i));
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
