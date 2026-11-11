package net.minecraft.server.v1_8_R3;

public class ItemWithAuxData extends ItemBlock {
   private String[] c;

   public ItemWithAuxData(Block block, boolean flag) {
      super(block);
      if (flag) {
         this.setMaxDurability(0);
         this.a(true);
      }
   }

   @Override
   public int filterData(int i) {
      return i;
   }

   public ItemWithAuxData a(String[] astring) {
      this.c = astring;
      return this;
   }

   @Override
   public String e_(ItemStack itemstack) {
      if (this.c == null) {
         return super.e_(itemstack);
      } else {
         int i = itemstack.getData();
         return i >= 0 && i < this.c.length ? super.e_(itemstack) + "." + this.c[i] : super.e_(itemstack);
      }
   }
}
