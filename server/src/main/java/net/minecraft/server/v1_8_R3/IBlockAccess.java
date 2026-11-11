package net.minecraft.server.v1_8_R3;

public interface IBlockAccess {
   TileEntity getTileEntity(BlockPosition var1);

   IBlockData getType(BlockPosition var1);

   default IBlockData getType(int x, int y, int z) {
      return this.getType(new BlockPosition(x, y, z));
   }

   boolean isEmpty(BlockPosition var1);

   int getBlockPower(BlockPosition var1, EnumDirection var2);
}
