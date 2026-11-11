package org.bukkit.craftbukkit.v1_8_R3.inventory;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableMap.Builder;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import net.minecraft.server.v1_8_R3.IAttribute;
import net.minecraft.server.v1_8_R3.NBTBase;
import net.minecraft.server.v1_8_R3.NBTCompressedStreamTools;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;
import net.minecraft.server.v1_8_R3.NBTTagString;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.v1_8_R3.Overridden;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;
import org.spigotmc.ValidateUtils;

@DelegateDeserialization(SerializableMeta.class)
class CraftMetaItem implements ItemMeta, Repairable {
   static final CraftMetaItem.ItemMetaKey NAME = new CraftMetaItem.ItemMetaKey("Name", "display-name");
   static final CraftMetaItem.ItemMetaKey DISPLAY = new CraftMetaItem.ItemMetaKey("display");
   static final CraftMetaItem.ItemMetaKey LORE = new CraftMetaItem.ItemMetaKey("Lore", "lore");
   static final CraftMetaItem.ItemMetaKey ENCHANTMENTS = new CraftMetaItem.ItemMetaKey("ench", "enchants");
   static final CraftMetaItem.ItemMetaKey ENCHANTMENTS_ID = new CraftMetaItem.ItemMetaKey("id");
   static final CraftMetaItem.ItemMetaKey ENCHANTMENTS_LVL = new CraftMetaItem.ItemMetaKey("lvl");
   static final CraftMetaItem.ItemMetaKey REPAIR = new CraftMetaItem.ItemMetaKey("RepairCost", "repair-cost");
   static final CraftMetaItem.ItemMetaKey ATTRIBUTES = new CraftMetaItem.ItemMetaKey("AttributeModifiers");
   static final CraftMetaItem.ItemMetaKey ATTRIBUTES_IDENTIFIER = new CraftMetaItem.ItemMetaKey("AttributeName");
   static final CraftMetaItem.ItemMetaKey ATTRIBUTES_NAME = new CraftMetaItem.ItemMetaKey("Name");
   static final CraftMetaItem.ItemMetaKey ATTRIBUTES_VALUE = new CraftMetaItem.ItemMetaKey("Amount");
   static final CraftMetaItem.ItemMetaKey ATTRIBUTES_TYPE = new CraftMetaItem.ItemMetaKey("Operation");
   static final CraftMetaItem.ItemMetaKey ATTRIBUTES_UUID_HIGH = new CraftMetaItem.ItemMetaKey("UUIDMost");
   static final CraftMetaItem.ItemMetaKey ATTRIBUTES_UUID_LOW = new CraftMetaItem.ItemMetaKey("UUIDLeast");
   static final CraftMetaItem.ItemMetaKey HIDEFLAGS = new CraftMetaItem.ItemMetaKey("HideFlags", "ItemFlags");
   static final CraftMetaItem.ItemMetaKey UNBREAKABLE = new CraftMetaItem.ItemMetaKey("Unbreakable");
   private String displayName;
   private List<String> lore;
   private Map<Enchantment, Integer> enchantments;
   private int repairCost;
   private int hideFlag;
   private static final Set<String> HANDLED_TAGS = Sets.newHashSet();
   private final Map<String, NBTBase> unhandledTags = new HashMap<>();
   private final ItemMeta.Spigot spigot = new ItemMeta.Spigot() {
      private boolean unbreakable;

      @Override
      public void setUnbreakable(boolean setUnbreakable) {
         this.unbreakable = setUnbreakable;
      }

      @Override
      public boolean isUnbreakable() {
         return this.unbreakable;
      }
   };

   CraftMetaItem(CraftMetaItem meta) {
      if (meta != null) {
         this.displayName = meta.displayName;
         if (meta.hasLore()) {
            this.lore = new ArrayList<>(meta.lore);
         }

         if (meta.enchantments != null) {
            this.enchantments = new HashMap<>(meta.enchantments);
         }

         this.repairCost = meta.repairCost;
         this.hideFlag = meta.hideFlag;
         this.unhandledTags.putAll(meta.unhandledTags);
         this.spigot.setUnbreakable(meta.spigot.isUnbreakable());
      }
   }

