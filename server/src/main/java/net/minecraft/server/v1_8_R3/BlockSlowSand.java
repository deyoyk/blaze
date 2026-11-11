package net.minecraft.server.v1_8_R3;

public class BlockSlowSand extends Block {
   public BlockSlowSand() {
      super(Material.SAND, MaterialMapColor.B);
      this.a(CreativeModeTab.b);
   }

   @Override
   public AxisAlignedBB a(World world, BlockPosition blockposition, IBlockData iblockdata) {
      return new AxisAlignedBB(
         blockposition.getX(),
         blockposition.getY(),
         blockposition.getZ(),
         blockposition.getX() + 1,
         blockposition.getY() + 1 - 0.125F,
         blockposition.getZ() + 1
      );
   }

   @Override
   public void a(World world, BlockPosition blockposition, IBlockData iblockdata, Entity entity) {
      entity.motX *= 0.4;
      entity.motZ *= 0.4;
   }
}
