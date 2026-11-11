package net.minecraft.server.v1_8_R3;

import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryDoubleChest;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryView;

public class ContainerChest extends Container {
   private final IInventory container;
   private final int f;
   private CraftInventoryView bukkitEntity = null;
   private final PlayerInventory player;

   public CraftInventoryView getBukkitView() {
      if (this.bukkitEntity != null) {
         return this.bukkitEntity;
      } else {
         CraftInventory inventory;
         if (this.container instanceof PlayerInventory) {
            inventory = new CraftInventoryPlayer((PlayerInventory)this.container);
         } else if (this.container instanceof InventoryLargeChest) {
            inventory = new CraftInventoryDoubleChest((InventoryLargeChest)this.container);
         } else {
            inventory = new CraftInventory(this.container);
         }

         this.bukkitEntity = new CraftInventoryView(this.player.player.getBukkitEntity(), inventory, this);
         return this.bukkitEntity;
      }
   }

   public ContainerChest(IInventory iinventory, IInventory iinventory1, EntityHuman entityhuman) {
      this.container = iinventory1;
      this.f = iinventory1.getSize() / 9;
      iinventory1.startOpen(entityhuman);
      int i = (this.f - 4) * 18;
      this.player = (PlayerInventory)iinventory;

      for (int j = 0; j < this.f; j++) {
         for (int k = 0; k < 9; k++) {
            this.a(new Slot(iinventory1, k + j * 9, 8 + k * 18, 18 + j * 18));
         }
      }

      for (int var7 = 0; var7 < 3; var7++) {
         for (int k = 0; k < 9; k++) {
            this.a(new Slot(iinventory, k + var7 * 9 + 9, 8 + k * 18, 103 + var7 * 18 + i));
         }
      }

      for (int var8 = 0; var8 < 9; var8++) {
         this.a(new Slot(iinventory, var8, 8 + var8 * 18, 161 + i));
      }
   }

   @Override
   public boolean a(EntityHuman entityhuman) {
      return !this.checkReachable ? true : this.container.a(entityhuman);
   }

   @Override
   public ItemStack b(EntityHuman entityhuman, int i) {
      ItemStack itemstack = null;
      Slot slot = this.c.get(i);
      if (slot != null && slot.hasItem()) {
         ItemStack itemstack1 = slot.getItem();
         itemstack = itemstack1.cloneItemStack();
         if (i < this.f * 9) {
            if (!this.a(itemstack1, this.f * 9, this.c.size(), true)) {
               return null;
            }
         } else if (!this.a(itemstack1, 0, this.f * 9, false)) {
            return null;
         }

         if (itemstack1.count == 0) {
            slot.set(null);
         } else {
            slot.f();
         }
      }

      return itemstack;
   }

   @Override
   public void b(EntityHuman entityhuman) {
      super.b(entityhuman);
      this.container.closeContainer(entityhuman);
   }

   public IInventory e() {
      return this.container;
   }
}
