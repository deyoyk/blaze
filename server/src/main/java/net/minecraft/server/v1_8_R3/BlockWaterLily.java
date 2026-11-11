package net.minecraft.server.v1_8_R3;

import java.util.List;

public class BlockWaterLily extends BlockPlant {
   protected BlockWaterLily() {
      float f = 0.5F;
      float f1 = 0.015625F;
      this.a(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f1, 0.5F + f);
      this.a(CreativeModeTab.c);
   }

   @Override
   public void a(World world, BlockPosition blockposition, IBlockData iblockdata, AxisAlignedBB axisalignedbb, List<AxisAlignedBB> list, Entity entity) {
      if (!(entity instanceof EntityBoat)) {
         super.a(world, blockposition, iblockdata, axisalignedbb, list, entity);
      }
   }

   @Override
   public AxisAlignedBB a(World world, BlockPosition blockposition, IBlockData iblockdata) {
      return new AxisAlignedBB(
         blockposition.getX() + this.minX,
         blockposition.getY() + this.minY,
         blockposition.getZ() + this.minZ,
         blockposition.getX() + this.maxX,
         blockposition.getY() + this.maxY,
         blockposition.getZ() + this.maxZ
      );
   }

   @Override
   protected boolean c(Block block) {
      return block == Blocks.WATER;
   }

   @Override
   public boolean f(World world, BlockPosition blockposition, IBlockData iblockdata) {
      if (blockposition.getY() >= 0 && blockposition.getY() < 256) {
         IBlockData iblockdata1 = world.getType(blockposition.down());
         return iblockdata1.getBlock().getMaterial() == Material.WATER && iblockdata1.get(BlockFluids.LEVEL) == 0;
      } else {
         return false;
      }
   }

   @Override
   public int toLegacyData(IBlockData iblockdata) {
      return 0;
   }
}
