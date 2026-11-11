package net.minecraft.server.v1_8_R3;

import lol.deyo.blaze.configs.BlazeConfig;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.inventory.BrewerInventory;

public class TileEntityBrewingStand extends TileEntityContainer implements IUpdatePlayerListBox, IWorldInventory {
   private static final int[] a = new int[]{3};
   private static final int[] f = new int[]{0, 1, 2};
   public int brewTime;
   public List<HumanEntity> transaction = new ArrayList<>();
   private ItemStack[] items = new ItemStack[4];
   private boolean[] i;
   private Item j;
   private String k;
   private int lastTick = MinecraftServer.currentTick;
   private int maxStack = 64;

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
   public ItemStack[] getContents() {
      return this.items;
   }

   @Override
   public String getName() {
      return this.hasCustomName() ? this.k : "container.brewing";
   }

   @Override
   public boolean hasCustomName() {
      return this.k != null && !this.k.isEmpty();
   }

   public void a(String s) {
      this.k = s;
   }

   @Override
   public int getSize() {
      return this.items.length;
   }

   @Override
   public void c() {
      if ((Boolean)BlazeConfig.tickBrewingStands.getValue()) {
         int elapsedTicks = MinecraftServer.currentTick - this.lastTick;
         this.lastTick = MinecraftServer.currentTick;
         if (this.brewTime > 0) {
            this.brewTime = this.brewTime - (int)(elapsedTicks * (Double)BlazeConfig.brewingModifier.getValue());
            if (this.brewTime <= 0) {
               this.o();
               this.update();
            } else if (!this.n()) {
               this.brewTime = 0;
               this.update();
            } else if (this.j != this.items[3].getItem()) {
               this.brewTime = 0;
               this.update();
            }
         } else if (this.n()) {
            this.brewTime = 400;
            this.j = this.items[3].getItem();
         }

         boolean[] aboolean = this.m();
         if (!Arrays.equals(aboolean, this.i)) {
            this.i = aboolean;
            IBlockData iblockdata = this.world.getType(this.getPosition());
            if (!(iblockdata.getBlock() instanceof BlockBrewingStand)) {
               return;
            }

            for (int i = 0; i < BlockBrewingStand.HAS_BOTTLE.length; i++) {
               iblockdata = iblockdata.set(BlockBrewingStand.HAS_BOTTLE[i], aboolean[i]);
            }

            this.world.setTypeAndData(this.position, iblockdata, 2);
         }
      }
   }

   private boolean n() {
      if (this.items[3] != null && this.items[3].count > 0) {
         ItemStack itemstack = this.items[3];
         if (!itemstack.getItem().l(itemstack)) {
            return false;
         } else {
            boolean flag = false;

            for (int i = 0; i < 3; i++) {
               if (this.items[i] != null && this.items[i].getItem() == Items.POTION) {
                  int j = this.items[i].getData();
                  int k = this.c(j, itemstack);
                  if (!ItemPotion.f(j) && ItemPotion.f(k)) {
                     flag = true;
                     break;
                  }

                  List<MobEffect> list = Items.POTION.e(j);
                  List<MobEffect> list1 = Items.POTION.e(k);
                  if ((j <= 0 || list != list1) && (list == null || !list.equals(list1) && list1 != null) && j != k) {
                     flag = true;
                     break;
                  }
               }
            }

            return flag;
         }
      } else {
         return false;
      }
   }

