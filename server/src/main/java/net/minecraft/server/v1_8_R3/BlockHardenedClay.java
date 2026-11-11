package net.minecraft.server.v1_8_R3;

public class BlockHardenedClay extends Block {
   public BlockHardenedClay() {
      super(Material.STONE);
      this.a(CreativeModeTab.b);
   }

   @Override
   public MaterialMapColor g(IBlockData var1) {
      return MaterialMapColor.q;
   }
}
