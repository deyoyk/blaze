package net.minecraft.server.v1_8_R3;

public class ItemGoldenApple extends ItemFood {
   public ItemGoldenApple(int paramInt, float paramFloat, boolean paramBoolean) {
      super(paramInt, paramFloat, paramBoolean);
      this.a(true);
   }

   @Override
   public EnumItemRarity g(ItemStack paramItemStack) {
      return paramItemStack.getData() == 0 ? EnumItemRarity.RARE : EnumItemRarity.EPIC;
   }

   @Override
   protected void c(ItemStack paramItemStack, World paramWorld, EntityHuman paramEntityHuman) {
      paramEntityHuman.addEffect(new MobEffect(MobEffectList.ABSORBTION.id, 2400, 0));
      if (paramItemStack.getData() > 0) {
         paramEntityHuman.addEffect(new MobEffect(MobEffectList.REGENERATION.id, 600, 4));
         paramEntityHuman.addEffect(new MobEffect(MobEffectList.RESISTANCE.id, 6000, 0));
         paramEntityHuman.addEffect(new MobEffect(MobEffectList.FIRE_RESISTANCE.id, 6000, 0));
      } else {
         super.c(paramItemStack, paramWorld, paramEntityHuman);
      }
   }
}
