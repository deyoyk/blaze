package net.minecraft.server.v1_8_R3;

import lol.deyo.blaze.configs.BlazeConfig;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.techcable.tacospigot.HopperHelper;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryDoubleChest;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
import org.bukkit.craftbukkit.v1_8_R3.util.LongHash;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.inventory.Inventory;

public class TileEntityHopper extends TileEntityContainer implements IHopper, IUpdatePlayerListBox {
   public List<HumanEntity> transaction = new ArrayList<>();
   private ItemStack[] items = new ItemStack[5];
   private String f;
   private int g = -1;
   private int maxStack = 64;

   private static boolean b(IInventory iinventory, EnumDirection enumdirection) {
      if (iinventory instanceof IWorldInventory) {
         IWorldInventory iworldinventory = (IWorldInventory)iinventory;

         int[] var6;
         for (int j : var6 = iworldinventory.getSlotsForFace(enumdirection)) {
            if (iworldinventory.getItem(j) != null) {
               return false;
            }
         }
      } else {
         int jx = iinventory.getSize();

         for (int k = 0; k < jx; k++) {
            if (iinventory.getItem(k) != null) {
               return false;
            }
         }
      }

      return true;
   }

   @Deprecated
   public static boolean a(IHopper ihopper) {
      IInventory iinventory;
      if (ihopper instanceof TileEntityHopper) {
         iinventory = HopperHelper.getInventory(ihopper.getWorld(), ((TileEntityHopper)ihopper).getPosition().up());
      } else {
         iinventory = b(ihopper);
      }

      return acceptItem(ihopper, iinventory);
   }

