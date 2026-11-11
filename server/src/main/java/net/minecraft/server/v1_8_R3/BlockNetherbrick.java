package net.minecraft.server.v1_8_R3;

public class BlockNetherbrick extends Block {
   public BlockNetherbrick() {
      super(Material.STONE);
      this.a(CreativeModeTab.b);
   }

   @Override
   public MaterialMapColor g(IBlockData var1) {
      return MaterialMapColor.K;
   }
}
