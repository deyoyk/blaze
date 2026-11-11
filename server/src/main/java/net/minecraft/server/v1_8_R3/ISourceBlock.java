package net.minecraft.server.v1_8_R3;

public interface ISourceBlock extends ILocationSource {
   @Override
   double getX();

   @Override
   double getY();

   @Override
   double getZ();

   BlockPosition getBlockPosition();

   int f();

   <T extends TileEntity> T getTileEntity();
}
