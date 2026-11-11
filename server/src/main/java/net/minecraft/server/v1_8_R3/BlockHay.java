package net.minecraft.server.v1_8_R3;

public class BlockHay extends BlockRotatable {
   public BlockHay() {
      super(Material.GRASS, MaterialMapColor.t);
      this.j(this.blockStateList.getBlockData().set(AXIS, EnumDirection.EnumAxis.Y));
      this.a(CreativeModeTab.b);
   }

   @Override
   public IBlockData fromLegacyData(int i) {
      EnumDirection.EnumAxis enumdirection_enumaxis = EnumDirection.EnumAxis.Y;
      int j = i & 12;
      if (j == 4) {
         enumdirection_enumaxis = EnumDirection.EnumAxis.X;
      } else if (j == 8) {
         enumdirection_enumaxis = EnumDirection.EnumAxis.Z;
      }

      return this.getBlockData().set(AXIS, enumdirection_enumaxis);
   }

   @Override
   public int toLegacyData(IBlockData iblockdata) {
      int i = 0;
      EnumDirection.EnumAxis enumdirection_enumaxis = iblockdata.get(AXIS);
      if (enumdirection_enumaxis == EnumDirection.EnumAxis.X) {
         i |= 4;
      } else if (enumdirection_enumaxis == EnumDirection.EnumAxis.Z) {
         i |= 8;
      }

      return i;
   }

   @Override
   protected BlockStateList getStateList() {
      return new BlockStateList(this, AXIS);
   }

   @Override
   protected ItemStack i(IBlockData iblockdata) {
      return new ItemStack(Item.getItemOf(this), 1, 0);
   }

   @Override
   public IBlockData getPlacedState(
      World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving
   ) {
      return super.getPlacedState(world, blockposition, enumdirection, f, f1, f2, i, entityliving).set(AXIS, enumdirection.k());
   }
}
