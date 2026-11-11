package org.bukkit.craftbukkit.v1_8_R3.inventory;

import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutHeldItemSlot;
import net.minecraft.server.v1_8_R3.PacketPlayOutSetSlot;
import org.apache.commons.lang.Validate;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class CraftInventoryPlayer extends CraftInventory implements PlayerInventory, EntityEquipment {
   public CraftInventoryPlayer(net.minecraft.server.v1_8_R3.PlayerInventory inventory) {
      super(inventory);
   }

   public net.minecraft.server.v1_8_R3.PlayerInventory getInventory() {
      return (net.minecraft.server.v1_8_R3.PlayerInventory)this.inventory;
   }

   @Override
   public int getSize() {
      return super.getSize() - 4;
   }

   @Override
   public ItemStack getItemInHand() {
      return CraftItemStack.asCraftMirror(this.getInventory().getItemInHand());
   }

   @Override
   public void setItemInHand(ItemStack stack) {
      this.setItem(this.getHeldItemSlot(), stack);
   }

   @Override
   public void setItem(int index, ItemStack item) {
      super.setItem(index, item);
      if (this.getHolder() != null) {
         EntityPlayer player = ((CraftPlayer)this.getHolder()).getHandle();
         if (player.playerConnection != null) {
            if (index < net.minecraft.server.v1_8_R3.PlayerInventory.getHotbarSize()) {
               index += 36;
            } else if (index > 35) {
               index = 8 - (index - 36);
            }

            player.playerConnection.sendPacket(new PacketPlayOutSetSlot(player.defaultContainer.windowId, index, CraftItemStack.asNMSCopy(item)));
         }
      }
   }

   @Override
   public int getHeldItemSlot() {
      return this.getInventory().itemInHandIndex;
   }

   @Override
   public void setHeldItemSlot(int slot) {
      Validate.isTrue(slot >= 0 && slot < net.minecraft.server.v1_8_R3.PlayerInventory.getHotbarSize(), "Slot is not between 0 and 8 inclusive");
      this.getInventory().itemInHandIndex = slot;
      ((CraftPlayer)this.getHolder()).getHandle().playerConnection.sendPacket(new PacketPlayOutHeldItemSlot(slot));
   }

   @Override
   public ItemStack getHelmet() {
      return this.getItem(this.getSize() + 3);
   }

   @Override
   public ItemStack getChestplate() {
      return this.getItem(this.getSize() + 2);
   }

   @Override
   public ItemStack getLeggings() {
      return this.getItem(this.getSize() + 1);
   }

   @Override
   public ItemStack getBoots() {
      return this.getItem(this.getSize());
   }

   @Override
   public void setHelmet(ItemStack helmet) {
      this.setItem(this.getSize() + 3, helmet);
   }

   @Override
   public void setChestplate(ItemStack chestplate) {
      this.setItem(this.getSize() + 2, chestplate);
   }

   @Override
   public void setLeggings(ItemStack leggings) {
      this.setItem(this.getSize() + 1, leggings);
   }

   @Override
   public void setBoots(ItemStack boots) {
      this.setItem(this.getSize(), boots);
   }

   @Override
   public ItemStack[] getArmorContents() {
      net.minecraft.server.v1_8_R3.ItemStack[] mcItems = this.getInventory().getArmorContents();
      ItemStack[] ret = new ItemStack[mcItems.length];

      for (int i = 0; i < mcItems.length; i++) {
         ret[i] = CraftItemStack.asCraftMirror(mcItems[i]);
      }

      return ret;
   }

   @Override
   public void setArmorContents(ItemStack[] items) {
      int cnt = this.getSize();
      if (items == null) {
         items = new ItemStack[4];
      }

      for (ItemStack item : items) {
         if (item != null && item.getTypeId() != 0) {
            this.setItem(cnt++, item);
         } else {
            this.clear(cnt++);
         }
      }
   }

   @Override
   public int clear(int id, int data) {
      int count = 0;
      ItemStack[] items = this.getContents();
      ItemStack[] armor = this.getArmorContents();
      int armorSlot = this.getSize();

      for (int i = 0; i < items.length; i++) {
         ItemStack item = items[i];
         if (item != null && (id <= -1 || item.getTypeId() == id) && (data <= -1 || item.getData().getData() == data)) {
            count += item.getAmount();
            this.setItem(i, null);
         }
      }

      for (ItemStack item : armor) {
         if (item != null && (id <= -1 || item.getTypeId() == id) && (data <= -1 || item.getData().getData() == data)) {
            count += item.getAmount();
            this.setItem(armorSlot++, null);
         }
      }

      return count;
   }

   @Override
   public HumanEntity getHolder() {
      return (HumanEntity)this.inventory.getOwner();
   }

   @Override
   public float getItemInHandDropChance() {
      return 1.0F;
   }

   @Override
   public void setItemInHandDropChance(float chance) {
      throw new UnsupportedOperationException();
   }

   @Override
   public float getHelmetDropChance() {
      return 1.0F;
   }

   @Override
   public void setHelmetDropChance(float chance) {
      throw new UnsupportedOperationException();
   }

   @Override
   public float getChestplateDropChance() {
      return 1.0F;
   }

   @Override
   public void setChestplateDropChance(float chance) {
      throw new UnsupportedOperationException();
   }

   @Override
   public float getLeggingsDropChance() {
      return 1.0F;
   }

   @Override
   public void setLeggingsDropChance(float chance) {
      throw new UnsupportedOperationException();
   }

   @Override
   public float getBootsDropChance() {
      return 1.0F;
   }

   @Override
   public void setBootsDropChance(float chance) {
      throw new UnsupportedOperationException();
   }
}
