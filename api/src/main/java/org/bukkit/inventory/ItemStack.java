package org.bukkit.inventory;

import com.google.common.collect.ImmutableMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

public class ItemStack implements Cloneable, ConfigurationSerializable {
   private int type = 0;
   private int amount = 0;
   private MaterialData data = null;
   private short durability = 0;
   private ItemMeta meta;

   protected ItemStack() {
   }

   @Deprecated
   public ItemStack(int type) {
      this(type, 1);
   }

   public ItemStack(org.bukkit.Material type) {
      this(type, 1);
   }

   @Deprecated
   public ItemStack(int type, int amount) {
      this(type, amount, (short)0);
   }

   public ItemStack(org.bukkit.Material type, int amount) {
      this(type.getId(), amount);
   }

   @Deprecated
   public ItemStack(int type, int amount, short damage) {
      this.type = type;
      this.amount = amount;
      this.durability = damage;
   }

   public ItemStack(org.bukkit.Material type, int amount, short damage) {
      this(type.getId(), amount, damage);
   }

   @Deprecated
   public ItemStack(int type, int amount, short damage, Byte data) {
      this.type = type;
      this.amount = amount;
      this.durability = damage;
      if (data != null) {
         this.createData(data);
         this.durability = data.byteValue();
      }
   }

   @Deprecated
   public ItemStack(org.bukkit.Material type, int amount, short damage, Byte data) {
      this(type.getId(), amount, damage, data);
   }

   public ItemStack(ItemStack stack) throws IllegalArgumentException {
      Validate.notNull(stack, "Cannot copy null stack");
      this.type = stack.getTypeId();
      this.amount = stack.getAmount();
      this.durability = stack.getDurability();
      this.data = stack.getData();
      if (stack.hasItemMeta()) {
         this.setItemMeta0(stack.getItemMeta(), this.getType0());
      }
   }

   public org.bukkit.Material getType() {
      return getType0(this.getTypeId());
   }

   private org.bukkit.Material getType0() {
      return getType0(this.type);
   }

   private static org.bukkit.Material getType0(int id) {
      org.bukkit.Material material = org.bukkit.Material.getMaterial(id);
      return material == null ? org.bukkit.Material.AIR : material;
   }

   public void setType(org.bukkit.Material type) {
      Validate.notNull(type, "Material cannot be null");
      this.setTypeId(type.getId());
   }

   @Deprecated
   public int getTypeId() {
      return this.type;
   }

   @Deprecated
   public void setTypeId(int type) {
      this.type = type;
      if (this.meta != null) {
         this.meta = org.bukkit.Bukkit.getItemFactory().asMetaFor(this.meta, this.getType0());
      }

      this.createData((byte)0);
   }

   public int getAmount() {
      return this.amount;
   }

   public void setAmount(int amount) {
      this.amount = amount;
   }

   public MaterialData getData() {
      org.bukkit.Material mat = this.getType();
      if (this.data == null && mat != null && mat.getData() != null) {
         this.data = mat.getNewData((byte)this.getDurability());
      }

      return this.data;
   }

   public void setData(MaterialData data) {
      org.bukkit.Material mat = this.getType();
      if (data != null && mat != null && mat.getData() != null) {
         if (data.getClass() != mat.getData() && data.getClass() != MaterialData.class) {
            throw new IllegalArgumentException("Provided data is not of type " + mat.getData().getName() + ", found " + data.getClass().getName());
         }

         this.data = data;
      } else {
         this.data = data;
      }
   }

   public void setDurability(short durability) {
      this.durability = durability;
   }

   public short getDurability() {
      return this.durability;
   }

   public int getMaxStackSize() {
      org.bukkit.Material material = this.getType();
      return material != null ? material.getMaxStackSize() : -1;
   }

   private void createData(byte data) {
      org.bukkit.Material mat = org.bukkit.Material.getMaterial(this.type);
      if (mat == null) {
         this.data = new MaterialData(this.type, data);
      } else {
         this.data = mat.getNewData(data);
      }
   }

   @Override
   public String toString() {
      StringBuilder toString = new StringBuilder("ItemStack{").append(this.getType().name()).append(" x ").append(this.getAmount());
      if (this.hasItemMeta()) {
         toString.append(", ").append(this.getItemMeta());
      }

      return toString.append('}').toString();
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (!(obj instanceof ItemStack)) {
         return false;
      } else {
         ItemStack stack = (ItemStack)obj;
         return this.getAmount() == stack.getAmount() && this.isSimilar(stack);
      }
   }

   public boolean isSimilar(ItemStack stack) {
      if (stack == null) {
         return false;
      } else {
         return stack == this
            ? true
            : this.getTypeId() == stack.getTypeId()
               && this.getDurability() == stack.getDurability()
               && this.hasItemMeta() == stack.hasItemMeta()
               && (!this.hasItemMeta() || org.bukkit.Bukkit.getItemFactory().equals(this.getItemMeta(), stack.getItemMeta()));
      }
   }

   public ItemStack clone() {
      try {
         ItemStack itemStack = (ItemStack)super.clone();
         if (this.meta != null) {
            itemStack.meta = this.meta.clone();
         }

         if (this.data != null) {
            itemStack.data = this.data.clone();
         }

         return itemStack;
      } catch (CloneNotSupportedException var2) {
         throw new Error(var2);
      }
   }

