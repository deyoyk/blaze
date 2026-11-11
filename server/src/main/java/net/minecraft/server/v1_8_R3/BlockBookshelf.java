package net.minecraft.server.v1_8_R3;

import java.util.Random;

public class BlockBookshelf extends Block {
   public BlockBookshelf() {
      super(Material.WOOD);
      this.a(CreativeModeTab.b);
   }

   @Override
   public int a(Random var1) {
      return 3;
   }

   @Override
   public Item getDropType(IBlockData var1, Random var2, int var3) {
      return Items.BOOK;
   }
}
