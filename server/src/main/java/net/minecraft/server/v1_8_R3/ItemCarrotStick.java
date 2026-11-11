package net.minecraft.server.v1_8_R3;

public class ItemCarrotStick extends Item {
   public ItemCarrotStick() {
      this.a(CreativeModeTab.e);
      this.c(1);
      this.setMaxDurability(25);
   }

   @Override
   public ItemStack a(ItemStack var1, World var2, EntityHuman var3) {
      if (var3.au() && var3.vehicle instanceof EntityPig) {
         EntityPig var4 = (EntityPig)var3.vehicle;
         if (var4.cm().h() && var1.j() - var1.getData() >= 7) {
            var4.cm().g();
            var1.damage(7, var3);
            if (var1.count == 0) {
               ItemStack var5 = new ItemStack(Items.FISHING_ROD);
               var5.setTag(var1.getTag());
               return var5;
            }
         }
      }

      var3.b(StatisticList.USE_ITEM_COUNT[Item.getId(this)]);
      return var1;
   }
}
