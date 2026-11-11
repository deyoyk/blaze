package org.bukkit.craftbukkit.v1_8_R3.inventory;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import net.minecraft.server.v1_8_R3.IHopper;
import net.minecraft.server.v1_8_R3.IInventory;
import net.minecraft.server.v1_8_R3.InventoryCrafting;
import net.minecraft.server.v1_8_R3.InventoryEnderChest;
import net.minecraft.server.v1_8_R3.InventoryMerchant;
import net.minecraft.server.v1_8_R3.PlayerInventory;
import net.minecraft.server.v1_8_R3.TileEntityBeacon;
import net.minecraft.server.v1_8_R3.TileEntityBrewingStand;
import net.minecraft.server.v1_8_R3.TileEntityDispenser;
import net.minecraft.server.v1_8_R3.TileEntityDropper;
import net.minecraft.server.v1_8_R3.TileEntityFurnace;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class CraftInventory implements Inventory {
   protected final IInventory inventory;

   public CraftInventory(IInventory inventory) {
      this.inventory = inventory;
   }

   public IInventory getInventory() {
      return this.inventory;
   }

   @Override
   public int getSize() {
      return this.getInventory().getSize();
   }

   @Override
   public String getName() {
      return this.getInventory().getName();
   }

   @Override
   public ItemStack getItem(int index) {
      net.minecraft.server.v1_8_R3.ItemStack item = this.getInventory().getItem(index);
      return item == null ? null : CraftItemStack.asCraftMirror(item);
   }

   @Override
   public ItemStack[] getContents() {
      ItemStack[] items = new ItemStack[this.getSize()];
      net.minecraft.server.v1_8_R3.ItemStack[] mcItems = this.getInventory().getContents();
      int size = Math.min(items.length, mcItems.length);

      for (int i = 0; i < size; i++) {
         net.minecraft.server.v1_8_R3.ItemStack mcItem = mcItems[i];
         items[i] = mcItem == null ? null : CraftItemStack.asCraftMirror(mcItem);
      }

      return items;
   }

   @Override
   public void setContents(ItemStack[] items) {
      if (this.getInventory().getContents().length < items.length) {
         throw new IllegalArgumentException("Invalid inventory size; expected " + this.getInventory().getContents().length + " or less");
      } else {
         net.minecraft.server.v1_8_R3.ItemStack[] mcItems = this.getInventory().getContents();

         for (int i = 0; i < mcItems.length; i++) {
            if (i >= items.length) {
               mcItems[i] = null;
            } else {
               mcItems[i] = CraftItemStack.asNMSCopy(items[i]);
            }
         }
      }
   }

   @Override
   public void setItem(int index, ItemStack item) {
      this.getInventory().setItem(index, item != null && item.getTypeId() != 0 ? CraftItemStack.asNMSCopy(item) : null);
   }

   @Override
   public boolean contains(int materialId) {
      ItemStack[] var5;
      for (ItemStack item : var5 = this.getContents()) {
         if (item != null && item.getTypeId() == materialId) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean contains(org.bukkit.Material material) {
      Validate.notNull(material, "Material cannot be null");
      return this.contains(material.getId());
   }

   @Override
   public boolean contains(ItemStack item) {
      if (item == null) {
         return false;
      } else {
         ItemStack[] var5;
         for (ItemStack i : var5 = this.getContents()) {
            if (item.equals(i)) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public boolean contains(int materialId, int amount) {
      if (amount <= 0) {
         return true;
      } else {
         ItemStack[] var6;
         for (ItemStack item : var6 = this.getContents()) {
            if (item != null && item.getTypeId() == materialId && (amount -= item.getAmount()) <= 0) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public boolean contains(org.bukkit.Material material, int amount) {
      Validate.notNull(material, "Material cannot be null");
      return this.contains(material.getId(), amount);
   }

   @Override
   public boolean contains(ItemStack item, int amount) {
      if (item == null) {
         return false;
      } else if (amount <= 0) {
         return true;
      } else {
         ItemStack[] var6;
         for (ItemStack i : var6 = this.getContents()) {
            if (item.equals(i)) {
               if (--amount <= 0) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   @Override
   public boolean containsAtLeast(ItemStack item, int amount) {
      if (item == null) {
         return false;
      } else if (amount <= 0) {
         return true;
      } else {
         ItemStack[] var6;
         for (ItemStack i : var6 = this.getContents()) {
            if (item.isSimilar(i) && (amount -= i.getAmount()) <= 0) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public HashMap<Integer, ItemStack> all(int materialId) {
      HashMap<Integer, ItemStack> slots = new HashMap<>();
      ItemStack[] inventory = this.getContents();

      for (int i = 0; i < inventory.length; i++) {
         ItemStack item = inventory[i];
         if (item != null && item.getTypeId() == materialId) {
            slots.put(i, item);
         }
      }

      return slots;
   }

   @Override
   public HashMap<Integer, ItemStack> all(org.bukkit.Material material) {
      Validate.notNull(material, "Material cannot be null");
      return this.all(material.getId());
   }

   @Override
   public HashMap<Integer, ItemStack> all(ItemStack item) {
      HashMap<Integer, ItemStack> slots = new HashMap<>();
      if (item != null) {
         ItemStack[] inventory = this.getContents();

         for (int i = 0; i < inventory.length; i++) {
            if (item.equals(inventory[i])) {
               slots.put(i, inventory[i]);
            }
         }
      }

      return slots;
   }

   @Override
   public int first(int materialId) {
      ItemStack[] inventory = this.getContents();

      for (int i = 0; i < inventory.length; i++) {
         ItemStack item = inventory[i];
         if (item != null && item.getTypeId() == materialId) {
            return i;
         }
      }

      return -1;
   }

   @Override
   public int first(org.bukkit.Material material) {
      Validate.notNull(material, "Material cannot be null");
      return this.first(material.getId());
   }

   @Override
   public int first(ItemStack item) {
      return this.first(item, true);
   }

   private int first(ItemStack item, boolean withAmount) {
      if (item == null) {
         return -1;
      } else {
         ItemStack[] inventory = this.getContents();

         for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] != null && (withAmount ? item.equals(inventory[i]) : item.isSimilar(inventory[i]))) {
               return i;
            }
         }

         return -1;
      }
   }

   @Override
   public int firstEmpty() {
      ItemStack[] inventory = this.getContents();

      for (int i = 0; i < inventory.length; i++) {
         if (inventory[i] == null) {
            return i;
         }
      }

      return -1;
   }

   public int firstPartial(int materialId) {
      ItemStack[] inventory = this.getContents();

      for (int i = 0; i < inventory.length; i++) {
         ItemStack item = inventory[i];
         if (item != null && item.getTypeId() == materialId && item.getAmount() < item.getMaxStackSize()) {
            return i;
         }
      }

      return -1;
   }

   public int firstPartial(org.bukkit.Material material) {
      Validate.notNull(material, "Material cannot be null");
      return this.firstPartial(material.getId());
   }

   private int firstPartial(ItemStack item) {
      ItemStack[] inventory = this.getContents();
      ItemStack filteredItem = CraftItemStack.asCraftCopy(item);
      if (item == null) {
         return -1;
      } else {
         for (int i = 0; i < inventory.length; i++) {
            ItemStack cItem = inventory[i];
            if (cItem != null && cItem.getAmount() < cItem.getMaxStackSize() && cItem.isSimilar(filteredItem)) {
               return i;
            }
         }

         return -1;
      }
   }

   @Override
   public HashMap<Integer, ItemStack> addItem(ItemStack... items) {
      Validate.noNullElements(items, "Item cannot be null");
      HashMap<Integer, ItemStack> leftover = new HashMap<>();

      for (int i = 0; i < items.length; i++) {
         ItemStack item = items[i];

         while (true) {
            int firstPartial = this.firstPartial(item);
            if (firstPartial == -1) {
               int firstFree = this.firstEmpty();
               if (firstFree == -1) {
                  leftover.put(i, item);
                  break;
               }

               if (item.getAmount() <= this.getMaxItemStack()) {
                  this.setItem(firstFree, item);
                  break;
               }

               CraftItemStack stack = CraftItemStack.asCraftCopy(item);
               stack.setAmount(this.getMaxItemStack());
               this.setItem(firstFree, stack);
               item.setAmount(item.getAmount() - this.getMaxItemStack());
            } else {
               ItemStack partialItem = this.getItem(firstPartial);
               int amount = item.getAmount();
               int partialAmount = partialItem.getAmount();
               int maxAmount = partialItem.getMaxStackSize();
               if (amount + partialAmount <= maxAmount) {
                  partialItem.setAmount(amount + partialAmount);
                  this.setItem(firstPartial, partialItem);
                  break;
               }

               partialItem.setAmount(maxAmount);
               this.setItem(firstPartial, partialItem);
               item.setAmount(amount + partialAmount - maxAmount);
            }
         }
      }

      return leftover;
   }

   @Override
   public HashMap<Integer, ItemStack> removeItem(ItemStack... items) {
      Validate.notNull(items, "Items cannot be null");
      HashMap<Integer, ItemStack> leftover = new HashMap<>();

      for (int i = 0; i < items.length; i++) {
         ItemStack item = items[i];
         int toDelete = item.getAmount();

         do {
            int first = this.first(item, false);
            if (first == -1) {
               item.setAmount(toDelete);
               leftover.put(i, item);
               break;
            }

            ItemStack itemStack = this.getItem(first);
            int amount = itemStack.getAmount();
            if (amount <= toDelete) {
               toDelete -= amount;
               this.clear(first);
            } else {
               itemStack.setAmount(amount - toDelete);
               this.setItem(first, itemStack);
               toDelete = 0;
            }
         } while (toDelete <= 0);
      }

      return leftover;
   }

   private int getMaxItemStack() {
      return this.getInventory().getMaxStackSize();
   }

   @Override
   public void remove(int materialId) {
      ItemStack[] items = this.getContents();

      for (int i = 0; i < items.length; i++) {
         if (items[i] != null && items[i].getTypeId() == materialId) {
            this.clear(i);
         }
      }
   }

   @Override
   public void remove(org.bukkit.Material material) {
      Validate.notNull(material, "Material cannot be null");
      this.remove(material.getId());
   }

   @Override
   public void remove(ItemStack item) {
      ItemStack[] items = this.getContents();

      for (int i = 0; i < items.length; i++) {
         if (items[i] != null && items[i].equals(item)) {
            this.clear(i);
         }
      }
   }

   @Override
   public void clear(int index) {
      this.setItem(index, null);
   }

   @Override
   public void clear() {
      for (int i = 0; i < this.getSize(); i++) {
         this.clear(i);
      }
   }

   @Override
   public ListIterator<ItemStack> iterator() {
      return new InventoryIterator(this);
   }

   @Override
   public ListIterator<ItemStack> iterator(int index) {
      if (index < 0) {
         index += this.getSize() + 1;
      }

      return new InventoryIterator(this, index);
   }

   @Override
   public List<HumanEntity> getViewers() {
      return this.inventory.getViewers();
   }

   @Override
   public String getTitle() {
      return this.inventory.getName();
   }

   @Override
   public InventoryType getType() {
      if (this.inventory instanceof InventoryCrafting) {
         return this.inventory.getSize() >= 9 ? InventoryType.WORKBENCH : InventoryType.CRAFTING;
      } else if (this.inventory instanceof PlayerInventory) {
         return InventoryType.PLAYER;
      } else if (this.inventory instanceof TileEntityDropper) {
         return InventoryType.DROPPER;
      } else if (this.inventory instanceof TileEntityDispenser) {
         return InventoryType.DISPENSER;
      } else if (this.inventory instanceof TileEntityFurnace) {
         return InventoryType.FURNACE;
      } else if (this instanceof CraftInventoryEnchanting) {
         return InventoryType.ENCHANTING;
      } else if (this.inventory instanceof TileEntityBrewingStand) {
         return InventoryType.BREWING;
      } else if (this.inventory instanceof CraftInventoryCustom.MinecraftInventory) {
         return ((CraftInventoryCustom.MinecraftInventory)this.inventory).getType();
      } else if (this.inventory instanceof InventoryEnderChest) {
         return InventoryType.ENDER_CHEST;
      } else if (this.inventory instanceof InventoryMerchant) {
         return InventoryType.MERCHANT;
      } else if (this.inventory instanceof TileEntityBeacon) {
         return InventoryType.BEACON;
      } else if (this instanceof CraftInventoryAnvil) {
         return InventoryType.ANVIL;
      } else {
         return this.inventory instanceof IHopper ? InventoryType.HOPPER : InventoryType.CHEST;
      }
   }

   @Override
   public InventoryHolder getHolder() {
      return this.inventory.getOwner();
   }

   @Override
   public int getMaxStackSize() {
      return this.inventory.getMaxStackSize();
   }

   @Override
   public void setMaxStackSize(int size) {
      this.inventory.setMaxStackSize(size);
   }

   @Override
   public int hashCode() {
      return this.inventory.hashCode();
   }

   @Override
   public boolean equals(Object obj) {
      return obj instanceof CraftInventory && ((CraftInventory)obj).inventory.equals(this.inventory);
   }
}
