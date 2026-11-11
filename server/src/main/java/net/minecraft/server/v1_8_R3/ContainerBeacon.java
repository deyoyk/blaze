package net.minecraft.server.v1_8_R3;

import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryBeacon;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryView;

public class ContainerBeacon extends Container {
   private final IInventory beacon;
   private final ContainerBeacon.SlotBeacon f;
   private CraftInventoryView bukkitEntity = null;
   private final PlayerInventory player;

   public ContainerBeacon(IInventory iinventory, IInventory iinventory1) {
      this.player = (PlayerInventory)iinventory;
      this.beacon = iinventory1;
      this.a(this.f = new ContainerBeacon.SlotBeacon(iinventory1, 0, 136, 110));
      byte b0 = 36;
      short short0 = 137;

      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 9; j++) {
            this.a(new Slot(iinventory, j + i * 9 + 9, b0 + j * 18, short0 + i * 18));
         }
      }

      for (int var7 = 0; var7 < 9; var7++) {
         this.a(new Slot(iinventory, var7, b0 + var7 * 18, 58 + short0));
      }
   }

   @Override
   public void addSlotListener(ICrafting icrafting) {
      super.addSlotListener(icrafting);
      icrafting.setContainerData(this, this.beacon);
   }

   public IInventory e() {
      return this.beacon;
   }

   @Override
   public void b(EntityHuman entityhuman) {
      super.b(entityhuman);
      ItemStack itemstack = this.f.a(this.f.getMaxStackSize());
      if (itemstack != null) {
         entityhuman.drop(itemstack, false);
      }
   }

   @Override
   public boolean a(EntityHuman entityhuman) {
      return !this.checkReachable ? true : this.beacon.a(entityhuman);
   }

   @Override
   public ItemStack b(EntityHuman entityhuman, int i) {
      ItemStack itemstack = null;
      Slot slot = this.c.get(i);
      if (slot != null && slot.hasItem()) {
         ItemStack itemstack1 = slot.getItem();
         itemstack = itemstack1.cloneItemStack();
         if (i == 0) {
            if (!this.a(itemstack1, 1, 37, true)) {
               return null;
            }

            slot.a(itemstack1, itemstack);
         } else if (!this.f.hasItem() && this.f.isAllowed(itemstack1) && itemstack1.count == 1) {
            if (!this.a(itemstack1, 0, 1, false)) {
               return null;
            }
         } else if (i < 28) {
            if (!this.a(itemstack1, 28, 37, false)) {
               return null;
            }
         } else if (i < 37) {
            if (!this.a(itemstack1, 1, 28, false)) {
               return null;
            }
         } else if (!this.a(itemstack1, 1, 37, false)) {
            return null;
         }

         if (itemstack1.count == 0) {
            slot.set(null);
         } else {
            slot.f();
         }

         if (itemstack1.count == itemstack.count) {
            return null;
         }

         slot.a(entityhuman, itemstack1);
      }

      return itemstack;
   }

   public CraftInventoryView getBukkitView() {
      if (this.bukkitEntity != null) {
         return this.bukkitEntity;
      } else {
         CraftInventory inventory = new CraftInventoryBeacon((TileEntityBeacon)this.beacon);
         this.bukkitEntity = new CraftInventoryView(this.player.player.getBukkitEntity(), inventory, this);
         return this.bukkitEntity;
      }
   }

   static class SlotBeacon extends Slot {
      public SlotBeacon(IInventory iinventory, int i, int j, int k) {
         super(iinventory, i, j, k);
      }

      @Override
      public boolean isAllowed(ItemStack itemstack) {
         return itemstack != null
            && (
               itemstack.getItem() == Items.EMERALD
                  || itemstack.getItem() == Items.DIAMOND
                  || itemstack.getItem() == Items.GOLD_INGOT
                  || itemstack.getItem() == Items.IRON_INGOT
            );
      }

      @Override
      public int getMaxStackSize() {
         return 1;
      }
   }
}
