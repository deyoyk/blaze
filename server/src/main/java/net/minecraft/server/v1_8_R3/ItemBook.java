package net.minecraft.server.v1_8_R3;

public class ItemBook extends Item {
   @Override
   public boolean f_(ItemStack var1) {
      return var1.count == 1;
   }

   @Override
   public int b() {
      return 1;
   }
}
