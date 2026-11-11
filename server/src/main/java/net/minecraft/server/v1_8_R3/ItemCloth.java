package net.minecraft.server.v1_8_R3;

public class ItemCloth extends ItemBlock {
   public ItemCloth(Block var1) {
      super(var1);
      this.setMaxDurability(0);
      this.a(true);
   }

   @Override
   public int filterData(int var1) {
      return var1;
   }

   @Override
   public String e_(ItemStack var1) {
      return super.getName() + "." + EnumColor.fromColorIndex(var1.getData()).d();
   }
}
