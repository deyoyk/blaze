package org.bukkit.craftbukkit.v1_8_R3.util;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.server.v1_8_R3.Block;
import net.minecraft.server.v1_8_R3.Blocks;
import net.minecraft.server.v1_8_R3.Item;
import net.minecraft.server.v1_8_R3.MinecraftKey;
import net.minecraft.server.v1_8_R3.MojangsonParseException;
import net.minecraft.server.v1_8_R3.MojangsonParser;
import net.minecraft.server.v1_8_R3.StatisticList;
import org.bukkit.craftbukkit.v1_8_R3.CraftStatistic;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

public final class CraftMagicNumbers implements org.bukkit.UnsafeValues {
   public static final org.bukkit.UnsafeValues INSTANCE = new CraftMagicNumbers();

   private CraftMagicNumbers() {
   }

   public static Block getBlock(org.bukkit.block.Block block) {
      return getBlock(block.getType());
   }

   @Deprecated
   public static Block getBlock(int id) {
      return getBlock(org.bukkit.Material.getMaterial(id));
   }

   @Deprecated
   public static int getId(Block block) {
      return Block.getId(block);
   }

   public static org.bukkit.Material getMaterial(Block block) {
      return org.bukkit.Material.getMaterial(Block.getId(block));
   }

   public static Item getItem(org.bukkit.Material material) {
      return Item.getById(material.getId());
   }

   @Deprecated
   public static Item getItem(int id) {
      return Item.getById(id);
   }

   @Deprecated
   public static int getId(Item item) {
      return Item.getId(item);
   }

   public static org.bukkit.Material getMaterial(Item item) {
      org.bukkit.Material material = org.bukkit.Material.getMaterial(Item.getId(item));
      return material == null ? org.bukkit.Material.AIR : material;
   }

   public static Block getBlock(org.bukkit.Material material) {
      Block block = Block.getById(material.getId());
      return block == null ? Blocks.AIR : block;
   }

   @Override
   public org.bukkit.Material getMaterialFromInternalName(String name) {
      return getMaterial((Item)Item.REGISTRY.get(new MinecraftKey(name)));
   }

   @Override
   public List<String> tabCompleteInternalMaterialName(String token, List<String> completions) {
      ArrayList<String> results = Lists.newArrayList();

      for (MinecraftKey key : Item.REGISTRY.keySet()) {
         results.add(key.toString());
      }

      return StringUtil.copyPartialMatches(token, results, completions);
   }

   @Override
   public ItemStack modifyItemStack(ItemStack stack, String arguments) {
      net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(stack);

      try {
         nmsStack.setTag(MojangsonParser.parse(arguments));
      } catch (MojangsonParseException var5) {
         Logger.getLogger(CraftMagicNumbers.class.getName()).log(Level.SEVERE, null, var5);
      }

      stack.setItemMeta(CraftItemStack.getItemMeta(nmsStack));
      return stack;
   }

   @Override
   public org.bukkit.Statistic getStatisticFromInternalName(String name) {
      return CraftStatistic.getBukkitStatisticByName(name);
   }

   @Override
   public org.bukkit.Achievement getAchievementFromInternalName(String name) {
      return CraftStatistic.getBukkitAchievementByName(name);
   }

   @Override
   public List<String> tabCompleteInternalStatisticOrAchievementName(String token, List<String> completions) {
      List<String> matches = new ArrayList<>();

      for (net.minecraft.server.v1_8_R3.Statistic value : StatisticList.stats) {
         String statistic = value.name;
         if (statistic.startsWith(token)) {
            matches.add(statistic);
         }
      }

      return matches;
   }
}
