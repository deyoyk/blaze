package net.minecraft.server.v1_8_R3;

public class BlockBeacon extends BlockContainer {
   public BlockBeacon() {
      super(Material.SHATTERABLE, MaterialMapColor.G);
      this.c(3.0F);
      this.a(CreativeModeTab.f);
   }

   public static void f(World world, BlockPosition blockposition) {
      Chunk chunk = world.getChunkAtWorldCoords(blockposition);
      BlockPosition.MutableBlockPosition mutableBlockPosition = new BlockPosition.MutableBlockPosition()
         .setValues(blockposition.getX(), blockposition.getY(), blockposition.getZ());

      for (int i = blockposition.getY() - 1; i >= 0; i--) {
         mutableBlockPosition.setY(i);
         if (!chunk.d(mutableBlockPosition)) {
            break;
         }

         IBlockData iblockdata = world.getType(mutableBlockPosition);
         if (iblockdata.getBlock() == Blocks.BEACON) {
            TileEntity tileentity = world.getTileEntity(blockposition);
            if (tileentity instanceof TileEntityBeacon) {
               ((TileEntityBeacon)tileentity).m();
               world.playBlockAction(blockposition, Blocks.BEACON, 1, 0);
            }
         }
      }
   }

   @Override
   public TileEntity a(World world, int i) {
      return new TileEntityBeacon();
   }

   @Override
   public boolean interact(
      World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumDirection enumdirection, float f, float f1, float f2
   ) {
      TileEntity tileentity = world.getTileEntity(blockposition);
      if (tileentity instanceof TileEntityBeacon) {
         entityhuman.openContainer((TileEntityBeacon)tileentity);
      }

      return true;
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
      return 3;
   }

   @Override
   public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving, ItemStack itemstack) {
      super.postPlace(world, blockposition, iblockdata, entityliving, itemstack);
      if (itemstack.hasName()) {
         TileEntity tileentity = world.getTileEntity(blockposition);
         if (tileentity instanceof TileEntityBeacon) {
            ((TileEntityBeacon)tileentity).a(itemstack.getName());
         }
      }
   }

   @Override
   public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
      TileEntity tileentity = world.getTileEntity(blockposition);
      if (tileentity instanceof TileEntityBeacon) {
         ((TileEntityBeacon)tileentity).m();
         world.playBlockAction(blockposition, this, 1, 0);
      }
   }
}
