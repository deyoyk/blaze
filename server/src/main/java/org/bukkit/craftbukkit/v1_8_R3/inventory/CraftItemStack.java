package org.bukkit.craftbukkit.v1_8_R3.inventory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.Map;
import net.minecraft.server.v1_8_R3.EnchantmentManager;
import net.minecraft.server.v1_8_R3.Item;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@DelegateDeserialization(ItemStack.class)
public final class CraftItemStack extends ItemStack {
   net.minecraft.server.v1_8_R3.ItemStack handle;

   public static net.minecraft.server.v1_8_R3.ItemStack asNMSCopy(ItemStack original) {
      if (original instanceof CraftItemStack) {
         CraftItemStack stack = (CraftItemStack)original;
         return stack.handle == null ? null : stack.handle.cloneItemStack();
      } else if (original != null && original.getTypeId() > 0) {
         Item item = CraftMagicNumbers.getItem(original.getType());
         if (item == null) {
            return null;
         } else {
            net.minecraft.server.v1_8_R3.ItemStack stack = new net.minecraft.server.v1_8_R3.ItemStack(item, original.getAmount(), original.getDurability());
            if (original.hasItemMeta()) {
               setItemMeta(stack, original.getItemMeta());
            }

            return stack;
         }
      } else {
         return null;
      }
   }

   public static net.minecraft.server.v1_8_R3.ItemStack copyNMSStack(net.minecraft.server.v1_8_R3.ItemStack original, int amount) {
      net.minecraft.server.v1_8_R3.ItemStack stack = original.cloneItemStack();
      stack.count = amount;
      return stack;
   }

   public static ItemStack asBukkitCopy(net.minecraft.server.v1_8_R3.ItemStack original) {
      if (original == null) {
         return new ItemStack(org.bukkit.Material.AIR);
      } else {
         ItemStack stack = new ItemStack(CraftMagicNumbers.getMaterial(original.getItem()), original.count, (short)original.getData());
         if (hasItemMeta(original)) {
            stack.setItemMeta(getItemMeta(original));
         }

         return stack;
      }
   }

   public static CraftItemStack asCraftMirror(net.minecraft.server.v1_8_R3.ItemStack original) {
      return new CraftItemStack(original);
   }

   public static CraftItemStack asCraftCopy(ItemStack original) {
      if (original instanceof CraftItemStack) {
         CraftItemStack stack = (CraftItemStack)original;
         return new CraftItemStack(stack.handle == null ? null : stack.handle.cloneItemStack());
      } else {
         return new CraftItemStack(original);
      }
   }

   public static CraftItemStack asNewCraftStack(Item item) {
      return asNewCraftStack(item, 1);
   }

   public static CraftItemStack asNewCraftStack(Item item, int amount) {
      return new CraftItemStack(CraftMagicNumbers.getMaterial(item), amount, (short)0, null);
   }

   private CraftItemStack(net.minecraft.server.v1_8_R3.ItemStack item) {
      this.handle = item;
   }

   private CraftItemStack(ItemStack item) {
      this(item.getTypeId(), item.getAmount(), item.getDurability(), item.hasItemMeta() ? item.getItemMeta() : null);
   }

   private CraftItemStack(org.bukkit.Material type, int amount, short durability, ItemMeta itemMeta) {
      this.setType(type);
      this.setAmount(amount);
      this.setDurability(durability);
      this.setItemMeta(itemMeta);
   }

   private CraftItemStack(int typeId, int amount, short durability, ItemMeta itemMeta) {
      this(org.bukkit.Material.getMaterial(typeId), amount, durability, itemMeta);
   }

   @Override
   public int getTypeId() {
      return this.handle != null ? CraftMagicNumbers.getId(this.handle.getItem()) : 0;
   }

   @Override
   public void setTypeId(int type) {
      if (this.getTypeId() != type) {
         if (type == 0) {
            this.handle = null;
         } else if (CraftMagicNumbers.getItem(type) == null) {
            this.handle = null;
         } else if (this.handle == null) {
            this.handle = new net.minecraft.server.v1_8_R3.ItemStack(CraftMagicNumbers.getItem(type), 1, 0);
         } else {
            this.handle.setItem(CraftMagicNumbers.getItem(type));
            if (this.hasItemMeta()) {
               setItemMeta(this.handle, getItemMeta(this.handle));
            }
         }

         this.setData(null);
      }
   }

