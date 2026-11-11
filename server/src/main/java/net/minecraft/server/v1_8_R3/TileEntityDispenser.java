package net.minecraft.server.v1_8_R3;

import lol.deyo.blaze.configs.BlazeConfig;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;

public class TileEntityDispenser extends TileEntityContainer implements IInventory {
   private static final Random f = new Random();
   private ItemStack[] items = new ItemStack[9];
   protected String a;
   public List<HumanEntity> transaction = new ArrayList<>();
   private int maxStack = 64;

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
   public void setMaxStackSize(int size) {
      this.maxStack = size;
   }

   @Override
   public int getSize() {
      return 9;
   }

   @Override
   public ItemStack getItem(int i) {
      return this.items[i];
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

   public int m() {
      if ((Boolean)BlazeConfig.dispenserRandomness.getValue()) {
         int i = -1;
         int j = 1;

         for (int k = 0; k < this.items.length; k++) {
            ItemStack itemstack = this.items[k];
            if (itemstack != null && f.nextInt(j++) == 0 && itemstack.count != 0) {
               i = k;
            }
         }

         return i;
      } else {
         for (int kx = 0; kx < this.items.length; kx++) {
            if (this.items[kx] != null) {
               return kx;
            }
         }

         return -1;
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

   public int addItem(ItemStack itemstack) {
      for (int i = 0; i < this.items.length; i++) {
         ItemStack itemstack1 = this.items[i];
         if (itemstack1 == null || itemstack1.getItem() == null) {
            this.setItem(i, itemstack);
            return i;
         }
      }

      return -1;
   }

   @Override
   public String getName() {
      return this.hasCustomName() ? this.a : "container.dispenser";
   }

   public void a(String s) {
      this.a = s;
   }

   @Override
   public boolean hasCustomName() {
      return this.a != null;
   }

   @Override
   public void a(NBTTagCompound nbttagcompound) {
      super.a(nbttagcompound);
      NBTTagList nbttaglist = nbttagcompound.getList("Items", 10);
      this.items = new ItemStack[this.getSize()];

      for (int i = 0; i < nbttaglist.size(); i++) {
         NBTTagCompound nbttagcompound1 = nbttaglist.get(i);
         int j = nbttagcompound1.getByte("Slot") & 255;
         if (j < this.items.length) {
            this.items[j] = ItemStack.createStack(nbttagcompound1);
         }
      }

      if (nbttagcompound.hasKeyOfType("CustomName", 8)) {
         this.a = nbttagcompound.getString("CustomName");
      }
   }

   @Override
   public void b(NBTTagCompound nbttagcompound) {
      super.b(nbttagcompound);
      NBTTagList nbttaglist = new NBTTagList();

      for (int i = 0; i < this.items.length; i++) {
         ItemStack itemstack = this.items[i];
         if (itemstack != null) {
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            nbttagcompound1.setByte("Slot", (byte)i);
            itemstack.save(nbttagcompound1);
            nbttaglist.add(nbttagcompound1);
         }
      }

      nbttagcompound.set("Items", nbttaglist);
      if (this.hasCustomName()) {
         nbttagcompound.setString("CustomName", this.a);
      }
   }

   @Override
   public int getMaxStackSize() {
      return this.maxStack;
   }

   @Override
   public boolean a(EntityHuman entityhuman) {
      return this.world.getTileEntity(this.position) == this
         && entityhuman.e(this.position.getX() + 0.5, this.position.getY() + 0.5, this.position.getZ() + 0.5) <= 64.0;
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
   public String getContainerName() {
      return "minecraft:dispenser";
   }

   @Override
   public Container createContainer(PlayerInventory playerinventory, EntityHuman entityhuman) {
      return new ContainerDispenser(playerinventory, this);
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
