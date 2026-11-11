package org.bukkit.entity;

import org.bukkit.projectiles.ProjectileSource;

public interface Projectile extends Entity {
   @Deprecated
   LivingEntity _INVALID_getShooter();

   ProjectileSource getShooter();

   @Deprecated
   void _INVALID_setShooter(LivingEntity var1);

   void setShooter(ProjectileSource var1);

   boolean doesBounce();

   void setBounce(boolean var1);
}