   @Override
   public int getAmount() {
      return this.handle != null ? this.handle.count : 0;
   }

   @Override
   public void setAmount(int amount) {
      if (this.handle != null) {
         if (amount == 0) {
            this.handle = null;
         } else {
            this.handle.count = amount;
         }
      }
   }

   @Override
   public void setDurability(short durability) {
      if (this.handle != null) {
         this.handle.setData(durability);
      }
   }

   @Override
   public short getDurability() {
      return this.handle != null ? (short)this.handle.getData() : -1;
   }

   @Override
   public int getMaxStackSize() {
      return this.handle == null ? org.bukkit.Material.AIR.getMaxStackSize() : this.handle.getItem().getMaxStackSize();
   }

   @Override
   public void addUnsafeEnchantment(Enchantment ench, int level) {
      Validate.notNull(ench, "Cannot add null enchantment");
      if (makeTag(this.handle)) {
         NBTTagList list = getEnchantmentList(this.handle);
         if (list == null) {
            list = new NBTTagList();
            this.handle.getTag().set(CraftMetaItem.ENCHANTMENTS.NBT, list);
         }

         int size = list.size();

         for (int i = 0; i < size; i++) {
            NBTTagCompound tag = list.get(i);
            short id = tag.getShort(CraftMetaItem.ENCHANTMENTS_ID.NBT);
            if (id == ench.getId()) {
               tag.setShort(CraftMetaItem.ENCHANTMENTS_LVL.NBT, (short)level);
               return;
            }
         }

         NBTTagCompound tag = new NBTTagCompound();
         tag.setShort(CraftMetaItem.ENCHANTMENTS_ID.NBT, (short)ench.getId());
         tag.setShort(CraftMetaItem.ENCHANTMENTS_LVL.NBT, (short)level);
         list.add(tag);
      }
   }

   static boolean makeTag(net.minecraft.server.v1_8_R3.ItemStack item) {
      if (item == null) {
         return false;
      } else {
         if (item.getTag() == null) {
            item.setTag(new NBTTagCompound());
         }

         return true;
      }
   }

   @Override
   public boolean containsEnchantment(Enchantment ench) {
      return this.getEnchantmentLevel(ench) > 0;
   }

   @Override
   public int getEnchantmentLevel(Enchantment ench) {
      Validate.notNull(ench, "Cannot find null enchantment");
      return this.handle == null ? 0 : EnchantmentManager.getEnchantmentLevel(ench.getId(), this.handle);
   }

   @Override
   public int removeEnchantment(Enchantment ench) {
      Validate.notNull(ench, "Cannot remove null enchantment");
      NBTTagList list = getEnchantmentList(this.handle);
      if (list == null) {
         return 0;
      } else {
         int index = Integer.MIN_VALUE;
         int level = Integer.MIN_VALUE;
         int size = list.size();

         for (int i = 0; i < size; i++) {
            NBTTagCompound enchantment = list.get(i);
            int id = '\uffff' & enchantment.getShort(CraftMetaItem.ENCHANTMENTS_ID.NBT);
            if (id == ench.getId()) {
               index = i;
               level = '\uffff' & enchantment.getShort(CraftMetaItem.ENCHANTMENTS_LVL.NBT);
               break;
            }
         }

         if (index == Integer.MIN_VALUE) {
            return 0;
         } else if (size == 1) {
            this.handle.getTag().remove(CraftMetaItem.ENCHANTMENTS.NBT);
            if (this.handle.getTag().isEmpty()) {
               this.handle.setTag(null);
            }

            return level;
         } else {
            NBTTagList listCopy = new NBTTagList();

            for (int ix = 0; ix < size; ix++) {
               if (ix != index) {
                  listCopy.add(list.get(ix));
               }
            }

            this.handle.getTag().set(CraftMetaItem.ENCHANTMENTS.NBT, listCopy);
            return level;
         }
      }
   }

   @Override
   public Map<Enchantment, Integer> getEnchantments() {
      return getEnchantments(this.handle);
   }

   static Map<Enchantment, Integer> getEnchantments(net.minecraft.server.v1_8_R3.ItemStack item) {
      NBTTagList list = item != null && item.hasEnchantments() ? item.getEnchantments() : null;
      if (list != null && list.size() != 0) {
         Builder<Enchantment, Integer> result = ImmutableMap.builder();

         for (int i = 0; i < list.size(); i++) {
            int id = '\uffff' & list.get(i).getShort(CraftMetaItem.ENCHANTMENTS_ID.NBT);
            int level = '\uffff' & list.get(i).getShort(CraftMetaItem.ENCHANTMENTS_LVL.NBT);
            result.put(Enchantment.getById(id), level);
         }

         return result.build();
      } else {
         return ImmutableMap.of();
      }
   }

