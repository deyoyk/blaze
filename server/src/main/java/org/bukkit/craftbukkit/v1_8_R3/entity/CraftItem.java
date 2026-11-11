package org.bukkit.craftbukkit.v1_8_R3.entity;

import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityItem;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

public class CraftItem extends CraftEntity implements Item {
   private final EntityItem item;

   public CraftItem(CraftServer server, Entity entity, EntityItem item) {
      super(server, entity);
      this.item = item;
   }

   public CraftItem(CraftServer server, EntityItem entity) {
      this(server, entity, entity);
   }

   @Override
   public ItemStack getItemStack() {
      return CraftItemStack.asCraftMirror(this.item.getItemStack());
   }

   @Override
   public void setItemStack(ItemStack stack) {
      this.item.setItemStack(CraftItemStack.asNMSCopy(stack));
   }

   @Override
   public int getPickupDelay() {
      return this.item.pickupDelay;
   }

   @Override
   public void setPickupDelay(int delay) {
      this.item.pickupDelay = Math.min(delay, 32767);
   }

   @Override
   public String toString() {
      return "CraftItem";
   }

   @Override
   public EntityType getType() {
      return EntityType.DROPPED_ITEM;
   }

   @Override
   public org.bukkit.entity.Entity getOwner() {
      return this.item.owner == null ? null : this.item.owner.getBukkitEntity();
   }

   @Override
   public void setOwner(org.bukkit.entity.Entity entity) {
      this.item.owner = ((CraftEntity)entity).getHandle();
   }
}
