package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lol.deyo.blaze.configs.BlazeConfig;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class EnchantmentManager {
   private static final Random a = new Random();
   private static final EnchantmentManager.EnchantmentModifierProtection b = new EnchantmentManager.EnchantmentModifierProtection(null);
   private static final EnchantmentManager.EnchantmentModifierDamage c = new EnchantmentManager.EnchantmentModifierDamage(null);
   private static final EnchantmentManager.EnchantmentModifierThorns d = new EnchantmentManager.EnchantmentModifierThorns(null);
   private static final EnchantmentManager.EnchantmentModifierArthropods e = new EnchantmentManager.EnchantmentModifierArthropods(null);

   public static int getEnchantmentLevel(int i, ItemStack itemstack) {
      if (itemstack != null) {
         NBTTagList nbttaglist = itemstack.getEnchantments();
         if (nbttaglist != null) {
            for (int j = 0; j < nbttaglist.size(); j++) {
               NBTTagCompound compound = nbttaglist.get(j);
               if (compound.getShort("id") == i) {
                  return compound.getShort("lvl");
               }
            }
         }
      }

      return 0;
   }

   public static LinkedHashMap<Integer, Integer> a(ItemStack itemstack) {
      LinkedHashMap<Integer, Integer> linkedhashmap = Maps.newLinkedHashMap();
      NBTTagList nbttaglist = itemstack.getItem() == Items.ENCHANTED_BOOK ? Items.ENCHANTED_BOOK.h(itemstack) : itemstack.getEnchantments();
      if (nbttaglist != null) {
         for (int i = 0; i < nbttaglist.size(); i++) {
            NBTTagCompound compound = nbttaglist.get(i);
            linkedhashmap.put(Integer.valueOf(compound.getShort("id")), Integer.valueOf(compound.getShort("lvl")));
         }
      }

      return linkedhashmap;
   }

   public static void a(Map<Integer, Integer> map, ItemStack itemstack) {
      NBTTagList nbttaglist = new NBTTagList();

      for (int i : map.keySet()) {
         Enchantment enchantment = Enchantment.getById(i);
         if (enchantment != null && enchantment.getEnchantLimit() != 0) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setShort("id", (short)i);
            nbttagcompound.setShort("lvl", (short)map.get(i).intValue());
            nbttaglist.add(nbttagcompound);
            if (itemstack.getItem() == Items.ENCHANTED_BOOK) {
               Items.ENCHANTED_BOOK.a(itemstack, new WeightedRandomEnchant(enchantment, map.get(i)));
            }
         }
      }

      if (!nbttaglist.isEmpty()) {
         if (itemstack.getItem() != Items.ENCHANTED_BOOK) {
            itemstack.a("ench", nbttaglist);
         }
      } else if (itemstack.hasTag()) {
         itemstack.getTag().remove("ench");
      }
   }

   public static int a(int i, ItemStack[] aitemstack) {
      if (aitemstack == null) {
         return 0;
      } else {
         int j = 0;

         for (ItemStack itemstack : aitemstack) {
            int i1 = getEnchantmentLevel(i, itemstack);
            if (i1 > j) {
               j = i1;
            }
         }

         return j;
      }
   }

   private static void a(EnchantmentManager.EnchantmentModifier enchantmentmanager_enchantmentmodifier, ItemStack itemstack) {
      if (itemstack != null) {
         NBTTagList nbttaglist = itemstack.getEnchantments();
         if (nbttaglist != null) {
            for (int i = 0; i < nbttaglist.size(); i++) {
               NBTTagCompound compound = nbttaglist.get(i);
               Enchantment enchantment = Enchantment.getById(compound.getShort("id"));
               if (enchantment != null) {
                  enchantmentmanager_enchantmentmodifier.a(enchantment, compound.getShort("lvl"));
               }
            }
         }
      }
   }

   private static void a(EnchantmentManager.EnchantmentModifier enchantmentmanager_enchantmentmodifier, ItemStack[] aitemstack) {
      for (ItemStack itemstack : aitemstack) {
         a(enchantmentmanager_enchantmentmodifier, itemstack);
      }
   }

   public static int a(ItemStack[] aitemstack, DamageSource damagesource) {
      b.a = 0;
      b.b = damagesource;
      a(b, aitemstack);
      if (b.a > 25) {
         b.a = 25;
      } else if (b.a < 0) {
         b.a = 0;
      }

      b.b = null;
      return BlazeConfig.protRandomness.getValue() ? (b.a + 1 >> 1) + a.nextInt((b.a >> 1) + 1) : b.a;
   }

   public static float a(ItemStack itemstack, EnumMonsterType enummonstertype) {
      c.a = 0.0F;
      c.b = enummonstertype;
      a(c, itemstack);
      return c.a;
   }

   public static void a(EntityLiving entityliving, Entity entity) {
      d.b = entity;
      d.a = entityliving;
      if (entityliving != null) {
         a(d, entityliving.getEquipment());
      }

      if (entity instanceof EntityHuman && entityliving != null) {
         a(d, entityliving.bA());
      }

      d.a = null;
      d.b = null;
   }

   public static void b(EntityLiving entityliving, Entity entity) {
      e.a = entityliving;
      e.b = entity;
      if (entityliving != null) {
         a(e, entityliving.getEquipment());
      }

      if (entityliving instanceof EntityHuman) {
         a(e, entityliving.bA());
      }

      e.a = null;
      e.b = null;
   }

   public static int a(EntityLiving entityliving) {
      return getEnchantmentLevel(Enchantment.KNOCKBACK.id, entityliving.bA());
   }

   public static int getFireAspectEnchantmentLevel(EntityLiving entityliving) {
      return getEnchantmentLevel(Enchantment.FIRE_ASPECT.id, entityliving.bA());
   }

   public static int getOxygenEnchantmentLevel(Entity entity) {
      return a(Enchantment.OXYGEN.id, entity.getEquipment());
   }

   public static int b(Entity entity) {
      return a(Enchantment.DEPTH_STRIDER.id, entity.getEquipment());
   }

   public static int getDigSpeedEnchantmentLevel(EntityLiving entityliving) {
      return getEnchantmentLevel(Enchantment.DIG_SPEED.id, entityliving.bA());
   }

   public static boolean hasSilkTouchEnchantment(EntityLiving entityliving) {
      return getEnchantmentLevel(Enchantment.SILK_TOUCH.id, entityliving.bA()) > 0;
   }

   public static int getBonusBlockLootEnchantmentLevel(EntityLiving entityliving) {
      return getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS.id, entityliving.bA());
   }

   public static int g(EntityLiving entityliving) {
      return getEnchantmentLevel(Enchantment.LUCK.id, entityliving.bA());
   }

   public static int h(EntityLiving entityliving) {
      return getEnchantmentLevel(Enchantment.LURE.id, entityliving.bA());
   }

   public static int getBonusMonsterLootEnchantmentLevel(EntityLiving entityliving) {
      return getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS.id, entityliving.bA());
   }

   public static boolean j(EntityLiving entityliving) {
      return a(Enchantment.WATER_WORKER.id, entityliving.getEquipment()) > 0;
   }

   public static ItemStack a(Enchantment enchantment, EntityLiving entityliving) {
      ItemStack[] var5;
      for (ItemStack itemstack : var5 = entityliving.getEquipment()) {
         if (itemstack != null && getEnchantmentLevel(enchantment.id, itemstack) > 0) {
            return itemstack;
         }
      }

      return null;
   }

   public static int a(Random random, int i, int j, ItemStack itemstack) {
      Item item = itemstack.getItem();
      int k = item.b();
      if (k <= 0) {
         return 0;
      } else {
         if (j > 15) {
            j = 15;
         }

         int l = random.nextInt(8) + 1 + (j >> 1) + random.nextInt(j + 1);
         return i == 0 ? Math.max(l / 3, 1) : (i == 1 ? l * 2 / 3 + 1 : Math.max(l, j * 2));
      }
   }

   public static ItemStack a(Random random, ItemStack itemstack, int i) {
      List<WeightedRandomEnchant> list = b(random, itemstack, i);
      boolean flag = itemstack.getItem() == Items.BOOK;
      if (flag) {
         itemstack.setItem(Items.ENCHANTED_BOOK);
      }

      if (list != null) {
         for (WeightedRandomEnchant weightedrandomenchant : list) {
            if (flag) {
               Items.ENCHANTED_BOOK.a(itemstack, weightedrandomenchant);
            } else {
               itemstack.addEnchantment(weightedrandomenchant.enchantment, weightedrandomenchant.level);
            }
         }
      }

      return itemstack;
   }

   public static List<WeightedRandomEnchant> b(Random random, ItemStack itemstack, int i) {
      Item item = itemstack.getItem();
      int j = item.b();
      if (j <= 0) {
         return null;
      } else {
         j /= 2;
         j = 1 + random.nextInt((j >> 1) + 1) + random.nextInt((j >> 1) + 1);
         int k = j + i;
         float f = (random.nextFloat() + random.nextFloat() - 1.0F) * 0.15F;
         int l = (int)(k * (1.0F + f) + 0.5F);
         if (l < 1) {
            l = 1;
         }

         ArrayList<WeightedRandomEnchant> arraylist = null;
         Map<Integer, WeightedRandomEnchant> map = b(l, itemstack);
         if (map != null && !map.isEmpty()) {
            WeightedRandomEnchant weightedrandomenchant = WeightedRandom.a(random, map.values());
            if (weightedrandomenchant != null) {
               arraylist = Lists.newArrayList();
               arraylist.add(weightedrandomenchant);

               for (int i1 = l; random.nextInt(50) <= i1; i1 >>= 1) {
                  Iterator<Integer> iterator = map.keySet().iterator();

                  while (iterator.hasNext()) {
                     Integer integer = iterator.next();
                     boolean flag = true;

                     for (WeightedRandomEnchant weightedrandomenchant1 : arraylist) {
                        if (!weightedrandomenchant1.enchantment.a(Enchantment.getById(integer))) {
                           flag = false;
                           break;
                        }
                     }

                     if (!flag) {
                        iterator.remove();
                     }
                  }

                  if (!map.isEmpty()) {
                     arraylist.add(WeightedRandom.a(random, map.values()));
                  }
               }
            }
         }

         return arraylist;
      }
   }

   public static Map<Integer, WeightedRandomEnchant> b(int i, ItemStack itemstack) {
      Item item = itemstack.getItem();
      HashMap<Integer, WeightedRandomEnchant> hashmap = null;
      boolean flag = itemstack.getItem() == Items.BOOK;

      for (Enchantment enchantment : Enchantment.b) {
         if (enchantment != null && (enchantment.slot.canEnchant(item) || flag)) {
            int limit = enchantment.getEnchantLimit();
            int maxLevel = enchantment.getMaxLevel();
            if (limit != 0) {
               for (int l = enchantment.getStartLevel(); l <= Math.min(maxLevel, limit); l++) {
                  if (i >= enchantment.a(l) && i <= enchantment.b(l)) {
                     if (hashmap == null) {
                        hashmap = Maps.newHashMap();
                     }

                     hashmap.put(enchantment.id, new WeightedRandomEnchant(enchantment, l));
                  }
               }
            }
         }
      }

      return hashmap;
   }

   interface EnchantmentModifier {
      void a(Enchantment var1, int var2);
   }

   static final class EnchantmentModifierArthropods implements EnchantmentManager.EnchantmentModifier {
      public EntityLiving a;
      public Entity b;

      private EnchantmentModifierArthropods() {
      }

      @Override
      public void a(Enchantment enchantment, int i) {
         enchantment.a(this.a, this.b, i);
      }

      EnchantmentModifierArthropods(EnchantmentManager.SyntheticClass_1 enchantmentmanager_syntheticclass_1) {
         this();
      }
   }

   static final class EnchantmentModifierDamage implements EnchantmentManager.EnchantmentModifier {
      public float a;
      public EnumMonsterType b;

      private EnchantmentModifierDamage() {
      }

      @Override
      public void a(Enchantment enchantment, int i) {
         this.a = this.a + enchantment.a(i, this.b);
      }

      EnchantmentModifierDamage(EnchantmentManager.SyntheticClass_1 enchantmentmanager_syntheticclass_1) {
         this();
      }
   }

   static final class EnchantmentModifierProtection implements EnchantmentManager.EnchantmentModifier {
      public int a;
      public DamageSource b;

      private EnchantmentModifierProtection() {
      }

      @Override
      public void a(Enchantment enchantment, int i) {
         this.a = this.a + enchantment.a(i, this.b);
      }

      EnchantmentModifierProtection(EnchantmentManager.SyntheticClass_1 enchantmentmanager_syntheticclass_1) {
         this();
      }
   }

   static final class EnchantmentModifierThorns implements EnchantmentManager.EnchantmentModifier {
      public EntityLiving a;
      public Entity b;

      private EnchantmentModifierThorns() {
      }

      @Override
      public void a(Enchantment enchantment, int i) {
         enchantment.b(this.a, this.b, i);
      }

      EnchantmentModifierThorns(EnchantmentManager.SyntheticClass_1 enchantmentmanager_syntheticclass_1) {
         this();
      }
   }

   static class SyntheticClass_1 {
   }
}
