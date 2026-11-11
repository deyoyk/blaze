package net.minecraft.server.v1_8_R3;

import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryView;

public class ContainerDispenser extends Container {
   public IInventory items;
   private CraftInventoryView bukkitEntity = null;
   private final PlayerInventory player;

   public ContainerDispenser(IInventory iinventory, IInventory iinventory1) {
      this.items = iinventory1;
      this.player = (PlayerInventory)iinventory;

      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 3; j++) {
            this.a(new Slot(iinventory1, j + i * 3, 62 + j * 18, 17 + i * 18));
         }
      }

      for (int var5 = 0; var5 < 3; var5++) {
         for (int j = 0; j < 9; j++) {
            this.a(new Slot(iinventory, j + var5 * 9 + 9, 8 + j * 18, 84 + var5 * 18));
         }
      }

      for (int var6 = 0; var6 < 9; var6++) {
         this.a(new Slot(iinventory, var6, 8 + var6 * 18, 142));
      }
   }

   @Override
   public boolean a(EntityHuman entityhuman) {
      return !this.checkReachable ? true : this.items.a(entityhuman);
   }

   @Override
   public ItemStack b(EntityHuman entityhuman, int i) {
      ItemStack itemstack = null;
      Slot slot = this.c.get(i);
      if (slot != null && slot.hasItem()) {
         ItemStack itemstack1 = slot.getItem();
         itemstack = itemstack1.cloneItemStack();
         if (i < 9) {
            if (!this.a(itemstack1, 9, 45, true)) {
               return null;
            }
         } else if (!this.a(itemstack1, 0, 9, false)) {
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
         CraftInventory inventory = new CraftInventory(this.items);
         this.bukkitEntity = new CraftInventoryView(this.player.player.getBukkitEntity(), inventory, this);
         return this.bukkitEntity;
      }
   }
}
