package net.minecraft.server.v1_8_R3;

public class SlotFurnaceFuel extends Slot {
   public SlotFurnaceFuel(IInventory var1, int var2, int var3, int var4) {
      super(var1, var2, var3, var4);
   }

   @Override
   public boolean isAllowed(ItemStack var1) {
      return TileEntityFurnace.isFuel(var1) || c_(var1);
   }

   @Override
   public int getMaxStackSize(ItemStack var1) {
      return c_(var1) ? 1 : super.getMaxStackSize(var1);
   }

   public static boolean c_(ItemStack var0) {
      return var0 != null && var0.getItem() != null && var0.getItem() == Items.BUCKET;
   }
}
