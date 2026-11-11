package net.minecraft.server.v1_8_R3;

import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryMerchant;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryView;

public class ContainerMerchant extends Container {
   private final IMerchant merchant;
   private final InventoryMerchant f;
   private CraftInventoryView bukkitEntity = null;
   private final PlayerInventory player;

   public CraftInventoryView getBukkitView() {
      if (this.bukkitEntity == null) {
         this.bukkitEntity = new CraftInventoryView(this.player.player.getBukkitEntity(), new CraftInventoryMerchant(this.f), this);
      }

      return this.bukkitEntity;
   }

   public ContainerMerchant(PlayerInventory playerinventory, IMerchant imerchant, World world) {
      this.merchant = imerchant;
      this.f = new InventoryMerchant(playerinventory.player, imerchant);
      this.a(new Slot(this.f, 0, 36, 53));
      this.a(new Slot(this.f, 1, 62, 53));
      this.a(new SlotMerchantResult(playerinventory.player, imerchant, this.f, 2, 120, 53));
      this.player = playerinventory;

      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 9; j++) {
            this.a(new Slot(playerinventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
         }
      }

      for (int var6 = 0; var6 < 9; var6++) {
         this.a(new Slot(playerinventory, var6, 8 + var6 * 18, 142));
      }
   }

   public InventoryMerchant e() {
      return this.f;
   }

   @Override
   public void addSlotListener(ICrafting icrafting) {
      super.addSlotListener(icrafting);
   }

   @Override
   public void b() {
      super.b();
   }

   @Override
   public void a(IInventory iinventory) {
      this.f.h();
      super.a(iinventory);
   }

   public void d(int i) {
      this.f.d(i);
   }

   @Override
   public boolean a(EntityHuman entityhuman) {
      return this.merchant.v_() == entityhuman;
   }

   @Override
   public ItemStack b(EntityHuman entityhuman, int i) {
      ItemStack itemstack = null;
      Slot slot = this.c.get(i);
      if (slot != null && slot.hasItem()) {
         ItemStack itemstack1 = slot.getItem();
         itemstack = itemstack1.cloneItemStack();
         if (i == 2) {
            if (!this.a(itemstack1, 3, 39, true)) {
               return null;
            }

            slot.a(itemstack1, itemstack);
         } else if (i != 0 && i != 1) {
            if (i >= 3 && i < 30) {
               if (!this.a(itemstack1, 30, 39, false)) {
                  return null;
               }
            } else if (i >= 30 && i < 39 && !this.a(itemstack1, 3, 30, false)) {
               return null;
            }
         } else if (!this.a(itemstack1, 3, 39, false)) {
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

   @Override
   public void b(EntityHuman entityhuman) {
      super.b(entityhuman);
      this.merchant.a_((EntityHuman)null);
      super.b(entityhuman);
      ItemStack itemstack = this.f.splitWithoutUpdate(0);
      if (itemstack != null) {
         entityhuman.drop(itemstack, false);
      }

      itemstack = this.f.splitWithoutUpdate(1);
      if (itemstack != null) {
         entityhuman.drop(itemstack, false);
      }
   }
}
