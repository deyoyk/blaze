package org.bukkit.craftbukkit.v1_8_R3.scoreboard;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.server.v1_8_R3.IScoreboardCriteria;
import net.minecraft.server.v1_8_R3.ScoreboardObjective;

final class CraftCriteria {
   static final Map<String, CraftCriteria> DEFAULTS;
   static final CraftCriteria DUMMY;
   final IScoreboardCriteria criteria;
   final String bukkitName;

   static {
      Builder<String, CraftCriteria> defaults = ImmutableMap.builder();

      for (Entry<?, ?> entry : IScoreboardCriteria.criteria.entrySet()) {
         String name = entry.getKey().toString();
         IScoreboardCriteria criteria = (IScoreboardCriteria)entry.getValue();
         defaults.put(name, new CraftCriteria(criteria));
      }

      DEFAULTS = defaults.build();
      DUMMY = DEFAULTS.get("dummy");
   }

   private CraftCriteria(String bukkitName) {
      this.bukkitName = bukkitName;
      this.criteria = DUMMY.criteria;
   }

   private CraftCriteria(IScoreboardCriteria criteria) {
      this.criteria = criteria;
      this.bukkitName = criteria.getName();
   }

   static CraftCriteria getFromNMS(ScoreboardObjective objective) {
      return DEFAULTS.get(objective.getCriteria().getName());
   }

   static CraftCriteria getFromBukkit(String name) {
      CraftCriteria criteria = DEFAULTS.get(name);
      return criteria != null ? criteria : new CraftCriteria(name);
   }

   @Override
   public boolean equals(Object that) {
      return !(that instanceof CraftCriteria) ? false : ((CraftCriteria)that).bukkitName.equals(this.bukkitName);
   }

   @Override
   public int hashCode() {
      return this.bukkitName.hashCode() ^ CraftCriteria.class.hashCode();
   }
}
