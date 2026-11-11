package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class StatisticList {
   protected static Map<String, Statistic> a = Maps.newHashMap();
   public static List<Statistic> stats = Lists.newArrayList();
   public static List<Statistic> c = Lists.newArrayList();
   public static List<CraftingStatistic> d = Lists.newArrayList();
   public static List<CraftingStatistic> e = Lists.newArrayList();
   public static Statistic f = new CounterStatistic("stat.leaveGame", new ChatMessage("stat.leaveGame")).i().h();
   public static Statistic g = new CounterStatistic("stat.playOneMinute", new ChatMessage("stat.playOneMinute"), Statistic.h).i().h();
   public static Statistic h = new CounterStatistic("stat.timeSinceDeath", new ChatMessage("stat.timeSinceDeath"), Statistic.h).i().h();
   public static Statistic i = new CounterStatistic("stat.walkOneCm", new ChatMessage("stat.walkOneCm"), Statistic.i).i().h();
   public static Statistic j = new CounterStatistic("stat.crouchOneCm", new ChatMessage("stat.crouchOneCm"), Statistic.i).i().h();
   public static Statistic k = new CounterStatistic("stat.sprintOneCm", new ChatMessage("stat.sprintOneCm"), Statistic.i).i().h();
   public static Statistic l = new CounterStatistic("stat.swimOneCm", new ChatMessage("stat.swimOneCm"), Statistic.i).i().h();
   public static Statistic m = new CounterStatistic("stat.fallOneCm", new ChatMessage("stat.fallOneCm"), Statistic.i).i().h();
   public static Statistic n = new CounterStatistic("stat.climbOneCm", new ChatMessage("stat.climbOneCm"), Statistic.i).i().h();
   public static Statistic o = new CounterStatistic("stat.flyOneCm", new ChatMessage("stat.flyOneCm"), Statistic.i).i().h();
   public static Statistic p = new CounterStatistic("stat.diveOneCm", new ChatMessage("stat.diveOneCm"), Statistic.i).i().h();
   public static Statistic q = new CounterStatistic("stat.minecartOneCm", new ChatMessage("stat.minecartOneCm"), Statistic.i).i().h();
   public static Statistic r = new CounterStatistic("stat.boatOneCm", new ChatMessage("stat.boatOneCm"), Statistic.i).i().h();
   public static Statistic s = new CounterStatistic("stat.pigOneCm", new ChatMessage("stat.pigOneCm"), Statistic.i).i().h();
   public static Statistic t = new CounterStatistic("stat.horseOneCm", new ChatMessage("stat.horseOneCm"), Statistic.i).i().h();
   public static Statistic u = new CounterStatistic("stat.jump", new ChatMessage("stat.jump")).i().h();
   public static Statistic v = new CounterStatistic("stat.drop", new ChatMessage("stat.drop")).i().h();
   public static Statistic w = new CounterStatistic("stat.damageDealt", new ChatMessage("stat.damageDealt"), Statistic.j).h();
   public static Statistic x = new CounterStatistic("stat.damageTaken", new ChatMessage("stat.damageTaken"), Statistic.j).h();
   public static Statistic y = new CounterStatistic("stat.deaths", new ChatMessage("stat.deaths")).h();
   public static Statistic z = new CounterStatistic("stat.mobKills", new ChatMessage("stat.mobKills")).h();
   public static Statistic A = new CounterStatistic("stat.animalsBred", new ChatMessage("stat.animalsBred")).h();
   public static Statistic B = new CounterStatistic("stat.playerKills", new ChatMessage("stat.playerKills")).h();
   public static Statistic C = new CounterStatistic("stat.fishCaught", new ChatMessage("stat.fishCaught")).h();
   public static Statistic D = new CounterStatistic("stat.junkFished", new ChatMessage("stat.junkFished")).h();
   public static Statistic E = new CounterStatistic("stat.treasureFished", new ChatMessage("stat.treasureFished")).h();
   public static Statistic F = new CounterStatistic("stat.talkedToVillager", new ChatMessage("stat.talkedToVillager")).h();
   public static Statistic G = new CounterStatistic("stat.tradedWithVillager", new ChatMessage("stat.tradedWithVillager")).h();
   public static Statistic H = new CounterStatistic("stat.cakeSlicesEaten", new ChatMessage("stat.cakeSlicesEaten")).h();
   public static Statistic I = new CounterStatistic("stat.cauldronFilled", new ChatMessage("stat.cauldronFilled")).h();
   public static Statistic J = new CounterStatistic("stat.cauldronUsed", new ChatMessage("stat.cauldronUsed")).h();
   public static Statistic K = new CounterStatistic("stat.armorCleaned", new ChatMessage("stat.armorCleaned")).h();
   public static Statistic L = new CounterStatistic("stat.bannerCleaned", new ChatMessage("stat.bannerCleaned")).h();
   public static Statistic M = new CounterStatistic("stat.brewingstandInteraction", new ChatMessage("stat.brewingstandInteraction")).h();
   public static Statistic N = new CounterStatistic("stat.beaconInteraction", new ChatMessage("stat.beaconInteraction")).h();
   public static Statistic O = new CounterStatistic("stat.dropperInspected", new ChatMessage("stat.dropperInspected")).h();
   public static Statistic P = new CounterStatistic("stat.hopperInspected", new ChatMessage("stat.hopperInspected")).h();
   public static Statistic Q = new CounterStatistic("stat.dispenserInspected", new ChatMessage("stat.dispenserInspected")).h();
   public static Statistic R = new CounterStatistic("stat.noteblockPlayed", new ChatMessage("stat.noteblockPlayed")).h();
   public static Statistic S = new CounterStatistic("stat.noteblockTuned", new ChatMessage("stat.noteblockTuned")).h();
   public static Statistic T = new CounterStatistic("stat.flowerPotted", new ChatMessage("stat.flowerPotted")).h();
   public static Statistic U = new CounterStatistic("stat.trappedChestTriggered", new ChatMessage("stat.trappedChestTriggered")).h();
   public static Statistic V = new CounterStatistic("stat.enderchestOpened", new ChatMessage("stat.enderchestOpened")).h();
   public static Statistic W = new CounterStatistic("stat.itemEnchanted", new ChatMessage("stat.itemEnchanted")).h();
   public static Statistic X = new CounterStatistic("stat.recordPlayed", new ChatMessage("stat.recordPlayed")).h();
   public static Statistic Y = new CounterStatistic("stat.furnaceInteraction", new ChatMessage("stat.furnaceInteraction")).h();
   public static Statistic Z = new CounterStatistic("stat.craftingTableInteraction", new ChatMessage("stat.workbenchInteraction")).h();
   public static Statistic aa = new CounterStatistic("stat.chestOpened", new ChatMessage("stat.chestOpened")).h();
   public static final Statistic[] MINE_BLOCK_COUNT = new Statistic[4096];
   public static final Statistic[] CRAFT_BLOCK_COUNT = new Statistic[32000];
   public static final Statistic[] USE_ITEM_COUNT = new Statistic[32000];
   public static final Statistic[] BREAK_ITEM_COUNT = new Statistic[32000];

   public static void a() {
      c();
      d();
      e();
      b();
      AchievementList.a();
      EntityTypes.a();
   }

   private static void b() {
      HashSet<Item> var0 = Sets.newHashSet();

      for (IRecipe var2 : CraftingManager.getInstance().getRecipes()) {
         if (var2.b() != null) {
            var0.add(var2.b().getItem());
         }
      }

      for (ItemStack var7 : RecipesFurnace.getInstance().getRecipes().values()) {
         var0.add(var7.getItem());
      }

      for (Item var8 : var0) {
         if (var8 != null) {
            int var3 = Item.getId(var8);
            String var4 = a(var8);
            if (var4 != null) {
               CRAFT_BLOCK_COUNT[var3] = new CraftingStatistic("stat.craftItem.", var4, new ChatMessage("stat.craftItem", new ItemStack(var8).C()), var8).h();
            }
         }
      }

      a(CRAFT_BLOCK_COUNT);
   }

   private static void c() {
      for (Block var1 : Block.REGISTRY) {
         Item var2 = Item.getItemOf(var1);
         if (var2 != null) {
            int var3 = Block.getId(var1);
            String var4 = a(var2);
            if (var4 != null && var1.J()) {
               MINE_BLOCK_COUNT[var3] = new CraftingStatistic("stat.mineBlock.", var4, new ChatMessage("stat.mineBlock", new ItemStack(var1).C()), var2).h();
               e.add((CraftingStatistic)MINE_BLOCK_COUNT[var3]);
            }
         }
      }

      a(MINE_BLOCK_COUNT);
   }

   private static void d() {
      for (Item var1 : Item.REGISTRY) {
         if (var1 != null) {
            int var2 = Item.getId(var1);
            String var3 = a(var1);
            if (var3 != null) {
               USE_ITEM_COUNT[var2] = new CraftingStatistic("stat.useItem.", var3, new ChatMessage("stat.useItem", new ItemStack(var1).C()), var1).h();
               if (!(var1 instanceof ItemBlock)) {
                  d.add((CraftingStatistic)USE_ITEM_COUNT[var2]);
               }
            }
         }
      }

      a(USE_ITEM_COUNT);
   }

   private static void e() {
      for (Item var1 : Item.REGISTRY) {
         if (var1 != null) {
            int var2 = Item.getId(var1);
            String var3 = a(var1);
            if (var3 != null && var1.usesDurability()) {
               BREAK_ITEM_COUNT[var2] = new CraftingStatistic("stat.breakItem.", var3, new ChatMessage("stat.breakItem", new ItemStack(var1).C()), var1).h();
            }
         }
      }

      a(BREAK_ITEM_COUNT);
   }

   private static String a(Item var0) {
      MinecraftKey var1 = Item.REGISTRY.c(var0);
      return var1 != null ? var1.toString().replace(':', '.') : null;
   }

   private static void a(Statistic[] var0) {
      a(var0, Blocks.WATER, Blocks.FLOWING_WATER);
      a(var0, Blocks.LAVA, Blocks.FLOWING_LAVA);
      a(var0, Blocks.LIT_PUMPKIN, Blocks.PUMPKIN);
      a(var0, Blocks.LIT_FURNACE, Blocks.FURNACE);
      a(var0, Blocks.LIT_REDSTONE_ORE, Blocks.REDSTONE_ORE);
      a(var0, Blocks.POWERED_REPEATER, Blocks.UNPOWERED_REPEATER);
      a(var0, Blocks.POWERED_COMPARATOR, Blocks.UNPOWERED_COMPARATOR);
      a(var0, Blocks.REDSTONE_TORCH, Blocks.UNLIT_REDSTONE_TORCH);
      a(var0, Blocks.LIT_REDSTONE_LAMP, Blocks.REDSTONE_LAMP);
      a(var0, Blocks.DOUBLE_STONE_SLAB, Blocks.STONE_SLAB);
      a(var0, Blocks.DOUBLE_WOODEN_SLAB, Blocks.WOODEN_SLAB);
      a(var0, Blocks.DOUBLE_STONE_SLAB2, Blocks.STONE_SLAB2);
      a(var0, Blocks.GRASS, Blocks.DIRT);
      a(var0, Blocks.FARMLAND, Blocks.DIRT);
   }

   private static void a(Statistic[] var0, Block var1, Block var2) {
      int var3 = Block.getId(var1);
      int var4 = Block.getId(var2);
      if (var0[var3] != null && var0[var4] == null) {
         var0[var4] = var0[var3];
      } else {
         stats.remove(var0[var3]);
         e.remove(var0[var3]);
         c.remove(var0[var3]);
         var0[var3] = var0[var4];
      }
   }

   public static Statistic a(EntityTypes.MonsterEggInfo var0) {
      String var1 = EntityTypes.b(var0.a);
      return var1 == null
         ? null
         : new Statistic("stat.killEntity." + var1, new ChatMessage("stat.entityKill", new ChatMessage("entity." + var1 + ".name"))).h();
   }

   public static Statistic b(EntityTypes.MonsterEggInfo var0) {
      String var1 = EntityTypes.b(var0.a);
      return var1 == null
         ? null
         : new Statistic("stat.entityKilledBy." + var1, new ChatMessage("stat.entityKilledBy", new ChatMessage("entity." + var1 + ".name"))).h();
   }

   public static Statistic getStatistic(String var0) {
      return a.get(var0);
   }
}
