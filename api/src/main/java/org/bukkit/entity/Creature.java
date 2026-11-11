package org.bukkit.entity;

public interface Creature extends LivingEntity {
   void setTarget(LivingEntity var1);

   LivingEntity getTarget();
}
