package org.bukkit.event.entity;

import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;

public class PotionEffectExpireEvent extends PotionEffectRemoveEvent {
   private int duration = 0;

   public PotionEffectExpireEvent(LivingEntity entity, PotionEffect effect) {
      super(entity, effect);
   }

   public int getDuration() {
      return this.duration;
   }

   public void setDuration(int duration) {
      this.duration = Math.max(0, duration);
   }

   @Override
   public boolean isCancelled() {
      return this.duration > 0;
   }

   @Override
   public void setCancelled(boolean cancel) {
      this.duration = cancel ? Integer.MAX_VALUE : 0;
   }
}