   private void o() {
      if (this.n()) {
         ItemStack itemstack = this.items[3];
         if (this.getOwner() != null) {
            BrewEvent event = new BrewEvent(
               this.world.getWorld().getBlockAt(this.position.getX(), this.position.getY(), this.position.getZ()),
               (BrewerInventory)this.getOwner().getInventory()
            );
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
               return;
            }
         }

         for (int i = 0; i < 3; i++) {
            if (this.items[i] != null && this.items[i].getItem() == Items.POTION) {
               int j = this.items[i].getData();
               int k = this.c(j, itemstack);
               List<MobEffect> list = Items.POTION.e(j);
               List<MobEffect> list1 = Items.POTION.e(k);
               if ((j <= 0 || list != list1) && (list == null || !list.equals(list1) && list1 != null)) {
                  if (j != k) {
                     this.items[i].setData(k);
                  }
               } else if (!ItemPotion.f(j) && ItemPotion.f(k)) {
                  this.items[i].setData(k);
               }
            }
         }

         if (itemstack.getItem().r()) {
            this.items[3] = new ItemStack(itemstack.getItem().q());
         } else {
            this.items[3].count--;
            if (this.items[3].count <= 0) {
               this.items[3] = null;
            }
         }
      }
   }

   private int c(int i, ItemStack itemstack) {
      return itemstack == null ? i : (itemstack.getItem().l(itemstack) ? PotionBrewer.a(i, itemstack.getItem().j(itemstack)) : i);
   }

   @Override
   public void a(NBTTagCompound nbttagcompound) {
      super.a(nbttagcompound);
      NBTTagList nbttaglist = nbttagcompound.getList("Items", 10);
      this.items = new ItemStack[this.getSize()];

      for (int i = 0; i < nbttaglist.size(); i++) {
         NBTTagCompound nbttagcompound1 = nbttaglist.get(i);
         byte b0 = nbttagcompound1.getByte("Slot");
         if (b0 >= 0 && b0 < this.items.length) {
            this.items[b0] = ItemStack.createStack(nbttagcompound1);
         }
      }

      this.brewTime = nbttagcompound.getShort("BrewTime");
      if (nbttagcompound.hasKeyOfType("CustomName", 8)) {
         this.k = nbttagcompound.getString("CustomName");
      }
   }

   @Override
   public void b(NBTTagCompound nbttagcompound) {
      super.b(nbttagcompound);
      nbttagcompound.setShort("BrewTime", (short)this.brewTime);
      NBTTagList nbttaglist = new NBTTagList();

      for (int i = 0; i < this.items.length; i++) {
         if (this.items[i] != null) {
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            nbttagcompound1.setByte("Slot", (byte)i);
            this.items[i].save(nbttagcompound1);
            nbttaglist.add(nbttagcompound1);
         }
      }

      nbttagcompound.set("Items", nbttaglist);
      if (this.hasCustomName()) {
         nbttagcompound.setString("CustomName", this.k);
      }
   }

   @Override
   public ItemStack getItem(int i) {
      return i >= 0 && i < this.items.length ? this.items[i] : null;
   }

   @Override
   public ItemStack splitStack(int i, int j) {
      if (i >= 0 && i < this.items.length) {
         ItemStack itemstack = this.items[i];
         this.items[i] = null;
         return itemstack;
      } else {
         return null;
      }
   }

   @Override
   public ItemStack splitWithoutUpdate(int i) {
      if (i >= 0 && i < this.items.length) {
         ItemStack itemstack = this.items[i];
         this.items[i] = null;
         return itemstack;
      } else {
         return null;
      }
   }

   @Override
   public void setItem(int i, ItemStack itemstack) {
      if (i >= 0 && i < this.items.length) {
         this.items[i] = itemstack;
      }
   }

   @Override
   public int getMaxStackSize() {
      return this.maxStack;
   }

   @Override
   public void setMaxStackSize(int size) {
      this.maxStack = size;
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
      return i == 3 ? itemstack.getItem().l(itemstack) : itemstack.getItem() == Items.POTION || itemstack.getItem() == Items.GLASS_BOTTLE;
   }

   public boolean[] m() {
      boolean[] aboolean = new boolean[3];

      for (int i = 0; i < 3; i++) {
         if (this.items[i] != null) {
            aboolean[i] = true;
         }
      }

      return aboolean;
   }

   @Override
   public int[] getSlotsForFace(EnumDirection enumdirection) {
      return enumdirection == EnumDirection.UP ? a : f;
   }

   @Override
   public boolean canPlaceItemThroughFace(int i, ItemStack itemstack, EnumDirection enumdirection) {
      return this.b(i, itemstack);
   }

   @Override
   public boolean canTakeItemThroughFace(int i, ItemStack itemstack, EnumDirection enumdirection) {
      return true;
   }

   @Override
   public String getContainerName() {
      return "minecraft:brewing_stand";
   }

   @Override
   public Container createContainer(PlayerInventory playerinventory, EntityHuman entityhuman) {
      return new ContainerBrewingStand(playerinventory, this);
   }

   @Override
   public int getProperty(int i) {
      return i == 0 ? this.brewTime : 0;
   }

   @Override
   public void b(int i, int j) {
      if (i == 0) {
         this.brewTime = j;
      }
   }

   @Override
   public int g() {
      return 1;
   }

   @Override
   public void l() {
      Arrays.fill(this.items, null);
   }
}
