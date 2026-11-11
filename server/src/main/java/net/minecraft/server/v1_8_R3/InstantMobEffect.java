package net.minecraft.server.v1_8_R3;

public class InstantMobEffect extends MobEffectList {
   public InstantMobEffect(int var1, MinecraftKey var2, boolean var3, int var4) {
      super(var1, var2, var3, var4);
   }

   @Override
   public boolean isInstant() {
      return true;
   }

   @Override
   public boolean a(int var1, int var2) {
      return var1 >= 1;
   }
}
