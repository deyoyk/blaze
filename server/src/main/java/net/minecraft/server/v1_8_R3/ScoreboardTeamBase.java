package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;

public abstract class ScoreboardTeamBase {
   public boolean isAlly(ScoreboardTeamBase scoreboardteambase) {
      return this == scoreboardteambase;
   }

   public abstract String getName();

   public abstract String getFormattedName(String var1);

   public abstract boolean allowFriendlyFire();

   public abstract Collection<String> getPlayerNameSet();

   public abstract ScoreboardTeamBase.EnumNameTagVisibility j();

   public static enum EnumNameTagVisibility {
      ALWAYS("always", 0),
      NEVER("never", 1),
      HIDE_FOR_OTHER_TEAMS("hideForOtherTeams", 2),
      HIDE_FOR_OWN_TEAM("hideForOwnTeam", 3);

      private static final Map<String, ScoreboardTeamBase.EnumNameTagVisibility> g = Maps.newHashMap();
      public final String e;
      public final int f;

      static {
         ScoreboardTeamBase.EnumNameTagVisibility[] ascoreboardteambase_enumnametagvisibility = values();

         for (ScoreboardTeamBase.EnumNameTagVisibility scoreboardteambase_enumnametagvisibility : ascoreboardteambase_enumnametagvisibility) {
            g.put(scoreboardteambase_enumnametagvisibility.e, scoreboardteambase_enumnametagvisibility);
         }
      }

      private EnumNameTagVisibility(String s, int i) {
         this.e = s;
         this.f = i;
      }

      public static String[] a() {
         return g.keySet().toArray(new String[0]);
      }

      public static ScoreboardTeamBase.EnumNameTagVisibility a(String s) {
         return g.get(s);
      }
   }
}
