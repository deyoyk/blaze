package net.minecraft.server.v1_8_R3;

public class BlockTransparent extends Block {
   protected boolean R;

   protected BlockTransparent(Material var1, boolean var2) {
      super(var1);
      this.R = var2;
   }

   @Override
   public boolean c() {
      return false;
   }
}
