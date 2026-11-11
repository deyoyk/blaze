package org.bukkit.craftbukkit.v1_8_R3.inventory;

import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.Container;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.IInventory;
import net.minecraft.server.v1_8_R3.PacketPlayOutOpenWindow;
import net.minecraft.server.v1_8_R3.Slot;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

public class CraftContainer extends Container {
   private final InventoryView view;
   private InventoryType cachedType;
   private String cachedTitle;
   private final int cachedSize;

   public CraftContainer(InventoryView view, int id) {
      this.view = view;
      this.windowId = id;
      IInventory top = ((CraftInventory)view.getTopInventory()).getInventory();
      IInventory bottom = ((CraftInventory)view.getBottomInventory()).getInventory();
      this.cachedType = view.getType();
      this.cachedTitle = view.getTitle();
      this.cachedSize = this.getSize();
      this.setupSlots(top, bottom);
   }

   public CraftContainer(final Inventory inventory, final HumanEntity player, int id) {
      this(new InventoryView() {
         @Override
         public Inventory getTopInventory() {
            return inventory;
         }

         @Override
         public Inventory getBottomInventory() {
            return player.getInventory();
         }

         @Override
         public HumanEntity getPlayer() {
            return player;
         }

         @Override
         public InventoryType getType() {
            return inventory.getType();
         }
      }, id);
   }

   public InventoryView getBukkitView() {
      return this.view;
   }

   private int getSize() {
      return this.view.getTopInventory().getSize();
   }

   public boolean c(EntityHuman entityhuman) {
      if (this.cachedType == this.view.getType() && this.cachedSize == this.getSize() && this.cachedTitle.equals(this.view.getTitle())) {
         return true;
      } else {
         boolean typeChanged = this.cachedType != this.view.getType();
         this.cachedType = this.view.getType();
         this.cachedTitle = this.view.getTitle();
         if (this.view.getPlayer() instanceof CraftPlayer) {
            CraftPlayer player = (CraftPlayer)this.view.getPlayer();
            String type = getNotchInventoryType(this.cachedType);
            IInventory top = ((CraftInventory)this.view.getTopInventory()).getInventory();
            IInventory bottom = ((CraftInventory)this.view.getBottomInventory()).getInventory();
            this.b.clear();
            this.c.clear();
            if (typeChanged) {
               this.setupSlots(top, bottom);
            }

            int size = this.getSize();
            player.getHandle().playerConnection.sendPacket(new PacketPlayOutOpenWindow(this.windowId, type, new ChatComponentText(this.cachedTitle), size));
            player.updateInventory();
         }

         return true;
      }
   }

   public static String getNotchInventoryType(InventoryType type) {
      switch (type) {
         case DISPENSER:
            return "minecraft:dispenser";
         case DROPPER:
         case CRAFTING:
         case PLAYER:
         case CREATIVE:
         case MERCHANT:
         case ENDER_CHEST:
         default:
            return "minecraft:chest";
         case FURNACE:
            return "minecraft:furnace";
         case WORKBENCH:
            return "minecraft:crafting_table";
         case ENCHANTING:
            return "minecraft:enchanting_table";
         case BREWING:
            return "minecraft:brewing_stand";
         case ANVIL:
            return "minecraft:anvil";
         case BEACON:
            return "minecraft:beacon";
         case HOPPER:
            return "minecraft:hopper";
      }
   }

   private void setupSlots(IInventory top, IInventory bottom) {
      switch (this.cachedType) {
         case CHEST:
         case PLAYER:
            this.setupChest(top, bottom);
            break;
         case DISPENSER:
            this.setupDispenser(top, bottom);
         case DROPPER:
         case CREATIVE:
         case MERCHANT:
         case ENDER_CHEST:
         case ANVIL:
         case BEACON:
         default:
            break;
         case FURNACE:
            this.setupFurnace(top, bottom);
            break;
         case WORKBENCH:
         case CRAFTING:
            this.setupWorkbench(top, bottom);
            break;
         case ENCHANTING:
            this.setupEnchanting(top, bottom);
            break;
         case BREWING:
            this.setupBrewing(top, bottom);
            break;
         case HOPPER:
            this.setupHopper(top, bottom);
      }
   }

