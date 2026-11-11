package net.minecraft.server.v1_8_R3;

public class ItemPiston extends ItemBlock {
   public ItemPiston(Block var1) {
      super(var1);
   }

   @Override
   public int filterData(int var1) {
      return 7;
   }
}
