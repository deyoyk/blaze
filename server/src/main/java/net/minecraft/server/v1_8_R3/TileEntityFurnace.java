package net.minecraft.server.v1_8_R3;

import lol.deyo.blaze.configs.BlazeConfig;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_8_R3.util.LongHash;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;

public class TileEntityFurnace extends TileEntityContainer implements IUpdatePlayerListBox, IWorldInventory {
   private static final int[] a = new int[1];
   private static final int[] f = new int[]{2, 1};
   private static final int[] g = new int[]{1};
   public int burnTime;
   public int cookTime;
   public List<HumanEntity> transaction = new ArrayList<>();
   private ItemStack[] items = new ItemStack[3];
   private int ticksForCurrentFuel;
   private int cookTimeTotal;
   private String m;
   private int lastTick = MinecraftServer.currentTick;
   private int maxStack = 64;

   public static int fuelTime(ItemStack itemstack) {
      if (itemstack == null) {
         return 0;
      } else {
         Item item = itemstack.getItem();
         if (item instanceof ItemBlock && Block.asBlock(item) != Blocks.AIR) {
            Block block = Block.asBlock(item);
            if (block == Blocks.WOODEN_SLAB) {
               return 150;
            }

            if (block.getMaterial() == Material.WOOD) {
               return 300;
            }

            if (block == Blocks.COAL_BLOCK) {
               return 16000;
            }
         }

         if (item == Items.COAL) {
            return 1600;
         } else {
            return item instanceof ItemTool && ((ItemTool)item).h().equals("WOOD")
               ? 200
               : (
                  item instanceof ItemSword && ((ItemSword)item).h().equals("WOOD")
                     ? 200
                     : (
                        item instanceof ItemHoe && ((ItemHoe)item).g().equals("WOOD")
                           ? 200
                           : (
                              item == Items.STICK
                                 ? 100
                                 : (item == Items.LAVA_BUCKET ? 20000 : (item == Item.getItemOf(Blocks.SAPLING) ? 100 : (item == Items.BLAZE_ROD ? 2400 : 0)))
                           )
                     )
               );
         }
      }
   }