   private void setupChest(IInventory top, IInventory bottom) {
      int rows = top.getSize() / 9;
      int i = (rows - 4) * 18;

      for (int row = 0; row < rows; row++) {
         for (int col = 0; col < 9; col++) {
            this.a(new Slot(top, col + row * 9, 8 + col * 18, 18 + row * 18));
         }
      }

      for (int var7 = 0; var7 < 3; var7++) {
         for (int col = 0; col < 9; col++) {
            this.a(new Slot(bottom, col + var7 * 9 + 9, 8 + col * 18, 103 + var7 * 18 + i));
         }
      }

      for (int col = 0; col < 9; col++) {
         this.a(new Slot(bottom, col, 8 + col * 18, 161 + i));
      }
   }

   private void setupWorkbench(IInventory top, IInventory bottom) {
      this.a(new Slot(top, 0, 124, 35));

      for (int row = 0; row < 3; row++) {
         for (int col = 0; col < 3; col++) {
            this.a(new Slot(top, 1 + col + row * 3, 30 + col * 18, 17 + row * 18));
         }
      }

      for (int var5 = 0; var5 < 3; var5++) {
         for (int col = 0; col < 9; col++) {
            this.a(new Slot(bottom, col + var5 * 9 + 9, 8 + col * 18, 84 + var5 * 18));
         }
      }

      for (int col = 0; col < 9; col++) {
         this.a(new Slot(bottom, col, 8 + col * 18, 142));
      }
   }

   private void setupFurnace(IInventory top, IInventory bottom) {
      this.a(new Slot(top, 0, 56, 17));
      this.a(new Slot(top, 1, 56, 53));
      this.a(new Slot(top, 2, 116, 35));

      for (int row = 0; row < 3; row++) {
         for (int col = 0; col < 9; col++) {
            this.a(new Slot(bottom, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
         }
      }

      for (int col = 0; col < 9; col++) {
         this.a(new Slot(bottom, col, 8 + col * 18, 142));
      }
   }

   private void setupDispenser(IInventory top, IInventory bottom) {
      for (int row = 0; row < 3; row++) {
         for (int col = 0; col < 3; col++) {
            this.a(new Slot(top, col + row * 3, 61 + col * 18, 17 + row * 18));
         }
      }

      for (int var5 = 0; var5 < 3; var5++) {
         for (int col = 0; col < 9; col++) {
            this.a(new Slot(bottom, col + var5 * 9 + 9, 8 + col * 18, 84 + var5 * 18));
         }
      }

      for (int col = 0; col < 9; col++) {
         this.a(new Slot(bottom, col, 8 + col * 18, 142));
      }
   }

   private void setupEnchanting(IInventory top, IInventory bottom) {
      this.a(new Slot(top, 0, 25, 47));

      for (int row = 0; row < 3; row++) {
         for (int i1 = 0; i1 < 9; i1++) {
            this.a(new Slot(bottom, i1 + row * 9 + 9, 8 + i1 * 18, 84 + row * 18));
         }
      }

      for (int var5 = 0; var5 < 9; var5++) {
         this.a(new Slot(bottom, var5, 8 + var5 * 18, 142));
      }
   }

   private void setupBrewing(IInventory top, IInventory bottom) {
      this.a(new Slot(top, 0, 56, 46));
      this.a(new Slot(top, 1, 79, 53));
      this.a(new Slot(top, 2, 102, 46));
      this.a(new Slot(top, 3, 79, 17));

      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 9; j++) {
            this.a(new Slot(bottom, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
         }
      }

      for (int var5 = 0; var5 < 9; var5++) {
         this.a(new Slot(bottom, var5, 8 + var5 * 18, 142));
      }
   }

   private void setupHopper(IInventory top, IInventory bottom) {
      byte b0 = 51;

      for (int i = 0; i < top.getSize(); i++) {
         this.a(new Slot(top, i, 44 + i * 18, 20));
      }

      for (int var6 = 0; var6 < 3; var6++) {
         for (int j = 0; j < 9; j++) {
            this.a(new Slot(bottom, j + var6 * 9 + 9, 8 + j * 18, var6 * 18 + b0));
         }
      }

      for (int var7 = 0; var7 < 9; var7++) {
         this.a(new Slot(bottom, var7, 8 + var7 * 18, 58 + b0));
      }
   }

   public boolean a(EntityHuman entity) {
      return true;
   }
}
