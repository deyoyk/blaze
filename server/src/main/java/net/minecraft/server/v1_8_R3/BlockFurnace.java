package net.minecraft.server.v1_8_R3;

import java.util.Random;

public class BlockFurnace extends BlockContainer {
   public static final BlockStateDirection FACING = BlockStateDirection.of("facing", EnumDirection.EnumDirectionLimit.HORIZONTAL);
   private static boolean N;

   protected BlockFurnace(boolean flag) {
      super(Material.STONE);
      this.j(this.blockStateList.getBlockData().set(FACING, EnumDirection.NORTH));
   }

   public static void a(boolean flag, World world, BlockPosition blockposition) {
      IBlockData iblockdata = world.getType(blockposition);
      TileEntity tileentity = world.getTileEntity(blockposition);
      N = true;
      if (flag) {
         world.setTypeAndData(blockposition, Blocks.LIT_FURNACE.getBlockData().set(FACING, iblockdata.get(FACING)), 3);
         world.setTypeAndData(blockposition, Blocks.LIT_FURNACE.getBlockData().set(FACING, iblockdata.get(FACING)), 3);
      } else {
         world.setTypeAndData(blockposition, Blocks.FURNACE.getBlockData().set(FACING, iblockdata.get(FACING)), 3);
         world.setTypeAndData(blockposition, Blocks.FURNACE.getBlockData().set(FACING, iblockdata.get(FACING)), 3);
      }

      N = false;
      if (tileentity != null) {
         tileentity.D();
         world.setTileEntity(blockposition, tileentity);
      }
   }

   @Override
   public Item getDropType(IBlockData iblockdata, Random random, int i) {
      return Item.getItemOf(Blocks.FURNACE);
   }

   @Override
   public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
      this.e(world, blockposition, iblockdata);
   }

   private void e(World world, BlockPosition blockposition, IBlockData iblockdata) {
      Block block = world.getType(blockposition.north()).getBlock();
      Block block1 = world.getType(blockposition.south()).getBlock();
      Block block2 = world.getType(blockposition.west()).getBlock();
      Block block3 = world.getType(blockposition.east()).getBlock();
      EnumDirection enumdirection = iblockdata.get(FACING);
      if (enumdirection == EnumDirection.NORTH && block.o() && !block1.o()) {
         enumdirection = EnumDirection.SOUTH;
      } else if (enumdirection == EnumDirection.SOUTH && block1.o() && !block.o()) {
         enumdirection = EnumDirection.NORTH;
      } else if (enumdirection == EnumDirection.WEST && block2.o() && !block3.o()) {
         enumdirection = EnumDirection.EAST;
      } else if (enumdirection == EnumDirection.EAST && block3.o() && !block2.o()) {
         enumdirection = EnumDirection.WEST;
      }

      world.setTypeAndData(blockposition, iblockdata.set(FACING, enumdirection), 2);
   }

   @Override
   public boolean interact(
      World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumDirection enumdirection, float f, float f1, float f2
   ) {
      TileEntity tileentity = world.getTileEntity(blockposition);
      if (tileentity instanceof TileEntityFurnace) {
         entityhuman.openContainer((TileEntityFurnace)tileentity);
      }

      return true;
   }

   @Override
   public TileEntity a(World world, int i) {
      return new TileEntityFurnace();
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
      if (itemstack.hasName()) {
         TileEntity tileentity = world.getTileEntity(blockposition);
         if (tileentity instanceof TileEntityFurnace) {
            ((TileEntityFurnace)tileentity).a(itemstack.getName());
         }
      }
   }

   @Override
   public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
      if (!N) {
         TileEntity tileentity = world.getTileEntity(blockposition);
         if (tileentity instanceof TileEntityFurnace) {
            InventoryUtils.dropInventory(world, blockposition, (TileEntityFurnace)tileentity);
            world.updateAdjacentComparators(blockposition, this);
         }
      }

      super.remove(world, blockposition, iblockdata);
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
   public int b() {
      return 3;
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
