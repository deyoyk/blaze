package org.bukkit.craftbukkit.v1_8_R3.inventory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

@DelegateDeserialization(SerializableMeta.class)
class CraftMetaEnchantedBook extends CraftMetaItem implements EnchantmentStorageMeta {
   static final CraftMetaItem.ItemMetaKey STORED_ENCHANTMENTS = new CraftMetaItem.ItemMetaKey("StoredEnchantments", "stored-enchants");
   private Map<Enchantment, Integer> enchantments;

   CraftMetaEnchantedBook(CraftMetaItem meta) {
      super(meta);
      if (meta instanceof CraftMetaEnchantedBook) {
         CraftMetaEnchantedBook that = (CraftMetaEnchantedBook)meta;
         if (that.hasEnchants()) {
            this.enchantments = new HashMap<>(that.enchantments);
         }
      }
   }

   CraftMetaEnchantedBook(NBTTagCompound tag) {
      super(tag);
      if (tag.hasKey(STORED_ENCHANTMENTS.NBT)) {
         this.enchantments = buildEnchantments(tag, STORED_ENCHANTMENTS);
      }
   }

   CraftMetaEnchantedBook(Map<String, Object> map) {
      super(map);
      this.enchantments = buildEnchantments(map, STORED_ENCHANTMENTS);
   }

   @Override
   void applyToItem(NBTTagCompound itemTag) {
      super.applyToItem(itemTag);
      applyEnchantments(this.enchantments, itemTag, STORED_ENCHANTMENTS);
   }

   @Override
   boolean applicableTo(org.bukkit.Material type) {
      return type == org.bukkit.Material.ENCHANTED_BOOK;
   }

   @Override
   boolean isEmpty() {
      return super.isEmpty() && this.isEnchantedEmpty();
   }

   @Override
   boolean equalsCommon(CraftMetaItem meta) {
      if (!super.equalsCommon(meta)) {
         return false;
      } else if (!(meta instanceof CraftMetaEnchantedBook)) {
         return true;
      } else {
         CraftMetaEnchantedBook that = (CraftMetaEnchantedBook)meta;
         return this.hasStoredEnchants() ? that.hasStoredEnchants() && this.enchantments.equals(that.enchantments) : !that.hasStoredEnchants();
      }
   }

   @Override
   boolean notUncommon(CraftMetaItem meta) {
      return super.notUncommon(meta) && (meta instanceof CraftMetaEnchantedBook || this.isEnchantedEmpty());
   }

   @Override
   int applyHash() {
      int original;
      int hash = original = super.applyHash();
      if (this.hasStoredEnchants()) {
         hash = 61 * hash + this.enchantments.hashCode();
      }

      return original != hash ? CraftMetaEnchantedBook.class.hashCode() ^ hash : hash;
   }

   public CraftMetaEnchantedBook clone() {
      CraftMetaEnchantedBook meta = (CraftMetaEnchantedBook)super.clone();
      if (this.enchantments != null) {
         meta.enchantments = new HashMap<>(this.enchantments);
      }

      return meta;
   }

   @Override
   Builder<String, Object> serialize(Builder<String, Object> builder) {
      super.serialize(builder);
      serializeEnchantments(this.enchantments, builder, STORED_ENCHANTMENTS);
      return builder;
   }

   boolean isEnchantedEmpty() {
      return !this.hasStoredEnchants();
   }

   @Override
   public boolean hasStoredEnchant(Enchantment ench) {
      return this.hasStoredEnchants() && this.enchantments.containsKey(ench);
   }

   @Override
   public int getStoredEnchantLevel(Enchantment ench) {
      Integer level = this.hasStoredEnchants() ? this.enchantments.get(ench) : null;
      return level == null ? 0 : level;
   }

   @Override
   public Map<Enchantment, Integer> getStoredEnchants() {
      return this.hasStoredEnchants() ? ImmutableMap.copyOf(this.enchantments) : ImmutableMap.of();
   }

   @Override
   public boolean addStoredEnchant(Enchantment ench, int level, boolean ignoreRestrictions) {
      if (this.enchantments == null) {
         this.enchantments = new HashMap<>(4);
      }

      if (ignoreRestrictions || level >= ench.getStartLevel() && level <= ench.getMaxLevel()) {
         Integer old = this.enchantments.put(ench, level);
         return old == null || old != level;
      } else {
         return false;
      }
   }

   @Override
   public boolean removeStoredEnchant(Enchantment ench) {
      return this.hasStoredEnchants() && this.enchantments.remove(ench) != null;
   }

   @Override
   public boolean hasStoredEnchants() {
      return this.enchantments != null && !this.enchantments.isEmpty();
   }

   @Override
   public boolean hasConflictingStoredEnchant(Enchantment ench) {
      return checkConflictingEnchants(this.enchantments, ench);
   }
}
