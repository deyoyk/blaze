package net.minecraft.server.v1_8_R3;

import java.util.List;

public class ScoreboardCriteriaInteger implements IScoreboardCriteria {
   private final String j;

   public ScoreboardCriteriaInteger(String var1, EnumChatFormat var2) {
      this.j = var1 + var2.e();
      IScoreboardCriteria.criteria.put(this.j, this);
   }

   @Override
   public String getName() {
      return this.j;
   }

   @Override
   public int getScoreModifier(List<EntityHuman> var1) {
      return 0;
   }

   @Override
   public boolean isReadOnly() {
      return false;
   }

   @Override
   public IScoreboardCriteria.EnumScoreboardHealthDisplay c() {
      return IScoreboardCriteria.EnumScoreboardHealthDisplay.INTEGER;
   }
}
