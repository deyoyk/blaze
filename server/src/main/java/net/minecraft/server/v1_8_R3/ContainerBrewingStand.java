package net.minecraft.server.v1_8_R3;

import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryBrewer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryView;

public class ContainerBrewingStand extends Container {
   private final IInventory brewingStand;
   private final Slot f;
   private int g;
   private CraftInventoryView bukkitEntity = null;
   private final PlayerInventory player;

   public ContainerBrewingStand(PlayerInventory playerinventory, IInventory iinventory) {
      this.player = playerinventory;
      this.brewingStand = iinventory;
      this.a(new ContainerBrewingStand.SlotPotionBottle(playerinventory.player, iinventory, 0, 56, 46));
      this.a(new ContainerBrewingStand.SlotPotionBottle(playerinventory.player, iinventory, 1, 79, 53));
      this.a(new ContainerBrewingStand.SlotPotionBottle(playerinventory.player, iinventory, 2, 102, 46));
      this.f = this.a(new ContainerBrewingStand.SlotBrewing(iinventory, 3, 79, 17));

      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 9; j++) {
            this.a(new Slot(playerinventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
         }
      }

      for (int var5 = 0; var5 < 9; var5++) {
         this.a(new Slot(playerinventory, var5, 8 + var5 * 18, 142));
      }
   }

   @Override
   public void addSlotListener(ICrafting icrafting) {
      super.addSlotListener(icrafting);
      icrafting.setContainerData(this, this.brewingStand);
   }

   @Override
   public void b() {
      super.b();

      for (ICrafting listener : this.listeners) {
         if (this.g != this.brewingStand.getProperty(0)) {
            listener.setContainerData(this, 0, this.brewingStand.getProperty(0));
         }
      }

      this.g = this.brewingStand.getProperty(0);
   }

   @Override
   public boolean a(EntityHuman entityhuman) {
      return !this.checkReachable ? true : this.brewingStand.a(entityhuman);
   }

   @Override
   public ItemStack b(EntityHuman entityhuman, int i) {
      ItemStack itemstack = null;
      Slot slot = this.c.get(i);
      if (slot != null && slot.hasItem()) {
         ItemStack itemstack1 = slot.getItem();
         itemstack = itemstack1.cloneItemStack();
         if (i > 2 && i != 3) {
            if (!this.f.hasItem() && this.f.isAllowed(itemstack1)) {
               if (!this.a(itemstack1, 3, 4, false)) {
                  return null;
               }
            } else if (ContainerBrewingStand.SlotPotionBottle.b_(itemstack)) {
               if (!this.a(itemstack1, 0, 3, false)) {
                  return null;
               }
            } else if (i < 31) {
               if (!this.a(itemstack1, 31, 40, false)) {
                  return null;
               }
            } else if (i < 40) {
               if (!this.a(itemstack1, 4, 31, false)) {
                  return null;
               }
            } else if (!this.a(itemstack1, 4, 40, false)) {
               return null;
            }
         } else {
            if (!this.a(itemstack1, 4, 40, true)) {
               return null;
            }

            slot.a(itemstack1, itemstack);
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
         this.bukkitEntity = new CraftInventoryView(this.player.player.getBukkitEntity(), new CraftInventoryBrewer(this.brewingStand), this);
         return this.bukkitEntity;
      }
   }

   static class SlotBrewing extends Slot {
      public SlotBrewing(IInventory iinventory, int i, int j, int k) {
         super(iinventory, i, j, k);
      }

      @Override
      public boolean isAllowed(ItemStack itemstack) {
         return itemstack != null && itemstack.getItem().l(itemstack);
      }

      @Override
      public int getMaxStackSize() {
         return 64;
      }
   }

   static class SlotPotionBottle extends Slot {
      public SlotPotionBottle(EntityHuman entityhuman, IInventory iinventory, int i, int j, int k) {
         super(iinventory, i, j, k);
      }

      @Override
      public boolean isAllowed(ItemStack itemstack) {
         return b_(itemstack);
      }

      @Override
      public int getMaxStackSize() {
         return 1;
      }

      public static boolean b_(ItemStack itemstack) {
         return itemstack != null && (itemstack.getItem() == Items.POTION || itemstack.getItem() == Items.GLASS_BOTTLE);
      }
   }
}
