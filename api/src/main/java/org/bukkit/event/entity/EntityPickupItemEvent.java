package org.bukkit.event.entity;

import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class EntityPickupItemEvent extends EntityEvent implements Cancellable {
   private static final HandlerList handlers = new HandlerList();
   private final Item item;
   private boolean cancel = false;
   private final int remaining;

   public EntityPickupItemEvent(@NotNull LivingEntity entity, @NotNull Item item, int remaining) {
      super(entity);
      this.item = item;
      this.remaining = remaining;
   }

   @NotNull
   public LivingEntity getEntity() {
      return (LivingEntity)this.entity;
   }

   @NotNull
   public Item getItem() {
      return this.item;
   }

   public int getRemaining() {
      return this.remaining;
   }

   @Override
   public boolean isCancelled() {
      return this.cancel;
   }

   @Override
   public void setCancelled(boolean cancel) {
      this.cancel = cancel;
   }

   @NotNull
   @Override
   public HandlerList getHandlers() {
      return handlers;
   }

   @NotNull
   public static HandlerList getHandlerList() {
      return handlers;
   }
}
