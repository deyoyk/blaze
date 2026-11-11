package org.bukkit.craftbukkit.v1_8_R3;

import com.google.common.base.CaseFormat;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableBiMap.Builder;
import net.minecraft.server.v1_8_R3.Block;
import net.minecraft.server.v1_8_R3.EntityTypes;
import net.minecraft.server.v1_8_R3.Item;
import net.minecraft.server.v1_8_R3.MinecraftKey;
import net.minecraft.server.v1_8_R3.StatisticList;
import net.minecraft.server.v1_8_R3.EntityTypes.MonsterEggInfo;
import org.bukkit.entity.EntityType;

public class CraftStatistic {
   private static final BiMap<String, org.bukkit.Statistic> statistics;
   private static final BiMap<String, org.bukkit.Achievement> achievements;

   static {
      ImmutableMap<String, org.bukkit.Achievement> specialCases = ImmutableMap.<String, org.bukkit.Achievement>builder()
         .put("achievement.buildWorkBench", org.bukkit.Achievement.BUILD_WORKBENCH)
         .put("achievement.diamonds", org.bukkit.Achievement.GET_DIAMONDS)
         .put("achievement.portal", org.bukkit.Achievement.NETHER_PORTAL)
         .put("achievement.ghast", org.bukkit.Achievement.GHAST_RETURN)
         .put("achievement.theEnd", org.bukkit.Achievement.END_PORTAL)
         .put("achievement.theEnd2", org.bukkit.Achievement.THE_END)
         .put("achievement.blazeRod", org.bukkit.Achievement.GET_BLAZE_ROD)
         .put("achievement.potion", org.bukkit.Achievement.BREW_POTION)
         .build();
      Builder<String, org.bukkit.Statistic> statisticBuilder = ImmutableBiMap.builder();
      Builder<String, org.bukkit.Achievement> achievementBuilder = ImmutableBiMap.builder();

      for (org.bukkit.Statistic statistic : org.bukkit.Statistic.values()) {
         if (statistic == org.bukkit.Statistic.PLAY_ONE_TICK) {
            statisticBuilder.put("stat.playOneMinute", statistic);
         } else {
            statisticBuilder.put("stat." + CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, statistic.name()), statistic);
         }
      }

      for (org.bukkit.Achievement achievement : org.bukkit.Achievement.values()) {
         if (!specialCases.containsValue(achievement)) {
            achievementBuilder.put("achievement." + CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, achievement.name()), achievement);
         }
      }

      achievementBuilder.putAll(specialCases);
      statistics = statisticBuilder.build();
      achievements = achievementBuilder.build();
   }

   private CraftStatistic() {
   }

   public static org.bukkit.Achievement getBukkitAchievement(net.minecraft.server.v1_8_R3.Achievement achievement) {
      return getBukkitAchievementByName(achievement.name);
   }

   public static org.bukkit.Achievement getBukkitAchievementByName(String name) {
      return (org.bukkit.Achievement)achievements.get(name);
   }

   public static org.bukkit.Statistic getBukkitStatistic(net.minecraft.server.v1_8_R3.Statistic statistic) {
      return getBukkitStatisticByName(statistic.name);
   }

   public static org.bukkit.Statistic getBukkitStatisticByName(String name) {
      if (name.startsWith("stat.killEntity")) {
         name = "stat.killEntity";
      }

      if (name.startsWith("stat.entityKilledBy")) {
         name = "stat.entityKilledBy";
      }

      if (name.startsWith("stat.breakItem")) {
         name = "stat.breakItem";
      }

      if (name.startsWith("stat.useItem")) {
         name = "stat.useItem";
      }

      if (name.startsWith("stat.mineBlock")) {
         name = "stat.mineBlock";
      }

      if (name.startsWith("stat.craftItem")) {
         name = "stat.craftItem";
      }

      return (org.bukkit.Statistic)statistics.get(name);
   }

   public static net.minecraft.server.v1_8_R3.Statistic getNMSStatistic(org.bukkit.Statistic statistic) {
      return StatisticList.getStatistic((String)statistics.inverse().get(statistic));
   }

   public static net.minecraft.server.v1_8_R3.Achievement getNMSAchievement(org.bukkit.Achievement achievement) {
      return (net.minecraft.server.v1_8_R3.Achievement)StatisticList.getStatistic((String)achievements.inverse().get(achievement));
   }

   public static net.minecraft.server.v1_8_R3.Statistic getMaterialStatistic(org.bukkit.Statistic stat, org.bukkit.Material material) {
      try {
         if (stat == org.bukkit.Statistic.MINE_BLOCK) {
            return StatisticList.MINE_BLOCK_COUNT[material.getId()];
         } else if (stat == org.bukkit.Statistic.CRAFT_ITEM) {
            return StatisticList.CRAFT_BLOCK_COUNT[material.getId()];
         } else if (stat == org.bukkit.Statistic.USE_ITEM) {
            return StatisticList.USE_ITEM_COUNT[material.getId()];
         } else {
            return stat == org.bukkit.Statistic.BREAK_ITEM ? StatisticList.BREAK_ITEM_COUNT[material.getId()] : null;
         }
      } catch (ArrayIndexOutOfBoundsException var3) {
         return null;
      }
   }

   public static net.minecraft.server.v1_8_R3.Statistic getEntityStatistic(org.bukkit.Statistic stat, EntityType entity) {
      MonsterEggInfo monsteregginfo = (MonsterEggInfo)EntityTypes.eggInfo.get(Integer.valueOf(entity.getTypeId()));
      return monsteregginfo != null ? monsteregginfo.killEntityStatistic : null;
   }

   public static EntityType getEntityTypeFromStatistic(net.minecraft.server.v1_8_R3.Statistic statistic) {
      String statisticString = statistic.name;
      return EntityType.fromName(statisticString.substring(statisticString.lastIndexOf(".") + 1));
   }

   public static org.bukkit.Material getMaterialFromStatistic(net.minecraft.server.v1_8_R3.Statistic statistic) {
      String statisticString = statistic.name;
      String val = statisticString.substring(statisticString.lastIndexOf(".") + 1);
      Item item = (Item)Item.REGISTRY.get(new MinecraftKey(val));
      if (item != null) {
         return org.bukkit.Material.getMaterial(Item.getId(item));
      } else {
         Block block = (Block)Block.REGISTRY.get(new MinecraftKey(val));
         if (block != null) {
            return org.bukkit.Material.getMaterial(Block.getId(block));
         } else {
            try {
               return org.bukkit.Material.getMaterial(Integer.parseInt(val));
            } catch (NumberFormatException var6) {
               return null;
            }
         }
      }
   }
}