   CraftMetaItem(NBTTagCompound tag) {
      if (tag.hasKey(DISPLAY.NBT)) {
         NBTTagCompound display = tag.getCompound(DISPLAY.NBT);
         if (display.hasKey(NAME.NBT)) {
            this.displayName = ValidateUtils.limit(display.getString(NAME.NBT), 1024);
         }

         if (display.hasKey(LORE.NBT)) {
            NBTTagList list = display.getList(LORE.NBT, 8);
            this.lore = new ArrayList<>(list.size());

            for (int index = 0; index < list.size(); index++) {
               String line = ValidateUtils.limit(list.getString(index), 1024);
               this.lore.add(line);
            }
         }
      }

      this.enchantments = buildEnchantments(tag, ENCHANTMENTS);
      if (tag.hasKey(REPAIR.NBT)) {
         this.repairCost = tag.getInt(REPAIR.NBT);
      }

      if (tag.hasKey(HIDEFLAGS.NBT)) {
         this.hideFlag = tag.getInt(HIDEFLAGS.NBT);
      }

      if (tag.get(ATTRIBUTES.NBT) instanceof NBTTagList) {
         NBTTagList save = null;
         NBTTagList nbttaglist = tag.getList(ATTRIBUTES.NBT, 10);
         TObjectDoubleHashMap<String> attributeTracker = new TObjectDoubleHashMap();
         TObjectDoubleHashMap<String> attributeTrackerX = new TObjectDoubleHashMap();
         Map<String, IAttribute> attributesByName = new HashMap<>();
         attributeTracker.put("generic.maxHealth", 20.0);
         attributesByName.put("generic.maxHealth", GenericAttributes.maxHealth);
         attributeTracker.put("generic.followRange", 32.0);
         attributesByName.put("generic.followRange", GenericAttributes.FOLLOW_RANGE);
         attributeTracker.put("generic.knockbackResistance", 0.0);
         attributesByName.put("generic.knockbackResistance", GenericAttributes.c);
         attributeTracker.put("generic.movementSpeed", 0.7);
         attributesByName.put("generic.movementSpeed", GenericAttributes.MOVEMENT_SPEED);
         attributeTracker.put("generic.attackDamage", 1.0);
         attributesByName.put("generic.attackDamage", GenericAttributes.ATTACK_DAMAGE);
         NBTTagList oldList = nbttaglist;
         nbttaglist = new NBTTagList();
         List<NBTTagCompound> op0 = new ArrayList<>();
         List<NBTTagCompound> op1 = new ArrayList<>();
         List<NBTTagCompound> op2 = new ArrayList<>();

         for (int i = 0; i < oldList.size(); i++) {
            NBTTagCompound nbttagcompound = oldList.get(i);
            if (nbttagcompound != null
               && nbttagcompound.hasKeyOfType(ATTRIBUTES_UUID_HIGH.NBT, 99)
               && nbttagcompound.hasKeyOfType(ATTRIBUTES_UUID_LOW.NBT, 99)
               && nbttagcompound.get(ATTRIBUTES_IDENTIFIER.NBT) instanceof NBTTagString
               && CraftItemFactory.KNOWN_NBT_ATTRIBUTE_NAMES.contains(nbttagcompound.getString(ATTRIBUTES_IDENTIFIER.NBT))
               && nbttagcompound.get(ATTRIBUTES_NAME.NBT) instanceof NBTTagString
               && !nbttagcompound.getString(ATTRIBUTES_NAME.NBT).isEmpty()
               && nbttagcompound.hasKeyOfType(ATTRIBUTES_VALUE.NBT, 99)
               && nbttagcompound.hasKeyOfType(ATTRIBUTES_TYPE.NBT, 99)
               && nbttagcompound.getInt(ATTRIBUTES_TYPE.NBT) >= 0
               && nbttagcompound.getInt(ATTRIBUTES_TYPE.NBT) <= 2) {
               switch (nbttagcompound.getInt(ATTRIBUTES_TYPE.NBT)) {
                  case 0:
                     op0.add(nbttagcompound);
                     break;
                  case 1:
                     op1.add(nbttagcompound);
                     break;
                  case 2:
                     op2.add(nbttagcompound);
               }
            }
         }

         for (NBTTagCompound nbtTagCompound : op0) {
            String name = nbtTagCompound.getString(ATTRIBUTES_IDENTIFIER.NBT);
            if (attributeTracker.containsKey(name)) {
               double val = attributeTracker.get(name);
               val += nbtTagCompound.getDouble(ATTRIBUTES_VALUE.NBT);
               if (val != attributesByName.get(name).a(val)) {
                  continue;
               }

               attributeTracker.put(name, val);
            }

            nbttaglist.add(nbtTagCompound);
         }

         for (String name : attributeTracker.keySet()) {
            attributeTrackerX.put(name, attributeTracker.get(name));
         }

         for (NBTTagCompound nbtTagCompound : op1) {
            String name = nbtTagCompound.getString(ATTRIBUTES_IDENTIFIER.NBT);
            if (attributeTracker.containsKey(name)) {
               double val = attributeTracker.get(name);
               double valX = attributeTrackerX.get(name);
               val += valX * nbtTagCompound.getDouble(ATTRIBUTES_VALUE.NBT);
               if (val != attributesByName.get(name).a(val)) {
                  continue;
               }

               attributeTracker.put(name, val);
            }

            nbttaglist.add(nbtTagCompound);
         }

         for (NBTTagCompound nbtTagCompound : op2) {
            String name = nbtTagCompound.getString(ATTRIBUTES_IDENTIFIER.NBT);
            if (attributeTracker.containsKey(name)) {
               double val = attributeTracker.get(name);
               val += val * nbtTagCompound.getDouble(ATTRIBUTES_VALUE.NBT);
               if (val != attributesByName.get(name).a(val)) {
                  continue;
               }

               attributeTracker.put(name, val);
            }

            nbttaglist.add(nbtTagCompound);
         }

         for (int ix = 0; ix < nbttaglist.size(); ix++) {
            if (nbttaglist.get(ix) != null) {
               NBTTagCompound nbttagcompound = nbttaglist.get(ix);
               if (nbttagcompound.hasKeyOfType(ATTRIBUTES_UUID_HIGH.NBT, 99)
                  && nbttagcompound.hasKeyOfType(ATTRIBUTES_UUID_LOW.NBT, 99)
                  && nbttagcompound.get(ATTRIBUTES_IDENTIFIER.NBT) instanceof NBTTagString
                  && CraftItemFactory.KNOWN_NBT_ATTRIBUTE_NAMES.contains(nbttagcompound.getString(ATTRIBUTES_IDENTIFIER.NBT))
                  && nbttagcompound.get(ATTRIBUTES_NAME.NBT) instanceof NBTTagString
                  && !nbttagcompound.getString(ATTRIBUTES_NAME.NBT).isEmpty()
                  && nbttagcompound.hasKeyOfType(ATTRIBUTES_VALUE.NBT, 99)
                  && nbttagcompound.hasKeyOfType(ATTRIBUTES_TYPE.NBT, 99)
                  && nbttagcompound.getInt(ATTRIBUTES_TYPE.NBT) >= 0
                  && nbttagcompound.getInt(ATTRIBUTES_TYPE.NBT) <= 2) {
                  if (save == null) {
                     save = new NBTTagList();
                  }

                  NBTTagCompound entry = new NBTTagCompound();
                  entry.set(ATTRIBUTES_UUID_HIGH.NBT, nbttagcompound.get(ATTRIBUTES_UUID_HIGH.NBT));
                  entry.set(ATTRIBUTES_UUID_LOW.NBT, nbttagcompound.get(ATTRIBUTES_UUID_LOW.NBT));
                  entry.set(ATTRIBUTES_IDENTIFIER.NBT, nbttagcompound.get(ATTRIBUTES_IDENTIFIER.NBT));
                  entry.set(ATTRIBUTES_NAME.NBT, nbttagcompound.get(ATTRIBUTES_NAME.NBT));
                  entry.set(ATTRIBUTES_VALUE.NBT, nbttagcompound.get(ATTRIBUTES_VALUE.NBT));
                  entry.set(ATTRIBUTES_TYPE.NBT, nbttagcompound.get(ATTRIBUTES_TYPE.NBT));
                  save.add(entry);
               }
            }
         }

         this.unhandledTags.put(ATTRIBUTES.NBT, save);
      }

      for (String key : tag.c()) {
         if (!getHandledTags().contains(key)) {
            this.unhandledTags.put(key, tag.get(key));
         }
      }

      if (tag.hasKey(UNBREAKABLE.NBT)) {
         this.spigot.setUnbreakable(tag.getBoolean(UNBREAKABLE.NBT));
      }
   }

