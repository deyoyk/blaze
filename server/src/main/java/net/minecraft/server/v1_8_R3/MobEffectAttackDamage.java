package net.minecraft.server.v1_8_R3;

import org.github.paperspigot.PaperSpigotConfig;

public class MobEffectAttackDamage extends MobEffectList {
   protected MobEffectAttackDamage(int i, MinecraftKey minecraftkey, boolean flag, int j) {
      super(i, minecraftkey, flag, j);
   }

   @Override
   public double a(int i, AttributeModifier attributemodifier) {
      return this.id == MobEffectList.WEAKNESS.id ? PaperSpigotConfig.weaknessEffectModifier * (i + 1) : PaperSpigotConfig.strengthEffectModifier * (i + 1);
   }
}
