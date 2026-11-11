package net.minecraft.server.v1_8_R3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.techcable.tacospigot.HopperPusher;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryHolder;

public abstract class EntityMinecartContainer extends EntityMinecartAbstract implements ITileInventory, HopperPusher {
   private ItemStack[] items = new ItemStack[27];
   private boolean b = true;
   public List<HumanEntity> transaction = new ArrayList<>();
   private int maxStack = 64;

   public boolean acceptItem(TileEntityHopper hopper) {
      return TileEntityHopper.acceptItem(hopper, this);
   }

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
   public InventoryHolder getOwner() {
      org.bukkit.entity.Entity cart = this.getBukkitEntity();
      return cart instanceof InventoryHolder ? (InventoryHolder)cart : null;
   }

   @Override
   public void setMaxStackSize(int size) {
      this.maxStack = size;
   }

   public EntityMinecartContainer(World world) {
      super(world);
   }

   public EntityMinecartContainer(World world, double d0, double d1, double d2) {
      super(world, d0, d1, d2);
   }

   @Override
   public void a(DamageSource damagesource) {
      super.a(damagesource);
      if (this.world.getGameRules().getBoolean("doEntityDrops")) {
         InventoryUtils.dropEntity(this.world, this, this);
      }
   }

   @Override
   public void t_() {
      super.t_();
      if (this.ticksLived % 6 == 0) {
         this.tryPutInHopper();
      }
   }

   @Override
   public void inactiveTick() {
      super.inactiveTick();
      if (this.ticksLived % 6 == 0) {
         this.tryPutInHopper();
      }
   }

   @Override
   public ItemStack getItem(int i) {
      return this.items[i];
   }

   @Override
   public ItemStack splitStack(int i, int j) {
      if (this.items[i] != null) {
         ItemStack itemstack;
         if (this.items[i].count <= j) {
            itemstack = this.items[i];
            this.items[i] = null;
         } else {
            itemstack = this.items[i].cloneAndSubtract(j);
            if (this.items[i].count == 0) {
               this.items[i] = null;
            }
         }

         return itemstack;
      } else {
         return null;
      }
   }

   @Override
   public ItemStack splitWithoutUpdate(int i) {
      if (this.items[i] != null) {
         ItemStack itemstack = this.items[i];
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
   }

   @Override
   public void update() {
   }

   @Override
   public boolean a(EntityHuman entityhuman) {
      return !this.dead && entityhuman.h(this) <= 64.0;
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
   public String getName() {
      return this.hasCustomName() ? this.getCustomName() : "container.minecart";
   }

   @Override
   public int getMaxStackSize() {
      return this.maxStack;
   }

   @Override
   public void c(int i) {
      for (HumanEntity human : new ArrayList<>(this.transaction)) {
         human.closeInventory();
      }

      this.b = false;
      super.c(i);
   }

   @Override
   public void die() {
      if (this.b) {
         InventoryUtils.dropEntity(this.world, this, this);
      }

      super.die();
   }

   @Override
   protected void b(NBTTagCompound nbttagcompound) {
      super.b(nbttagcompound);
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
   }

   @Override
   protected void a(NBTTagCompound nbttagcompound) {
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
   }

   @Override
   public boolean e(EntityHuman entityhuman) {
      entityhuman.openContainer(this);
      return true;
   }

   @Override
   protected void o() {
      int i = 15 - Container.b(this);
      float f = 0.98F + i * 0.001F;
      this.motX *= f;
      this.motY *= 0.0;
      this.motZ *= f;
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
   public boolean r_() {
      return false;
   }

   @Override
   public void a(ChestLock chestlock) {
   }

   @Override
   public ChestLock i() {
      return ChestLock.a;
   }

   @Override
   public void l() {
      Arrays.fill(this.items, null);
   }
}