   static Map<Enchantment, Integer> buildEnchantments(NBTTagCompound tag, CraftMetaItem.ItemMetaKey key) {
      if (!tag.hasKey(key.NBT)) {
         return null;
      } else {
         NBTTagList ench = tag.getList(key.NBT, 10);
         Map<Enchantment, Integer> enchantments = new HashMap<>(ench.size());

         for (int i = 0; i < ench.size(); i++) {
            int id = '\uffff' & ench.get(i).getShort(ENCHANTMENTS_ID.NBT);
            int level = '\uffff' & ench.get(i).getShort(ENCHANTMENTS_LVL.NBT);
            Enchantment e = Enchantment.getById(id);
            if (e != null) {
               enchantments.put(e, level);
            }
         }

         return enchantments;
      }
   }

   CraftMetaItem(Map<String, Object> map) {
      this.setDisplayName(SerializableMeta.getString(map, NAME.BUKKIT, true));
      Iterable<?> lore = SerializableMeta.getObject(Iterable.class, map, LORE.BUKKIT, true);
      if (lore != null) {
         safelyAdd(lore, this.lore = new ArrayList<>(), Integer.MAX_VALUE);
      }

      this.enchantments = buildEnchantments(map, ENCHANTMENTS);
      Integer repairCost = SerializableMeta.getObject(Integer.class, map, REPAIR.BUKKIT, true);
      if (repairCost != null) {
         this.setRepairCost(repairCost);
      }

      Set hideFlags = SerializableMeta.getObject(Set.class, map, HIDEFLAGS.BUKKIT, true);
      if (hideFlags != null) {
         for (Object hideFlagObject : hideFlags) {
            String hideFlagString = (String)hideFlagObject;

            try {
               ItemFlag hideFlatEnum = ItemFlag.valueOf(hideFlagString);
               this.addItemFlags(hideFlatEnum);
            } catch (IllegalArgumentException var12) {
            }
         }
      }

      Boolean unbreakable = SerializableMeta.getObject(Boolean.class, map, UNBREAKABLE.BUKKIT, true);
      if (unbreakable != null) {
         this.spigot.setUnbreakable(unbreakable);
      }

      String internal = SerializableMeta.getString(map, "internal", true);
      if (internal != null) {
         ByteArrayInputStream buf = new ByteArrayInputStream(Base64.decodeBase64(internal));

         try {
            NBTTagCompound tag = NBTCompressedStreamTools.a(buf);
            this.deserializeInternal(tag);

            for (String key : tag.c()) {
               if (!getHandledTags().contains(key)) {
                  this.unhandledTags.put(key, tag.get(key));
               }
            }
         } catch (IOException var13) {
            Logger.getLogger(CraftMetaItem.class.getName()).log(Level.SEVERE, null, var13);
         }
      }
   }

