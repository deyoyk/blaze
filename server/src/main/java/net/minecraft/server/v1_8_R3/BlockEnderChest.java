package net.minecraft.server.v1_8_R3;

import java.util.Random;

public class BlockEnderChest extends BlockContainer {
   public static final BlockStateDirection FACING = BlockStateDirection.of("facing", EnumDirection.EnumDirectionLimit.HORIZONTAL);

   protected BlockEnderChest() {
      super(Material.STONE);
      this.j(this.blockStateList.getBlockData().set(FACING, EnumDirection.NORTH));
      this.a(CreativeModeTab.c);
      this.a(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
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
   public int b() {
      return 2;
   }

   @Override
   public Item getDropType(IBlockData iblockdata, Random random, int i) {
      return Item.getItemOf(Blocks.OBSIDIAN);
   }

   @Override
   public int a(Random random) {
      return 8;
   }

   @Override
   protected boolean I() {
      return true;
   }

   @Override
   public IBlockData getPlacedState(
      World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving
   ) {
      return this.getBlockData().set(FACING, entityliving.getDirection().opposite());
   }

   @Override
   public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving, ItemStack itemstack) {
      world.setTypeAndData(blockposition, iblockdata.set(FACING, entityliving.getDirection().opposite()), 2);
   }

   @Override
   public boolean interact(
      World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumDirection enumdirection, float f, float f1, float f2
   ) {
      InventoryEnderChest inventoryenderchest = entityhuman.getEnderChest();
      TileEntity tileentity = world.getTileEntity(blockposition);
      if (inventoryenderchest != null && tileentity instanceof TileEntityEnderChest && !world.getType(blockposition.up()).getBlock().isOccluding()) {
         inventoryenderchest.a((TileEntityEnderChest)tileentity);
         entityhuman.openContainer(inventoryenderchest);
      }

      return true;
   }

   @Override
   public TileEntity a(World world, int i) {
      return new TileEntityEnderChest();
   }

   @Override
   public IBlockData fromLegacyData(int i) {
      EnumDirection enumdirection = EnumDirection.fromType1(i);
      if (enumdirection.k() == EnumDirection.EnumAxis.Y) {
         enumdirection = EnumDirection.NORTH;
      }

      return this.getBlockData().set(FACING, enumdirection);
   }

   @Override
   public int toLegacyData(IBlockData iblockdata) {
      return iblockdata.get(FACING).a();
   }

   @Override
   protected BlockStateList getStateList() {
      return new BlockStateList(this, FACING);
   }
}
