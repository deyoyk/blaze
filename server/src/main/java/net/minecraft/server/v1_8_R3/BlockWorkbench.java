package net.minecraft.server.v1_8_R3;

public class BlockWorkbench extends Block {
   protected BlockWorkbench() {
      super(Material.WOOD);
      this.a(CreativeModeTab.c);
   }

   @Override
   public boolean interact(
      World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumDirection enumdirection, float f, float f1, float f2
   ) {
      entityhuman.openTileEntity(new BlockWorkbench.TileEntityContainerWorkbench(world, blockposition));
      return true;
   }

   public static class TileEntityContainerWorkbench implements ITileEntityContainer {
      private final World a;
      private final BlockPosition b;

      public TileEntityContainerWorkbench(World world, BlockPosition blockposition) {
         this.a = world;
         this.b = blockposition;
      }

      @Override
      public String getName() {
         return null;
      }

      @Override
      public boolean hasCustomName() {
         return false;
      }

      @Override
      public IChatBaseComponent getScoreboardDisplayName() {
         return new ChatMessage(Blocks.CRAFTING_TABLE.a() + ".name");
      }

      @Override
      public Container createContainer(PlayerInventory playerinventory, EntityHuman entityhuman) {
         return new ContainerWorkbench(playerinventory, this.a, this.b);
      }

      @Override
      public String getContainerName() {
         return "minecraft:crafting_table";
      }
   }
}