   void deserializeInternal(NBTTagCompound tag) {
   }

   static Map<Enchantment, Integer> buildEnchantments(Map<String, Object> map, CraftMetaItem.ItemMetaKey key) {
      Map<?, ?> ench = SerializableMeta.getObject(Map.class, map, key.BUKKIT, true);
      if (ench == null) {
         return null;
      } else {
         Map<Enchantment, Integer> enchantments = new HashMap<>(ench.size());

         for (Entry<?, ?> entry : ench.entrySet()) {
            Enchantment enchantment = Enchantment.getByName(entry.getKey().toString());
            if (enchantment != null && entry.getValue() instanceof Integer) {
               enchantments.put(enchantment, (Integer)entry.getValue());
            }
         }

         return enchantments;
      }
   }

   @Overridden
   void applyToItem(NBTTagCompound itemTag) {
      if (this.hasDisplayName()) {
         this.setDisplayTag(itemTag, NAME.NBT, new NBTTagString(this.displayName));
      }

      if (this.hasLore()) {
         this.setDisplayTag(itemTag, LORE.NBT, createStringList(this.lore));
      }

      if (this.hideFlag != 0) {
         itemTag.setInt(HIDEFLAGS.NBT, this.hideFlag);
      }

      applyEnchantments(this.enchantments, itemTag, ENCHANTMENTS);
      if (this.spigot.isUnbreakable()) {
         itemTag.setBoolean(UNBREAKABLE.NBT, true);
      }

      if (this.hasRepairCost()) {
         itemTag.setInt(REPAIR.NBT, this.repairCost);
      }

      for (Entry<String, NBTBase> e : this.unhandledTags.entrySet()) {
         itemTag.set(e.getKey(), e.getValue());
      }
   }

