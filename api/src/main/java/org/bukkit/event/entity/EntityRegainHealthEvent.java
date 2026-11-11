package org.bukkit.event.entity;

import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.util.NumberConversions;

public class EntityRegainHealthEvent extends EntityEvent implements Cancellable {
   private static final HandlerList handlers = new HandlerList();
   private boolean cancelled;
   private double amount;
   private final EntityRegainHealthEvent.RegainReason regainReason;

   @Deprecated
   public EntityRegainHealthEvent(Entity entity, int amount, EntityRegainHealthEvent.RegainReason regainReason) {
      this(entity, (double)amount, regainReason);
   }

   public EntityRegainHealthEvent(Entity entity, double amount, EntityRegainHealthEvent.RegainReason regainReason) {
      super(entity);
      this.amount = amount;
      this.regainReason = regainReason;
   }

   public double getAmount() {
      return this.amount;
   }

   @Deprecated
   public int _INVALID_getAmount() {
      return NumberConversions.ceil(this.getAmount());
   }

   public void setAmount(double amount) {
      this.amount = amount;
   }

   @Deprecated
   public void _INVALID_setAmount(int amount) {
      this.setAmount(amount);
   }

   @Override
   public boolean isCancelled() {
      return this.cancelled;
   }

   @Override
   public void setCancelled(boolean cancel) {
      this.cancelled = cancel;
   }

   public EntityRegainHealthEvent.RegainReason getRegainReason() {
      return this.regainReason;
   }

   @Override
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }

   public static enum RegainReason {
      REGEN,
      SATIATED,
      EATING,
      ENDER_CRYSTAL,
      MAGIC,
      MAGIC_REGEN,
      WITHER_SPAWN,
      WITHER,
      CUSTOM;
   }
}
