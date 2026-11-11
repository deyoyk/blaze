package org.bukkit.craftbukkit.v1_8_R3.inventory;

import net.minecraft.server.v1_8_R3.Container;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class CraftInventoryView extends InventoryView {
   private final Container container;
   private final CraftHumanEntity player;
   private final CraftInventory viewing;

   public CraftInventoryView(HumanEntity player, Inventory viewing, Container container) {
      this.player = (CraftHumanEntity)player;
      this.viewing = (CraftInventory)viewing;
      this.container = container;
   }

   @Override
   public Inventory getTopInventory() {
      return this.viewing;
   }

   @Override
   public Inventory getBottomInventory() {
      return this.player.getInventory();
   }

   @Override
   public HumanEntity getPlayer() {
      return this.player;
   }

   @Override
   public InventoryType getType() {
      InventoryType type = this.viewing.getType();
      return type == InventoryType.CRAFTING && this.player.getGameMode() == org.bukkit.GameMode.CREATIVE ? InventoryType.CREATIVE : type;
   }

   @Override
   public void setItem(int slot, ItemStack item) {
      net.minecraft.server.v1_8_R3.ItemStack stack = CraftItemStack.asNMSCopy(item);
      if (slot != -999) {
         this.container.getSlot(slot).set(stack);
      } else {
         this.player.getHandle().drop(stack, false);
      }
   }

   @Override
   public ItemStack getItem(int slot) {
      return slot == -999 ? null : CraftItemStack.asCraftMirror(this.container.getSlot(slot).getItem());
   }

   public boolean isInTop(int rawSlot) {
      return rawSlot < this.viewing.getSize();
   }

   public Container getHandle() {
      return this.container;
   }

   public static InventoryType.SlotType getSlotType(InventoryView inventory, int slot) {
      InventoryType.SlotType type = InventoryType.SlotType.CONTAINER;
      if (slot >= 0 && slot < inventory.getTopInventory().getSize()) {
         switch (inventory.getType()) {
            case FURNACE:
               if (slot == 2) {
                  type = InventoryType.SlotType.RESULT;
               } else if (slot == 1) {
                  type = InventoryType.SlotType.FUEL;
               } else {
                  type = InventoryType.SlotType.CRAFTING;
               }
               break;
            case WORKBENCH:
            case CRAFTING:
               if (slot == 0) {
                  type = InventoryType.SlotType.RESULT;
               } else {
                  type = InventoryType.SlotType.CRAFTING;
               }
               break;
            case ENCHANTING:
               type = InventoryType.SlotType.CRAFTING;
               break;
            case BREWING:
               if (slot == 3) {
                  type = InventoryType.SlotType.FUEL;
               } else {
                  type = InventoryType.SlotType.CRAFTING;
               }
            case PLAYER:
            case CREATIVE:
            case ENDER_CHEST:
            default:
               break;
            case MERCHANT:
               if (slot == 2) {
                  type = InventoryType.SlotType.RESULT;
               } else {
                  type = InventoryType.SlotType.CRAFTING;
               }
               break;
            case ANVIL:
               if (slot == 2) {
                  type = InventoryType.SlotType.RESULT;
               } else {
                  type = InventoryType.SlotType.CRAFTING;
               }
               break;
            case BEACON:
               type = InventoryType.SlotType.CRAFTING;
         }
      } else if (slot == -999) {
         type = InventoryType.SlotType.OUTSIDE;
      } else if (inventory.getType() == InventoryType.CRAFTING) {
         if (slot < 9) {
            type = InventoryType.SlotType.ARMOR;
         } else if (slot > 35) {
            type = InventoryType.SlotType.QUICKBAR;
         }
      } else if (slot >= inventory.countSlots() - 9) {
         type = InventoryType.SlotType.QUICKBAR;
      }

      return type;
   }
}
