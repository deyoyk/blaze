package net.minecraft.server.v1_8_R3;

public class MobEffectHealthBoost extends MobEffectList {
   public MobEffectHealthBoost(int var1, MinecraftKey var2, boolean var3, int var4) {
      super(var1, var2, var3, var4);
   }

   @Override
   public void a(EntityLiving var1, AttributeMapBase var2, int var3) {
      super.a(var1, var2, var3);
      if (var1.getHealth() > var1.getMaxHealth()) {
         var1.setHealth(var1.getMaxHealth());
      }
   }
}
