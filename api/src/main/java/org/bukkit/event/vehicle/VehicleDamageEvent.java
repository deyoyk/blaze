package org.bukkit.event.vehicle;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.util.NumberConversions;

public class VehicleDamageEvent extends VehicleEvent implements Cancellable {
   private static final HandlerList handlers = new HandlerList();
   private final Entity attacker;
   private double damage;
   private boolean cancelled;

   @Deprecated
   public VehicleDamageEvent(Vehicle vehicle, Entity attacker, int damage) {
      this(vehicle, attacker, (double)damage);
   }

   public VehicleDamageEvent(Vehicle vehicle, Entity attacker, double damage) {
      super(vehicle);
      this.attacker = attacker;
      this.damage = damage;
   }

   public Entity getAttacker() {
      return this.attacker;
   }

   public double getDamage() {
      return this.damage;
   }

   @Deprecated
   public int _INVALID_getDamage() {
      return NumberConversions.ceil(this.getDamage());
   }

   public void setDamage(double damage) {
      this.damage = damage;
   }

   @Deprecated
   public void _INVALID_setDamage(int damage) {
      this.setDamage(damage);
   }

   @Override
   public boolean isCancelled() {
      return this.cancelled;
   }

   @Override
   public void setCancelled(boolean cancel) {
      this.cancelled = cancel;
   }

   @Override
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
