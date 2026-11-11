package net.minecraft.server.v1_8_R3;

import java.util.List;

public class BlockHopper extends BlockContainer {
   public static final BlockStateDirection FACING = BlockStateDirection.of("facing", enumdirection -> enumdirection != EnumDirection.UP);
   public static final BlockStateBoolean ENABLED = BlockStateBoolean.of("enabled");

   public BlockHopper() {
      super(Material.ORE, MaterialMapColor.m);
      this.j(this.blockStateList.getBlockData().set(FACING, EnumDirection.DOWN).set(ENABLED, true));
      this.a(CreativeModeTab.d);
      this.a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
   }

   public static EnumDirection b(int i) {
      return EnumDirection.fromType1(i & 7);
   }

   public static boolean f(int i) {
      return (i & 8) != 8;
   }

   @Override
   public void updateShape(IBlockAccess iblockaccess, BlockPosition blockposition) {
      this.a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
   }

   @Override
   public void a(World world, BlockPosition blockposition, IBlockData iblockdata, AxisAlignedBB axisalignedbb, List<AxisAlignedBB> list, Entity entity) {
      this.a(0.0F, 0.625F, 0.125F, 1.0F, 1.0F, 0.0F);
      super.a(world, blockposition, iblockdata, axisalignedbb, list, entity);
      this.a(0.0F, 0.625F, 0.875F, 1.0F, 1.0F, 1.0F);
      super.a(world, blockposition, iblockdata, axisalignedbb, list, entity);
      this.a(0.125F, 0.625F, 0.0F, 0.0F, 1.0F, 1.0F);
      super.a(world, blockposition, iblockdata, axisalignedbb, list, entity);
      this.a(0.875F, 0.625F, 0.0F, 1.0F, 1.0F, 1.0F);
      super.a(world, blockposition, iblockdata, axisalignedbb, list, entity);
      this.a(0.0F, 0.0F, 0.0F, 1.0F, 0.625F, 1.0F);
      super.a(world, blockposition, iblockdata, axisalignedbb, list, entity);
      this.updateShape(world, blockposition);
   }

   @Override
   public IBlockData getPlacedState(
      World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving
   ) {
      EnumDirection enumdirection1 = enumdirection.opposite();
      if (enumdirection1 == EnumDirection.UP) {
         enumdirection1 = EnumDirection.DOWN;
      }

      return this.getBlockData().set(FACING, enumdirection1).set(ENABLED, true);
   }

   @Override
   public TileEntity a(World world, int i) {
      return new TileEntityHopper();
   }

   @Override
   public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving, ItemStack itemstack) {
      super.postPlace(world, blockposition, iblockdata, entityliving, itemstack);
      if (itemstack.hasName()) {
         TileEntity tileentity = world.getTileEntity(blockposition);
         if (tileentity instanceof TileEntityHopper) {
            ((TileEntityHopper)tileentity).a(itemstack.getName());
         }
      }
   }

   @Override
   public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
      this.e(world, blockposition, iblockdata);
   }

   @Override
   public boolean interact(
      World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumDirection enumdirection, float f, float f1, float f2
   ) {
      TileEntity tileentity = world.getTileEntity(blockposition);
      if (tileentity instanceof TileEntityHopper) {
         entityhuman.openContainer((TileEntityHopper)tileentity);
      }

      return true;
   }

   @Override
   public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
      this.e(world, blockposition, iblockdata);
   }

   private void e(World world, BlockPosition blockposition, IBlockData iblockdata) {
      boolean flag = !world.isBlockIndirectlyPowered(blockposition);
      if (flag != iblockdata.get(ENABLED)) {
         world.setTypeAndData(blockposition, iblockdata.set(ENABLED, flag), 4, false, false);
      }
   }

   @Override
   public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
      TileEntity tileentity = world.getTileEntity(blockposition);
      if (tileentity instanceof TileEntityHopper) {
         InventoryUtils.dropInventory(world, blockposition, (TileEntityHopper)tileentity);
         world.updateAdjacentComparators(blockposition, this);
      }

      super.remove(world, blockposition, iblockdata);
   }

   @Override
   public int b() {
      return 3;
   }

   @Override
   public boolean d() {
      return false;
   }

   @Override
   public boolean c() {
      return false;
   }

   @Override
   public boolean isComplexRedstone() {
      return true;
   }

   @Override
   public int l(World world, BlockPosition blockposition) {
      return Container.a(world.getTileEntity(blockposition));
   }

   @Override
   public IBlockData fromLegacyData(int i) {
      return this.getBlockData().set(FACING, b(i)).set(ENABLED, f(i));
   }

   @Override
   public int toLegacyData(IBlockData iblockdata) {
      int i = iblockdata.get(FACING).a();
      if (!iblockdata.get(ENABLED)) {
         i |= 8;
      }

      return i;
   }

   @Override
   protected BlockStateList getStateList() {
      return new BlockStateList(this, FACING, ENABLED);
   }
}