   @Override
   public final int hashCode() {
      int hash = 1;
      hash = hash * 31 + this.getTypeId();
      hash = hash * 31 + this.getAmount();
      hash = hash * 31 + (this.getDurability() & '\uffff');
      return hash * 31 + (this.hasItemMeta() ? (this.meta == null ? this.getItemMeta().hashCode() : this.meta.hashCode()) : 0);
   }

   public boolean containsEnchantment(Enchantment ench) {
      return this.meta == null ? false : this.meta.hasEnchant(ench);
   }

   public int getEnchantmentLevel(Enchantment ench) {
      return this.meta == null ? 0 : this.meta.getEnchantLevel(ench);
   }

   public Map<Enchantment, Integer> getEnchantments() {
      return (Map<Enchantment, Integer>)(this.meta == null ? ImmutableMap.of() : this.meta.getEnchants());
   }

   public void addEnchantments(Map<Enchantment, Integer> enchantments) {
      Validate.notNull(enchantments, "Enchantments cannot be null");

      for (Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
         this.addEnchantment(entry.getKey(), entry.getValue());
      }
   }

   public void addEnchantment(Enchantment ench, int level) {
      Validate.notNull(ench, "Enchantment cannot be null");
      if (level < ench.getStartLevel() || level > ench.getMaxLevel()) {
         throw new IllegalArgumentException(
            "Enchantment level is either too low or too high (given " + level + ", bounds are " + ench.getStartLevel() + " to " + ench.getMaxLevel() + ")"
         );
      } else if (!ench.canEnchantItem(this)) {
         throw new IllegalArgumentException("Specified enchantment cannot be applied to this itemstack");
      } else {
         this.addUnsafeEnchantment(ench, level);
      }
   }

   public void addUnsafeEnchantments(Map<Enchantment, Integer> enchantments) {
      for (Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
         this.addUnsafeEnchantment(entry.getKey(), entry.getValue());
      }
   }

   public void addUnsafeEnchantment(Enchantment ench, int level) {
      (this.meta == null ? (this.meta = org.bukkit.Bukkit.getItemFactory().getItemMeta(this.getType0())) : this.meta).addEnchant(ench, level, true);
   }

   public int removeEnchantment(Enchantment ench) {
      int level = this.getEnchantmentLevel(ench);
      if (level != 0 && this.meta != null) {
         this.meta.removeEnchant(ench);
         return level;
      } else {
         return level;
      }
   }

   @Override
   public Map<String, Object> serialize() {
      Map<String, Object> result = new LinkedHashMap<>();
      result.put("type", this.getType().name());
      if (this.getDurability() != 0) {
         result.put("damage", this.getDurability());
      }

      if (this.getAmount() != 1) {
         result.put("amount", this.getAmount());
      }

      ItemMeta meta = this.getItemMeta();
      if (!org.bukkit.Bukkit.getItemFactory().equals(meta, null)) {
         result.put("meta", meta);
      }

      return result;
   }

   public static ItemStack deserialize(Map<String, Object> args) {
      org.bukkit.Material type = org.bukkit.Material.getMaterial((String)args.get("type"));
      short damage = 0;
      int amount = 1;
      if (args.containsKey("damage")) {
         damage = ((Number)args.get("damage")).shortValue();
      }

      if (args.containsKey("amount")) {
         amount = ((Number)args.get("amount")).intValue();
      }

      ItemStack result = new ItemStack(type, amount, damage);
      if (args.containsKey("enchantments")) {
         Object raw = args.get("enchantments");
         if (raw instanceof Map) {
            Map<?, ?> map = (Map<?, ?>)raw;

            for (Entry<?, ?> entry : map.entrySet()) {
               Enchantment enchantment = Enchantment.getByName(entry.getKey().toString());
               if (enchantment != null && entry.getValue() instanceof Integer) {
                  result.addUnsafeEnchantment(enchantment, (Integer)entry.getValue());
               }
            }
         }
      } else if (args.containsKey("meta")) {
         Object raw = args.get("meta");
         if (raw instanceof ItemMeta) {
            result.setItemMeta((ItemMeta)raw);
         }
      }

      return result;
   }

   public ItemMeta getItemMeta() {
      return this.meta == null ? org.bukkit.Bukkit.getItemFactory().getItemMeta(this.getType0()) : this.meta.clone();
   }

   public boolean hasItemMeta() {
      return !org.bukkit.Bukkit.getItemFactory().equals(this.meta, null);
   }

   public boolean setItemMeta(ItemMeta itemMeta) {
      return this.setItemMeta0(itemMeta, this.getType0());
   }

   private boolean setItemMeta0(ItemMeta itemMeta, org.bukkit.Material material) {
      if (itemMeta == null) {
         this.meta = null;
         return true;
      } else if (!org.bukkit.Bukkit.getItemFactory().isApplicable(itemMeta, material)) {
         return false;
      } else {
         this.meta = org.bukkit.Bukkit.getItemFactory().asMetaFor(itemMeta, material);
         if (this.meta == itemMeta) {
            this.meta = itemMeta.clone();
         }

         return true;
      }
   }
}
