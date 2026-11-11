package net.minecraft.server.v1_8_R3;

import java.util.Random;

public class BlockGlass extends BlockHalfTransparent {
   public BlockGlass(Material var1, boolean var2) {
      super(var1, var2);
      this.a(CreativeModeTab.b);
   }

   @Override
   public int a(Random var1) {
      return 0;
   }

   @Override
   public boolean d() {
      return false;
   }

   @Override
   protected boolean I() {
      return true;
   }
}
