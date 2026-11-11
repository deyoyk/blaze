package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryHolder;

public class InventorySubcontainer implements IInventory {
   private String a;
   private final int b;
   public ItemStack[] items;
   private List<IInventoryListener> d;
   private boolean e;
   public List<HumanEntity> transaction = new ArrayList<>();
   protected InventoryHolder bukkitOwner;

   @Override
   public ItemStack[] getContents() {
      return this.items;
   }

   @Override
   public void onOpen(CraftHumanEntity who) {
      this.transaction.add(who);
   }

   @Override
   public void onClose(CraftHumanEntity who) {
      this.transaction.remove(who);
   }

   @Override
   public List<HumanEntity> getViewers() {
      return this.transaction;
   }

   @Override
   public void setMaxStackSize(int i) {
   }

   @Override
   public InventoryHolder getOwner() {
      return this.bukkitOwner;
   }

   public InventorySubcontainer(String s, boolean flag, int i) {
      this(s, flag, i, null);
   }

   public InventorySubcontainer(String s, boolean flag, int i, InventoryHolder owner) {
      this.bukkitOwner = owner;
      this.a = s;
      this.e = flag;
      this.b = i;
      this.items = new ItemStack[i];
   }

   public void a(IInventoryListener iinventorylistener) {
      if (this.d == null) {
         this.d = Lists.newArrayList();
      }

      this.d.add(iinventorylistener);
   }

   public void b(IInventoryListener iinventorylistener) {
      this.d.remove(iinventorylistener);
   }

   @Override
   public ItemStack getItem(int i) {
      return i >= 0 && i < this.items.length ? this.items[i] : null;
   }

   @Override
   public ItemStack splitStack(int i, int j) {
      ItemStack itemstack = this.items[i];
      if (itemstack != null) {
         if (itemstack.count <= j) {
            this.items[i] = null;
         } else {
            if (itemstack.count == 0) {
               this.items[i] = null;
            }

            itemstack = itemstack.cloneAndSubtract(j);
         }

         this.update();
         return itemstack;
      } else {
         return null;
      }
   }

   public ItemStack a(ItemStack itemstack) {
      ItemStack itemstack1 = itemstack.cloneItemStack();

      for (int i = 0; i < this.b; i++) {
         ItemStack itemstack2 = this.getItem(i);
         if (itemstack2 == null) {
            this.setItem(i, itemstack1);
            this.update();
            return null;
         }

         if (ItemStack.c(itemstack2, itemstack1)) {
            int j = Math.min(this.getMaxStackSize(), itemstack2.getMaxStackSize());
            int k = Math.min(itemstack1.count, j - itemstack2.count);
            if (k > 0) {
               itemstack2.count += k;
               itemstack1.count -= k;
               if (itemstack1.count <= 0) {
                  this.update();
                  return null;
               }
            }
         }
      }

      if (itemstack1.count != itemstack.count) {
         this.update();
      }

      return itemstack1;
   }

   @Override
   public ItemStack splitWithoutUpdate(int i) {
      ItemStack itemstack = this.items[i];
      if (itemstack != null) {
         this.items[i] = null;
         return itemstack;
      } else {
         return null;
      }
   }

   @Override
   public void setItem(int i, ItemStack itemstack) {
      this.items[i] = itemstack;
      if (itemstack != null && itemstack.count > this.getMaxStackSize()) {
         itemstack.count = this.getMaxStackSize();
      }

      this.update();
   }

   @Override
   public int getSize() {
      return this.b;
   }

   @Override
   public String getName() {
      return this.a;
   }

   @Override
   public boolean hasCustomName() {
      return this.e;
   }

   public void a(String s) {
      this.e = true;
      this.a = s;
   }

   @Override
   public IChatBaseComponent getScoreboardDisplayName() {
      return (IChatBaseComponent)(this.hasCustomName() ? new ChatComponentText(this.getName()) : new ChatMessage(this.getName()));
   }

   @Override
   public int getMaxStackSize() {
      return 64;
   }

   @Override
   public void update() {
      if (this.d != null) {
         for (IInventoryListener iInventoryListener : this.d) {
            iInventoryListener.a(this);
         }
      }
   }

   @Override
   public boolean a(EntityHuman entityhuman) {
      return true;
   }

   @Override
   public void startOpen(EntityHuman entityhuman) {
   }

   @Override
   public void closeContainer(EntityHuman entityhuman) {
   }

   @Override
   public boolean b(int i, ItemStack itemstack) {
      return true;
   }

   @Override
   public int getProperty(int i) {
      return 0;
   }

   @Override
   public void b(int i, int j) {
   }

   @Override
   public int g() {
      return 0;
   }

   @Override
   public void l() {
      Arrays.fill(this.items, null);
   }
}
