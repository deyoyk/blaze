package net.minecraft.server.v1_8_R3;

public class BlockPowered extends Block {
   public BlockPowered(Material var1, MaterialMapColor var2) {
      super(var1, var2);
   }

   @Override
   public boolean isPowerSource() {
      return true;
   }

   @Override
   public int a(IBlockAccess var1, BlockPosition var2, IBlockData var3, EnumDirection var4) {
      return 15;
   }
}
