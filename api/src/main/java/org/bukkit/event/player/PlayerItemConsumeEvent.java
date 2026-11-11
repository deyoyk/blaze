package org.bukkit.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class PlayerItemConsumeEvent extends PlayerEvent implements Cancellable {
   private static final HandlerList handlers = new HandlerList();
   private boolean isCancelled = false;
   private ItemStack item;

   public PlayerItemConsumeEvent(Player player, ItemStack item) {
      super(player);
      this.item = item;
   }

   public ItemStack getItem() {
      return this.item.clone();
   }

   public void setItem(ItemStack item) {
      if (item == null) {
         this.item = new ItemStack(org.bukkit.Material.AIR);
      } else {
         this.item = item;
      }
   }

   @Override
   public boolean isCancelled() {
      return this.isCancelled;
   }

   @Override
   public void setCancelled(boolean cancel) {
      this.isCancelled = cancel;
   }

   @Override
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
