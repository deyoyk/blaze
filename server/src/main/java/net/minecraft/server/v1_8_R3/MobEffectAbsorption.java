package net.minecraft.server.v1_8_R3;

public class MobEffectAbsorption extends MobEffectList {
   protected MobEffectAbsorption(int var1, MinecraftKey var2, boolean var3, int var4) {
      super(var1, var2, var3, var4);
   }

   @Override
   public void a(EntityLiving var1, AttributeMapBase var2, int var3) {
      var1.setAbsorptionHearts(var1.getAbsorptionHearts() - 4 * (var3 + 1));
      super.a(var1, var2, var3);
   }

   @Override
   public void b(EntityLiving var1, AttributeMapBase var2, int var3) {
      var1.setAbsorptionHearts(var1.getAbsorptionHearts() + 4 * (var3 + 1));
      super.b(var1, var2, var3);
   }
}
