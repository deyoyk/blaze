package net.minecraft.server.v1_8_R3;

import java.util.List;
import java.util.Random;

public class BlockEnderPortalFrame extends Block {
   public static final BlockStateDirection FACING = BlockStateDirection.of("facing", EnumDirection.EnumDirectionLimit.HORIZONTAL);
   public static final BlockStateBoolean EYE = BlockStateBoolean.of("eye");

   public BlockEnderPortalFrame() {
      super(Material.STONE, MaterialMapColor.C);
      this.j(this.blockStateList.getBlockData().set(FACING, EnumDirection.NORTH).set(EYE, Boolean.FALSE));
   }

   @Override
   public boolean c() {
      return false;
   }

   @Override
   public void j() {
      this.a(0.0F, 0.0F, 0.0F, 1.0F, 0.8125F, 1.0F);
   }

   @Override
   public void a(World world, BlockPosition blockposition, IBlockData iblockdata, AxisAlignedBB axisalignedbb, List<AxisAlignedBB> list, Entity entity) {
      this.a(0.0F, 0.0F, 0.0F, 1.0F, 0.8125F, 1.0F);
      super.a(world, blockposition, iblockdata, axisalignedbb, list, entity);
      if (world.getType(blockposition).get(EYE)) {
         this.a(0.3125F, 0.8125F, 0.3125F, 0.6875F, 1.0F, 0.6875F);
         super.a(world, blockposition, iblockdata, axisalignedbb, list, entity);
      }

      this.j();
   }

   @Override
   public Item getDropType(IBlockData iblockdata, Random random, int i) {
      return null;
   }

   @Override
   public IBlockData getPlacedState(
      World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving
   ) {
      return this.getBlockData().set(FACING, entityliving.getDirection().opposite()).set(EYE, Boolean.FALSE);
   }

   @Override
   public boolean isComplexRedstone() {
      return true;
   }

   @Override
   public int l(World world, BlockPosition blockposition) {
      return world.getType(blockposition).get(EYE) ? 15 : 0;
   }

   @Override
   public IBlockData fromLegacyData(int i) {
      return this.getBlockData().set(EYE, (i & 4) != 0).set(FACING, EnumDirection.fromType2(i & 3));
   }

   @Override
   public int toLegacyData(IBlockData iblockdata) {
      byte b0 = 0;
      int i = b0 | iblockdata.get(FACING).b();
      if (iblockdata.get(EYE)) {
         i |= 4;
      }

      return i;
   }

   @Override
   protected BlockStateList getStateList() {
      return new BlockStateList(this, FACING, EYE);
   }
}
