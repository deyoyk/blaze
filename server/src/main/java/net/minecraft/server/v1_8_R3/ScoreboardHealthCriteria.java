package net.minecraft.server.v1_8_R3;

import java.util.List;

public class ScoreboardHealthCriteria extends ScoreboardBaseCriteria {
   public ScoreboardHealthCriteria(String var1) {
      super(var1);
   }

   @Override
   public int getScoreModifier(List<EntityHuman> var1) {
      float var2 = 0.0F;

      for (EntityHuman var4 : var1) {
         var2 += var4.getHealth() + var4.getAbsorptionHearts();
      }

      if (var1.size() > 0) {
         var2 /= var1.size();
      }

      return MathHelper.f(var2);
   }

   @Override
   public boolean isReadOnly() {
      return true;
   }

   @Override
   public IScoreboardCriteria.EnumScoreboardHealthDisplay c() {
      return IScoreboardCriteria.EnumScoreboardHealthDisplay.HEARTS;
   }
}