   public static boolean acceptItem(IHopper ihopper, IInventory iinventory) {
      if (iinventory != null) {
         EnumDirection enumdirection = EnumDirection.DOWN;
         if (b(iinventory, enumdirection)) {
            return false;
         }

         if (iinventory instanceof IWorldInventory) {
            int[] var6;
            for (int j : var6 = ((IWorldInventory)iinventory).getSlotsForFace(enumdirection)) {
               if (a(ihopper, iinventory, j, enumdirection)) {
                  return true;
               }
            }
         } else {
            int jx = iinventory.getSize();

            for (int k = 0; k < jx; k++) {
               if (a(ihopper, iinventory, k, enumdirection)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   private static boolean a(IHopper ihopper, IInventory iinventory, int i, EnumDirection enumdirection) {
      ItemStack itemstack = iinventory.getItem(i);
      if (itemstack != null && b(iinventory, itemstack, i, enumdirection)) {
         ItemStack itemstack1 = itemstack.cloneItemStack();
         CraftItemStack oitemstack = CraftItemStack.asCraftMirror(iinventory.splitStack(i, ihopper.getWorld().spigotConfig.hopperAmount));
         org.bukkit.inventory.ItemStack stack;
         if (HopperHelper.isFireInventoryMoveItemEvent(ihopper)) {
            Inventory sourceInventory;
            if (iinventory instanceof InventoryLargeChest) {
               sourceInventory = new CraftInventoryDoubleChest((InventoryLargeChest)iinventory);
            } else {
               sourceInventory = iinventory.getOwner().getInventory();
            }

            InventoryMoveItemEvent event = new InventoryMoveItemEvent(sourceInventory, oitemstack.clone(), ihopper.getOwner().getInventory(), false);
            ihopper.getWorld().getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
               iinventory.setItem(i, itemstack1);
               if (ihopper instanceof TileEntityHopper) {
                  ((TileEntityHopper)ihopper).d(ihopper.getWorld().spigotConfig.hopperTransfer);
               } else if (ihopper instanceof EntityMinecartHopper) {
                  ((EntityMinecartHopper)ihopper).m(ihopper.getWorld().spigotConfig.hopperTransfer / 2);
               }

               return false;
            }

            stack = event.getItem();
         } else {
            stack = oitemstack;
         }

         int origCount = stack.getAmount();
         ItemStack itemstack2 = addItem(ihopper, CraftItemStack.asNMSCopy(stack), null);
         if (itemstack2 == null || itemstack2.count == 0) {
            if (stack.equals(oitemstack)) {
               iinventory.update();
            } else {
               iinventory.setItem(i, itemstack1);
            }

            return true;
         }

         itemstack1.count = itemstack1.count - (origCount - itemstack2.count);
         iinventory.setItem(i, itemstack1);
      }

      return false;
   }

   public static boolean a(IInventory iinventory, EntityItem entityitem) {
      boolean flag = false;
      if (entityitem == null) {
         return false;
      } else {
         InventoryPickupItemEvent event = new InventoryPickupItemEvent(
            iinventory.getOwner().getInventory(), (org.bukkit.entity.Item)entityitem.getBukkitEntity()
         );
         entityitem.world.getServer().getPluginManager().callEvent(event);
         if (event.isCancelled()) {
            return false;
         } else {
            ItemStack itemstack1 = addItem(iinventory, entityitem.getItemStack().cloneItemStack(), null);
            if (itemstack1 != null && itemstack1.count != 0) {
               entityitem.setItemStack(itemstack1);
            } else {
               flag = true;
               entityitem.die();
            }

            return flag;
         }
      }
   }

   public static ItemStack addItem(IInventory iinventory, ItemStack itemstack, EnumDirection enumdirection) {
      if (iinventory instanceof IWorldInventory && enumdirection != null) {
         IWorldInventory iworldinventory = (IWorldInventory)iinventory;
         int[] aint = iworldinventory.getSlotsForFace(enumdirection);

         for (int i = 0; i < aint.length && itemstack != null && itemstack.count > 0; i++) {
            itemstack = c(iinventory, itemstack, aint[i], enumdirection);
         }
      } else {
         int j = iinventory.getSize();

         for (int k = 0; k < j && itemstack != null && itemstack.count > 0; k++) {
            itemstack = c(iinventory, itemstack, k, enumdirection);
         }
      }

      if (itemstack != null && itemstack.count == 0) {
         itemstack = null;
      }

      return itemstack;
   }

   private static boolean a(IInventory iinventory, ItemStack itemstack, int i, EnumDirection enumdirection) {
      return iinventory.b(i, itemstack)
         && (!(iinventory instanceof IWorldInventory) || ((IWorldInventory)iinventory).canPlaceItemThroughFace(i, itemstack, enumdirection));
   }

   private static boolean b(IInventory iinventory, ItemStack itemstack, int i, EnumDirection enumdirection) {
      return !(iinventory instanceof IWorldInventory) || ((IWorldInventory)iinventory).canTakeItemThroughFace(i, itemstack, enumdirection);
   }

   private static ItemStack c(IInventory iinventory, ItemStack itemstack, int i, EnumDirection enumdirection) {
      ItemStack itemstack1 = iinventory.getItem(i);
      if (a(iinventory, itemstack, i, enumdirection)) {
         boolean flag = false;
         if (itemstack1 == null) {
            iinventory.setItem(i, itemstack);
            itemstack = null;
            flag = true;
         } else if (a(itemstack1, itemstack)) {
            int k = Math.min(itemstack.count, itemstack.getMaxStackSize() - itemstack1.count);
            itemstack.count -= k;
            itemstack1.count += k;
            flag = k > 0;
         }

         if (flag) {
            if (iinventory instanceof TileEntityHopper) {
               TileEntityHopper tileentityhopper = (TileEntityHopper)iinventory;
               if (tileentityhopper.o()) {
                  tileentityhopper.d(tileentityhopper.world.spigotConfig.hopperTransfer);
               }

               iinventory.update();
            }

            iinventory.update();
         }
      }

      return itemstack;
   }

   public static IInventory b(IHopper ihopper) {
      return b(ihopper.getWorld(), ihopper.A(), ihopper.B() + 1.0, ihopper.C());
   }

   public static List<EntityItem> a(World world, double d0, double d1, double d2) {
      return world.a(EntityItem.class, new AxisAlignedBB(d0 - 0.5, d1 - 0.5, d2 - 0.5, d0 + 0.5, d1 + 0.5, d2 + 0.5), IEntitySelector.a);
   }

   public static IInventory b(World world, double d0, double d1, double d2) {
      Object object = null;
      int i = MathHelper.floor(d0);
      int j = MathHelper.floor(d1);
      int k = MathHelper.floor(d2);
      if (!world.isValidLocation(i, j, k)) {
         return null;
      } else {
         long hash = LongHash.toLong(i >> 4, k >> 4);
         Chunk chunk = world.getChunkIfLoaded(hash);
         if (chunk != null && !((ChunkProviderServer)world.chunkProvider).unloadQueue.contains(hash)) {
            Block block = world.getType(chunk, i, j, k, true).getBlock();
            BlockPosition blockposition = new BlockPosition(i, j, k);
            if (block.isTileEntity()) {
               TileEntity tileentity = world.getTileEntity(chunk, blockposition, block);
               if (tileentity instanceof IInventory) {
                  object = tileentity;
                  if (tileentity instanceof TileEntityChest && block instanceof BlockChest) {
                     object = ((BlockChest)block).f(world, blockposition);
                  }
               }
            }

            if (object == null && !CraftMagicNumbers.getMaterial(block).isOccluding() && chunk.getItemCount(blockposition) > 0) {
               List<Entity> list = world.a((Entity)null, new AxisAlignedBB(d0 - 0.5, d1 - 0.5, d2 - 0.5, d0 + 0.5, d1 + 0.5, d2 + 0.5), IEntitySelector.c);
               if (!list.isEmpty()) {
                  object = list.get(world.random.nextInt(list.size()));
               }
            }

            return (IInventory)object;
         } else {
            return null;
         }
      }
   }

   private static boolean a(ItemStack itemstack, ItemStack itemstack1) {
      return itemstack.getItem() == itemstack1.getItem()
         && itemstack.getData() == itemstack1.getData()
         && itemstack.count <= itemstack.getMaxStackSize()
         && ItemStack.equals(itemstack, itemstack1);
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
   public void a(NBTTagCompound nbttagcompound) {
      super.a(nbttagcompound);
      NBTTagList nbttaglist = nbttagcompound.getList("Items", 10);
      this.items = new ItemStack[this.getSize()];
      if (nbttagcompound.hasKeyOfType("CustomName", 8)) {
         this.f = nbttagcompound.getString("CustomName");
      }

      this.g = nbttagcompound.getInt("TransferCooldown");

      for (int i = 0; i < nbttaglist.size(); i++) {
         NBTTagCompound nbttagcompound1 = nbttaglist.get(i);
         byte b0 = nbttagcompound1.getByte("Slot");
         if (b0 >= 0 && b0 < this.items.length) {
            this.items[b0] = ItemStack.createStack(nbttagcompound1);
         }
      }
   }

   @Override
   public void b(NBTTagCompound nbttagcompound) {
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
      nbttagcompound.setInt("TransferCooldown", this.g);
      if (this.hasCustomName()) {
         nbttagcompound.setString("CustomName", this.f);
      }
   }

   @Override
   public int getSize() {
      return this.items.length;
   }

   @Override
   public ItemStack getItem(int i) {
      return this.items[i];
   }

   @Override
   public ItemStack splitStack(int i, int j) {
      if (this.items[i] != null) {
         ItemStack itemstack = this.items[i];
         if (itemstack.count <= j) {
            this.items[i] = null;
         } else {
            if (itemstack.count == 0) {
               this.items[i] = null;
            }

            itemstack = itemstack.cloneAndSubtract(j);
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
   public String getName() {
      return this.hasCustomName() ? this.f : "container.hopper";
   }

   @Override
   public boolean hasCustomName() {
      return this.f != null && !this.f.isEmpty();
   }

   public void a(String s) {
      this.f = s;
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
      return true;
   }

   @Override
   public void c() {
      if ((Boolean)BlazeConfig.tickHoppers.getValue() && this.world != null) {
         this.g--;
         if (!this.n()) {
            this.d(0);
            this.m();
         }
      }
   }

   public boolean m() {
      if (this.world != null && !this.n() && BlockHopper.f(this.u())) {
         boolean flag = false;
         if (!this.p()) {
            flag = this.r();
         }

         if (!this.q()) {
            flag = a(this) || flag;
         }

         if (flag) {
            this.d(this.world.spigotConfig.hopperTransfer);
            this.update();
            return true;
         }
      }

      return false;
   }

   private boolean p() {
      for (ItemStack itemstack : this.items) {
         if (itemstack != null) {
            return false;
         }
      }

      return true;
   }

   public boolean canAcceptItems() {
      return !this.n() && !this.q() && BlockHopper.f(this.u());
   }

   private boolean q() {
      for (ItemStack itemstack : this.items) {
         if (itemstack == null || itemstack.count != itemstack.getMaxStackSize()) {
            return false;
         }
      }

      return true;
   }

   private boolean r() {
      EnumDirection enumdirection = BlockHopper.b(this.u());
      IInventory iinventory = HopperHelper.getInventory(this.getWorld(), this.getPosition().shift(enumdirection));
      if (iinventory != null) {
         EnumDirection enumdirection1 = enumdirection.opposite();
         if (!this.a(iinventory, enumdirection1)) {
            for (int i = 0; i < this.getSize(); i++) {
               ItemStack item = this.getItem(i);
               if (item != null) {
                  ItemStack itemstack = item.cloneItemStack();
                  CraftItemStack oitemstack = CraftItemStack.asCraftMirror(this.splitStack(i, this.world.spigotConfig.hopperAmount));
                  org.bukkit.inventory.ItemStack stack;
                  if (HopperHelper.isFireInventoryMoveItemEvent(this)) {
                     Inventory destinationInventory;
                     if (iinventory instanceof InventoryLargeChest) {
                        destinationInventory = new CraftInventoryDoubleChest((InventoryLargeChest)iinventory);
                     } else {
                        destinationInventory = iinventory.getOwner().getInventory();
                     }

                     InventoryMoveItemEvent event = new InventoryMoveItemEvent(this.getOwner().getInventory(), oitemstack.clone(), destinationInventory, true);
                     this.getWorld().getServer().getPluginManager().callEvent(event);
                     if (event.isCancelled()) {
                        this.setItem(i, itemstack);
                        this.d(this.world.spigotConfig.hopperTransfer);
                        return false;
                     }

                     stack = event.getItem();
                  } else {
                     stack = oitemstack;
                  }

                  int origCount = stack.getAmount();
                  ItemStack itemstack1 = addItem(iinventory, CraftItemStack.asNMSCopy(stack), enumdirection1);
                  if (itemstack1 == null || itemstack1.count == 0) {
                     if (stack.equals(oitemstack)) {
                        iinventory.update();
                     } else {
                        this.setItem(i, itemstack);
                     }

                     return true;
                  }

                  itemstack.count = itemstack.count - (origCount - itemstack1.count);
                  this.setItem(i, itemstack);
               }
            }
         }
      }

      return false;
   }

   private boolean a(IInventory iinventory, EnumDirection enumdirection) {
      if (iinventory instanceof IWorldInventory) {
         IWorldInventory iworldinventory = (IWorldInventory)iinventory;

         int[] var7;
         for (int j : var7 = iworldinventory.getSlotsForFace(enumdirection)) {
            ItemStack itemstack = iworldinventory.getItem(j);
            if (itemstack == null || itemstack.count != itemstack.getMaxStackSize()) {
               return false;
            }
         }
      } else {
         int jx = iinventory.getSize();

         for (int k = 0; k < jx; k++) {
            ItemStack itemstack1 = iinventory.getItem(k);
            if (itemstack1 == null || itemstack1.count != itemstack1.getMaxStackSize()) {
               return false;
            }
         }
      }

      return true;
   }

   @Override
   public double A() {
      return this.position.getX() + 0.5;
   }

   @Override
   public double B() {
      return this.position.getY() + 0.5;
   }

   @Override
   public double C() {
      return this.position.getZ() + 0.5;
   }

   public void d(int i) {
      this.g = i;
   }

   public boolean n() {
      return this.g > 0;
   }

   public boolean o() {
      return this.g <= 1;
   }

   @Override
   public String getContainerName() {
      return "minecraft:hopper";
   }

   @Override
   public Container createContainer(PlayerInventory playerinventory, EntityHuman entityhuman) {
      return new ContainerHopper(playerinventory, this, entityhuman);
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