   static NBTTagList createStringList(List<String> list) {
      if (list != null && !list.isEmpty()) {
         NBTTagList tagList = new NBTTagList();

         for (String value : list) {
            tagList.add(new NBTTagString(value));
         }

         return tagList;
      } else {
         return null;
      }
   }

   static void applyEnchantments(Map<Enchantment, Integer> enchantments, NBTTagCompound tag, CraftMetaItem.ItemMetaKey key) {
      if (enchantments != null) {
         NBTTagList list = new NBTTagList();

         for (Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            NBTTagCompound subtag = new NBTTagCompound();
            subtag.setShort(ENCHANTMENTS_ID.NBT, (short)entry.getKey().getId());
            subtag.setShort(ENCHANTMENTS_LVL.NBT, entry.getValue().shortValue());
            list.add(subtag);
         }

         tag.set(key.NBT, list);
      }
   }

   void setDisplayTag(NBTTagCompound tag, String key, NBTBase value) {
      NBTTagCompound display = tag.getCompound(DISPLAY.NBT);
      if (!tag.hasKey(DISPLAY.NBT)) {
         tag.set(DISPLAY.NBT, display);
      }

      display.set(key, value);
   }

   @Overridden
   boolean applicableTo(org.bukkit.Material type) {
      return type != org.bukkit.Material.AIR;
   }

   @Overridden
   boolean isEmpty() {
      return !this.hasDisplayName()
         && !this.hasEnchants()
         && !this.hasLore()
         && !this.hasRepairCost()
         && this.unhandledTags.isEmpty()
         && this.hideFlag == 0
         && !this.spigot.isUnbreakable();
   }

   @Override
   public String getDisplayName() {
      return this.displayName;
   }

   @Override
   public final void setDisplayName(String name) {
      this.displayName = name;
   }

   @Override
   public boolean hasDisplayName() {
      return !Strings.isNullOrEmpty(this.displayName);
   }

   @Override
   public boolean hasLore() {
      return this.lore != null && !this.lore.isEmpty();
   }

   @Override
   public boolean hasRepairCost() {
      return this.repairCost > 0;
   }

   @Override
   public boolean hasEnchant(Enchantment ench) {
      return this.hasEnchants() && this.enchantments.containsKey(ench);
   }

   @Override
   public int getEnchantLevel(Enchantment ench) {
      Integer level = this.hasEnchants() ? this.enchantments.get(ench) : null;
      return level == null ? 0 : level;
   }

   @Override
   public Map<Enchantment, Integer> getEnchants() {
      return this.hasEnchants() ? ImmutableMap.copyOf(this.enchantments) : ImmutableMap.of();
   }

