package net.minecraft.server.v1_8_R3;

public class SourceBlock implements ISourceBlock {
   private final World a;
   private final BlockPosition b;

   public SourceBlock(World var1, BlockPosition var2) {
      this.a = var1;
      this.b = var2;
   }

   @Override
   public World getWorld() {
      return this.a;
   }

   @Override
   public double getX() {
      return this.b.getX() + 0.5;
   }

   @Override
   public double getY() {
      return this.b.getY() + 0.5;
   }

   @Override
   public double getZ() {
      return this.b.getZ() + 0.5;
   }

   @Override
   public BlockPosition getBlockPosition() {
      return this.b;
   }

   @Override
   public int f() {
      IBlockData var1 = this.a.getType(this.b);
      return var1.getBlock().toLegacyData(var1);
   }

   @Override
   public <T extends TileEntity> T getTileEntity() {
      return (T)this.a.getTileEntity(this.b);
   }
}
