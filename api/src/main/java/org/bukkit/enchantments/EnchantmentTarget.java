package org.bukkit.enchantments;

import org.bukkit.inventory.ItemStack;

public enum EnchantmentTarget {
   ALL {
      @Override
      public boolean includes(org.bukkit.Material item) {
         return true;
      }
   },
   ARMOR {
      @Override
      public boolean includes(org.bukkit.Material item) {
         return ARMOR_FEET.includes(item) || ARMOR_LEGS.includes(item) || ARMOR_HEAD.includes(item) || ARMOR_TORSO.includes(item);
      }
   },
   ARMOR_FEET {
      @Override
      public boolean includes(org.bukkit.Material item) {
         return item.equals(org.bukkit.Material.LEATHER_BOOTS)
            || item.equals(org.bukkit.Material.CHAINMAIL_BOOTS)
            || item.equals(org.bukkit.Material.IRON_BOOTS)
            || item.equals(org.bukkit.Material.DIAMOND_BOOTS)
            || item.equals(org.bukkit.Material.GOLD_BOOTS);
      }
   },
   ARMOR_LEGS {
      @Override
      public boolean includes(org.bukkit.Material item) {
         return item.equals(org.bukkit.Material.LEATHER_LEGGINGS)
            || item.equals(org.bukkit.Material.CHAINMAIL_LEGGINGS)
            || item.equals(org.bukkit.Material.IRON_LEGGINGS)
            || item.equals(org.bukkit.Material.DIAMOND_LEGGINGS)
            || item.equals(org.bukkit.Material.GOLD_LEGGINGS);
      }
   },
   ARMOR_TORSO {
      @Override
      public boolean includes(org.bukkit.Material item) {
         return item.equals(org.bukkit.Material.LEATHER_CHESTPLATE)
            || item.equals(org.bukkit.Material.CHAINMAIL_CHESTPLATE)
            || item.equals(org.bukkit.Material.IRON_CHESTPLATE)
            || item.equals(org.bukkit.Material.DIAMOND_CHESTPLATE)
            || item.equals(org.bukkit.Material.GOLD_CHESTPLATE);
      }
   },
   ARMOR_HEAD {
      @Override
      public boolean includes(org.bukkit.Material item) {
         return item.equals(org.bukkit.Material.LEATHER_HELMET)
            || item.equals(org.bukkit.Material.CHAINMAIL_HELMET)
            || item.equals(org.bukkit.Material.DIAMOND_HELMET)
            || item.equals(org.bukkit.Material.IRON_HELMET)
            || item.equals(org.bukkit.Material.GOLD_HELMET);
      }
   },
   WEAPON {
      @Override
      public boolean includes(org.bukkit.Material item) {
         return item.equals(org.bukkit.Material.WOOD_SWORD)
            || item.equals(org.bukkit.Material.STONE_SWORD)
            || item.equals(org.bukkit.Material.IRON_SWORD)
            || item.equals(org.bukkit.Material.DIAMOND_SWORD)
            || item.equals(org.bukkit.Material.GOLD_SWORD);
      }
   },
   TOOL {
      @Override
      public boolean includes(org.bukkit.Material item) {
         return item.equals(org.bukkit.Material.WOOD_SPADE)
            || item.equals(org.bukkit.Material.STONE_SPADE)
            || item.equals(org.bukkit.Material.IRON_SPADE)
            || item.equals(org.bukkit.Material.DIAMOND_SPADE)
            || item.equals(org.bukkit.Material.GOLD_SPADE)
            || item.equals(org.bukkit.Material.WOOD_PICKAXE)
            || item.equals(org.bukkit.Material.STONE_PICKAXE)
            || item.equals(org.bukkit.Material.IRON_PICKAXE)
            || item.equals(org.bukkit.Material.DIAMOND_PICKAXE)
            || item.equals(org.bukkit.Material.GOLD_PICKAXE)
            || item.equals(org.bukkit.Material.WOOD_HOE)
            || item.equals(org.bukkit.Material.STONE_HOE)
            || item.equals(org.bukkit.Material.IRON_HOE)
            || item.equals(org.bukkit.Material.DIAMOND_HOE)
            || item.equals(org.bukkit.Material.GOLD_HOE)
            || item.equals(org.bukkit.Material.WOOD_AXE)
            || item.equals(org.bukkit.Material.STONE_AXE)
            || item.equals(org.bukkit.Material.IRON_AXE)
            || item.equals(org.bukkit.Material.DIAMOND_AXE)
            || item.equals(org.bukkit.Material.GOLD_AXE)
            || item.equals(org.bukkit.Material.SHEARS)
            || item.equals(org.bukkit.Material.FLINT_AND_STEEL);
      }
   },
   BOW {
      @Override
      public boolean includes(org.bukkit.Material item) {
         return item.equals(org.bukkit.Material.BOW);
      }
   },
   FISHING_ROD {
      @Override
      public boolean includes(org.bukkit.Material item) {
         return item.equals(org.bukkit.Material.FISHING_ROD);
      }
   };

   private EnchantmentTarget() {
   }

   public abstract boolean includes(org.bukkit.Material var1);

   public boolean includes(ItemStack item) {
      return this.includes(item.getType());
   }
}
