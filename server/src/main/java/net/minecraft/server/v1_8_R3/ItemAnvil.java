package net.minecraft.server.v1_8_R3;

public class ItemAnvil extends ItemMultiTexture {
   public ItemAnvil(Block var1) {
      super(var1, var1, new String[]{"intact", "slightlyDamaged", "veryDamaged"});
   }

   @Override
   public int filterData(int var1) {
      return var1 << 2;
   }
}
