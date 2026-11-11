package net.minecraft.server.v1_8_R3;

import java.util.List;

public class BlockFence extends Block {
   public static final BlockStateBoolean NORTH = BlockStateBoolean.of("north");
   public static final BlockStateBoolean EAST = BlockStateBoolean.of("east");
   public static final BlockStateBoolean SOUTH = BlockStateBoolean.of("south");
   public static final BlockStateBoolean WEST = BlockStateBoolean.of("west");

   public BlockFence(Material material) {
      this(material, material.r());
   }

   public BlockFence(Material material, MaterialMapColor materialmapcolor) {
      super(material, materialmapcolor);
      this.j(this.blockStateList.getBlockData().set(NORTH, Boolean.FALSE).set(EAST, Boolean.FALSE).set(SOUTH, Boolean.FALSE).set(WEST, Boolean.FALSE));
      this.a(CreativeModeTab.c);
   }

   @Override
   public void a(World world, BlockPosition blockposition, IBlockData iblockdata, AxisAlignedBB axisalignedbb, List<AxisAlignedBB> list, Entity entity) {
      boolean flag = this.e(world, blockposition.north());
      boolean flag1 = this.e(world, blockposition.south());
      boolean flag2 = this.e(world, blockposition.west());
      boolean flag3 = this.e(world, blockposition.east());
      float f = 0.375F;
      float f1 = 0.625F;
      float f2 = 0.375F;
      float f3 = 0.625F;
      if (flag) {
         f2 = 0.0F;
      }

      if (flag1) {
         f3 = 1.0F;
      }

      if (flag || flag1) {
         this.a(f, 0.0F, f2, f1, 1.5F, f3);
         super.a(world, blockposition, iblockdata, axisalignedbb, list, entity);
      }

      f2 = 0.375F;
      f3 = 0.625F;
      if (flag2) {
         f = 0.0F;
      }

      if (flag3) {
         f1 = 1.0F;
      }

      if (flag2 || flag3 || !flag && !flag1) {
         this.a(f, 0.0F, f2, f1, 1.5F, f3);
         super.a(world, blockposition, iblockdata, axisalignedbb, list, entity);
      }

      if (flag) {
         f2 = 0.0F;
      }

      if (flag1) {
         f3 = 1.0F;
      }

      this.a(f, 0.0F, f2, f1, 1.0F, f3);
   }

   @Override
   public void updateShape(IBlockAccess iblockaccess, BlockPosition blockposition) {
      boolean flag = this.e(iblockaccess, blockposition.north());
      boolean flag1 = this.e(iblockaccess, blockposition.south());
      boolean flag2 = this.e(iblockaccess, blockposition.west());
      boolean flag3 = this.e(iblockaccess, blockposition.east());
      float f = 0.375F;
      float f1 = 0.625F;
      float f2 = 0.375F;
      float f3 = 0.625F;
      if (flag) {
         f2 = 0.0F;
      }

      if (flag1) {
         f3 = 1.0F;
      }

      if (flag2) {
         f = 0.0F;
      }

      if (flag3) {
         f1 = 1.0F;
      }

      this.a(f, 0.0F, f2, f1, 1.0F, f3);
   }

   @Override
   public boolean c() {
      return false;
   }

   @Override
   public boolean d() {
      return false;
   }

   @Override
   public boolean b(IBlockAccess iblockaccess, BlockPosition blockposition) {
      return false;
   }

   public boolean e(IBlockAccess iblockaccess, BlockPosition blockposition) {
      Block block = iblockaccess.getType(blockposition).getBlock();
      return block != Blocks.BARRIER
         && (
            block instanceof BlockFence && block.material == this.material
               || block instanceof BlockFenceGate
               || block.material.k() && block.d() && block.material != Material.PUMPKIN
         );
   }

   @Override
   public boolean interact(
      World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumDirection enumdirection, float f, float f1, float f2
   ) {
      return ItemLeash.a(entityhuman, world, blockposition);
   }

   @Override
   public int toLegacyData(IBlockData iblockdata) {
      return 0;
   }

   @Override
   public IBlockData updateState(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
      return iblockdata.set(NORTH, this.e(iblockaccess, blockposition.north()))
         .set(EAST, this.e(iblockaccess, blockposition.east()))
         .set(SOUTH, this.e(iblockaccess, blockposition.south()))
         .set(WEST, this.e(iblockaccess, blockposition.west()));
   }

   @Override
   protected BlockStateList getStateList() {
      return new BlockStateList(this, NORTH, EAST, WEST, SOUTH);
   }
}
