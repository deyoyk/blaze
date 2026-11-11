package net.minecraft.server.v1_8_R3;

public class ItemSoup extends ItemFood {
   public ItemSoup(int var1) {
      super(var1, false);
      this.c(1);
   }

   @Override
   public ItemStack b(ItemStack var1, World var2, EntityHuman var3) {
      super.b(var1, var2, var3);
      return new ItemStack(Items.BOWL);
   }
}