   static NBTTagList getEnchantmentList(net.minecraft.server.v1_8_R3.ItemStack item) {
      return item != null && item.hasEnchantments() ? item.getEnchantments() : null;
   }

   public CraftItemStack clone() {
      CraftItemStack itemStack = (CraftItemStack)super.clone();
      if (this.handle != null) {
         itemStack.handle = this.handle.cloneItemStack();
      }

      return itemStack;
   }

   @Override
   public ItemMeta getItemMeta() {
      return getItemMeta(this.handle);
   }

   public static ItemMeta getItemMeta(net.minecraft.server.v1_8_R3.ItemStack item) {
      if (!hasItemMeta(item)) {
         return CraftItemFactory.instance().getItemMeta(getType(item));
      } else {
         switch (getType(item)) {
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
               return new CraftMetaBlockState(item.getTag(), CraftMagicNumbers.getMaterial(item.getItem()));
            case LEATHER_HELMET:
            case LEATHER_CHESTPLATE:
            case LEATHER_LEGGINGS:
            case LEATHER_BOOTS:
               return new CraftMetaLeatherArmor(item.getTag());
            case MAP:
               return new CraftMetaMap(item.getTag());
            case POTION:
               return new CraftMetaPotion(item.getTag());
            case BOOK_AND_QUILL:
               return new CraftMetaBook(item.getTag());
            case WRITTEN_BOOK:
               return new CraftMetaBookSigned(item.getTag());
            case SKULL_ITEM:
               return new CraftMetaSkull(item.getTag());
            case FIREWORK:
               return new CraftMetaFirework(item.getTag());
            case FIREWORK_CHARGE:
               return new CraftMetaCharge(item.getTag());
            case ENCHANTED_BOOK:
               return new CraftMetaEnchantedBook(item.getTag());
            case BANNER:
               return new CraftMetaBanner(item.getTag());
            default:
               return new CraftMetaItem(item.getTag());
         }
      }
   }

   static org.bukkit.Material getType(net.minecraft.server.v1_8_R3.ItemStack item) {
      org.bukkit.Material material = org.bukkit.Material.getMaterial(item == null ? 0 : CraftMagicNumbers.getId(item.getItem()));
      return material == null ? org.bukkit.Material.AIR : material;
   }

   @Override
   public boolean setItemMeta(ItemMeta itemMeta) {
      return setItemMeta(this.handle, itemMeta);
   }

   public static boolean setItemMeta(net.minecraft.server.v1_8_R3.ItemStack item, ItemMeta itemMeta) {
      if (item == null) {
         return false;
      } else if (CraftItemFactory.instance().equals(itemMeta, null)) {
         item.setTag(null);
         return true;
      } else if (!CraftItemFactory.instance().isApplicable(itemMeta, getType(item))) {
         return false;
      } else {
         itemMeta = CraftItemFactory.instance().asMetaFor(itemMeta, getType(item));
         if (itemMeta == null) {
            return true;
         } else {
            NBTTagCompound tag = new NBTTagCompound();
            item.setTag(tag);
            ((CraftMetaItem)itemMeta).applyToItem(tag);
            return true;
         }
      }
   }

   @Override
   public boolean isSimilar(ItemStack stack) {
      if (stack == null) {
         return false;
      } else if (stack == this) {
         return true;
      } else if (!(stack instanceof CraftItemStack)) {
         return stack.getClass() == ItemStack.class && stack.isSimilar(this);
      } else {
         CraftItemStack that = (CraftItemStack)stack;
         if (this.handle == that.handle) {
            return true;
         } else if (this.handle != null && that.handle != null) {
            if (that.getTypeId() == this.getTypeId() && this.getDurability() == that.getDurability()) {
               return this.hasItemMeta() ? that.hasItemMeta() && this.handle.getTag().equals(that.handle.getTag()) : !that.hasItemMeta();
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   @Override
   public boolean hasItemMeta() {
      return hasItemMeta(this.handle);
   }

   static boolean hasItemMeta(net.minecraft.server.v1_8_R3.ItemStack item) {
      return item != null && item.getTag() != null && !item.getTag().isEmpty();
   }
}
