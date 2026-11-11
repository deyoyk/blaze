package net.minecraft.server.v1_8_R3;

import java.util.Random;

public class BlockSign extends BlockContainer {
   protected BlockSign() {
      super(Material.WOOD);
      float f = 0.25F;
      float f1 = 1.0F;
      this.a(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f1, 0.5F + f);
   }

   @Override
   public AxisAlignedBB a(World world, BlockPosition blockposition, IBlockData iblockdata) {
      return null;
   }

   @Override
   public boolean d() {
      return false;
   }

   @Override
   public boolean b(IBlockAccess iblockaccess, BlockPosition blockposition) {
      return true;
   }

   @Override
   public boolean c() {
      return false;
   }

   @Override
   public boolean g() {
      return true;
   }

   @Override
   public TileEntity a(World world, int i) {
      return new TileEntitySign();
   }

   @Override
   public Item getDropType(IBlockData iblockdata, Random random, int i) {
      return Items.SIGN;
   }

   @Override
   public boolean interact(
      World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumDirection enumdirection, float f, float f1, float f2
   ) {
      TileEntity tileentity = world.getTileEntity(blockposition);
      return tileentity instanceof TileEntitySign && ((TileEntitySign)tileentity).b(entityhuman);
   }

   @Override
   public boolean canPlace(World world, BlockPosition blockposition) {
      return !this.e(world, blockposition) && super.canPlace(world, blockposition);
   }
}
