package org.bukkit.event.entity;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;
import java.util.EnumMap;
import java.util.Map;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.util.NumberConversions;

public class EntityDamageEvent extends EntityEvent implements Cancellable {
   private static final HandlerList handlers = new HandlerList();
   private static final EntityDamageEvent.DamageModifier[] MODIFIERS = EntityDamageEvent.DamageModifier.values();
   private static final Function<? super Double, Double> ZERO = Functions.constant(-0.0);
   private final Map<EntityDamageEvent.DamageModifier, Double> modifiers;
   private final Map<EntityDamageEvent.DamageModifier, ? extends Function<? super Double, Double>> modifierFunctions;
   private final Map<EntityDamageEvent.DamageModifier, Double> originals;
   private boolean cancelled;
   private final EntityDamageEvent.DamageCause cause;

   @Deprecated
   public EntityDamageEvent(Entity damagee, EntityDamageEvent.DamageCause cause, int damage) {
      this(damagee, cause, (double)damage);
   }

   @Deprecated
   public EntityDamageEvent(Entity damagee, EntityDamageEvent.DamageCause cause, double damage) {
      this(
         damagee,
         cause,
         new EnumMap<>(ImmutableMap.of(EntityDamageEvent.DamageModifier.BASE, damage)),
         new EnumMap<>(ImmutableMap.of(EntityDamageEvent.DamageModifier.BASE, ZERO))
      );
   }

   public EntityDamageEvent(
      Entity damagee,
      EntityDamageEvent.DamageCause cause,
      Map<EntityDamageEvent.DamageModifier, Double> modifiers,
      Map<EntityDamageEvent.DamageModifier, ? extends Function<? super Double, Double>> modifierFunctions
   ) {
      super(damagee);
      Validate.isTrue(modifiers.containsKey(EntityDamageEvent.DamageModifier.BASE), "BASE DamageModifier missing");
      Validate.isTrue(!modifiers.containsKey(null), "Cannot have null DamageModifier");
      Validate.noNullElements(modifiers.values(), "Cannot have null modifier values");
      Validate.isTrue(modifiers.keySet().equals(modifierFunctions.keySet()), "Must have a modifier function for each DamageModifier");
      Validate.noNullElements(modifierFunctions.values(), "Cannot have null modifier function");
      this.originals = new EnumMap<>(modifiers);
      this.cause = cause;
      this.modifiers = modifiers;
      this.modifierFunctions = modifierFunctions;
   }

   @Override
   public boolean isCancelled() {
      return this.cancelled;
   }

   @Override
   public void setCancelled(boolean cancel) {
      this.cancelled = cancel;
   }

   public double getOriginalDamage(EntityDamageEvent.DamageModifier type) throws IllegalArgumentException {
      Double damage = this.originals.get(type);
      if (damage != null) {
         return damage;
      } else if (type == null) {
         throw new IllegalArgumentException("Cannot have null DamageModifier");
      } else {
         return 0.0;
      }
   }

   public void setDamage(EntityDamageEvent.DamageModifier type, double damage) throws IllegalArgumentException, UnsupportedOperationException {
      if (!this.modifiers.containsKey(type)) {
         throw type == null
            ? new IllegalArgumentException("Cannot have null DamageModifier")
            : new UnsupportedOperationException(type + " is not applicable to " + this.getEntity());
      } else {
         this.modifiers.put(type, damage);
      }
   }

   public double getDamage(EntityDamageEvent.DamageModifier type) throws IllegalArgumentException {
      Validate.notNull(type, "Cannot have null DamageModifier");
      Double damage = this.modifiers.get(type);
      return damage == null ? 0.0 : damage;
   }

   public boolean isApplicable(EntityDamageEvent.DamageModifier type) throws IllegalArgumentException {
      Validate.notNull(type, "Cannot have null DamageModifier");
      return this.modifiers.containsKey(type);
   }

   public double getDamage() {
      return this.getDamage(EntityDamageEvent.DamageModifier.BASE);
   }

   public final double getFinalDamage() {
      double damage = 0.0;

      for (EntityDamageEvent.DamageModifier modifier : MODIFIERS) {
         damage += this.getDamage(modifier);
      }

      return damage;
   }

   @Deprecated
   public int _INVALID_getDamage() {
      return NumberConversions.ceil(this.getDamage());
   }

   public void setDamage(double damage) {
      double remaining = damage;
      double oldRemaining = this.getDamage(EntityDamageEvent.DamageModifier.BASE);

      for (EntityDamageEvent.DamageModifier modifier : MODIFIERS) {
         if (this.isApplicable(modifier)) {
            Function<? super Double, Double> modifierFunction = (Function<? super Double, Double>)this.modifierFunctions.get(modifier);
            double newVanilla = (Double)modifierFunction.apply(remaining);
            double oldVanilla = (Double)modifierFunction.apply(oldRemaining);
            double difference = oldVanilla - newVanilla;
            double old = this.getDamage(modifier);
            if (old > 0.0) {
               this.setDamage(modifier, Math.max(0.0, old - difference));
            } else {
               this.setDamage(modifier, Math.min(0.0, old - difference));
            }

            remaining += newVanilla;
            oldRemaining += oldVanilla;
         }
      }

      this.setDamage(EntityDamageEvent.DamageModifier.BASE, damage);
   }

   @Deprecated
   public void _INVALID_setDamage(int damage) {
      this.setDamage(damage);
   }

   public EntityDamageEvent.DamageCause getCause() {
      return this.cause;
   }

   @Override
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }

   public static enum DamageCause {
      CONTACT,
      ENTITY_ATTACK,
      PROJECTILE,
      SUFFOCATION,
      FALL,
      FIRE,
      FIRE_TICK,
      MELTING,
      LAVA,
      DROWNING,
      BLOCK_EXPLOSION,
      ENTITY_EXPLOSION,
      VOID,
      LIGHTNING,
      SUICIDE,
      STARVATION,
      POISON,
      MAGIC,
      WITHER,
      FALLING_BLOCK,
      THORNS,
      CUSTOM;
   }

   public static enum DamageModifier {
      BASE,
      HARD_HAT,
      BLOCKING,
      ARMOR,
      RESISTANCE,
      MAGIC,
      ABSORPTION;
   }
}
