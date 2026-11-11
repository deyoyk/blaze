package org.bukkit.projectiles;

import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;

public interface ProjectileSource {
   <T extends Projectile> T launchProjectile(Class<? extends T> var1);

   <T extends Projectile> T launchProjectile(Class<? extends T> var1, Vector var2);
}
