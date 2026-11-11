package org.bukkit.craftbukkit.v1_8_R3.inventory;

import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class CraftItemFactory implements ItemFactory {
   static final org.bukkit.Color DEFAULT_LEATHER_COLOR = org.bukkit.Color.fromRGB(10511680);
   static final Collection<String> KNOWN_NBT_ATTRIBUTE_NAMES = ImmutableSet.<String>builder()
      .add("generic.attackDamage")
      .add("generic.followRange")
      .add("generic.knockbackResistance")
      .add("generic.maxHealth")
      .add("generic.movementSpeed")
      .add("horse.jumpStrength")
      .add("zombie.spawnReinforcements")
      .build();
   private static final CraftItemFactory instance = new CraftItemFactory();

   static {
      ConfigurationSerialization.registerClass(SerializableMeta.class);
   }

   private CraftItemFactory() {
   }

   @Override
   public boolean isApplicable(ItemMeta meta, ItemStack itemstack) {
      return itemstack == null ? false : this.isApplicable(meta, itemstack.getType());
   }

   @Override
   public boolean isApplicable(ItemMeta meta, org.bukkit.Material type) {
      if (type == null || meta == null) {
         return false;
      } else if (!(meta instanceof CraftMetaItem)) {
         throw new IllegalArgumentException("Meta of " + meta.getClass().toString() + " not created by " + CraftItemFactory.class.getName());
      } else {
         return ((CraftMetaItem)meta).applicableTo(type);
      }
   }

   @Override
   public ItemMeta getItemMeta(org.bukkit.Material material) {
      Validate.notNull(material, "Material cannot be null");
      return this.getItemMeta(material, null);
   }

   private ItemMeta getItemMeta(org.bukkit.Material material, CraftMetaItem meta) {
      switch (material) {
         case AIR:
            return null;
         case DISPENSER:
         case NOTE_BLOCK:
         case PISTON_BASE:
         case MOB_SPAWNER:
         case CHEST:
         case FURNACE:
         case JUKEBOX:
         case ENCHANTMENT_TABLE:
         case COMMAND:
         case BEACON:
         case TRAPPED_CHEST:
         case DAYLIGHT_DETECTOR:
         case HOPPER:
         case DROPPER:
         case DAYLIGHT_DETECTOR_INVERTED:
         case SIGN:
         case BREWING_STAND_ITEM:
         case FLOWER_POT_ITEM:
         case REDSTONE_COMPARATOR:
            return new CraftMetaBlockState(meta, material);
         case LEATHER_HELMET:
         case LEATHER_CHESTPLATE:
         case LEATHER_LEGGINGS:
         case LEATHER_BOOTS:
            return (ItemMeta)(meta instanceof CraftMetaLeatherArmor ? meta : new CraftMetaLeatherArmor(meta));
         case MAP:
            return (ItemMeta)(meta instanceof CraftMetaMap ? meta : new CraftMetaMap(meta));
         case POTION:
            return (ItemMeta)(meta instanceof CraftMetaPotion ? meta : new CraftMetaPotion(meta));
         case BOOK_AND_QUILL:
            return (ItemMeta)(meta != null && meta.getClass().equals(CraftMetaBook.class) ? meta : new CraftMetaBook(meta));
         case WRITTEN_BOOK:
            return (ItemMeta)(meta instanceof CraftMetaBookSigned ? meta : new CraftMetaBookSigned(meta));
         case SKULL_ITEM:
            return (ItemMeta)(meta instanceof CraftMetaSkull ? meta : new CraftMetaSkull(meta));
         case FIREWORK:
            return (ItemMeta)(meta instanceof CraftMetaFirework ? meta : new CraftMetaFirework(meta));
         case FIREWORK_CHARGE:
            return (ItemMeta)(meta instanceof CraftMetaCharge ? meta : new CraftMetaCharge(meta));
         case ENCHANTED_BOOK:
            return (ItemMeta)(meta instanceof CraftMetaEnchantedBook ? meta : new CraftMetaEnchantedBook(meta));
         case BANNER:
            return (ItemMeta)(meta instanceof CraftMetaBanner ? meta : new CraftMetaBanner(meta));
         default:
            return new CraftMetaItem(meta);
      }
   }

   @Override
   public boolean equals(ItemMeta meta1, ItemMeta meta2) {
      if (meta1 == meta2) {
         return true;
      } else if (meta1 != null && !(meta1 instanceof CraftMetaItem)) {
         throw new IllegalArgumentException("First meta of " + meta1.getClass().getName() + " does not belong to " + CraftItemFactory.class.getName());
      } else if (meta2 != null && !(meta2 instanceof CraftMetaItem)) {
         throw new IllegalArgumentException("Second meta " + meta2.getClass().getName() + " does not belong to " + CraftItemFactory.class.getName());
      } else if (meta1 == null) {
         return ((CraftMetaItem)meta2).isEmpty();
      } else {
         return meta2 == null ? ((CraftMetaItem)meta1).isEmpty() : this.equals((CraftMetaItem)meta1, (CraftMetaItem)meta2);
      }
   }

   boolean equals(CraftMetaItem meta1, CraftMetaItem meta2) {
      return meta1.equalsCommon(meta2) && meta1.notUncommon(meta2) && meta2.notUncommon(meta1);
   }

   public static CraftItemFactory instance() {
      return instance;
   }

   @Override
   public ItemMeta asMetaFor(ItemMeta meta, ItemStack stack) {
      Validate.notNull(stack, "Stack cannot be null");
      return this.asMetaFor(meta, stack.getType());
   }

   @Override
   public ItemMeta asMetaFor(ItemMeta meta, org.bukkit.Material material) {
      Validate.notNull(material, "Material cannot be null");
      if (!(meta instanceof CraftMetaItem)) {
         throw new IllegalArgumentException(
            "Meta of " + (meta != null ? meta.getClass().toString() : "null") + " not created by " + CraftItemFactory.class.getName()
         );
      } else {
         return this.getItemMeta(material, (CraftMetaItem)meta);
      }
   }

   @Override
   public org.bukkit.Color getDefaultLeatherColor() {
      return DEFAULT_LEATHER_COLOR;
   }
}
