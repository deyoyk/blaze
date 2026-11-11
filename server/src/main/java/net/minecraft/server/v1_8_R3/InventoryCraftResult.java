package net.minecraft.server.v1_8_R3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryHolder;

public class InventoryCraftResult implements IInventory {
   private final ItemStack[] items = new ItemStack[1];
   private int maxStack = 64;

   @Override
   public ItemStack[] getContents() {
      return this.items;
   }

   @Override
   public InventoryHolder getOwner() {
      return null;
   }

   @Override
   public void onOpen(CraftHumanEntity who) {
   }

   @Override
   public void onClose(CraftHumanEntity who) {
   }

   @Override
   public List<HumanEntity> getViewers() {
      return new ArrayList<>();
   }

   @Override
   public void setMaxStackSize(int size) {
      this.maxStack = size;
   }

   @Override
   public int getSize() {
      return 1;
   }

   @Override
   public ItemStack getItem(int i) {
      return this.items[0];
   }

   @Override
   public String getName() {
      return "Result";
   }

   @Override
   public boolean hasCustomName() {
      return false;
   }

   @Override
   public IChatBaseComponent getScoreboardDisplayName() {
      return (IChatBaseComponent)(this.hasCustomName() ? new ChatComponentText(this.getName()) : new ChatMessage(this.getName()));
   }

   @Override
   public ItemStack splitStack(int i, int j) {
      ItemStack itemstack = this.items[0];
      if (itemstack != null) {
         this.items[0] = null;
         return itemstack;
      } else {
         return null;
      }
   }

   @Override
   public ItemStack splitWithoutUpdate(int i) {
      ItemStack itemstack = this.items[0];
      if (itemstack != null) {
         this.items[0] = null;
         return itemstack;
      } else {
         return null;
      }
   }

   @Override
   public void setItem(int i, ItemStack itemstack) {
      this.items[0] = itemstack;
   }

   @Override
   public int getMaxStackSize() {
      return this.maxStack;
   }

   @Override
   public void update() {
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