   @Override
   public boolean addEnchant(Enchantment ench, int level, boolean ignoreRestrictions) {
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
   public boolean removeEnchant(Enchantment ench) {
      boolean b = this.hasEnchants() && this.enchantments.remove(ench) != null;
      if (this.enchantments != null && this.enchantments.isEmpty()) {
         this.enchantments = null;
      }

      return b;
   }

   @Override
   public boolean hasEnchants() {
      return this.enchantments != null && !this.enchantments.isEmpty();
   }

   @Override
   public boolean hasConflictingEnchant(Enchantment ench) {
      return checkConflictingEnchants(this.enchantments, ench);
   }

   @Override
   public void addItemFlags(ItemFlag... hideFlags) {
      for (ItemFlag f : hideFlags) {
         this.hideFlag = this.hideFlag | this.getBitModifier(f);
      }
   }

   @Override
   public void removeItemFlags(ItemFlag... hideFlags) {
      for (ItemFlag f : hideFlags) {
         this.hideFlag = this.hideFlag & ~this.getBitModifier(f);
      }
   }

   @Override
   public Set<ItemFlag> getItemFlags() {
      Set<ItemFlag> currentFlags = EnumSet.noneOf(ItemFlag.class);

      ItemFlag[] var5;
      for (ItemFlag f : var5 = ItemFlag.values()) {
         if (this.hasItemFlag(f)) {
            currentFlags.add(f);
         }
      }

      return currentFlags;
   }

   @Override
   public boolean hasItemFlag(ItemFlag flag) {
      int bitModifier = this.getBitModifier(flag);
      return (this.hideFlag & bitModifier) == bitModifier;
   }

   private byte getBitModifier(ItemFlag hideFlag) {
      return (byte)(1 << hideFlag.ordinal());
   }

   @Override
   public List<String> getLore() {
      return this.lore == null ? null : new ArrayList<>(this.lore);
   }

   @Override
   public void setLore(List<String> lore) {
      if (lore == null) {
         this.lore = null;
      } else if (this.lore == null) {
         safelyAdd(lore, this.lore = new ArrayList<>(lore.size()), Integer.MAX_VALUE);
      } else {
         this.lore.clear();
         safelyAdd(lore, this.lore, Integer.MAX_VALUE);
      }
   }

   @Override
   public int getRepairCost() {
      return this.repairCost;
   }

   @Override
   public void setRepairCost(int cost) {
      this.repairCost = cost;
   }

   @Override
   public final boolean equals(Object object) {
      if (object == null) {
         return false;
      } else if (object == this) {
         return true;
      } else {
         return !(object instanceof CraftMetaItem) ? false : CraftItemFactory.instance().equals(this, (ItemMeta)object);
      }
   }

   @Overridden
   boolean equalsCommon(CraftMetaItem that) {
      return (this.hasDisplayName() ? that.hasDisplayName() && this.displayName.equals(that.displayName) : !that.hasDisplayName())
         && (this.hasEnchants() ? that.hasEnchants() && this.enchantments.equals(that.enchantments) : !that.hasEnchants())
         && (this.hasLore() ? that.hasLore() && this.lore.equals(that.lore) : !that.hasLore())
         && (this.hasRepairCost() ? that.hasRepairCost() && this.repairCost == that.repairCost : !that.hasRepairCost())
         && this.unhandledTags.equals(that.unhandledTags)
         && this.hideFlag == that.hideFlag
         && this.spigot.isUnbreakable() == that.spigot.isUnbreakable();
   }

   @Overridden
   boolean notUncommon(CraftMetaItem meta) {
      return true;
   }

   @Override
   public final int hashCode() {
      return this.applyHash();
   }

   @Overridden
   int applyHash() {
      int hash = 3;
      hash = 61 * hash + (this.hasDisplayName() ? this.displayName.hashCode() : 0);
      hash = 61 * hash + (this.hasLore() ? this.lore.hashCode() : 0);
      hash = 61 * hash + (this.hasEnchants() ? this.enchantments.hashCode() : 0);
      hash = 61 * hash + (this.hasRepairCost() ? this.repairCost : 0);
      hash = 61 * hash + this.unhandledTags.hashCode();
      hash = 61 * hash + this.hideFlag;
      return 61 * hash + (this.spigot.isUnbreakable() ? 1231 : 1237);
   }

   @Overridden
   public CraftMetaItem clone() {
      try {
         CraftMetaItem clone = (CraftMetaItem)super.clone();
         if (this.lore != null) {
            clone.lore = new ArrayList<>(this.lore);
         }

         if (this.enchantments != null) {
            clone.enchantments = new HashMap<>(this.enchantments);
         }

         clone.hideFlag = this.hideFlag;
         return clone;
      } catch (CloneNotSupportedException var2) {
         throw new Error(var2);
      }
   }

   @Override
   public final Map<String, Object> serialize() {
      Builder<String, Object> map = ImmutableMap.builder();
      map.put("meta-type", SerializableMeta.classMap.get(this.getClass()));
      this.serialize(map);
      return map.build();
   }

   @Overridden
   Builder<String, Object> serialize(Builder<String, Object> builder) {
      if (this.hasDisplayName()) {
         builder.put(NAME.BUKKIT, this.displayName);
      }

      if (this.hasLore()) {
         builder.put(LORE.BUKKIT, ImmutableList.copyOf(this.lore));
      }

      serializeEnchantments(this.enchantments, builder, ENCHANTMENTS);
      if (this.hasRepairCost()) {
         builder.put(REPAIR.BUKKIT, this.repairCost);
      }

      if (this.spigot.isUnbreakable()) {
         builder.put(UNBREAKABLE.BUKKIT, true);
      }

      Set<String> hideFlags = new HashSet<>();

      for (ItemFlag hideFlagEnum : this.getItemFlags()) {
         hideFlags.add(hideFlagEnum.name());
      }

      if (!hideFlags.isEmpty()) {
         builder.put(HIDEFLAGS.BUKKIT, hideFlags);
      }

      Map<String, NBTBase> internalTags = new HashMap<>(this.unhandledTags);
      this.serializeInternal(internalTags);
      if (!internalTags.isEmpty()) {
         NBTTagCompound internal = new NBTTagCompound();

         for (Entry<String, NBTBase> e : internalTags.entrySet()) {
            internal.set(e.getKey(), e.getValue());
         }

         try {
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            NBTCompressedStreamTools.a(internal, buf);
            builder.put("internal", Base64.encodeBase64String(buf.toByteArray()));
         } catch (IOException var7) {
            Logger.getLogger(CraftMetaItem.class.getName()).log(Level.SEVERE, null, var7);
         }
      }

      return builder;
   }

   void serializeInternal(Map<String, NBTBase> unhandledTags) {
   }

   static void serializeEnchantments(Map<Enchantment, Integer> enchantments, Builder<String, Object> builder, CraftMetaItem.ItemMetaKey key) {
      if (enchantments != null && !enchantments.isEmpty()) {
         Builder<String, Integer> enchants = ImmutableMap.builder();

         for (Entry<? extends Enchantment, Integer> enchant : enchantments.entrySet()) {
            enchants.put(enchant.getKey().getName(), enchant.getValue());
         }

         builder.put(key.BUKKIT, enchants.build());
      }
   }

   static void safelyAdd(Iterable<?> addFrom, Collection<String> addTo, int maxItemLength) {
      if (addFrom != null) {
         for (Object object : addFrom) {
            if (!(object instanceof String)) {
               if (object != null) {
                  throw new IllegalArgumentException(addFrom + " cannot contain non-string " + object.getClass().getName());
               }

               addTo.add("");
            } else {
               String page = object.toString();
               if (page.length() > maxItemLength) {
                  page = page.substring(0, maxItemLength);
               }

               addTo.add(page);
            }
         }
      }
   }

   static boolean checkConflictingEnchants(Map<Enchantment, Integer> enchantments, Enchantment ench) {
      if (enchantments != null && !enchantments.isEmpty()) {
         for (Enchantment enchant : enchantments.keySet()) {
            if (enchant.conflictsWith(ench)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   @Override
   public final String toString() {
      return (String)SerializableMeta.classMap.get(this.getClass()) + "_META:" + this.serialize();
   }

   public static Set<String> getHandledTags() {
      synchronized (HANDLED_TAGS) {
         if (HANDLED_TAGS.isEmpty()) {
            HANDLED_TAGS.addAll(
               Arrays.asList(
                  UNBREAKABLE.NBT,
                  DISPLAY.NBT,
                  REPAIR.NBT,
                  ENCHANTMENTS.NBT,
                  HIDEFLAGS.NBT,
                  CraftMetaMap.MAP_SCALING.NBT,
                  CraftMetaPotion.POTION_EFFECTS.NBT,
                  CraftMetaSkull.SKULL_OWNER.NBT,
                  CraftMetaSkull.SKULL_PROFILE.NBT,
                  CraftMetaBlockState.BLOCK_ENTITY_TAG.NBT,
                  CraftMetaBook.BOOK_TITLE.NBT,
                  CraftMetaBook.BOOK_AUTHOR.NBT,
                  CraftMetaBook.BOOK_PAGES.NBT,
                  CraftMetaBook.RESOLVED.NBT,
                  CraftMetaBook.GENERATION.NBT,
                  CraftMetaFirework.FIREWORKS.NBT,
                  CraftMetaEnchantedBook.STORED_ENCHANTMENTS.NBT,
                  CraftMetaCharge.EXPLOSION.NBT,
                  CraftMetaBlockState.BLOCK_ENTITY_TAG.NBT
               )
            );
         }

         return HANDLED_TAGS;
      }
   }

   @Override
   public ItemMeta.Spigot spigot() {
      return this.spigot;
   }

   static class ItemMetaKey {
      final String BUKKIT;
      final String NBT;

      ItemMetaKey(String both) {
         this(both, both);
      }

      ItemMetaKey(String nbt, String bukkit) {
         this.NBT = nbt;
         this.BUKKIT = bukkit;
      }

      @Retention(RetentionPolicy.SOURCE)
      @Target({ElementType.FIELD})
      @interface Specific {
         CraftMetaItem.ItemMetaKey.Specific.To value();

         public static enum To {
            BUKKIT,
            NBT;
         }
      }
   }
}