   public static boolean isFuel(ItemStack itemstack) {
      return fuelTime(itemstack) > 0;
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
   public int getSize() {
      return this.items.length;
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

   @Override
   public void setItem(int i, ItemStack itemstack) {
      ItemStack itemstack1 = this.items[i];
      boolean flag = itemstack != null && itemstack.doMaterialsMatch(itemstack1) && ItemStack.equals(itemstack, itemstack1);
      this.items[i] = itemstack;
      if (itemstack != null && itemstack.count > this.getMaxStackSize()) {
         itemstack.count = this.getMaxStackSize();
      }

      if (i == 0 && !flag) {
         this.cookTimeTotal = this.a(itemstack);
         this.cookTime = this.cookTime - this.cookTimeTotal;
         this.update();
      }
   }

   @Override
   public String getName() {
      return this.hasCustomName() ? this.m : "container.furnace";
   }

   @Override
   public boolean hasCustomName() {
      return this.m != null && !this.m.isEmpty();
   }

   public void a(String s) {
      this.m = s;
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

      this.burnTime = nbttagcompound.getShort("BurnTime");
      this.cookTime = nbttagcompound.getShort("CookTime");
      this.cookTimeTotal = nbttagcompound.getShort("CookTimeTotal");
      this.ticksForCurrentFuel = fuelTime(this.items[1]);
      if (nbttagcompound.hasKeyOfType("CustomName", 8)) {
         this.m = nbttagcompound.getString("CustomName");
      }
   }

   @Override
   public void b(NBTTagCompound nbttagcompound) {
      super.b(nbttagcompound);
      nbttagcompound.setShort("BurnTime", (short)this.burnTime);
      nbttagcompound.setShort("CookTime", (short)this.cookTime);
      nbttagcompound.setShort("CookTimeTotal", (short)this.cookTimeTotal);
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
         nbttagcompound.setString("CustomName", this.m);
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

   public boolean isBurning() {
      return this.burnTime > 0;
   }

   @Override
   public void c() {
      if ((Boolean)BlazeConfig.tickFurnaces.getValue()) {
         boolean flag = this.w() == Blocks.LIT_FURNACE;
         boolean flag1 = false;
         int elapsedTicks = MinecraftServer.currentTick - this.lastTick;
         this.lastTick = MinecraftServer.currentTick;
         if (this.isBurning() && this.canBurn()) {
            this.cookTime = this.cookTime + (int)(elapsedTicks * (Double)BlazeConfig.furnaceModifier.getValue());
            if (this.cookTime >= this.cookTimeTotal) {
               this.cookTime = 0;
               this.cookTimeTotal = this.a(this.items[0]);
               this.burn();
               flag1 = true;
            }
         } else {
            this.cookTime = 0;
         }

         if (this.isBurning()) {
            this.burnTime -= elapsedTicks;
         }

         ItemStack itemstack1 = this.items[1];
         if (!this.isBurning() && (itemstack1 == null || this.items[0] == null)) {
            if (!this.isBurning() && this.cookTime > 0) {
               this.cookTime = MathHelper.clamp(this.cookTime - 2, 0, this.cookTimeTotal);
            }
         } else if (itemstack1 != null && itemstack1.getItem() != Items.BUCKET && this.burnTime <= 0 && this.canBurn()) {
            FurnaceBurnEvent furnaceBurnEvent = new FurnaceBurnEvent(
               this.world.getWorld().getBlockAt(this.position.getX(), this.position.getY(), this.position.getZ()),
               CraftItemStack.asCraftMirror(itemstack1),
               fuelTime(itemstack1)
            );
            this.world.getServer().getPluginManager().callEvent(furnaceBurnEvent);
            if (furnaceBurnEvent.isCancelled()) {
               return;
            }

            this.ticksForCurrentFuel = furnaceBurnEvent.getBurnTime();
            this.burnTime = this.burnTime + this.ticksForCurrentFuel;
            if (this.burnTime > 0 && furnaceBurnEvent.isBurning()) {
               flag1 = true;
               itemstack1 = this.items[1];
               if (itemstack1 != null) {
                  itemstack1.count--;
                  if (itemstack1.count == 0) {
                     Item item = itemstack1.getItem().q();
                     this.items[1] = item != null ? new ItemStack(item) : null;
                  }
               }
            }
         }

         if (flag != this.isBurning()) {
            flag1 = true;
            BlockFurnace.a(this.isBurning(), this.world, this.position);
            this.E();
         }

         if (flag1) {
            this.update();
         }
      }
   }

   public int a(ItemStack itemstack) {
      return 200;
   }

   private boolean canBurn() {
      ItemStack firstItem = this.items[0];
      if (firstItem != null
         && this.world.isLoaded(this.position)
         && !((WorldServer)this.world).chunkProviderServer.unloadQueue.contains(LongHash.toLong(this.position.getX() >> 4, this.position.getZ() >> 4))) {
         ItemStack result = RecipesFurnace.getInstance().getResult(firstItem);
         ItemStack secondItem = this.items[2];
         return result != null
            && (
               secondItem == null
                  || secondItem.doMaterialsMatch(result)
                     && (
                        secondItem.count + result.count <= this.getMaxStackSize() && secondItem.count < secondItem.getMaxStackSize()
                           || secondItem.count + result.count <= result.getMaxStackSize()
                     )
            );
      } else {
         return false;
      }
   }

   public void burn() {
      if (this.canBurn()) {
         ItemStack itemstack = RecipesFurnace.getInstance().getResult(this.items[0]);
         CraftItemStack source = CraftItemStack.asCraftMirror(this.items[0]);
         org.bukkit.inventory.ItemStack result = CraftItemStack.asBukkitCopy(itemstack);
         FurnaceSmeltEvent furnaceSmeltEvent = new FurnaceSmeltEvent(
            this.world.getWorld().getBlockAt(this.position.getX(), this.position.getY(), this.position.getZ()), source, result
         );
         this.world.getServer().getPluginManager().callEvent(furnaceSmeltEvent);
         if (furnaceSmeltEvent.isCancelled()) {
            return;
         }

         result = furnaceSmeltEvent.getResult();
         itemstack = CraftItemStack.asNMSCopy(result);
         if (itemstack != null) {
            if (this.items[2] == null) {
               this.items[2] = itemstack;
            } else {
               if (!CraftItemStack.asCraftMirror(this.items[2]).isSimilar(result)) {
                  return;
               }

               this.items[2].count = this.items[2].count + itemstack.count;
            }
         }

         if (this.items[0].getItem() == Item.getItemOf(Blocks.SPONGE)
            && this.items[0].getData() == 1
            && this.items[1] != null
            && this.items[1].getItem() == Items.BUCKET) {
            this.items[1] = new ItemStack(Items.WATER_BUCKET);
         }

         this.items[0].count--;
         if (this.items[0].count <= 0) {
            this.items[0] = null;
         }
      }
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
      return i != 2 && (i != 1 || isFuel(itemstack) || SlotFurnaceFuel.c_(itemstack));
   }

   @Override
   public int[] getSlotsForFace(EnumDirection enumdirection) {
      return enumdirection == EnumDirection.DOWN ? f : (enumdirection == EnumDirection.UP ? a : g);
   }

   @Override
   public boolean canPlaceItemThroughFace(int i, ItemStack itemstack, EnumDirection enumdirection) {
      return this.b(i, itemstack);
   }

   @Override
   public boolean canTakeItemThroughFace(int i, ItemStack itemstack, EnumDirection enumdirection) {
      if (enumdirection == EnumDirection.DOWN && i == 1) {
         Item item = itemstack.getItem();
         return item == Items.WATER_BUCKET || item == Items.BUCKET;
      } else {
         return true;
      }
   }

   @Override
   public String getContainerName() {
      return "minecraft:furnace";
   }

   @Override
   public Container createContainer(PlayerInventory playerinventory, EntityHuman entityhuman) {
      return new ContainerFurnace(playerinventory, this);
   }

   @Override
   public int getProperty(int i) {
      switch (i) {
         case 0:
            return this.burnTime;
         case 1:
            return this.ticksForCurrentFuel;
         case 2:
            return this.cookTime;
         case 3:
            return this.cookTimeTotal;
         default:
            return 0;
      }
   }

   @Override
   public void b(int i, int j) {
      switch (i) {
         case 0:
            this.burnTime = j;
            break;
         case 1:
            this.ticksForCurrentFuel = j;
            break;
         case 2:
            this.cookTime = j;
            break;
         case 3:
            this.cookTimeTotal = j;
      }
   }

   @Override
   public int g() {
      return 4;
   }

   @Override
   public void l() {
      Arrays.fill(this.items, null);
   }
}
