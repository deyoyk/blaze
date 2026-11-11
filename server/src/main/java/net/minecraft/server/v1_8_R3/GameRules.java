package net.minecraft.server.v1_8_R3;

import lol.deyo.blaze.configs.BlazeConfig;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class GameRules {
   private final Map<String, GameRules.GameRuleValue> values = new LinkedHashMap<>();

   public GameRules() {
      this.a("doFireTick", "true", GameRules.EnumGameRuleType.BOOLEAN_VALUE);
      this.a("mobGriefing", "true", GameRules.EnumGameRuleType.BOOLEAN_VALUE);
      this.a("keepInventory", "false", GameRules.EnumGameRuleType.BOOLEAN_VALUE);
      this.a("doMobSpawning", ((Boolean)BlazeConfig.naturalMobSpawn.getValue()).toString(), GameRules.EnumGameRuleType.BOOLEAN_VALUE);
      this.a("doMobLoot", "true", GameRules.EnumGameRuleType.BOOLEAN_VALUE);
      this.a("doTileDrops", "true", GameRules.EnumGameRuleType.BOOLEAN_VALUE);
      this.a("doEntityDrops", "true", GameRules.EnumGameRuleType.BOOLEAN_VALUE);
      this.a("commandBlockOutput", "true", GameRules.EnumGameRuleType.BOOLEAN_VALUE);
      this.a("naturalRegeneration", "true", GameRules.EnumGameRuleType.BOOLEAN_VALUE);
      this.a("doDaylightCycle", "true", GameRules.EnumGameRuleType.BOOLEAN_VALUE);
      this.a("logAdminCommands", "true", GameRules.EnumGameRuleType.BOOLEAN_VALUE);
      this.a("showDeathMessages", "true", GameRules.EnumGameRuleType.BOOLEAN_VALUE);
      this.a("randomTickSpeed", ((Integer)BlazeConfig.randomTickSpeed.getValue()).toString(), GameRules.EnumGameRuleType.NUMERICAL_VALUE);
      this.a("sendCommandFeedback", "true", GameRules.EnumGameRuleType.BOOLEAN_VALUE);
      this.a("reducedDebugInfo", "false", GameRules.EnumGameRuleType.BOOLEAN_VALUE);
      this.a("doWeatherCycle", "true", GameRules.EnumGameRuleType.BOOLEAN_VALUE);
   }

   public void a(String name, String value, GameRules.EnumGameRuleType type) {
      this.values.put(name, new GameRules.GameRuleValue(value, type));
   }

   public void set(String name, String defaultValue) {
      GameRules.GameRuleValue value = this.values.get(name);
      if (value != null) {
         value.a(defaultValue);
      } else {
         this.a(name, defaultValue, GameRules.EnumGameRuleType.ANY_VALUE);
      }
   }

   public String get(String name) {
      GameRules.GameRuleValue value = this.values.get(name);
      return value != null ? value.a() : "";
   }

   public boolean getBoolean(String name) {
      GameRules.GameRuleValue value = this.values.get(name);
      return value != null && value.b();
   }

   public int c(String name) {
      GameRules.GameRuleValue gameRuleValue = this.values.get(name);
      return gameRuleValue != null ? gameRuleValue.c() : 0;
   }

   public NBTTagCompound a() {
      NBTTagCompound compound = new NBTTagCompound();

      for (String name : this.values.keySet()) {
         compound.setString(name, this.values.get(name).a());
      }

      return compound;
   }

   public void a(NBTTagCompound compound) {
      for (String str1 : compound.c()) {
         this.set(str1, compound.getString(str1));
      }
   }

   public String[] getGameRules() {
      Set<String> set = this.values.keySet();
      return set.toArray(new String[0]);
   }

   public boolean contains(String name) {
      return this.values.containsKey(name);
   }

   public boolean a(String name, GameRules.EnumGameRuleType type) {
      GameRules.GameRuleValue value = this.values.get(name);
      return value != null && (value.e() == type || type == GameRules.EnumGameRuleType.ANY_VALUE);
   }

   public static enum EnumGameRuleType {
      ANY_VALUE,
      BOOLEAN_VALUE,
      NUMERICAL_VALUE;
   }

   static class GameRuleValue {
      private final GameRules.EnumGameRuleType e;
      private String a;
      private boolean b;
      private int c;

      public GameRuleValue(String value, GameRules.EnumGameRuleType type) {
         this.e = type;
         this.a(value);
      }

      public void a(String value) {
         this.a = value;
         this.b = Boolean.parseBoolean(value);
         this.c = this.b ? 1 : 0;

         try {
            this.c = Integer.parseInt(value);
         } catch (NumberFormatException var3) {
         }
      }

      public String a() {
         return this.a;
      }

      public boolean b() {
         return this.b;
      }

      public int c() {
         return this.c;
      }

      public GameRules.EnumGameRuleType e() {
         return this.e;
      }
   }
}
