package net.minecraft.server.v1_8_R3;

import java.util.List;

public class ScoreboardBaseCriteria implements IScoreboardCriteria {
   private final String j;

   public ScoreboardBaseCriteria(String var1) {
      this.j = var1;
      IScoreboardCriteria.criteria.put(var1, this);
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
